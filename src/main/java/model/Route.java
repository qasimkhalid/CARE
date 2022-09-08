package model;

import helper.AutomatedOperations;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private List<String> path = new ArrayList<>();
    private final int length;
    private long cost;

    public Route(List<String> route) {
        this.path = route;
        this.length = route.size();

        for (int i = 0; i < route.size() - 1; i++) {
            try {
                this.cost += AutomatedOperations.getODPairCostInSeconds(route.get(i), route.get(i+1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getPath() {
        return path;
    }

    public long getCost() {
        return cost;
    }

    public int getLength() {
        return length;
    }
}
