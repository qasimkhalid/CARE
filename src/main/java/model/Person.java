package model;

public class Person {

    private String person;
    private String location;
    private String id;
    private String type;
    private float allowedSafetyValue;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Person(String location, String id) {
        this.location = location;
        this.id = id;
    }

    public Person(String person, String location, String type) {
        this.person = person;
        String [] strs = location.split("<");
        this.location = strs.length > 0 ? strs[strs.length-1] : strs[0];
        this.type = type;
        String[] personSplit = person.split("Person");
        this.id = personSplit[1];
    }

    public String getName() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReadableName() {
        return person.split("#")[1];
    }

    public double getAllowedSafetyValue() {
        return this.allowedSafetyValue;
    }

    public void setAllowedSafetyValue(float allowedSafetyValue) {
        this.allowedSafetyValue = allowedSafetyValue;
    }
}
