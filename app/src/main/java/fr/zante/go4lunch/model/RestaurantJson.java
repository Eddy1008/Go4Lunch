package fr.zante.go4lunch.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RestaurantJson implements Serializable {

    private String name;
    private String place_id;
    private String formatted_phone_number;
    private String website;
    private String vicinity;
    private GeometryJson geometry;
    private OpeningJson opening_hours;
    private List<PhotoJson> photos;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {return place_id;}
    public void setPlace_id(String place_id) {this.place_id = place_id;}

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }
    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }

    public String getVicinity() {
        return vicinity;
    }
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public GeometryJson getGeometry() {
        return geometry;
    }
    public void setGeometry(GeometryJson geometry) {
        this.geometry = geometry;
    }

    public OpeningJson getOpening_hours() {
        return opening_hours;
    }
    public void setOpening_hours(OpeningJson opening_hours) {
        this.opening_hours = opening_hours;
    }

    public List<PhotoJson> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoJson> photos) {
        this.photos = photos;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RestaurantJson restaurant = (RestaurantJson) obj;
        return Objects.equals(name, restaurant.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
