package model.graph;

import operations.AutomatedOperations;

public class Edge {
    private final String start;
    private final String end;
    private long cost;

    public Edge(String origin, String destination) {
        this.start = origin;
        this.end = destination;

        try {
            this.cost = AutomatedOperations.getODPairCostInSeconds(this.start, this.end);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getCost() {
        return cost;
    }

    public String getOrigin() {
        return start;
    }

    public String getDestination() {
        return end;
    }
}
