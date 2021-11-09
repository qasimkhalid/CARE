package model;

public class PersonTimerInformation {

    private String person;
    private long timeRequired;
    private long timeElapsed;
    private String origin;
    private String destination;
    private String id;


    public PersonTimerInformation(String person, long timeRequired, long timeElapsed ) {
        this.person = person;
        this.timeRequired = timeRequired;
        this.timeElapsed = timeElapsed;
    }

    public PersonTimerInformation(String person, long timeRequired, long timeElapsed, String origin, String id ) {
        this.person = person;
        this.timeRequired = timeRequired;
        this.timeElapsed = timeElapsed;
        this.origin = origin;
        this.id = id;
    }

    public PersonTimerInformation(String person, long timeRequired, long timeElapsed, String origin, String destination, String id ) {
        this.person = person;
        this.timeRequired = timeRequired;
        this.timeElapsed = timeElapsed;
        this.origin = origin;
        this.destination = destination;
        this.id = id;
    }

    public PersonTimerInformation(String person, String origin, String id) {
        this.person = person;
        this.origin = origin;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
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

    public void incrementTimeRequired( long additionalTime ) {
        this.timeRequired += additionalTime;
    }

    @Override
    public String toString() {
        return origin + '\t' +
                destination + '\t' +
                person + '\t' +
                timeRequired;
    }
}
