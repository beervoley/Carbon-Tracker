package com.vsevolod.carbontracker.Model;

/**
 * Individual Skytrain Info.
 */

public class Skytrain extends TransportationImpl {
    private double skytrain_emission_GPerKM = 0.027;
    private String startingStation;
    private String destinationStation;
    private int startingStationIndex = 0;
    private int destinationStationIndex = 0;
    private int nameIndex = 0;


    public Skytrain(){}

    public Skytrain(String name, boolean visible) {
        super(name, visible);
    }

    public Skytrain(String name, String startingStation,
                    String destinationStation, int startingStationIndex,
                    int destinationStationIndex, int nameIndex, boolean visible) {

        super(name, visible);
        this.startingStation = startingStation;
        this.destinationStation = destinationStation;
        this.startingStationIndex = startingStationIndex;
        this.destinationStationIndex = destinationStationIndex;
        this.nameIndex = nameIndex;
    }

    public double getEmission() {
        return skytrain_emission_GPerKM;
    }

    public String getStartingStation() {
        return startingStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public int getStartingStationIndex() {
        return startingStationIndex;
    }

    public int getDestinationStationIndex() {
        return destinationStationIndex;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setStartingStationIndex(int startingStationIndex) {
        this.startingStationIndex = startingStationIndex;
    }

    public void setDestinationStationIndex(int destinationStationIndex) {
        this.destinationStationIndex = destinationStationIndex;
    }
}
