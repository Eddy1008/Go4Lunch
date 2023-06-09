package fr.zante.go4lunch.model;

import java.io.Serializable;

public class ViewportJson implements Serializable {

    private NortheastJson northeast;
    private SouthwestJson southwest;

    public NortheastJson getNortheast() {
        return northeast;
    }

    public void setNortheast(NortheastJson northeast) {
        this.northeast = northeast;
    }

    public SouthwestJson getSouthwest() {
        return southwest;
    }

    public void setSouthwest(SouthwestJson southwest) {
        this.southwest = southwest;
    }
}
