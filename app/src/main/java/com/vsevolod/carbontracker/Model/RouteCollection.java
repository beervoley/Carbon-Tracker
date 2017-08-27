package com.vsevolod.carbontracker.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Route collection class to store collection of routes.
 */

public class RouteCollection {
    private List<Route> routeList = new ArrayList<>();

    public void addNewRoute(Route route) {
        routeList.add(getDescriptionSize(), route);
    }

    public void hideRoute(int index) {
        validateIndexWithException(index);
        getRoute(index).setVisible(false);
        //move route to end of routeList to avoid index conflicts in AddRouteActivity
        routeList.add(getRoute(index));
        removeRoute(index);
    }

    public void displayRoute(int index) {
        validateIndexWithException(index);
        getRoute(index).setVisible(true);
    }

    public void removeRoute(int index) {
        validateIndexWithException(index);
        routeList.remove(index);
    }

    public void changeRoute(Route route, int indexOfEditingRoute) {
        validateIndexWithException(indexOfEditingRoute);
        routeList.remove(indexOfEditingRoute);
        routeList.add(indexOfEditingRoute, route);
    }

    public int countRoute() {
        return routeList.size();
    }

    public double countCitySegment(Route route) {
        return route.getCityRouteSegment();
    }

    public double countHighwaySegment(Route route) {
        return route.getHighwayRouteSegment();
    }

    public Route getRoute(int index) {
        validateIndexWithException(index);
        return (Route) routeList.get(index);
    }

    public List<String> getRouteDescription() {
        List<String> descriptions = new ArrayList<>();
        for (int i = 0; i < countRoute(); i++) {
            Route route = getRoute(i);
            if (route.isVisible()) {
                descriptions.add(route.getName() + " - " + route.getLength() + "km\n"
                );
            }
        }
        return descriptions;
    }

    public int getDescriptionSize() {
        return getRouteDescription().size();
    }

    private void validateIndexWithException(int index) {
        if (index < 0 || index >= countRoute()) {
            throw new IllegalArgumentException();
        }
    }


}
