package com.vsevolod.carbontracker.Model;

/**
 *  All related information about a car.
 *  Characteristic: name, make, model, transmission,
 *  engine displacement, iconID, year, cityMPG and highwayMPG.
 */

public class Car extends TransportationImpl{
    private String name;
    private String make;
    private String model;

    private String transmission;
    private double engineDisplacement;
    private int year;
    private int iconID;
    private int cityMPG;
    private int highwayMPG;


    private String fuelType;
    private boolean visible;

    int makeIndex = 0;
    int modelIndex = 0;
    int yearIndex = 0;
    int transmissionIndex = 0;
    int engineDisplacementIndex = 0;
    int iconIndex = 0;

    public Car(){
    }

    public Car(String name, boolean visible) {
        super(name, visible);
    }

    public Car(String name, String make, String model, String transmission, double engineDisplacement,
               int year, int cityMPG, int highwayMPG, String fuelType, boolean visible,
               int makeIndex, int modelIndex, int yearIndex, int transmissionIndex, int engineDisplacementIndex,
               int iconID, int iconIndex) {
        super(name,visible);
        this.name = name;
        this.make = make;
        this.model = model;
        this.transmission = transmission;
        this.engineDisplacement = engineDisplacement;
        this.year = year;
        this.cityMPG = cityMPG;
        this.highwayMPG = highwayMPG;
        this.fuelType = fuelType;
        this.visible = visible;
        this.makeIndex = makeIndex;
        this.modelIndex = modelIndex;
        this.yearIndex = yearIndex;
        this.transmissionIndex = transmissionIndex;
        this.engineDisplacementIndex = engineDisplacementIndex;
        this.iconID = iconID;
        this.iconIndex = iconIndex;
    }

    public Car(String name, String make, String model, String transmission, double engineDisplacement,
               int year, int cityMPG, int highwayMPG, String fuelType, boolean visible) {
        super(name, visible);
        this.name = name;
        this.make = make;
        this.model = model;
        this.transmission = transmission;
        this.engineDisplacement = engineDisplacement;
        this.year = year;
        this.iconID = iconID;
        this.cityMPG = cityMPG;
        this.highwayMPG = highwayMPG;
        this.fuelType = fuelType;
        this.visible = visible;
    }

    public int getIconID(){
        return iconID;
    }
    public int getCityMPG() { return cityMPG; }

    public int getHighwayMPG() { return highwayMPG; }

    public String getName(){
        return name;
    }


    public String getMake(){
        return make;
    }

    public String getModel(){
        return model;
    }

    public int getYear(){ return year; }

    public String getTransmission() {
        return transmission;
    }

    public double getEngineDisplacement() {
        return engineDisplacement;
    }

    public void setCityMPG(int cityMPG) {
        if(cityMPG >= 0){
            this.cityMPG = cityMPG;
        }else{
            throw new IllegalArgumentException("city MPG is negative value. Invalid.");
        }

    }
    public void setIconID(int iconID){
        this.iconID = iconID;
    }

    public void setHighwayMPG(int highwayMPG) {
        if(highwayMPG >= 0 ){
            this.highwayMPG = highwayMPG;
        }else{
            throw new IllegalArgumentException("highway MPG is a negative value. Invalid.");
        }

    }
    public void setYear(int year) {
        if(year <=0){
            throw new IllegalArgumentException("Car's year must be greater than 0.");
        }
        this.year = year;
    }
    public void setName(String name){
        if (name == null) {
            throw new IllegalArgumentException("Car's name must not be null.");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Car's name must not be empty.");
        }
        this.name = name;
    }
    public void setMake(String make){
        if (make == null) {
            throw new IllegalArgumentException("Car's make must not be null.");
        }
        if (make.length() == 0) {
            throw new IllegalArgumentException("Car's make must not be empty.");
        }
        this.make = make;
        }
    public void setModel(String model){
        if (model == null) {
            throw new IllegalArgumentException("Car's model must not be null.");
        }
        if (model.length() == 0) {
            throw new IllegalArgumentException("Car's model must not be empty.");
        }
        this.model = model;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }



    public void setTransmission(String transmission) {
        if (transmission == null) {
            throw new IllegalArgumentException("Car's make must not be null.");
        }
        if (transmission.length() == 0) {
            throw new IllegalArgumentException("Car's make must not be empty.");
        }
        this.transmission = transmission;
    }

    public void setEngineDisplacement(double engineDisplacement) {
        if(engineDisplacement < 0) {
            throw new IllegalArgumentException("engineDisplacement must be >= 0");
        }
        this.engineDisplacement = engineDisplacement;
    }

    public int getMakeIndex() {
        return makeIndex;
    }

    public int getModelIndex() {
        return modelIndex;
    }

    public int getYearIndex() {
        return yearIndex;
    }

    public int getTransmissionIndex() {
        return transmissionIndex;
    }

    public int getEngineDisplacementIndex() {
        return engineDisplacementIndex;
    }

    public int getIconIndex(){
        return iconIndex;
    }
}
