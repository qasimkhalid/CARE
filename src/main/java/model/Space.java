package model;

public class Space {
    private String name;

    private float area;
    private String type;
    private float safetyValue = 1f;
    private int capacity;
    private float humiditySensorValue;
    private float temperatureSensorValue;
    private float humanDetectionSensorValue;
    private boolean smokeExists;
    private boolean available;

    public Space(String name) {
        this.name = name;
    }

    public Space(String name, String area, String capacity, String type ) {
        this.name = name;
        this.area = Float.parseFloat(area.trim().substring(1, area.length() - 12));
        this.capacity = Integer.parseInt(capacity.trim());
        this.type = type;
    }

    public Space(String name, float humiditySensorValue, float temperatureSensorValue, float humanDetectionSensorValue, boolean smokeExists, boolean available) {
        this.name = name;
        this.humiditySensorValue = humiditySensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.humanDetectionSensorValue = humanDetectionSensorValue;
        this.smokeExists = smokeExists;
        this.available = available;
    }


    public Space(String name, String area, String type, float safetyValue) {
        this.name = name;
        this.area = Float.parseFloat(area.trim().substring(1, area.length() - 12));
        this.type = type;
        this.safetyValue = safetyValue;
    }

    public Space(String name, float humiditySensorValue, float temperatureSensorValue, boolean smokeExists) {
        this.name = name;
        this.humiditySensorValue = humiditySensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.smokeExists = smokeExists;
    }

    public Space(String name, float area, String type, float safetyValue, int capacity, float humiditySensorValue, float temperatureSensorValue, float humanDetectionSensorValue, boolean smokeExists, boolean available) {
        this.name = name;
        this.area = area;
        this.type = type;
        this.safetyValue = safetyValue;
        this.capacity = capacity;
        this.humiditySensorValue = humiditySensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.humanDetectionSensorValue = humanDetectionSensorValue;
        this.smokeExists = smokeExists;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getHumiditySensorValue() {
        return humiditySensorValue;
    }

    public void setHumiditySensorValue(float humiditySensorValue) {
        this.humiditySensorValue = humiditySensorValue;
    }

    public float getTemperatureSensorValue() {
        return temperatureSensorValue;
    }

    public void setTemperatureSensorValue(float temperatureSensorValue) {
        this.temperatureSensorValue = temperatureSensorValue;
    }

    public float getHumanDetectionSensorValue() {
        return humanDetectionSensorValue;
    }

    public void setHumanDetectionSensorValue(float humanDetectionSensorValue) {
        this.humanDetectionSensorValue = humanDetectionSensorValue;
    }

    public boolean isSmokeExists() {
        return smokeExists;
    }

    public void setSmokeExists(boolean smokeExists) {
        this.smokeExists = smokeExists;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public float getSafetyValue() {
        return safetyValue;
    }

    public void setSafetyValue(float safetyValue) {
        this.safetyValue = safetyValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getArea() {
        return area;
    }

    public void setArea( float area ) {
        this.area = area;
    }
}

