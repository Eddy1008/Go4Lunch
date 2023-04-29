package fr.zante.go4lunch.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.BuildConfig;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.RestaurantsResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlacesRepository {

    private final GooglePlacesApi googlePlacesAPi;

    public GooglePlacesRepository(GooglePlacesApi googlePlacesAPi) {
        this.googlePlacesAPi = googlePlacesAPi;
    }

    public LiveData<List<RestaurantJson>> getRestaurantLiveData(String myLocation) {
        MutableLiveData<List<RestaurantJson>> restaurantJsonsMutableLiveData = new MutableLiveData<>();

        String myResearchRadius = "500";
        String myResearchType = "restaurant";
        String myResearchFields = "name,geometry,opening_hours,formatted_phone_number,photos,website";
        String apiKey = BuildConfig.MAPS_API_KEY;

        googlePlacesAPi.getNearbyPlaces(myLocation, myResearchRadius, myResearchType, myResearchFields, apiKey)
                .enqueue(new Callback<RestaurantsResult>() {
                    @Override
                    public void onResponse(Call<RestaurantsResult> call, Response<RestaurantsResult> response) {
                        if (response.body() != null) {
                            restaurantJsonsMutableLiveData.setValue(response.body().getRestaurants());
                        }
                    }

                    @Override
                    public void onFailure(Call<RestaurantsResult> call, Throwable t) {
                        restaurantJsonsMutableLiveData.setValue(null);
                    }
                });

        return restaurantJsonsMutableLiveData;
    }
}
