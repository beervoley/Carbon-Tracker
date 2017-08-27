package com.vsevolod.carbontracker.Model;
/**
 *  A collection of cars
 *  Allow user to add, edit, and remove cars
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class CarsCollection {
    private List<Car> cars = new ArrayList<>();


    public void addCar(Car car) {
        boolean ifDuplicate = false;
        for(Car currentCar : cars) {
            if (car.getName().equals(currentCar.getName())) {
                ifDuplicate = true;
                break;
            }
        }
        if(!ifDuplicate) {
            cars.add(getCarDescriptions().length,car);
        }
    }


    public void hideCar(int index) {
        validateIndexWithException(index);
        Car car = getCar(index);
        car.setVisibility(false);
        deleteCar(index);
        addCar(car);
        //Log.i("app","" + car.isVisible());
    }

    public void deleteCar(int index)
    {
        validateIndexWithException(index);
        cars.remove(index);
    }

    public void changeCar(Car car, int indexOfCarEditing){
        validateIndexWithException(indexOfCarEditing);
        cars.remove(indexOfCarEditing);
        cars.add(indexOfCarEditing, car);
    }


    public int countCars(){
        int count = 0;
        for(int i = 0; i < cars.size(); i ++){
            Car currentCar = cars.get(i);
            if(currentCar.getVisibility()){
                count++;
            }
        }
        return count;
    }

    public Car getCar(int index){
        validateIndexWithException(index);
        return cars.get(index);
    }

    public String[] getCarDescriptions(){
        String[] descriptions = new String [countCars()];
        for(int i = 0; i < countCars(); i ++) {
            Car car = getCar(i);
            if (car.getVisibility()) {
                descriptions[i] = car.getName() + "-" + car.getMake() + "\n" + car.getModel() + ","
                        + car.getYear() + " " + car.getTransmission() + " " + car.getEngineDisplacement();
            }
        }
        return descriptions;
    }

    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countCars()){
            throw new IllegalArgumentException();
        }
    }

}
