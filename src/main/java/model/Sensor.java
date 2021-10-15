package model;

import java.util.Objects;

public class Sensor {

    private String sensorName;
    private String observationType;
    private String location;
    private Object value;
//    private Object previousValue;


    public Sensor( String sensorName, String observationType, String location ) {
        this.sensorName = sensorName;
        this.observationType = observationType;
        this.location = location;
    }

    public Sensor( String sensorName, String observationType, String location, float value ) {
        this.sensorName = sensorName;
        this.observationType = observationType;
        this.location = location;
        this.value = value;
    }

    public Sensor( String sensorName, String observationType, String location, boolean value ) {
        this.sensorName = sensorName;
        this.observationType = observationType;
        this.location = location;
        this.value = value;
    }

    public Object getValue() {
        if (Objects.isNull(value) ){
            return null;
        } else {
            return this.value;
        }
    }

    public void setValue( float value ) {
        this.value = value;
    }

    public void setValue( boolean value ) {
        this.value = value;
    }

//    public Object getPreviousValue() {
//        return this.previousValue.getClass().getSimpleName();
//    }

//    public void setPreviousValue( float previousValue ) {
//        this.previousValue = previousValue;
//    }

//    public void setPreviousValue( boolean previousValue ) {
//        this.previousValue = previousValue;
//    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName( String sensorName ) {
        this.sensorName = sensorName;
    }

    public String getObservationType() {
        return observationType;
    }

    public void setObservationType( String observationType ) {
        this.observationType = observationType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation( String location ) {
        this.location = location;
    }
}
