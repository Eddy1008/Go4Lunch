package fr.zante.go4lunch.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class RestaurantJson implements Serializable {

    private String name;
    private String vicinity;
    private GeometryJson geometry;
    private OpeningJson opening_hours;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
