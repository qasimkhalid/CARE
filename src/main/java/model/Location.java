package model;

public class Location {

    private String locationName;
    private float humiditySensorValue;
    private float temperatureSensorValue;
    private float humanDetectionSensorValue;
    private boolean smokeExists;
    private boolean available;

    public Location( String locationName, float humiditySensorValue, float temperatureSensorValue, float humanDetectionSensorValue, boolean smokeExists, boolean available ) {
        this.locationName = locationName;
        this.humiditySensorValue = humiditySensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.humanDetectionSensorValue = humanDetectionSensorValue;
        this.smokeExists = smokeExists;
        this.available = available;
    }

    public Location( String locationName, float humiditySensorValue, float temperatureSensorValue, boolean smokeExists ) {
        this.locationName = locationName;
        this.humiditySensorValue = humiditySensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.smokeExists = smokeExists;
    }

    public Location( String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocation( String locationName ) {
        this.locationName = locationName;
    }

    public float getHumiditySensorValue() {
        return humiditySensorValue;
    }

    public void setHumiditySensorValue( float humiditySensorValue ) {
        this.humiditySensorValue = humiditySensorValue;
    }

    public float getTemperatureSensorValue() {
        return temperatureSensorValue;
    }

    public void setTemperatureSensorValue( float temperatureSensorValue ) {
        this.temperatureSensorValue = temperatureSensorValue;
    }

    public float getHumanDetectionSensorValue() {
        return humanDetectionSensorValue;
    }

    public void setHumanDetectionSensorValue( float humanDetectionSensorValue ) {
        this.humanDetectionSensorValue = humanDetectionSensorValue;
    }

    public boolean isSmokeExists() {
        return smokeExists;
    }

    public void setSmokeExists( boolean smokeExists ) {
        this.smokeExists = smokeExists;
    }

    public void setLocationName( String locationName ) {
        this.locationName = locationName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable( boolean available ) {
        this.available = available;
    }
}
