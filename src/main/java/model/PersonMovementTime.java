package model;

public class PersonMovementTime {

    private String person;
    private long timeRequired;
    private long timeElapsed;
    private String origin;
    private String destination;


    public PersonMovementTime( String person, long timeRequired, long timeElapsed ) {
        this.person = person;
        this.timeRequired = timeRequired;
        this.timeElapsed = timeElapsed;
    }

    public PersonMovementTime( String person, long timeRequired, long timeElapsed, String origin, String destination ) {
        this.person = person;
        this.timeRequired = timeRequired;
        this.timeElapsed = timeElapsed;
        this.origin = origin;
        this.destination = destination;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson( String person ) {
        this.person = person;
    }

    public long getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired( long timeRequired ) {
        this.timeRequired = timeRequired;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed( long timeElapsed ) {
        this.timeElapsed = timeElapsed;
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
}
