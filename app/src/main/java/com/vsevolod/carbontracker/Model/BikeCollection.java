package com.vsevolod.carbontracker.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class BikeCollection {
    private List<Bike> bikes = new ArrayList<>();


    public void addBike(Bike bike) {
        boolean ifDuplicate = false;
        for(Bike currentBike : bikes) {
            if (bike.getName().equals(currentBike.getName())) {
                ifDuplicate = true;
                break;
            }
        }
        if(!ifDuplicate) {
            bikes.add(getBikeDescriptions().length,bike);
        }
    }


    public void hideBike(int index) {
        validateIndexWithException(index);
        Bike bike = getBike(index);
        bike.setVisibility(false);
        deleteBike(index);
        addBike(bike);
        //Log.i("app","" + car.isVisible());
    }

    private void deleteBike(int index){
        bikes.remove(index);
    }

    public void changeBike(Bike bike, int indexOfBikeEditing){
        validateIndexWithException(indexOfBikeEditing);
        bikes.remove(indexOfBikeEditing);
        bikes.add(indexOfBikeEditing, bike);
    }


    public int countBikes(){
        int count = 0;
        for(int i = 0; i < bikes.size(); i ++){
           Bike currentBike = bikes.get(i);
            if(currentBike.getVisibility()){
                count++;
            }
        }
        return count;
    }

    public Bike getBike(int index){
        validateIndexWithException(index);
        return bikes.get(index);
    }

    public String[] getBikeDescriptions(){
        String[] descriptions = new String [countBikes()];
        for(int i = 0; i < countBikes(); i ++) {
            Bike bike = getBike(i);
            if (bike.getVisibility()) {
                descriptions[i] = bike.getName();
            }
        }
        return descriptions;
    }

    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countBikes()){
            throw new IllegalArgumentException();
        }
    }

}
