package com.vsevolod.carbontracker.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class SkytrainCollection {
    private List<Skytrain> skytrains = new ArrayList<>();


    public void addSkytrain(Skytrain skytrain) {
        boolean ifDuplicate = false;
        for(Skytrain currentSkytrain : skytrains) {
            if (skytrain.getName().equals(currentSkytrain.getName()) &&
                    skytrain.getStartingStation().equals(currentSkytrain.getStartingStation())&&
                    skytrain.getDestinationStation().equals(currentSkytrain.getDestinationStation())) {
                ifDuplicate = true;
                break;
            }
        }
        if(!ifDuplicate) {
            skytrains.add(getSkytrainDescriptions().length,skytrain);
        }
    }

    public void hideSkytrain(int index) {
        validateIndexWithException(index);
        Skytrain skytrain = getSkytrain(index);
        skytrain.setVisibility(false);
        deleteSkytrain(index);
        addSkytrain(skytrain);
    }

    private void deleteSkytrain(int index){
        skytrains.remove(index);
    }

    public void changeSkytrain(Skytrain skytrain, int index){
        validateIndexWithException(index);
        skytrains.remove(index);
        skytrains.add(index, skytrain);
    }


    public int countSkytrains(){
        int count = 0;
        for(Skytrain skytrain: skytrains){
            if(skytrain.getVisibility()){
                count++;
            }
        }
        return count;
    }

    public Skytrain getSkytrain(int index){
        validateIndexWithException(index);
        return skytrains.get(index);
    }

    public String[] getSkytrainDescriptions(){
        String[] descriptions = new String [countSkytrains()];
        for(int i = 0; i < countSkytrains(); i ++) {
            Skytrain skytrain = getSkytrain(i);
            if (skytrain.getVisibility()) {
                descriptions[i] = "Name: " + skytrain.getName() + "\n" +
                        "Starting Station: " + skytrain.getStartingStation() + "\n" +
                        "Destination Station: " + skytrain.getDestinationStation();
            }
        }
        return descriptions;
    }

    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countSkytrains()){
            throw new IllegalArgumentException();
        }
    }

}
