package fr.zante.go4lunch.ui.listview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.BuildConfig;
import fr.zante.go4lunch.data.GooglePlacesApi;
import fr.zante.go4lunch.databinding.FragmentListviewBinding;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.RestaurantsResult;
import fr.zante.go4lunch.ui.RestaurantsViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListviewFragment extends Fragment {

    private FragmentListviewBinding binding;

    private List<RestaurantJson> restaurants = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListviewRecyclerViewAdapter adapter = new ListviewRecyclerViewAdapter(this.restaurants);
    private RestaurantsViewModel restaurantsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.listviewRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        configureViewModel();
        getRestaurants();
        initList(restaurants);

        /**
        // TODO create retrofit instance with GsonConverterFactory
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        GooglePlacesApi service = retrofit.create(GooglePlacesApi.class);

        // TODO get parameters used by api request
        String myLocation = "50.6292,3.0573";
        String myResearchRadius = "1000";
        String myResearchType = "restaurant";
        String myResearchFields = "name,geometry,opening_hours,formatted_phone_number,photos,website";
        String apiKey = BuildConfig.MAPS_API_KEY;

         // TODO call Api
         Call<RestaurantsResult> result = service.getNearbyPlaces(
                 myLocation,
                 myResearchRadius,
                 myResearchType,
                 myResearchFields,
                 apiKey);

        result.enqueue(new Callback<RestaurantsResult>() {
            @Override
            public void onResponse(Call<RestaurantsResult> call, Response<RestaurantsResult> response) {
                if (response.isSuccessful()) {
                    // Recupere la liste de restaurant:
                    List<RestaurantJson> myList = response.body().getRestaurants();
                    // Alimenter le recyclerView avec cette liste:
                    initList(myList);
                }
            }

            @Override
            public void onFailure(Call<RestaurantsResult> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), "Erreur serveur", Toast.LENGTH_SHORT).show();
            }
        });
         */

        return root;
    }

    private void configureViewModel() {
        this.restaurantsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);
        this.restaurantsViewModel.init();
    }

    private void getRestaurants() {
        restaurantsViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurantJsons -> {
            restaurants = new ArrayList<>(restaurantJsons);
            adapter.updateRestaurants(restaurants);
        });
    }

    private void initList(List<RestaurantJson> myList) {
        // TODO recupérer la liste de restaurant via un repository
        restaurants = myList;
        //recyclerView.setAdapter(new ListviewRecyclerViewAdapter(restaurants));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}