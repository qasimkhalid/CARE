package model;

import java.util.List;

public class ODPair {

    private String origin;
    private String destination;
    private long value;
    private List<String> originDestination;

    public List<String> originDestination() {
        return originDestination;
    }

    public void originDestination( List<String> originDestination ) {
        this.originDestination = originDestination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin( String origin ) {
        this.origin = origin;
    }

    public List<String> getOriginDestination() {
        return originDestination;
    }

    public void setOriginDestination( List<String> originDestination ) {
        this.originDestination = originDestination;
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

    public ODPair( String origin, String destination, String value, List<String> originDestination ) {
        this.origin = origin;
        this.destination = destination;
        this.value = Long.parseLong(value.substring(0, value.length() - 1).trim());
        this.originDestination = originDestination;
    }

    public ODPair( String origin, String destination, String value ) {
        this.origin = origin;
        this.destination = destination;
        this.value = Long.parseLong(value.substring(0, value.length() - 1).trim());
    }
}
