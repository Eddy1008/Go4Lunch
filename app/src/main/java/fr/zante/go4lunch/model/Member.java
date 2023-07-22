package fr.zante.go4lunch.model;

import java.io.Serializable;

public class Member implements Serializable {

    private String memberId;
    private String name;
    private String mail;
    private String avatarUrl;
    private String selectedRestaurantId;
    private String selectedRestaurantName;
    private boolean isNotificationsAllowed;

    public Member(String memberId, String name, String mail, String avatarUrl, String selectedRestaurantId, String selectedRestaurantName, boolean isNotificationsAllowed) {
        this.memberId = memberId;
        this.name = name;
        this.mail = mail;
        this.avatarUrl = avatarUrl;
        this.selectedRestaurantId = selectedRestaurantId;
        this.selectedRestaurantName = selectedRestaurantName;
        this.isNotificationsAllowed = isNotificationsAllowed;
    }

    public Member() {}

    public String getMemberId() {
        return memberId;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }
    public void setSelectedRestaurantId(String selectedRestaurantId) {
        this.selectedRestaurantId = selectedRestaurantId;
    }

    public String getSelectedRestaurantName() {
        return selectedRestaurantName;
    }
    public void setSelectedRestaurantName(String selectedRestaurantName) {
        this.selectedRestaurantName = selectedRestaurantName;
    }

    public boolean isNotificationsAllowed() { return isNotificationsAllowed; }
    public void setNotificationsAllowed(boolean notificationsAllowed) { isNotificationsAllowed = notificationsAllowed; }
}
