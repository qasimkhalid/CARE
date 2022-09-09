package model;

import graph.Node;
import helper.AutomatedOperations;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private List<String> path = new ArrayList<>();
    private final int length;
    private long cost;

    public Route(List<Node> shortestPath) {
        this.length = shortestPath.size();
        this.cost = shortestPath.get(shortestPath.size()-1).getDistance();
        for (int i = 0; i < shortestPath.size(); i++) {
            path.add(shortestPath.get(i).getName());
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
