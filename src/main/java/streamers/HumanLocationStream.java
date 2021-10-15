package streamers;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import helper.MathOperations;
import helper.SparqlFunctions;
import model.ODPair;
import model.PersonMovementTime;
import model.Scheduler;
import model.Space;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.thrift.TException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class HumanLocationStream extends RdfStream implements Runnable {

    private static InfModel infModel;
    private final static String foafPrefix = "http://xmlns.com/foaf/0.1/";
    private final static String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String sbeoPrefix = "https://w3id.org/sbeo#";
    private final static String sosaPrefix = "http://www.w3.org/ns/sosa/";
    private final static String exPrefix = "https://w3id.org/sbeo/example/officescenario#";
    private final static Resource motionStateStanding = ResourceFactory.createResource(exPrefix + "Standing");
    private final static Resource motionStateWalking = ResourceFactory.createResource(exPrefix + "Walking");
    private final static  Resource activityStatusEvacuating = ResourceFactory.createResource(exPrefix + "Evacuating");
    private Property motionState;
    private Property locatedIn;
    private Property atTime;


    private final long timeStep;
    private final String streamIRI;
    private boolean keepRunning = true;
    private long initialTime;
    private static float areaPerPersonM2 = 1f;
    private boolean freeFlow;

    private static Resource originInstance ;
    private static Resource destinationInstance ;
    private static Resource personInstance;






    public HumanLocationStream(final String iri, long timeStep, InfModel model, boolean freeFlow, float areaPerPersonM2 ) {
        super(iri);
        this.streamIRI = iri;
        this.timeStep = timeStep;
        this.infModel = model;
        this.initialTime = System.currentTimeMillis();
        this.freeFlow= freeFlow;
        this.areaPerPersonM2 = areaPerPersonM2;
    }

    public void stop() {
        keepRunning = false;
    }

    @Override
    public synchronized void run() {

        int count = 1;

        motionState = infModel.getProperty(sbeoPrefix + "hasMotionState");
        locatedIn = infModel.getProperty(sbeoPrefix + "locatedIn");
        atTime = infModel.getProperty(sbeoPrefix + "atTime");

        List<ODPair> odPairList = new ArrayList<>();
        List<Space> spaceInfoList = new ArrayList<>();
        Scheduler scheduler = new Scheduler();
        List<String> personNeedToMoveODQueryResult;
        List<PersonMovementTime> personWhoFinished;
        OutputStream out;
        long deltaTime;
        long timeRequired;
        long extraTime;
        float area;
        try {
            odPairList = getCostOfAllODPairs(infModel);
            spaceInfoList = getSpaceInfo(infModel);
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (keepRunning) {


            System.out.println("Human Location Streamer Time Step No: " + count);
            try {
                deltaTime = System.currentTimeMillis() - initialTime;

                personNeedToMoveODQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/PersonWhoNeedToMove.txt");
                if (!personNeedToMoveODQueryResult.isEmpty()) {

                    Map<String, Integer> spaceDensityMap = new HashMap<>();
                    List<PersonMovementTime> personNeedToMove = new ArrayList<>();
                    String p;
                    String[] tokens;
                    String id;
                    for (int i = 0; i < personNeedToMoveODQueryResult.size() - 3; i += 4) {

                        p = personNeedToMoveODQueryResult.get(i);
                        tokens = personNeedToMoveODQueryResult.get(i + 1).split("\"");
                        id = tokens[1] + "^^http://www.w3.org/2001/XMLSchema#integer";
                        String origin = personNeedToMoveODQueryResult.get(i + 2);
                        String destination = personNeedToMoveODQueryResult.get(i + 3);

                        // calculate required time
                        Optional<ODPair> odPair = odPairList.stream()
                                .filter(x -> x.getOrigin().equals(origin) && x.getDestination().equals(destination)).findFirst();

                        if (odPair.isPresent()) {
                            timeRequired = odPair.get().getValue() * 1000;
                        } else {
                            throw new Exception("Origin and Destination not found in odPairList");
                        }

                        // calculate density of each space
                        spaceDensityMap.merge(origin, 1, Integer::sum);

                        PersonMovementTime person = new PersonMovementTime(p, timeRequired, 0, origin, destination, id);
                        personNeedToMove.add(person);

                    }

                    StringBuilder sb = new StringBuilder();
                    // Finding extra time needed for each person if the total number of persons exceeds the provided limit (area per person) in any space.
                    for (PersonMovementTime person : personNeedToMove) {

                        // Checking if the movement of persons is free flow or density dependent.
                        // If its density dependent, then extra time is added to the required time for each person.
                        if (!freeFlow) {

                            // find space area
                            Optional<Space> space = spaceInfoList.stream()
                                    .filter(x -> x.getSpace().equals(person.getOrigin())).findFirst();
                            if (space.isPresent()) {
                                area = space.get().getArea();
                            } else {
                                throw new Exception("Origin of person not found in spaceInfoList");
                            }

                            // get the space density status where the person is located
                            Integer density = spaceDensityMap.get(person.getOrigin());
                            extraTime = MathOperations.getExtraTime(area, density, areaPerPersonM2);
                            if (extraTime > 0) {
                                person.incrementTimeRequired(extraTime);
                            }
                        }
                        sb.append(initialTime + "\t" + person.toString() + "\n");

                        // add person in the scheduler
                        scheduler.addMovingPerson(person);
                    }
                    Files.write(Paths.get("data/output/O-DPairAreaSpecificNeededTime.txt"), sb.toString().getBytes(), StandardOpenOption.APPEND);
                    int x = 0;
                }

                updateModelBeforePersonMoves(infModel, scheduler.getMovingPersons());


//                    s = new FileOutputStream("data/output/4.txt");
//                    RDFDataMgr.write(s, infModel, RDFFormat.TURTLE_PRETTY);
                personWhoFinished = scheduler.update(deltaTime, scheduler.getMovingPersons());

                if (!personWhoFinished.isEmpty()) {
                    updateModelWhenPersonFinishedMoving(infModel, personWhoFinished);
//                    out = new FileOutputStream("data/output/5.txt");
//                    RDFDataMgr.write(out, infModel, RDFFormat.TURTLE_PRETTY);
                    int test = 0;
                }

                this.initialTime = System.currentTimeMillis();
                count++;
                Thread.sleep(timeStep);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void updateModelWhenPersonFinishedMoving( InfModel infModel, List<PersonMovementTime> list) {
        RdfQuadruple q;
//        List<RdfQuadruple> quadruples = new ArrayList<>();

        for (int i=0; i < list.size(); i++) {
//            String observationCounter = String.valueOf(System.currentTimeMillis())+i;
            String timeNow = String.valueOf(System.currentTimeMillis());
            String observationCounter = "_"+ i;
            personInstance = infModel.getResource(list.get(i).getPerson());
            destinationInstance = infModel.getResource(list.get(i).getDestination());
            infModel.remove(personInstance, motionState, motionStateWalking);
            infModel.add(personInstance, motionState, motionStateStanding);
            infModel.add(personInstance, locatedIn, destinationInstance);

//            System.out.println(observationCounter  + " "+  p.getPerson() + " "+ p.getOrigin() + " " + p.getDestination());

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, rdfPrefix + "type", sosaPrefix+ "Observation", System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sosaPrefix + "observedProperty", exPrefix+ "HumanDetection", System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sosaPrefix+ "hasSimpleResult", list.get(i).getId()+"^^xsd:int", System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sbeoPrefix+ "atTime", timeNow , System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sosaPrefix+ "madeBySensor", list.get(i).getDestination()+"_HumanDetection_Sensor" , System.currentTimeMillis());
            this.put(q);
        }

    }

    private void updateModelBeforePersonMoves( InfModel infModel, List<PersonMovementTime> list) {
        RdfQuadruple q;
        for (PersonMovementTime t : list) {
            personInstance = infModel.getResource(t.getPerson());
            originInstance = infModel.getResource(t.getOrigin());
            infModel.remove(personInstance, motionState, motionStateStanding);
            infModel.remove(personInstance, locatedIn, originInstance);
            infModel.add(personInstance, motionState, motionStateWalking);

            q = new RdfQuadruple(t.getPerson(), sbeoPrefix + "hasMotionState", exPrefix + "Walking", System.currentTimeMillis());
            this.put(q);
        }
    }

    private List<ODPair> getCostOfAllODPairs( InfModel infModel) throws Exception {
        List<String> odPairQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindO-DPairs.txt");
        List<ODPair> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        ODPair odp;
        for(int i=0; i < odPairQueryResult.size()-2; i+=3) {
            odp = new ODPair(odPairQueryResult.get(i), odPairQueryResult.get(i+1), odPairQueryResult.get(i+2));
            list.add(odp);
            sb.append(odPairQueryResult.get(i) + "\t" + odPairQueryResult.get(i+1) + "\t" + odPairQueryResult.get(i+2) + "\n");
            odp = new ODPair(odPairQueryResult.get(i+1), odPairQueryResult.get(i), odPairQueryResult.get(i+2));
            sb.append(odPairQueryResult.get(i+1) + "\t" + odPairQueryResult.get(i) + "\t" + odPairQueryResult.get(i+2) + "\n");
            list.add(odp);
        }
        Files.write(Paths.get("data/output/O-DPairDistance.txt"), sb.toString().getBytes(), StandardOpenOption.WRITE);
        return list;
    }

    private List<Space> getSpaceInfo( InfModel infModel) throws Exception {
        List<String> spaceInfoQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/GetSpaceInfo.txt");
        List<Space> list = new ArrayList<>();
        Space s;
        for(int i=0; i < spaceInfoQueryResult.size()-1; i+=2) {
            s = new Space(spaceInfoQueryResult.get(i), spaceInfoQueryResult.get(i+1));
            list.add(s);
        }
        return list;
    }

}
