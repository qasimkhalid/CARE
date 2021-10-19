package streamers;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import com.hp.hpl.jena.rdf.model.Resource;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import helper.AutomatedOperations;
import helper.MathOperations;
import helper.HelpingVariables;
import model.*;

public class HumanLocationStreamer extends RdfStream implements Runnable {

    private final long timeStep;
    private boolean keepRunning = true;
    private long initialTime;
    private static float areaPerPersonM2 = 1f;
    private final boolean freeFlow;

    public HumanLocationStreamer( final String iri, long timeStep, boolean freeFlow, float areaPerPersonM2 ) {
        super(iri);
        this.timeStep = timeStep;
        this.initialTime = System.currentTimeMillis();
        this.freeFlow= freeFlow;
        HumanLocationStreamer.areaPerPersonM2 = areaPerPersonM2;
    }

    public void stop() {
        keepRunning = false;
    }

    @Override
    public void run() {

        int count = 1;
        long deltaTime;
        long timeRequired;
        long extraTime;
        float area;
        OutputStream out;

        List<ODPair> odPairList = new ArrayList<>();
        List<Space> spaceInfoList = new ArrayList<>();
        Scheduler scheduler = new Scheduler();
        List<String> personNeedToMoveODQueryResult;

        try {
            odPairList = AutomatedOperations.getCostOfAllODPairs(CareeInfModel.Instance().getInfModel());
            spaceInfoList = AutomatedOperations.getSpaceInfo(CareeInfModel.Instance().getInfModel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (keepRunning) {
            System.out.println("Human Location Streamer Time Step No: " + count);
            try {
                deltaTime = System.currentTimeMillis() - initialTime;

                personNeedToMoveODQueryResult = CareeInfModel.Instance().getQueryResult("data/Queries/sparql/PersonWhoNeedToMove.txt");

                if (!personNeedToMoveODQueryResult.isEmpty()) {

                    String p;
                    String[] tokens;
                    String id;
                    Map<String, Integer> spaceDensityMap = new HashMap<>();
                    PersonMovementTime pmt;
                    List<PersonMovementTime> personNeedToMove = new ArrayList<>();
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

                        pmt = new PersonMovementTime(p, timeRequired, 0, origin, destination, id);
                        personNeedToMove.add(pmt);

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
                }

                AutomatedOperations.updateModelBeforePersonMoves(scheduler.getMovingPersons());

                List<PersonMovementTime> personWhoFinished = scheduler.update(deltaTime, scheduler.getMovingPersons());
                if (!personWhoFinished.isEmpty()) {
                    AutomatedOperations.updateModelWhenPersonFinishedMoving(personWhoFinished);
                }

                detectPersonLocationUsingId();

                this.initialTime = System.currentTimeMillis();
                count++;
                Thread.sleep(timeStep);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void detectPersonLocationUsingId(){
        RdfQuadruple q;
        String timeNow = String.valueOf(System.currentTimeMillis());
        List<Person> pList = new ArrayList<>();
        List<String> p = CareeInfModel.Instance().getQueryResult("data/queries/sparql/GetPersonHavingStandingMotionStatus.txt");

        for (int i = 0; i < p.size() - 2; i+=3) {
            String[] tokens = p.get(i+2).split("\"");
            pList.add(new Person(p.get(i), p.get(i+1), tokens[1]+"^^http://www.w3.org/2001/XMLSchema#integer"));
        }

        for (int i=0; i < pList.size(); i++) {
            String observationCounter = "_"+ i;
            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation"+timeNow+observationCounter,
                    HelpingVariables.rdfPrefix + "type",
                    HelpingVariables.sosaPrefix+ "Observation",
                    System.currentTimeMillis());

            this.put(q);
            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation"+timeNow+observationCounter,
                    HelpingVariables.sosaPrefix + "observedProperty",
                    HelpingVariables.exPrefix+ "HumanDetection",
                    System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation"+timeNow+observationCounter,
                    HelpingVariables.sosaPrefix+ "hasSimpleResult", pList.get(i).getId()+"",
                    System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation"+timeNow+observationCounter,
                    HelpingVariables.sbeoPrefix+ "atTime",
                    ""+ timeNow ,
                    System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation"+timeNow+observationCounter,
                    HelpingVariables.sosaPrefix+ "madeBySensor",
                    pList.get(i).getLocation()+"_HumanDetection_Sensor" ,
                    System.currentTimeMillis());
            this.put(q);
        }


    }

}
























