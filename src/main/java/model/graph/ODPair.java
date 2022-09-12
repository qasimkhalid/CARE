package model.graph;

import model.Space;

public class ODPair {

    private String origin;
    private String destination;
    private Space space;
    private long cost;

    public ODPair( String origin, String destination, String cost) {
        this.origin = origin;
        this.destination = destination;
//        this.value = Long.parseLong(value.substring(0, value.length() - 1).trim());
        this.cost = Long.parseLong(cost.trim());
    }

    public ODPair(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin( String origin ) {
        this.origin = origin;
    }


    public String getDestination() {
        return destination;
    }

    public void setDestination( String destination ) {
        this.destination = destination;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
