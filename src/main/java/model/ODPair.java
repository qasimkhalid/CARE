package model;

import java.util.List;

public class ODPair {

    private String origin;
    private String destination;
    private long value;


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

    public long getValue() {
        return value;
    }

    public void setValue( long value ) {
        this.value = value;
    }

    public ODPair( String origin, String destination, String value ) {
        this.origin = origin;
        this.destination = destination;
//        this.value = Long.parseLong(value.substring(0, value.length() - 1).trim());
        this.value = Long.parseLong(value.trim());
    }

    public ODPair( String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }
}
