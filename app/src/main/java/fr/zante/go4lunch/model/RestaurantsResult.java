package fr.zante.go4lunch.model;

import java.util.List;

public class RestaurantsResult {

    private List<RestaurantJson> results;

    public List<RestaurantJson> getRestaurants() {
        return results;
    }

    public void setRestaurants(List<RestaurantJson> restaurants) {
        this.results = restaurants;
    }
}
