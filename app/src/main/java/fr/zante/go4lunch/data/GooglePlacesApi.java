package fr.zante.go4lunch.data;

import fr.zante.go4lunch.model.RestaurantResultById;
import fr.zante.go4lunch.model.RestaurantsResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    @GET("nearbysearch/json")
    Call<RestaurantsResult> getNearbyPlaces(@Query("location") String myLocation,
                                            @Query("radius") String myResearchRadius,
                                            @Query("type") String myResearchType,
                                            @Query("fields") String myResearchFields,
                                            @Query("key") String apiKey);

    @GET("details/json")
    Call<RestaurantResultById> getPlaceInfoById(@Query("place_id") String myPlaceId,
                                                @Query("fields") String myFields,
                                                @Query("key") String apiKey);

}