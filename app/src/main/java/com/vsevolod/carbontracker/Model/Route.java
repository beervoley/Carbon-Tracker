package com.vsevolod.carbontracker.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Route class.
 */

public class Route {
    private String name;
    private boolean visible;
    private double cityRouteSegment;
    private double highwayRouteSegment;


    // Constructor
    public Route() {
    }

    public Route(String name, boolean visible) {
        this.name = name;
        this.visible = visible;
    }

    public Route(String name, double cityRouteSegment, double highwayRouteSegment, boolean visible) {
        this.name = name;
        this.cityRouteSegment = cityRouteSegment;
        this.highwayRouteSegment = highwayRouteSegment;
        this.visible = visible;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public double getLength() {
        return cityRouteSegment + highwayRouteSegment;
    }

    public double getCityRouteSegment() {
        return cityRouteSegment;
    }

    public void setCityRouteSegment(double segment) {
        cityRouteSegment = segment;
    }

    public double getHighwayRouteSegment() {
        return highwayRouteSegment;
    }

    public void setHighwayRouteSegment(double segment) {
        highwayRouteSegment = segment;
    }


    public void setName(String name) {
        if (name.length() != 0 && name != null) {
            this.name = name;
        } else {
            throw new IllegalArgumentException(
                    "The name of the route is invalid.");
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
