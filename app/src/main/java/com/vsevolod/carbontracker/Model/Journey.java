package com.vsevolod.carbontracker.Model;

import static java.lang.StrictMath.abs;

import java.util.Date;

/**
 * Journey class
 */

public class  Journey {
    //Transportation Mode and Select Route and Journey name
    private final double AVERAGEDISTANCESTOPS = 0.4;
    private final double AVERAGEDISTANCESTATION = 1.0;
    private final double KG_CO2_GALLON = 10.15;
    private final float KG_PER_TREE_EMISSION = (float) 5.023;
    private Car car;
    private Bus bus;
    private float distance;
    private Skytrain skytrain;
    private Bike bike;
    private Route route;
    private String name;
    private Date date;
    private float CO2Emission = 0;

    public boolean isHumanMode() {
        return humanMode;
    }

    public void setHumanMode(boolean humanMode) {
        countEmission();
    }

    private boolean humanMode = false;

    //Constructor
    public Journey(){}

    public Journey(String name, Car car, Route route){
        this.name = name;
        this.car = car;
        this.route = route;
        countEmission();
    }

    public String getTransType(){
        String type = null;
        if(car != null){
            type = "Car";
        }else if(bus != null){
            type = "Bus";
        }else if(skytrain != null){
            type = "Skytrain";
        }else{
            type = "Bike";
        }
        return type;
    }

    public Journey(String name, Car car, Route route, Date date){
        this.name = name;
        this.car = car;
        this.route = route;
        this.date = date;
        countEmission();
    }

    public Journey(String name, Bus bus,  Date date){
        this.name = name;
        this.bus = bus;
        this.date = date;
        countEmission();
    }

    public Journey(String name, Skytrain skytain,  Date date){
        this.name = name;
        this.skytrain = skytain;
        this.date = date;
        countEmission();
    }

    public Journey(String name, Bike bike,  Date date, float distance){
        this.name = name;
        this.bike = bike;
        this.date = date;
        this.distance = distance;
        countEmission();
    }


    private void countEmission() {
        if(getTransType().equals("Car")){
            if(car.getFuelType().equals("Electricity")) {
                CO2Emission = 0;
            }else{
                int cityMpg = car.getCityMPG();
                int highwayMpg = car.getHighwayMPG();

                double cityLength = route.getCityRouteSegment();
                double highwayLength = route.getHighwayRouteSegment();
                System.out.println("City Length : " + route.getCityRouteSegment());
                System.out.println("Highway Length : " + route.getHighwayRouteSegment());

                CO2Emission =  (int) ((cityLength / cityMpg +  highwayLength / highwayMpg) * KG_CO2_GALLON);
            }
        }if(getTransType().equals("Bus")){
            distance = (float) (abs(bus.getDestinationStopIndex() - bus.getStartingStopIndex() *
                                AVERAGEDISTANCESTOPS));
            System.out.println(bus.getStartingStopIndex());
            System.out.println(bus.getDestinationStopIndex());
            System.out.println(distance);
            CO2Emission = (float) (bus.getEmission() * distance );
        }if(getTransType().equals("Skytrain")){
            distance = (float)(abs(skytrain.getDestinationStationIndex() - skytrain.getStartingStationIndex()) *
                                AVERAGEDISTANCESTATION);

            System.out.println("when counting " + skytrain.getDestinationStationIndex());
            System.out.println("when counting " + skytrain.getStartingStationIndex());



            CO2Emission = (float) (( skytrain.getEmission() * distance));
        }else{

        }

        if(humanMode) {
            CO2Emission = CO2Emission / 5;
            System.out.println(CO2Emission + " THIS IS MY EMISSION");
        }

    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Date getDate(){
        return date; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Journey's name must not be null.");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Journey's name must not be empty.");
        }
        this.name = name;
    }

    public float getCO2Emission() {
        countEmission();
        return (float)CO2Emission;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Skytrain getSkytrain() {
        return skytrain;
    }

    public void setSkytrain(Skytrain skytrain) {
        this.skytrain = skytrain;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getDistance() {
        return distance;
    }
}
