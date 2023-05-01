package fr.zante.go4lunch.ui.listview;

import android.os.Bundle;
import android.util.Log;
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
import fr.zante.go4lunch.SharedViewModel;
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

    //SharedViewModel
    private SharedViewModel sharedViewModel;
    private double myLat = 0;
    private double myLng = 0;

    private List<RestaurantJson> restaurants = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListviewRecyclerViewAdapter adapter;
    private RestaurantsViewModel restaurantsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.listviewRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // SharedViewModel
        this.sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        myLat = sharedViewModel.getMyLat();
        myLng = sharedViewModel.getMyLng();
        adapter = new ListviewRecyclerViewAdapter(this.restaurants, this.myLat, this.myLng);

        configureViewModel();
        getRestaurants();
        initList();

        return root;
    }

    private void configureViewModel() {
        this.restaurantsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);
        this.restaurantsViewModel.init(myLat, myLng);
    }

    private void getRestaurants() {
        restaurantsViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurantJsons -> {
            restaurants = new ArrayList<>(restaurantJsons);
            adapter.updateRestaurants(restaurants);
        });
    }

    private void initList() {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}