package fr.zante.go4lunch.model;

import java.io.Serializable;

public class GeometryJson implements Serializable {

    private LocationJson location;
    private ViewportJson viewport;

    public LocationJson getLocation() {
        return location;
    }

    public void setLocation(LocationJson location) {
        this.location = location;
    }

    public ViewportJson getViewport() {
        return viewport;
    }

    public void setViewport(ViewportJson viewport) {
        this.viewport = viewport;
    }
}
