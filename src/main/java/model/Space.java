package model;

import java.util.ArrayList;
import java.util.List;

public class Space {
    private String name;

    private float area;
    private String type;
    private double safetyValue = 1f;
    private int accommodationCapacity;
    private int currentOccupancyRatio;
    private double humiditySensorValue;
    private double temperatureSensorValue;
    private double humanDetectionSensorValue;
    private boolean smokeExists;
    private boolean available;
    private final List<Space> adjacentSpaces = new ArrayList<>();

    public Space(String name) {
        this.name = name;
    }

    public Space(String name, String area, String accommodationCapacity, String type ) {
        this.name = name;
        this.area = Float.parseFloat(area.trim().substring(1, area.length() - 12));
        this.accommodationCapacity = Integer.parseInt(accommodationCapacity.trim());
        this.type = type;
    }

    public Space(String name, double humiditySensorValue, double temperatureSensorValue, double humanDetectionSensorValue, boolean smokeExists, boolean available) {
        this.name = name;
        this.humiditySensorValue = humiditySensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.humanDetectionSensorValue = humanDetectionSensorValue;
        this.smokeExists = smokeExists;
        this.available = available;
    }


    public Space(String name, String area, String type, double safetyValue) {
        this.name = name;
        this.area = Float.parseFloat(area.trim().substring(1, area.length() - 12));
        this.type = type;
        this.safetyValue = safetyValue;
    }

    public Space(String name, double humiditySensorValue, double temperatureSensorValue, boolean smokeExists) {
        this.name = name;
        this.humiditySensorValue = humiditySensorValue;
        this.temperatureSensorValue = temperatureSensorValue;
        this.smokeExists = smokeExists;
    }

    public Space(String name, float area, String type, double safetyValue, int accommodationCapacity, double humiditySensorValue, double temperatureSensorValue, double humanDetectionSensorValue, boolean smokeExists, boolean available) {
        this.name = name;
        this.area = area;
        this.type = type;
        this.safetyValue = safetyValue;
        this.accommodationCapacity = accommodationCapacity;
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
        return accommodationCapacity;
    }

    public void setCapacity(int accommodationCapacity) {
        this.accommodationCapacity = accommodationCapacity;
    }

    public double getHumiditySensorValue() {
        return Math.round(humiditySensorValue* 100d) / 100d;
    }

    public void setHumiditySensorValue(double humiditySensorValue) {
        this.humiditySensorValue = humiditySensorValue;
    }

    public double getTemperatureSensorValue() {
        return Math.round(temperatureSensorValue* 100d) / 100d;
    }

    public void setTemperatureSensorValue(double temperatureSensorValue) {
        this.temperatureSensorValue = temperatureSensorValue;
    }

    public double getHumanDetectionSensorValue() {
        return Math.round(humanDetectionSensorValue* 100d) / 100d;
    }

    public void setHumanDetectionSensorValue(double humanDetectionSensorValue) {
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

    public double getSafetyValue() {
        return  Math.round(safetyValue* 100d) / 100d;
    }

    public void setSafetyValue(double safetyValue) {
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

    public int getCurrentOccupancyRatio() {
        return currentOccupancyRatio;
    }

    public void setCurrentOccupancyRatio(int currentOccupancyRatio) {
        this.currentOccupancyRatio = currentOccupancyRatio;
    }

    public int getAccommodationCapacity() {
        return accommodationCapacity;
    }

    public void setAccommodationCapacity(int accommodationCapacity) {
        this.accommodationCapacity = accommodationCapacity;
    }

    public List<Space> getAdjacentSpaces() {
        return adjacentSpaces;
    }
}

