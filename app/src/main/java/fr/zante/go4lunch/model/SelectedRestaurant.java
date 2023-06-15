package fr.zante.go4lunch.model;

public class SelectedRestaurant {

    private String restaurantId;
    private String name;
    private int memberJoiningNumber;

    public SelectedRestaurant(String restaurantId, String name, int memberJoiningNumber) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.memberJoiningNumber = memberJoiningNumber;
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

    public int getMemberJoiningNumber() { return memberJoiningNumber; }
    public void setMemberJoiningNumber(int memberJoiningNumber) { this.memberJoiningNumber = memberJoiningNumber; }
}