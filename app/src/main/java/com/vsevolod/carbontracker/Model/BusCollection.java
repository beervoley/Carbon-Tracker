package com.vsevolod.carbontracker.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of buses.
 */

public class BusCollection {

    private List<Bus> buses = new ArrayList<>();

    private Bus bus = new Bus();

    public void addBus(Bus bus) {
        boolean ifDuplicate = false;
        for(Bus currentBus : buses) {
            if (bus.getName().equals(currentBus.getName())&&
                    bus.getDestinationStop().equals(currentBus.getDestinationStop())&&
                    bus.getStartingStop().equals(currentBus.getStartingStop())) {
                ifDuplicate = true;
                break;
            }
        }
        if(!ifDuplicate) {
            buses.add(getBusDescriptions().length,bus);
        }
    }


    public void hideBus(int index) {
        validateIndexWithException(index);
        Bus bus = getBus(index);
        bus.setVisibility(false);
        deleteBus(index);
        addBus(bus);
        //Log.i("app","" + car.isVisible());
    }

    public void deleteBus(int index){
        buses.remove(index);
    }

    public void changeBus(Bus bus, int indexOfBusEditing){
        validateIndexWithException(indexOfBusEditing);
        buses.remove(indexOfBusEditing);
        buses.add(indexOfBusEditing, bus);
    }


    public int countBuses(){
        int count = 0;
        for(Bus currentBus : buses){
            if(currentBus.getVisibility()){
                count++;
            }
        }
        return count;
    }

    public Bus getBus(int index){
        validateIndexWithException(index);
        return buses.get(index);
    }

    public String[] getBusDescriptions(){
        String[] descriptions = new String [countBuses()];
        for(int i = 0; i < countBuses(); i ++) {
            Bus bus = getBus(i);
            if (bus.getVisibility()) {
                descriptions[i] ="Name: " + bus.getName() + "\n"
                        + "Starting stop: " + bus.getStartingStop() + "\n"
                        + "Destination stop: " + bus.getDestinationStop() + "\n";
            }
        }
        return descriptions;
    }

    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countBuses()){
            throw new IllegalArgumentException();
        }
    }


    public Bus getLastBus() {
        return bus;
    }

    public void setLastBus(Bus bus) {
        this.bus = bus;
    }

}
