package com.vsevolod.carbontracker.Model;

import android.content.Context;

import com.vsevolod.carbontracker.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JourneyCollection class to store collection of journeys.
 */

public class JourneyCollection {

    private List<Journey> journeyList = new ArrayList<>();
    private List<String> journeyType = new ArrayList<>();
//    private Singleton singleton = Singleton.getInstance();
    private Journey journey = new Journey();

    private Journey lastJourney;
    private String lastJourneyType;
    private float c02ForLast365DaysCars;
    private float c02ForLast365DaysSkytrains;
    private float c02ForLast365DaysBuses;

    public void setHumanMode(boolean humanMode) {
        for(Journey journey: journeyList) {
            journey.setHumanMode(humanMode);
        }
    }

    public List<Journey> getJourneyList() {
        return journeyList;
    }


    public void addJourney(Journey journey) {
        lastJourney = journey;
        journeyType.add(journey.getTransType());
        journeyList.add(journey);
    }

    public int countJourney(){
        return journeyList.size();
    }

    public Journey getJourney(int index){
        validateIndexWithException(index);
        return journeyList.get(index);
    }

    public List<String> getJourneyType(){
        return journeyType;
    }

    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countJourney()){
            throw new IllegalArgumentException();
        }
    }
    public void removeRoute(int index){
        validateIndexWithException(index);
        journeyList.remove(index);
        journeyType.remove(index);
    }

    public List<Journey> getJourneysForADay(Date date) {
        List<Journey> myList = new ArrayList<>();

        for (Journey jour: journeyList) {
            Date currentDate = jour.getDate();
            if(currentDate.getMonth() == date.getMonth() &&
                    currentDate.getDate() == date.getDate() &&
                    currentDate.getYear() == date.getYear()) {
                myList.add(jour);
            }
        }
        return myList;
    }




    public void changeJourney(Journey journey, int index){
        validateIndexWithException(index);
        journeyList.remove(index);
        journeyType.remove(index);
        journeyList.add(index, journey);
        journeyType.add(index, journey.getTransType());
    }


    public List<String> getJourneyDescription(

    ){
        List<String> descriptions = new ArrayList<>();
        for(int i =0; i < countJourney();i++){
            Journey journey = getJourney(i);
            String type = journey.getTransType();
            if(type.equals("Car")) {
                Car car = journey.getCar();
                Route route = journey.getRoute();
                descriptions.add(journey.getDate() + "\n"+ car.getName() + " - " + route.getName());
            } else if(type.equals("Skytrain")) {
                Skytrain skytrain = journey.getSkytrain();
                descriptions.add(journey.getDate() + "\n"+skytrain.getStartingStation() + " - "
                        + skytrain.getDestinationStation());
            } else if(type.equals("Bus")) {
                Bus bus = journey.getBus();
                descriptions.add(journey.getDate() +"\n" +bus.getStartingStop() + " - "
                        + bus.getDestinationStop());
            } else if(type.equals("Bike")) {
                Bike bike = journey.getBike();
                descriptions.add(journey.getDate() + "\n"+ bike.getName() + " - "
                        + journey.getDistance());
            }
        }
        return descriptions;
    }

    public Journey getLastJourney() {
        return lastJourney;
    }

    public void setLastJourney(Journey lastJourney) {
        this.lastJourney = lastJourney;
    }

    public float getC02ForLast28Days() {
        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 28) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;
    }

    public void removeJourney(int position) {
        validateIndexWithException(position);
        journeyList.remove(position);
    }

    public Journey getJourneyForBus(Bus bus) {
        for(Journey journey: journeyList) {
            if (journey.getBus().equals(bus)) {
                System.out.println("works");
                return journey;
            }
        }
        return null;
    }

    public Journey getJourneyForSkyTrain(Skytrain skytrain) {
        for(Journey journey: journeyList) {
            if (journey.getSkytrain().equals(skytrain)) {
                System.out.println("works");
                return journey;
            }
        }
        return null;
    }

    public int getIndexForBus(Bus bus) {

        for(int i = 0; i < countJourney(); i++) {
            if (journeyList.get(i).getBus().equals(bus)) {
                System.out.println("works");
                return i;
            }
        }
        return -500;


    }

    public float getC02ForLast28DaysCars() {
        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 28 &&
                    journey.getTransType().equals("Car")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;


    }

    public float getC02ForLast28DaysSkytrains() {

        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 28 &&
                    journey.getTransType().equals("Skytrain")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;
    }

    public float getC02ForLast28DaysBuses() {

        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 28 &&
                    journey.getTransType().equals("Bus")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;
    }


    public float getC02ForLast365DaysElectricity() {
        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Electricity")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;

    }

    public float getC02ForLast365DaysGas() {
        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Gas")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;

    }


    public float getC02ForLast365DaysCars() {
        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Car")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;
    }

    public float getC02ForLast365DaysSkytrains() {
        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Skytrain")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;
    }

    public float getC02ForLast365DaysBuses() {
        float toReturn = 0;
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Bus")) {
                toReturn += journey.getCO2Emission();
            }
        }

        return toReturn;
    }

    public List<Journey> getCarJourneys() {
        List<Journey> carJourneys = new ArrayList<>();

        for (Journey currentJourney: journeyList) {
            if (currentJourney.getTransType().equals("Car")) {
                carJourneys.add(currentJourney);
            }
        }
        return carJourneys;
    }

    public List<Journey> getSkyTrainJourneys() {
        List<Journey> skyTrainJourneys = new ArrayList<>();

        for (Journey currentJourney: journeyList) {
            if (currentJourney.getTransType().equals("Skytrain")) {
                skyTrainJourneys.add(currentJourney);
            }
        }
        return skyTrainJourneys;
    }
    public List<Journey> getBusJourneys() {
        List<Journey> busJourneys = new ArrayList<>();

        for (Journey currentJourney: journeyList) {
            if (currentJourney.getTransType().equals("Bus")) {
                busJourneys.add(currentJourney);
            }
        }
        return busJourneys;
    }
    public List<Journey> getBikeJourneys() {
        List<Journey> bikeJourneys = new ArrayList<>();

        for (Journey currentJourney: journeyList) {
            if (currentJourney.getTransType().equals("Bike")) {
                bikeJourneys.add(currentJourney);
            }
        }
        return bikeJourneys;
    }


    public List<Journey> getCarJourneysForLast365Days() {
        List<Journey> toReturn = new ArrayList<>();
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Car")) {
                toReturn.add(journey);
            }
        }

        return toReturn;

    }
    public List<Journey> getBusJourneysForLast365Days() {
        List<Journey> toReturn = new ArrayList<>();
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Bus")) {
                toReturn.add(journey);
            }
        }

        return toReturn;

    }
    public List<Journey> getSkyTrainJourneysForLast365Days() {
        List<Journey> toReturn = new ArrayList<>();
        Date toDay = new Date();


        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 365 &&
                    journey.getTransType().equals("Skytrain")) {
                toReturn.add(journey);
            }
        }

        return toReturn;

    }
    public List<Journey> getCarJourneysForLast28Days() {
        List<Journey> toReturn = new ArrayList<>();
        Date toDay = new Date();
        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 28 &&
                    journey.getTransType().equals("Car")) {
                toReturn.add(journey);
            }
        }

        return toReturn;
    }
    public List<Journey> getCarJourneysFor1Day(Date date) {
        List<Journey> carsFor1Day = new ArrayList<>();
        for(Journey journey: journeyList) {
            if(journey.getTransType().equals("Car") && journey.getDate().getMonth() == date.getMonth()
                    && journey.getDate().getDate() == date.getDate()) {
                carsFor1Day.add(journey);
            }
        }
        return carsFor1Day;
    }
    public List<Journey> getBusJourneysFor1Day(Date date) {
        List<Journey> busFor1Day = new ArrayList<>();
        for(Journey journey: journeyList) {
            if(journey.getTransType().equals("Bus") && journey.getDate().getMonth() == date.getMonth()
                    && journey.getDate().getDate() == date.getDate()) {
                busFor1Day.add(journey);
            }
        }
        return busFor1Day;
    }

    public List<Journey> getSkyTrainJourneysFor1Day(Date date) {
        List<Journey> skyTrainFor1Day = new ArrayList<>();
        for(Journey journey: journeyList) {
            if(journey.getTransType().equals("Skytrain") && journey.getDate().getMonth() == date.getMonth()
                    && journey.getDate().getDate() == date.getDate()) {
                skyTrainFor1Day.add(journey);
            }
        }
        return skyTrainFor1Day;
    }


    public List<Journey> getBusJourneysForLast28Days() {
        List<com.vsevolod.carbontracker.Model.Journey> toReturn = new ArrayList<>();
        Date toDay = new Date();


        for(com.vsevolod.carbontracker.Model.Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 28 &&
                    journey.getTransType().equals("Bus")) {
                toReturn.add(journey);
            }
        }

        return toReturn;

    }
    public List<Journey> getSkyTrainJourneysForLast28Days() {
        List<Journey> toReturn = new ArrayList<>();
        Date toDay = new Date();
        for(Journey journey: journeyList) {

            if(journey.getDate().before(toDay) && Bill.getDatesDifference(journey.getDate(), toDay) < 28 &&
                    journey.getTransType().equals("Skytrain")) {
                toReturn.add(journey);
            }
        }

        return toReturn;
    }



    public List<Journey> getJourneysForCars28Days() {

        List<Journey> carJourneys = getCarJourneysForLast28Days();
        List<Journey> toReturn = new ArrayList<>();
        Car currentCar;
        Set<String> set = new HashSet<>();
        if(journeyList.isEmpty()) {
            return toReturn;
        } else {
            for(Journey journey: carJourneys) {
                currentCar = journey.getCar();
                if(!set.contains(currentCar.getName())) {
                    set.add(currentCar.getName());
                    Journey toAdd = new Journey("whatever", currentCar, new Route("whatever", 0.0, 0.0, true));
                    toReturn.add(toAdd);
                    for(Journey currentJourney: carJourneys) {
                        if (currentJourney.getCar().getName().equals(currentCar.getName())) {
                            System.out.println("EQUALS");
                            Route route = new Route(toAdd.getRoute().getName(), true);
                            route.setCityRouteSegment(toAdd.getRoute().getCityRouteSegment() + currentJourney.getRoute().getCityRouteSegment());
                            route.setHighwayRouteSegment(toAdd.getRoute().getHighwayRouteSegment() + currentJourney.getRoute().getHighwayRouteSegment());
                            toAdd.setRoute(route);
                        }
                    }
                } else {
                    continue;
                }
            }
        }



    return toReturn;

    }

    public List<Journey> getJourneysForCars365Days() {

        List<Journey> carJourneys = getCarJourneysForLast365Days();
        List<Journey> toReturn = new ArrayList<>();
        Car currentCar;
        Set<String> set = new HashSet<>();
        if(journeyList.isEmpty()) {
            return toReturn;
        } else {
            for(Journey journey: carJourneys) {
                currentCar = journey.getCar();
                if(!set.contains(currentCar.getName())) {
                    set.add(currentCar.getName());
                    Journey toAdd = new Journey("whatever", currentCar, new Route("whatever", 0.0, 0.0, true));
                    toReturn.add(toAdd);
                    for(Journey currentJourney: carJourneys) {
                        if (currentJourney.getCar().getName().equals(currentCar.getName())) {
                            Route route = new Route();
                            route.setCityRouteSegment(toAdd.getRoute().getCityRouteSegment() + currentJourney.getRoute().getCityRouteSegment());
                            route.setHighwayRouteSegment(toAdd.getRoute().getHighwayRouteSegment() + currentJourney.getRoute().getHighwayRouteSegment());
                            toAdd.setRoute(route);
                        }
                    }
                } else {
                    continue;
                }
            }
        }



        return toReturn;

    }

    public List<Journey> getJourneysForCars1Day(Date pickedDate) {
        List<Journey> carJourneys = getCarJourneysFor1Day(pickedDate);
        List<Journey> toReturn = new ArrayList<>();
        Car currentCar;
        Set<String> set = new HashSet<>();
        if(journeyList.isEmpty()) {
            return toReturn;
        } else {
            for(Journey journey: carJourneys) {
                currentCar = journey.getCar();
                if(!set.contains(currentCar.getName())) {
                    set.add(currentCar.getName());
                    Journey toAdd = new Journey("whatever", currentCar, new Route("whatever", 0.0, 0.0, true));
                    toReturn.add(toAdd);
                    for(Journey currentJourney: carJourneys) {
                        if (currentJourney.getCar().getName().equals(currentCar.getName())) {
                            Route route = new Route();
                            route.setCityRouteSegment(toAdd.getRoute().getCityRouteSegment() + currentJourney.getRoute().getCityRouteSegment());
                            route.setHighwayRouteSegment(toAdd.getRoute().getHighwayRouteSegment() + currentJourney.getRoute().getHighwayRouteSegment());
                            toAdd.setRoute(route);
                        }
                    }
                } else {
                    continue;
                }
            }
        }



        return toReturn;


    }


    public List<Journey> getJourneysForCars28DaysRoutes() {

        List<Journey> carJourneys = getCarJourneysForLast28Days();
        List<Journey> toReturn = new ArrayList<>();
        Route currentRoute;
        Car currentCar;
        Set<String> set = new HashSet<>();
        if(journeyList.isEmpty()) {
            return toReturn;
        } else {
            for(Journey journey: carJourneys) {
                currentRoute = journey.getRoute();
                currentCar = journey.getCar();
                if(!set.contains(currentRoute.getName())) {
                    set.add(currentRoute.getName());
                    Journey toAdd = new Journey("whatever", currentCar, currentRoute);
                    toReturn.add(toAdd);
                    for(Journey currentJourney: carJourneys) {
                        if (currentJourney.getRoute().getName().equals(currentRoute.getName())) {
                            System.out.println("EQUALS");
                            Route route = new Route(toAdd.getRoute().getName(), true);
                            route.setCityRouteSegment(toAdd.getRoute().getCityRouteSegment() + currentJourney.getRoute().getCityRouteSegment());
                            route.setHighwayRouteSegment(toAdd.getRoute().getHighwayRouteSegment() + currentJourney.getRoute().getHighwayRouteSegment());
                            toAdd.setRoute(route);
                        }
                    }
                } else {
                    continue;
                }
            }
        }



        return toReturn;

    }


    public List<Journey> getJourneysForCars1DayRoutes(Date pickedDate) {

        List<Journey> carJourneys = getCarJourneysFor1Day(pickedDate);
        List<Journey> toReturn = new ArrayList<>();
        Route currentRoute;
        Car currentCar;
        Set<String> set = new HashSet<>();
        if(journeyList.isEmpty()) {
            return toReturn;
        } else {
            for(Journey journey: carJourneys) {
                currentRoute = journey.getRoute();
                currentCar = journey.getCar();
                if(!set.contains(currentRoute.getName())) {
                    set.add(currentRoute.getName());
                    Journey toAdd = new Journey("whatever", currentCar, currentRoute);
                    toReturn.add(toAdd);
                    for(Journey currentJourney: carJourneys) {
                        if (currentJourney.getRoute().getName().equals(currentRoute.getName())) {
                            System.out.println("EQUALS");
                            Route route = new Route(toAdd.getRoute().getName(), true);
                            route.setCityRouteSegment(toAdd.getRoute().getCityRouteSegment() + currentJourney.getRoute().getCityRouteSegment());
                            route.setHighwayRouteSegment(toAdd.getRoute().getHighwayRouteSegment() + currentJourney.getRoute().getHighwayRouteSegment());
                            toAdd.setRoute(route);
                        }
                    }
                } else {
                    continue;
                }
            }
        }



        return toReturn;

    }

    public List<Journey> getJourneysForCars365DaysRoutes() {

        List<Journey> carJourneys = getCarJourneysForLast365Days();
        List<Journey> toReturn = new ArrayList<>();
        Route currentRoute;
        Car currentCar;
        Set<String> set = new HashSet<>();
        if(journeyList.isEmpty()) {
            return toReturn;
        } else {
            for(Journey journey: carJourneys) {
                currentRoute = journey.getRoute();
                currentCar = journey.getCar();
                if(!set.contains(currentRoute.getName())) {
                    set.add(currentRoute.getName());
                    Journey toAdd = new Journey("whatever", currentCar, currentRoute);
                    toReturn.add(toAdd);
                    for(Journey currentJourney: carJourneys) {
                        if (currentJourney.getRoute().getName().equals(currentRoute.getName())) {
                            System.out.println("EQUALS");
                            Route route = new Route(toAdd.getRoute().getName(), true);
                            route.setCityRouteSegment(toAdd.getRoute().getCityRouteSegment() + currentJourney.getRoute().getCityRouteSegment());
                            route.setHighwayRouteSegment(toAdd.getRoute().getHighwayRouteSegment() + currentJourney.getRoute().getHighwayRouteSegment());
                            toAdd.setRoute(route);
                        }
                    }
                } else {
                    continue;
                }
            }
        }



        return toReturn;

    }












    public List<Journey> getCarJourneysFor1Day() {
        List<Journey> carJourneysFor1Day = new ArrayList<>();




        return carJourneysFor1Day;
    }
}
