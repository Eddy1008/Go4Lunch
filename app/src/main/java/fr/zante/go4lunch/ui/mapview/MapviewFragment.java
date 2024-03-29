package fr.zante.go4lunch.ui.mapview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.zante.go4lunch.BuildConfig;
import fr.zante.go4lunch.R;
import fr.zante.go4lunch.databinding.FragmentMapviewBinding;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.SelectedRestaurant;
import fr.zante.go4lunch.ui.MembersViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantActivity;

public class MapviewFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;

    // DATA
    private MembersViewModel membersViewModel;
    private List<RestaurantJson> restaurants = new ArrayList<>();
    private List<SelectedRestaurant> selectedRestaurantsList = new ArrayList<>();
    private LatLng myLatLng;
    private final List<Marker> markers = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    // Default location
    private final LatLng defaultLocation = new LatLng(50.6292, 3.0573);
    // Default zoom
    private static final int DEFAULT_ZOOM = 14;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        locationPermissionGranted = true;
                        // Turn on the My Location layer and the related control on the map.
                        updateLocationUI();
                        // Get the current location of the device and set the position of the map.
                        getDeviceLocation();
                    } else {
                        locationPermissionGranted = false;
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // PlacesClient
        Places.initialize(Objects.requireNonNull(getActivity()).getApplicationContext(), BuildConfig.MAPS_API_KEY);

        // FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        // MembersViewModel
        membersViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(MembersViewModel.class);
        membersViewModel.initSelectedRestaurantsList();

        fr.zante.go4lunch.databinding.FragmentMapviewBinding binding = FragmentMapviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.fabFindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapReady(map);
            }
        });

        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(defaultLocation).title("Marker Lille"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                myLatLng = new LatLng( lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                membersViewModel.setMyLatLng(myLatLng);

                                configureRestaurantViewModel();
                                getRestaurantsList();
                            }
                        } else {
                            Log.e("TAG", "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void configureRestaurantViewModel() {
        membersViewModel.init(myLatLng.latitude, myLatLng.longitude);
    }

    private void getRestaurantsList() {
        membersViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurantJsons -> {
            restaurants = new ArrayList<>(restaurantJsons);
            membersViewModel.getSelectedRestaurants().observe(this, selectedRestaurants -> {
                selectedRestaurantsList = new ArrayList<>(selectedRestaurants);
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers.clear();
                for (RestaurantJson restaurant : restaurants) {
                    if (checkIsIsInMyList(restaurant.getPlace_id(), selectedRestaurantsList)) {
                        Marker marker = map.addMarker(new MarkerOptions()
                                        .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                                        .title(restaurant.getName())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                );
                        if (marker != null) {
                            marker.setTag(restaurant.getPlace_id());
                        }
                        markers.add(marker);
                    } else {
                        Marker marker = map.addMarker(new MarkerOptions()
                                        .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                                        .title(restaurant.getName())
                                );
                        if (marker != null) {
                            marker.setTag(restaurant.getPlace_id());
                        }
                        markers.add(marker);
                    }
                }
            });

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    Intent intent = new Intent(getContext(), RestaurantActivity.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putString("RESTAURANT_PLACE_ID", (String) marker.getTag());
                    myBundle.putString("USER_NAME", membersViewModel.getMyUserName());
                    intent.putExtra("BUNDLE_RESTAURANT_SELECTED", myBundle);
                    startActivity(intent);
                    return false;
                }
            });
        });
    }

    private boolean checkIsIsInMyList(String restaurantId, List<SelectedRestaurant> mySelectedRestaurantList) {
        for (SelectedRestaurant selectedRestaurant : mySelectedRestaurantList) {
            if (selectedRestaurant.getRestaurantId().equals(restaurantId)) {
                return true;
            }
        }
        return false;
    }
}