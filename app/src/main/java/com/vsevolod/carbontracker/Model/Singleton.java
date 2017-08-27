package com.vsevolod.carbontracker.Model;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Facade class to store references to current objects.
 */


public class Singleton {

    private static Singleton instance = null;

    private BillCollection billCollection;
    private BusCollection busesCollection;
    private BikeCollection bikesCollection;
    private SkytrainCollection skytrainsCollection;
    private CarsCollection carsCollection;
    private RouteCollection routeCollection;
    private JourneyCollection journeyCollection;
    private Tips tipMaker;
    private Notifications notifications;
    private boolean humanRelatedUnit = false;

    public void setCurrentMode(String currentMode) {
        this.currentMode = currentMode;
    }

    private static String currentMode;

    public BikeCollection getBikesCollection() {
        return bikesCollection;
    }

    public BusCollection getBusesCollection() {
        return busesCollection;
    }

    public SkytrainCollection getSkytrainsCollection() {
        return skytrainsCollection;
    }

    public String getCurrentMode() {
        return currentMode;
    }


    private Singleton(){
        busesCollection = new BusCollection();
        carsCollection = new CarsCollection();
        bikesCollection = new BikeCollection();
        skytrainsCollection = new SkytrainCollection();
        routeCollection = new RouteCollection();
        journeyCollection = new JourneyCollection();
        billCollection = new BillCollection();
        currentMode = "mode";
        tipMaker = null;
    }
    public static Singleton getInstance() {
        if(instance == null ) {
            instance = new Singleton();
        }
        return instance;
    }


    public CarsCollection getCarsCollection(){
        return carsCollection;
    }

    public RouteCollection getRouteCollection(){
        return routeCollection;
    }

    public JourneyCollection getJourneyCollection(){
        return journeyCollection;
    }
    public BillCollection getBillCollection() {return billCollection;}

    public void setCarsCollection(CarsCollection carsCollection) {
        this.carsCollection = carsCollection;
    }

    public void setBusesCollection(BusCollection busesCollection) {
        this.busesCollection = busesCollection;
    }


    public void setBikesCollection(BikeCollection bikesCollection) {
        this.bikesCollection = bikesCollection;
    }

    public void setSkytrainsCollection(SkytrainCollection skytrainsCollection) {
        this.skytrainsCollection = skytrainsCollection;
    }


    public void setRouteCollection(RouteCollection routeCollection) {
        this.routeCollection = routeCollection;
    }

    public void setJourneyCollection(JourneyCollection journeyCollection) {
        this.journeyCollection = journeyCollection;
    }

    public void setBillCollection(BillCollection billCollection) {
        this.billCollection = billCollection;
    }

    public void addBus(Bus bus){ busesCollection.addBus(bus); }

    public void addBike(Bike bike){ bikesCollection.addBike(bike); }

    public void addSkytrain(Skytrain skytrain){ skytrainsCollection.addSkytrain(skytrain);}

    public void addRoute(Route route) {
        routeCollection.addNewRoute(route);
    }

    public void addCar(Car car) {
        carsCollection.addCar(car);
    }

    public void addJourney(Journey journey){ journeyCollection.addJourney(journey);}

    public void addBill(Bill bill) {billCollection.addBill(bill);}

    public void setLastJourney(Journey journey) {journeyCollection.setLastJourney(journey);}

    public Tips getTipMaker() {return tipMaker;}

    public Notifications getNotifications() {return notifications;}


    public void setTips(Context context) {
        tipMaker = new Tips(context);
    }

    public void setNotifications(Context context) {
        notifications = new Notifications(context);
    }

    public static void setInstance(Singleton singleton) {
        instance = singleton;
    }

    public void setHumanRelatedUnit(boolean humanRelatedUnit) {
        this.humanRelatedUnit = humanRelatedUnit;
        journeyCollection.setHumanMode(humanRelatedUnit);
        billCollection.setHumanMode(humanRelatedUnit);
    }

    public boolean getHumanRelatedUnit() {
        return humanRelatedUnit;
    }
}
