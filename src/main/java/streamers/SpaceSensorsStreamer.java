package streamers;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import helper.MathOperations;
import helper.SparqlFunctions;
import model.Location;
import model.Sensor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SpaceSensorsStreamer extends RdfStream implements Runnable{

//    private volatile static InfModel infModel;
    private static InfModel infModel;
    private final long timeStep;
    private long initialTime;
    private boolean keepRunning = true;

//    private final static long initialTime = System.currentTimeMillis();

    private final static String foafPrefix = "http://xmlns.com/foaf/0.1/";
    private final static String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String sbeoPrefix = "https://w3id.org/sbeo#";
    private final static String exPrefix = "https://w3id.org/sbeo/example/officescenario#";
    private final static String sosaPrefix = "http://www.w3.org/ns/sosa/";

    private static Resource personInstance;
    private static final Resource motionStateStanding = ResourceFactory.createResource(exPrefix + "Standing");
    private static final Resource motionStateWalking = ResourceFactory.createResource(exPrefix + "Walking");
    private static final Resource activityStatusEvacuating = ResourceFactory.createResource(exPrefix + "Evacuating");

    public SpaceSensorsStreamer( final String iri, long timeStep, InfModel model) {
        super(iri);
        this.timeStep = timeStep;
        this.infModel = model;
        this.initialTime = System.currentTimeMillis();
    }

    public void stop() {
        keepRunning = false;
    }

    @Override
    public synchronized void run() {

        int count = 1;
        List<Sensor> sensorDetailsList = new ArrayList<>();
        Map<String, Location> allSensorsValueAtSpecificLocationList = new HashMap<>();
        List<String> sparqlQueryAllSensorsList;
        String timeNow;

        try {
            sparqlQueryAllSensorsList = SparqlFunctions.getSPARQLQueryResult(infModel,"data/Queries/sparql/FindAllSensorsInTheBuildingAlongWithTheirSpace.txt");

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

                    generateSensorValue(sensor, timeNow, allSensorsValueAtSpecificLocationList.get(locationName), count);
                }
            }
            Thread.sleep(timeStep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        while (keepRunning){
            System.out.println("Sensors Streamer Time Step No: " +  count);
            timeNow = String.valueOf(System.currentTimeMillis());

            for (Sensor s: sensorDetailsList) {
                generateSensorValue(s, timeNow, allSensorsValueAtSpecificLocationList.get(s.getLocation()), count);
            }

            //Checking the fire scenario & making a space unavailable

//            Resource unavailableInstance = infModel.getResource(sbeoPrefix+"UnAvailable");
//            Resource AvailableInstance = infModel.getResource(sbeoPrefix+"Available");
//            Property hasAvailabilityStatus = infModel.getProperty(sbeoPrefix + "hasAvailabilityStatus");
//
//
//            for (String key : allSensorsValueAtSpecificLocationList.keySet()) {
//                Location l = allSensorsValueAtSpecificLocationList.get(key);
//                if(l.isSmokeExists() && l.getTemperatureSensorValue() >=55f){
//                    Resource locationInstance = infModel.getResource(l.getLocationName());
//                    if (infModel.contains(locationInstance, hasAvailabilityStatus)) {
//                        infModel.remove(infModel.getRequiredProperty(locationInstance, hasAvailabilityStatus));
//                    }
//                    infModel.add(locationInstance, hasAvailabilityStatus, unavailableInstance);
//                }
//
//            }


//            allSensorsValueAtSpecificLocationList.forEach((key, value) -> {
//                if (value.isSmokeExists() && value.getTemperatureSensorValue() >= 55f) {
//                    Resource locationInstance = infModel.getResource(value.getLocationName());
//                    if (infModel.contains(locationInstance, hasAvailabilityStatus)) {
//                        infModel.remove(infModel.getRequiredProperty(locationInstance, hasAvailabilityStatus));
//                    }
//                    infModel.add(locationInstance, hasAvailabilityStatus, unavailableInstance);
//                }
//            });





            count ++;
            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void generateSensorValue( Sensor sensor, String time, Location location, int count ) {
        float sensorValueNumber;
        boolean sensorValueBoolean;
        String sensorValueString;
        RdfQuadruple q;
        String type = sensor.getObservationType();

        switch (type) {

            case exPrefix + "Temperature":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        rdfPrefix + "type",
                        sosaPrefix + "Observation", System.currentTimeMillis());
                this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "observedProperty",
                        exPrefix + "Temperature", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                if(sensor.getValue() == null) {
                    sensor.setValue(25f);
                }
                sensorValueNumber = MathOperations.getRandomNumberInRange((Float) sensor.getValue() + 15, (Float) sensor.getValue() - 2);
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "hasSimpleResult",
                        sensorValueNumber  + "^^http://www.w3.org/2001/XMLSchema#float", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);


                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                location.setTemperatureSensorValue(sensorValueNumber);
                sensor.setValue(sensorValueNumber);
                break;

            case exPrefix + "Smoke":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        rdfPrefix + "type",
                        sosaPrefix + "Observation", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "observedProperty",
                        exPrefix + "Smoke", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                if(sensor.getValue() == null) {
                    sensor.setValue(false);
                } else if(location.getTemperatureSensorValue() >= 50){ //check if the temperature of the same location is greater than 50
                    sensor.setValue(true);
                }
                sensorValueBoolean = (boolean) sensor.getValue();
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "hasSimpleResult",
                        String.valueOf(sensorValueBoolean) + "^^http://www.w3.org/2001/XMLSchema#boolean", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                location.setSmokeExists(sensorValueBoolean);
                sensor.setValue(sensorValueBoolean);
                break;

            case exPrefix + "Humidity":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        rdfPrefix + "type",
                        sosaPrefix + "Observation", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "observedProperty",
                        exPrefix + "Humidity", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                if(sensor.getValue() == null) {
                    sensor.setValue(0.4f);
                }
                sensorValueNumber = MathOperations.getRandomNumberInRange((Float) sensor.getValue() + .025f, (Float) sensor.getValue() - .025f);
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "hasSimpleResult",
                        sensorValueNumber + "^^http://www.w3.org/2001/XMLSchema#float", System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                    this.put(q);
//                System.out.println(q);

                location.setHumiditySensorValue(sensorValueNumber);
                sensor.setValue(sensorValueNumber);
                break;

            case exPrefix + "HumanDetection":
                break;

            case exPrefix + "SpaceAccessibility":
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        rdfPrefix + "type",
                        sosaPrefix + "Observation", System.currentTimeMillis());
                this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "observedProperty",
                        exPrefix + "SpaceAccessibility", System.currentTimeMillis());
                this.put(q);
//                System.out.println(q);

                if(sensor.getValue() == null) {
                    sensor.setValue(true);
                } else if (location.getTemperatureSensorValue() >= 50 && location.isSmokeExists()){ //check if the temperature of the same location is greater than 50
                    sensor.setValue(false);
                }
                sensorValueBoolean = (boolean) sensor.getValue();
                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "hasSimpleResult",
                        String.valueOf(sensorValueBoolean) + "^^http://www.w3.org/2001/XMLSchema#boolean", System.currentTimeMillis());
                this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sbeoPrefix + "atTime",
                        ""+ time, System.currentTimeMillis());
                this.put(q);
//                System.out.println(q);

                q = new RdfQuadruple(
                        sensor.getSensorName() + "_Observation",
                        sosaPrefix + "madeBySensor",
                        ""+ sensor.getSensorName(), System.currentTimeMillis());
                this.put(q);
//                System.out.println(q);

                location.setAvailable(sensorValueBoolean);
                break;

            default:
                System.out.println("Observable Property (e.g., Temperature, Smoke) not found");
        }
    }


}
