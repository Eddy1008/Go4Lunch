package fr.zante.go4lunch.model;

public class Member {

    private String name;

    private String avatarUrl;

    private String selectedRestaurantName;

    public Member(String name, String avatarUrl, String selectedRestaurantName) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.selectedRestaurantName = selectedRestaurantName;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getSelectedRestaurantName() {
        return selectedRestaurantName;
    }
    public void setSelectedRestaurantName(String selectedRestaurantName) {
        this.selectedRestaurantName = selectedRestaurantName;
    }
}
