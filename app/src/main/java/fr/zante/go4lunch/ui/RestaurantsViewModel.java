package fr.zante.go4lunch.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.model.RestaurantJson;

public class RestaurantsViewModel extends ViewModel {

    // Repository
    private final GooglePlacesRepository repository;

    // DATA
    private LiveData<List<RestaurantJson>> restaurantsData;
    private LiveData<RestaurantJson> selectedRestaurant;

    // Constructor
    public RestaurantsViewModel(GooglePlacesRepository repository) {
        this.repository = repository;
    }

    public void init(double lat, double lng) {
        if (this.restaurantsData != null) {
            return;
        }
        restaurantsData = repository.getRestaurantLiveData(lat, lng);
    }

    public LiveData<List<RestaurantJson>> getRestaurants() {
        return this.restaurantsData;
    }

    public void initSelectedRestaurant(String myPlaceId) {
        selectedRestaurant = repository.getRestaurantLiveDataById(myPlaceId);
    }

    public LiveData<RestaurantJson> getRestaurantById() { return this.selectedRestaurant; }

}
