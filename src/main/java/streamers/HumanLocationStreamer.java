package streamers;

import java.io.OutputStream;
import java.util.*;

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


        Scheduler personMovementScheduler = new Scheduler();
        Scheduler personRestingScheduler = new Scheduler();

        while (keepRunning) {
            System.out.println("Human Location Streamer Time Step No: " + count);
            try {
                deltaTime = System.currentTimeMillis() - initialTime;

                //Making people to rest for a specific Time Interval (which is chosen randomly between (e.g., 1 and 10)), before making a move again.
                AutomatedOperations.computeRestingPhase(deltaTime, personRestingScheduler);

                List<String> personNeedToMoveODQueryResult = CareeInfModel.Instance().getQueryResult("data/Queries/sparql/PersonWhoNeedToMove.txt");
                if (!personNeedToMoveODQueryResult.isEmpty()) {
                    Map<String, Integer> spaceOccupancyMap = new HashMap<>();
                    List<PersonTimerInformation> personNeedToMove = new ArrayList<>();
                    for (int i = 0; i < personNeedToMoveODQueryResult.size() - 4; i += 5) {
                        String p = personNeedToMoveODQueryResult.get(i);
                        String[] tokens = personNeedToMoveODQueryResult.get(i + 1).split("\"");
                        String id = tokens[1] + "^^http://www.w3.org/2001/XMLSchema#integer";
                        String origin = personNeedToMoveODQueryResult.get(i + 2);
                        String destination = personNeedToMoveODQueryResult.get(i + 3);

                        // calculate required time
                        Optional<ODPair> odPair = HelpingVariables.odPairList.stream()
                                .filter(x -> x.getOrigin().equals(origin) && x.getDestination().equals(destination)).findFirst();

                        if (odPair.isPresent()) {
                            timeRequired = odPair.get().getCost() * 1000;
                        } else {
                            throw new Exception("Origin and Destination not found in odPairList");
                        }

                        // Calculating instantaneous occupancy status of each space.
                        spaceOccupancyMap.merge(origin, 1, Integer::sum);

                        PersonTimerInformation pti = new PersonTimerInformation(p, timeRequired, 0, origin, destination, id);
                        personNeedToMove.add(pti);

                    }

                    // Finding extra time needed for each person if the total number of persons exceeds the provided limit (area per person) in any space.
                    for (PersonTimerInformation pti : personNeedToMove) {

                        // Checking if the movement of persons is free flow or space occupancy dependent.
                        // If its space occupancy dependent, then extra time is added to the previously computed free-flow cost for each person.
                        if (!freeFlow) {
                            AutomatedOperations.ComputeAndAddExtraTime(spaceOccupancyMap, pti, areaPerPersonM2);
                        }

                        // Adding person in the personMovementScheduler.
                        personMovementScheduler.addMovingPerson(pti);
                    }
                }
                //Updating the model before the persons start their movements.
                AutomatedOperations.updateModelBeforePersonMoves(personMovementScheduler.getMovingPersons());

                List<PersonTimerInformation> personWhoFinished = personMovementScheduler.updatePersonMovement(deltaTime, personMovementScheduler.getMovingPersons());
                if (!personWhoFinished.isEmpty()) {

                    //Updating the model if someone completes his/her movement.
                    AutomatedOperations.updateModelWhenPersonFinishedMoving(personWhoFinished);
                }


                detectPersonLocationUsingIdQuadrupleGenerator();

                this.initialTime = System.currentTimeMillis();
                count++;
                Thread.sleep(timeStep);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }





    private void detectPersonLocationUsingIdQuadrupleGenerator(){
        RdfQuadruple q;
        String timeNow = String.valueOf(System.currentTimeMillis());
        List<Person> pList = new ArrayList<>();
        List<String> p = CareeInfModel.Instance().getQueryResult("data/queries/sparql/GetPersonHavingRestingMotionStatus.txt");

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
























