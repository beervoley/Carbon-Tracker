package com.vsevolod.carbontracker.Model;

/**
 *  Individual Bus Info.
 */

public class Bus extends TransportationImpl{
    private final double bus_emission_KgPerKM = 0.089;
    private String startingStop;
    private String destinationStop;
    private int nameIndex = 0;
    private int startingStopIndex = 0;
    private int destinationStopIndex = 0;

    public Bus(){ }

    public Bus(String name, boolean visible) {
        super(name, visible);
    }

    public Bus(String name, String startingStop, String destinationStop, boolean visible){
        super(name, visible);
        this.startingStop = startingStop;
        this.destinationStop = destinationStop;
    }


    public Bus(String name, String startingStop,
                    String destinationStop, int startingStopIndex,
                    int destinationStopIndex, int nameIndex, boolean visible) {

        super(name, visible);
        this.startingStop = startingStop;
        this.destinationStop = destinationStop;
        this.startingStopIndex = startingStopIndex;
        this.destinationStopIndex = destinationStopIndex;
        this.nameIndex = nameIndex;
    }

    public double getEmission() {
        return bus_emission_KgPerKM;
    }

    public void setStartingStopIndex(int startingStopIndex) {
        this.startingStopIndex = startingStopIndex;
    }

    public void setDestinationStopIndex(int destinationStopIndex) {
        this.destinationStopIndex = destinationStopIndex;
    }

    public String getStartingStop() {
        return startingStop;
    }

    public String getDestinationStop() {
        return destinationStop;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public int getStartingStopIndex() {
        return startingStopIndex;
    }

    public int getDestinationStopIndex() {
        return destinationStopIndex;
    }


}
