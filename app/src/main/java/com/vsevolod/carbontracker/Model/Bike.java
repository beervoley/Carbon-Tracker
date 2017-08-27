package com.vsevolod.carbontracker.Model;

/**
 *
 */

public class Bike extends TransportationImpl{

    private final double bike_emission_gPerKM = 0;

    public Bike() { }

    public Bike(String name, boolean visible) {
        super(name, visible);
    }


    public double getEmission() {
        return bike_emission_gPerKM;
    }


}
