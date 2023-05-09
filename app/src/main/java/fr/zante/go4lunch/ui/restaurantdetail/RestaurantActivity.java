package fr.zante.go4lunch.ui.restaurantdetail;

import static android.Manifest.permission.CALL_PHONE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import fr.zante.go4lunch.BuildConfig;
import fr.zante.go4lunch.databinding.ActivityRestaurantBinding;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.ui.RestaurantsViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;
    private RestaurantJson restaurant;
    private RestaurantsViewModel restaurantsViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setPreviousPageButton();
        getRestaurantDataFromBundle();
    }

    void getRestaurantDataFromBundle() {
        Intent intent = getIntent();

        Bundle myBundle = intent.getBundleExtra("BUNDLE_RESTAURANT_SELECTED");
        String myPlaceId = (String) myBundle.get("RESTAURANT_PLACE_ID");

        this.restaurantsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);
        this.restaurantsViewModel.initSelectedRestaurant(myPlaceId);
        this.restaurantsViewModel.getRestaurantById().observe(this, restaurantJson -> {

            binding.restaurantDetailName.setText(restaurantJson.getName());
            binding.restaurantDetailAddress.setText(restaurantJson.getVicinity());
            String myBasePhotoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
            String apiKey = "&key=" + BuildConfig.MAPS_API_KEY;
            String myPhotoURL = myBasePhotoURL + restaurantJson.getPhotos().get(0).getPhoto_reference() + apiKey;

            Glide.with(this.getApplicationContext())
                    .load(myPhotoURL)
                    .into(binding.restaurantDetailPhoto);

            binding.restaurantDetailLinearLayoutPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + restaurantJson.getFormatted_phone_number()));
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(intent);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{CALL_PHONE}, 1);
                        }
                    }
                }
            });

            if (restaurantJson.getWebsite() != null) {
                binding.restaurantDetailLinearLayoutWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentWebsite = new Intent(Intent.ACTION_VIEW);
                        intentWebsite.setData(Uri.parse(restaurantJson.getWebsite()));
                        startActivity(intentWebsite);
                    }
                });
            } else {
                binding.restaurantDetailLinearLayoutWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RestaurantActivity.this, "No website for the moment !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void setPreviousPageButton() {
        binding.restaurantDetailPreviousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
