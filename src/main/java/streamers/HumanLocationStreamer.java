package streamers;

import java.io.OutputStream;
import java.util.*;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import helper.AutomatedOperations;
import helper.HelpingVariables;
import model.*;
import model.scheduler.MovementScheduler;
import model.scheduler.RestingScheduler;
import model.scheduler.RouteFollowingScheduler;

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
        OutputStream out;


        int MoveToConnectedArbitrarySpace = 0;
        int FollowARoute = 1;

        int type = FollowARoute;

        MovementScheduler movementScheduler = new MovementScheduler();
        RestingScheduler restingScheduler = new RestingScheduler();
        RouteFollowingScheduler personFollowingRouteScheduler = new RouteFollowingScheduler();

//        Map<String, List<String>> routeMap = new HashMap<>();
        List<Route> routesInformationList = new ArrayList<>();

        Map<String, PersonController> personsMap = new HashMap<String, PersonController>();

        getAvailableAndPresetRoutes(routesInformationList);

        while (keepRunning) {
            System.out.println("Human Location Streamer Time Step No: " + count);

            deltaTime = System.currentTimeMillis() - initialTime;

            // it will setup persons map, add new persons if missing as well
            SetupPersonsMap(personsMap);

            // update personMap with routes information
            assignRouteToPersonsWhoAreReadyToMove(routesInformationList, personsMap);

            for (String key : personsMap.keySet()) {
                PersonController person = personsMap.get(key);
                if (!person.isResting())
                    person.Update(deltaTime);
            }

            detectPersonLocationUsingIdQuadrupleGenerator();

            this.initialTime = System.currentTimeMillis();
            count++;

            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        while (keepRunning) {
//            System.out.println("Human Location Streamer Time Step No: " + count);
//
//            deltaTime = System.currentTimeMillis() - initialTime;
//
//            //Making people to rest for a specific Time Interval (which is chosen randomly between (e.g., 1 and 10)), before making a move again.
//            AutomatedOperations.computeRestingPhase(deltaTime, restingScheduler);
//
//            getAllPersons(personMovementInformationMap, personMovementInformationList);
//
//
//            switch (type) {
//
//                case 0:
//                    List<String> personNeedToMoveToConnectedArbitrarySpaceQueryResult = CareeInfModel.Instance().getQueryResult("data/Queries/sparql/PersonWhoNeedToMove.txt");
//                    Map<String, Integer> spaceOccupancyMap = new HashMap<>();
//                    List<PersonMovementInformation> personNeedToMove = new ArrayList<>();
//                    try {
//                        computeTimeRequiredForPersonFromOriginToDestination(personNeedToMoveToConnectedArbitrarySpaceQueryResult, personNeedToMove, spaceOccupancyMap);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    // Finding extra time needed for each person if the total number of persons exceeds the provided limit (area per person) in any space.
//                    for (PersonMovementInformation pti : personNeedToMove) {
//                        // Checking if the movement of persons is free flow or space occupancy dependent.
//                        // If its space occupancy dependent, then extra time is added to the previously computed free-flow cost for each person.
//                        try {
//                            AutomatedOperations.ComputeAndAddExtraTime(spaceOccupancyMap, pti, areaPerPersonM2);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        // Adding person in the personMovementScheduler.
//                        movementScheduler.addMovingPerson(pti);
//
//                        // Updating the model before the persons start their movements.
//                        AutomatedOperations.updateModelBeforePersonStartsStepBasedMovement(movementScheduler.getMovingPersons());
//
//                        List<PersonMovementInformation> personWhoFinishedStepBasedMovement = movementScheduler.updatePersonMovement(deltaTime, movementScheduler.getMovingPersons());
//
//                        //Updating the model if someone completes his/her movement.
//                        AutomatedOperations.updateModelWhenPersonFinishesStepBasedMovement(personWhoFinishedStepBasedMovement);
//
//                    }
//                    break;
//
//                case 1:
//
//
//                    /*
//                    Todo: find unavailable routes and remove from the route list.
//                     */
//
//                    assignRouteToPersonsWhoAreReadyToMove(routesInformationList, personMovementInformationMap);
//
//
////                    for (PersonMovementInformation pmi : personMovementInformationList) {
////                        if(pmi.getRouteCompletedSoFar().isEmpty()){
////
////                            String origin = pmi.getRouteAssigned().get(0);
////                            String destination = pmi.getRouteAssigned().get(1);
////
////                            try {
////                                pmi.setStepBasedTimeRequired(AutomatedOperations.getODPairCostInSeconds(origin, destination));
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////
////                            pmi.getRouteCompletedSoFar().add(origin);
////                            pmi.getRouteCompletedSoFar().add(destination);
////                            movementScheduler.addMovingPerson(pmi);
////
////                        } else if(!pmi.getRouteCompletedSoFar().isEmpty() && pmi.getRouteCompletedSoFar().size() < pmi.getRouteAssigned().size()){
////
////                            String origin = pmi.getRouteCompletedSoFar().get(pmi.getRouteCompletedSoFar().size() - 1);
////                            String destination = pmi.getRouteAssigned().get(pmi.getRouteCompletedSoFar().size());
////
////                            try {
////                                pmi.setStepBasedTimeRequired(AutomatedOperations.getODPairCostInSeconds(origin, destination));
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                            movementScheduler.addMovingPerson(pmi);
////                            pmi.getRouteCompletedSoFar().add(destination);
////
////
////                        } else if(pmi.getRouteCompletedSoFar().size() == pmi.getRouteAssigned().size()){
////                            int z =0;
////                        }
////
////                    }
//
//                    //(D) Use a query to find the route from the location where the person is located.
//                    //(D) Put that route as assigned route for that person.
//                    //(D) find the cumulative required time (cost) for the route.
//                    //(D) check the routeCovered property and match with assignedRoute. (empty, equal, or less)
//
//                    // Initiate the route
//                    // keep updating Elapsed time.
//                    // keep updating cumulative Elapsed time.
//                    // Choose first two elements of the route. Mark them origin and destination.
//                    // Once a person finishes reaching destination.
//                    // check the routeCovered property and match with assignedRoute.
//                    // take the other next pair from assignedRoute.
//                    // if the route is completes, give a person resting time.
//
//                    // If any emergency is detected,
//                    // Another while loop should be started.
//                    // The persons should be assigned the routes and the same process should be done as above.
//
//
//
//                    break;
//
//                default: System.out.println("Type not found!");
//            }
//
//
//
//
//            detectPersonLocationUsingIdQuadrupleGenerator();
//
//            this.initialTime = System.currentTimeMillis();
//            count++;
//
//            try {
//                Thread.sleep(timeStep);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }


    private void assignRouteToPersonsWhoAreReadyToMove(List<Route> availableRoutes, Map<String, PersonController> personsMap) {
        List<String> getEachPersonLocationQueryResult = CareeInfModel.Instance().getQueryResult("data/queries/sparql/GetPersonsLocationWhoAreStanding(ReadyToMove).txt");

        if(!getEachPersonLocationQueryResult.isEmpty()) {

            for (int i = 0; i < getEachPersonLocationQueryResult.size() - 1; i += 2) {
                String person = getEachPersonLocationQueryResult.get(i);
                String location = getEachPersonLocationQueryResult.get(i + 1);

                Optional<Route> r = availableRoutes.stream()
                        .filter(x -> x.getRoute().get(0).equals(location)).findFirst();

                if (r.isPresent()) {
                    // todo: add conditional assignment, dont assign if previous route is not finished
                    personsMap.get(person).assignRoute(r.get().getRoute());
                } else {
                    System.out.println("Route Starting from person's location has not been found in RouteMap");
                }
            }
        }
    }

    private void SetupPersonsMap(Map<String, PersonController> personControllerMap) {
        List<String> getAllPersonQueryResult = CareeInfModel.Instance().getQueryResult("data/queries/sparql/GetAllPersons.txt");
        for (int i = 0; i < getAllPersonQueryResult.size() - 1; i+=2) {
            String person = getAllPersonQueryResult.get(i);
            if(!personControllerMap.containsKey(person)){
                PersonController p = new PersonController(person, getAllPersonQueryResult.get(i+1));
                personControllerMap.put(person, p);
            }
        }
    }

    private void computeTimeRequiredForPersonFromOriginToDestination(List<String> personWithOD, List<PersonMovementInformation> personMovementInformation, Map<String, Integer> spaceOccupancyMap) throws Exception {
        long timeRequired;
        for (int i = 0; i < personWithOD.size() - 4; i += 5) {
            String p = personWithOD.get(i);
            String[] tokens = personWithOD.get(i + 1).split("\"");
            String id = tokens[1] + "^^http://www.w3.org/2001/XMLSchema#integer";
            String origin = personWithOD.get(i + 2);
            String destination = personWithOD.get(i + 3);

            timeRequired = AutomatedOperations.getODPairCostInSeconds(origin, destination);
            PersonMovementInformation pti = new PersonMovementInformation(p, timeRequired, 0, origin, destination, id);
            personMovementInformation.add(pti);

            //*No being used for the moment*
            // Calculating instantaneous occupancy status of each space.
//            spaceOccupancyMap.merge(origin, 1, Integer::sum);
        }
    }


    private void getAvailableAndPresetRoutes(List<Route> routesInformationList) {
        List<String> getAvailableRoutesQueryResult = CareeInfModel.Instance().getQueryResult("data/queries/sparql/FindAllRoutesWithTheirElements.txt");
        Map<String, List<String>> routeMap = new HashMap<>();
        if (!getAvailableRoutesQueryResult.isEmpty()) {
            for (int i = 0; i < getAvailableRoutesQueryResult.size() - 2; i += 3) {
                String routeName = getAvailableRoutesQueryResult.get(i);
                String routeElementIndex = getAvailableRoutesQueryResult.get(i+1);
                String routeElement = getAvailableRoutesQueryResult.get(i+2);
                if (!routeMap.containsKey(routeName)) {
                    routeMap.put(routeName, new ArrayList<>());
                }
                routeMap.get(routeName).add(routeElement);
            }
        }
        for (Map.Entry<String, List<String>> entry : routeMap.entrySet()) {
            routesInformationList.add(new Route(entry.getKey(), entry.getValue()));
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