package fr.zante.go4lunch.model;

public class RestaurantJson {

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
}
