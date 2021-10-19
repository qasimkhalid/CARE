package streamers;

import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import helper.MathOperations;
import helper.HelpingVariables;
import model.CareeInfModel;
import model.Location;
import model.Sensor;

public class SpaceSensorsStreamer extends RdfStream implements Runnable{

    private final long timeStep;
    private boolean keepRunning = true;

    public SpaceSensorsStreamer( final String iri, long timeStep) {
        super(iri);
        this.timeStep = timeStep;
    }

    public void stop() {
        keepRunning = false;
    }

    @Override
    public void run() {

        int count = 1;
        List<Sensor> sensorDetailsList = new ArrayList<>();
        Map<String, Location> allSensorsValueAtSpecificLocationList = new HashMap<>();
        List<String> sparqlQueryAllSensorsList;
        String timeNow;

        try {
            sparqlQueryAllSensorsList = CareeInfModel.Instance().getQueryResult("data/Queries/sparql/FindAllSensorsInTheBuildingAlongWithTheirSpace.txt");

            if(!sparqlQueryAllSensorsList.isEmpty()) {

                Sensor sensor;
                String locationName;
                timeNow = String.valueOf(System.currentTimeMillis());

                for (int i = 0; i < sparqlQueryAllSensorsList.size() - 2; i += 3) {
                    locationName = sparqlQueryAllSensorsList.get(i + 1);
                    sensor = new Sensor(sparqlQueryAllSensorsList.get(i), sparqlQueryAllSensorsList.get(i + 2), locationName);
                    sensorDetailsList.add(sensor);

                    if(!allSensorsValueAtSpecificLocationList.containsKey(locationName)){
                        allSensorsValueAtSpecificLocationList.put(locationName, new Location(locationName));
                    }

                    generateSensorValue(sensor, timeNow, allSensorsValueAtSpecificLocationList.get(locationName));
                }
            }
            Thread.sleep(timeStep);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (keepRunning){
            System.out.println("Sensors Streamer Time Step No: " +  count);
            timeNow = String.valueOf(System.currentTimeMillis());

            for (Sensor s: sensorDetailsList) {
                generateSensorValue(s, timeNow, allSensorsValueAtSpecificLocationList.get(s.getLocation()));
            }

            /*
            Making a space unavailable in the model if it has become unavailable because of fire scenario (Temperature > 50 and Smoke is true).
            By doing so, this specific space is no longer considered as a destination of a person.
             */
            for (String key : allSensorsValueAtSpecificLocationList.keySet()) {
                Location l = allSensorsValueAtSpecificLocationList.get(key);
                if (!l.isAvailable()) {
                    Resource locationInstance = CareeInfModel.Instance().getResource(l.getLocationName());
                    CareeInfModel.Instance().add(locationInstance, HelpingVariables.hasAvailabilityStatus, HelpingVariables.unavailableInstance);
                }
            }
            count ++;
            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void generateSensorValue( Sensor sensor, String time, Location location) {
        float sensorValueNumber;
        boolean sensorValueBoolean;
        RdfQuadruple q;
        String type = sensor.getObservationType();

        switch (type) {

            case HelpingVariables.exPrefix + "Temperature":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.rdfPrefix + "type",
                        HelpingVariables.sosaPrefix + "Observation", System.currentTimeMillis());
                this.put(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "observedProperty",
                        HelpingVariables.exPrefix + "Temperature", System.currentTimeMillis());
                    this.put(q);

                if(sensor.getValue() == null) {
                    sensor.setValue(25f);
                }
                sensorValueNumber = MathOperations.getRandomNumberInRange((Float) sensor.getValue() + 5, (Float) sensor.getValue() - 2);
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "hasSimpleResult",
                        sensorValueNumber  + "^^http://www.w3.org/2001/XMLSchema#float", System.currentTimeMillis());
                    this.put(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                    this.put(q);
                    
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                    this.put(q);

                location.setTemperatureSensorValue(sensorValueNumber);
                sensor.setValue(sensorValueNumber);
                break;

            case HelpingVariables.exPrefix + "Smoke":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.rdfPrefix + "type",
                        HelpingVariables.sosaPrefix + "Observation", System.currentTimeMillis());
                    this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "observedProperty",
                        HelpingVariables.exPrefix + "Smoke", System.currentTimeMillis());
                    this.put(q);


                if(sensor.getValue() == null) {
                    sensor.setValue(false);
                } else if(location.getTemperatureSensorValue() >= 50){ //check if the temperature of the same location is greater than 50
                    sensor.setValue(true);
                }
                sensorValueBoolean = (boolean) sensor.getValue();
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "hasSimpleResult",
                        String.valueOf(sensorValueBoolean) + "^^http://www.w3.org/2001/XMLSchema#boolean", System.currentTimeMillis());
                    this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                    this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                    this.put(q);


                location.setSmokeExists(sensorValueBoolean);
                sensor.setValue(sensorValueBoolean);
                break;

            case HelpingVariables.exPrefix + "Humidity":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.rdfPrefix + "type",
                        HelpingVariables.sosaPrefix + "Observation", System.currentTimeMillis());
                    this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "observedProperty",
                        HelpingVariables.exPrefix + "Humidity", System.currentTimeMillis());
                    this.put(q);


                if(sensor.getValue() == null) {
                    sensor.setValue(0.4f);
                }
                sensorValueNumber = MathOperations.getRandomNumberInRange((Float) sensor.getValue() + .025f, (Float) sensor.getValue() - .025f);
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "hasSimpleResult",
                        sensorValueNumber + "^^http://www.w3.org/2001/XMLSchema#float", System.currentTimeMillis());
                    this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                    this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                    this.put(q);


                location.setHumiditySensorValue(sensorValueNumber);
                sensor.setValue(sensorValueNumber);
                break;

            case HelpingVariables.exPrefix + "HumanDetection":
                break;

            case HelpingVariables.exPrefix + "SpaceAccessibility":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.rdfPrefix + "type",
                        HelpingVariables.sosaPrefix + "Observation", System.currentTimeMillis());
                this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "observedProperty",
                        HelpingVariables.exPrefix + "SpaceAccessibility", System.currentTimeMillis());
                this.put(q);


                if(sensor.getValue() == null) {
                    sensor.setValue(true);
                } else if (location.getTemperatureSensorValue() >= 50 && location.isSmokeExists()){ //check if the temperature of the same location is greater than 50
                    sensor.setValue(false);
                }
                sensorValueBoolean = (boolean) sensor.getValue();
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "hasSimpleResult",
                        String.valueOf(sensorValueBoolean) + "^^http://www.w3.org/2001/XMLSchema#boolean", System.currentTimeMillis());
                this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                this.put(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        HelpingVariables.sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                this.put(q);


                location.setAvailable(sensorValueBoolean);
                break;

            default:
                System.out.println("Observable Property (e.g., Temperature, Smoke) not found");
        }
    }


}
