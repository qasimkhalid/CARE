package model;

import helper.AutomatedOperations;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private String routeName;
    private List<String> route = new ArrayList<>();
    private int length;
    private long cost;

    public Route() {
    }

    public Route(String routeName, List<String> route) {
        this.routeName = routeName;
        this.route = route;
        this.length = route.size();

        for (int i = 0; i < route.size() - 1; i++) {
            try {
                setCost(getCost() + AutomatedOperations.getODPairCostInSeconds(route.get(i), route.get(i+1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public List<String> getRoute() {
        return route;
    }

    public void setRoute(List<String> route) {
        this.route = route;
        this.length = route.size();
    }
}
