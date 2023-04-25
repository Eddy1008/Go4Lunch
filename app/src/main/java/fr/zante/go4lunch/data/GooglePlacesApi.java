package fr.zante.go4lunch.data;

import fr.zante.go4lunch.model.RestaurantsResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=50.6292%2C3.0573&radius=2000&type=restaurant&fields=name%2Copening_hours%2Cgeometry%2Cformatted_phone_number%2Cphotos%2Cwebsite&key=
    // String myLocation = "50.6292,3.0573"
    // String myResearchRadius = "2000"
    // String myResearchType = "restaurant"
    // String myResearchFields = "name,geometry,opening_hours,formatted_phone_number,photos,website"
    @GET("nearbysearch/json")
    Call<RestaurantsResult> getNearbyPlaces(@Query("location") String myLocation,
                                            @Query("radius") String myResearchRadius,
                                            @Query("type") String myResearchType,
                                            @Query("fields") String myResearchFields,
                                            @Query("key") String apiKey);

}