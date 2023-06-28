package fr.zante.go4lunch.ui.listview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.databinding.FragmentListviewBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.SelectedRestaurant;
import fr.zante.go4lunch.ui.MembersViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class ListviewFragment extends Fragment {

    private FragmentListviewBinding binding;

    private double myLat = 0;
    private double myLng = 0;
    private String userName;

    private List<RestaurantJson> restaurants = new ArrayList<>();
    private List<SelectedRestaurant> selectedRestaurantsList = new ArrayList<>();
    private List<Integer> restaurantsMembersNumber = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListviewRecyclerViewAdapter adapter;
    private MembersViewModel membersViewModel;

    private SearchView searchView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.listviewRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        this.membersViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(MembersViewModel.class);
        myLat = membersViewModel.getMyLat();
        myLng = membersViewModel.getMyLng();
        userName = membersViewModel.getMyUserName();
        adapter = new ListviewRecyclerViewAdapter(this.restaurants, this.myLat, this.myLng, this.userName, this.restaurantsMembersNumber);

        getSelectedRestaurantList();
        configureViewModel();
        getRestaurants();
        initList();

        initSearch();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSelectedRestaurantList();
        getRestaurants();
    }

    private void configureViewModel() {
        this.membersViewModel.init(myLat, myLng);
    }

    private void getSelectedRestaurantList() {
        membersViewModel.getSelectedRestaurants().observe(getViewLifecycleOwner(), selectedRestaurants -> {
            selectedRestaurantsList = new ArrayList<>(selectedRestaurants);
        });
    }

    private void getRestaurants() {
        membersViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurantJsons -> {
            restaurants = new ArrayList<>(restaurantJsons);
            restaurantsMembersNumber.clear();
            for (RestaurantJson restaurant : restaurants) {
                int myRestaurantMemberNumber = 0;
                for (int i=0; i<selectedRestaurantsList.size(); i++) {
                    if (restaurant.getPlace_id().equals(selectedRestaurantsList.get(i).getRestaurantId())) {
                        myRestaurantMemberNumber = selectedRestaurantsList.get(i).getMemberJoiningNumber();
                        break;
                    }
                }
                restaurantsMembersNumber.add(myRestaurantMemberNumber);
            }
            adapter.updateRestaurants(restaurants, restaurantsMembersNumber);
        });
    }

    private void initSearch() {
        SearchView searchView = binding.listviewSearchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<RestaurantJson> filteredList = new ArrayList<>();
                for (RestaurantJson restaurantJson : restaurants) {
                    if (restaurantJson.getName().toLowerCase().contains(s.toLowerCase())) {
                        filteredList.add(restaurantJson);
                    }
                }
                ArrayList<Integer> filteredListInteger = new ArrayList<>();
                for (RestaurantJson restaurantJson : filteredList) {
                    int myInt = 0;
                    for (int i=0; i<selectedRestaurantsList.size(); i++) {
                        if (restaurantJson.getPlace_id().equals(selectedRestaurantsList.get(i).getRestaurantId())) {
                            myInt = selectedRestaurantsList.get(i).getMemberJoiningNumber();
                            break;
                        }
                    }
                    filteredListInteger.add(myInt);
                }
                adapter = new ListviewRecyclerViewAdapter(filteredList, myLat, myLng, userName, filteredListInteger);
                recyclerView.setAdapter(adapter);
                return false;
            }
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