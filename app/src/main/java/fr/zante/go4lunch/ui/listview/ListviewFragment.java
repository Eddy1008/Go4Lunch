package fr.zante.go4lunch.ui.listview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.SharedViewModel;
import fr.zante.go4lunch.databinding.FragmentListviewBinding;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.ui.RestaurantsViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class ListviewFragment extends Fragment {

    private FragmentListviewBinding binding;

    //SharedViewModel
    private SharedViewModel sharedViewModel;
    private double myLat = 0;
    private double myLng = 0;
    private String userName;

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
        userName = sharedViewModel.getMyUserName();
        adapter = new ListviewRecyclerViewAdapter(this.restaurants, this.myLat, this.myLng, this.userName);

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