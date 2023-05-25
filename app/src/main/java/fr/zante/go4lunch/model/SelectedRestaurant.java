package fr.zante.go4lunch.model;

public class SelectedRestaurant {

    private String restaurantId;
    private String name;

    public SelectedRestaurant(String restaurantId, String name) {
        this.restaurantId = restaurantId;
        this.name = name;
    }

    public SelectedRestaurant() {}

    public String getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}