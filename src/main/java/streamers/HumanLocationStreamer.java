package streamers;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import helper.AutomatedOperations;
import helper.EvacuationController;
import helper.HelpingVariables;
import helper.IEvacuationPlan;
import model.*;
import model.scheduler.MovementScheduler;
import model.scheduler.RestingScheduler;
import model.scheduler.RouteFollowingScheduler;

import java.io.OutputStream;
import java.util.*;

public class HumanLocationStreamer extends RdfStream implements Runnable {

    private final long timeStep;
    private boolean keepRunning = true;
    private long initialTime;
    private static float areaPerPersonM2 = 1f;
    private final boolean freeFlow;
    private static boolean evacuationStarted;
    private static boolean interruptionWhileEvacuation;
    private int stepCount = 1;

    public static boolean isEvacuationStarted() {
        return evacuationStarted;
    }

    public static void setEvacuationStarted(boolean evacuationStarted) {
        HumanLocationStreamer.evacuationStarted = evacuationStarted;
    }

    public HumanLocationStreamer(final String iri, long timeStep, boolean freeFlow, float areaPerPersonM2) {
        super(iri);
        this.timeStep = timeStep;
        this.initialTime = System.currentTimeMillis();
        this.freeFlow = freeFlow;
        HumanLocationStreamer.areaPerPersonM2 = areaPerPersonM2;
    }

    //Flag to stop the streamer
    public void stop() {
        keepRunning = false;
    }

    public static void hazardFoundToInitiateEvacuation() {
        evacuationStarted = true;
    }

    public static void interruptionWhileEvacuating() {
        interruptionWhileEvacuation = true;
    }

    @Override
    public void run() {


        long deltaTime;

        Map<String, PersonController> personsMap = new HashMap<>();
        // it will set up persons in a map, add new persons if missing as well
        SetupPersonsMap(personsMap);
        EvacuationController ec = new EvacuationController(personsMap, timeStep);

        // Getting preset routes for pre-emergency phase
//        List<Route> routesInformationList = new ArrayList<>();
//        getAvailableAndPresetRoutes(routesInformationList);

        while (keepRunning) {

            if(evacuationStarted){
                ec.start();
            }

            if (interruptionWhileEvacuation){


            }


            }


            System.out.println("Human Movement Simulation Streamer Time Step No: " + stepCount);
            deltaTime = System.currentTimeMillis() - initialTime;

            for (String key : personsMap.keySet()) {
                PersonController person = personsMap.get(key);
                if (!person.isResting())
                    person.Update(deltaTime);
            }

            detectPersonLocationUsingIdQuadrupleGenerator();

            this.initialTime = System.currentTimeMillis();
            stepCount++;

            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    private void assignRouteToPersonsWhoAreReadyToMove(List<Route> availableRoutes,
                                                       Map<String, PersonController> personsMap) {
        List<String> getEachPersonLocationQueryResult = CareeInfModel.Instance()
                .getQueryResult("data/queries/sparql/GetPersonsLocationWhoAreStanding(ReadyToMove).txt");

        if (!getEachPersonLocationQueryResult.isEmpty()) {

            for (int i = 0; i < getEachPersonLocationQueryResult.size() - 1; i += 2) {
                String person = getEachPersonLocationQueryResult.get(i);
                String location = getEachPersonLocationQueryResult.get(i + 1);

                Optional<Route> r = availableRoutes.stream()
                        .filter(x -> x.getRoute().get(0).equals(location)).findFirst();

                if (r.isPresent()) {
                    // todo: add conditional assignment, dont assign if previous route is not
                    // finished
//                    personsMap.get(person).assignRoute(r.get().getRoute());
                } else {
                    System.out.println("Route Starting from person's location has not been found in RouteMap");
                }
            }
        }
    }

    public static void SetupPersonsMap(Map<String, PersonController> personControllerMap) {
        List<String> getAllPersonQueryResult = CareeInfModel.Instance()
                .getQueryResult("data/queries/sparql/GetAllPersons.txt");
        for (int i = 0; i < getAllPersonQueryResult.size() - 2; i += 3) {
            String person = getAllPersonQueryResult.get(i);
            String type = getAllPersonQueryResult.get(i+1);
            String personId = getAllPersonQueryResult.get(i+2);
            if (!personControllerMap.containsKey(person)) {
                Person p = new Person(person, personId, type); // Person object is being created
                PersonController pc = new PersonController(p); // PersonController object is being created using a
                // person object
                personControllerMap.put(person, pc);
            }
            // handling the issue of transitive memory model in which whole tree is associated with a person mobility impairment
            // updating the type of the person
            else if (type.equals("https://w3id.org/sbeo#NonMotorisedWheelchairPerson")){
                personControllerMap.get(person).getPerson().setType(type);
            }
        }
    }

    private void computeTimeRequiredForPersonFromOriginToDestination(List<String> personWithOD,
            List<PersonMovementInformation> personMovementInformation, Map<String, Integer> spaceOccupancyMap)
            throws Exception {
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

            // *No being used for the moment*
            // Calculating instantaneous occupancy status of each space.
//             spaceOccupancyMap.merge(origin, 1, Integer::sum);
        }
    }

    private void getAvailableAndPresetRoutes(List<Route> routesInformationList) {
        List<String> getAvailableRoutesQueryResult = CareeInfModel.Instance()
                .getQueryResult("data/queries/sparql/FindAllRoutesWithTheirElements.txt");
        Map<String, List<String>> routeMap = new HashMap<>();
        if (!getAvailableRoutesQueryResult.isEmpty()) {
            for (int i = 0; i < getAvailableRoutesQueryResult.size() - 2; i += 3) {
                String routeName = getAvailableRoutesQueryResult.get(i);
                String routeElementIndex = getAvailableRoutesQueryResult.get(i + 1);
                String routeElement = getAvailableRoutesQueryResult.get(i + 2);
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

    private void detectPersonLocationUsingIdQuadrupleGenerator() {
        RdfQuadruple q;
        String timeNow = String.valueOf(System.currentTimeMillis());
        List<Person> pList = new ArrayList<>();
        List<String> p = CareeInfModel.Instance()
                .getQueryResult("data/queries/sparql/GetPersonHavingRestingMotionStatus.txt");

        for (int i = 0; i < p.size() - 2; i += 3) {
            String[] tokens = p.get(i + 2).split("\"");
            pList.add(new Person(p.get(i + 1), tokens[1] + "^^http://www.w3.org/2001/XMLSchema#integer"));
        }

        for (int i = 0; i < pList.size(); i++) {
            String observationCounter = "_" + i;
            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
                    HelpingVariables.rdfPrefix + "type",
                    HelpingVariables.sosaPrefix + "Observation",
                    System.currentTimeMillis());

            this.put(q);
            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
                    HelpingVariables.sosaPrefix + "observedProperty",
                    HelpingVariables.exPrefix + "HumanDetection",
                    System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
                    HelpingVariables.sosaPrefix + "hasSimpleResult", pList.get(i).getId() + "",
                    System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
                    HelpingVariables.sbeoPrefix + "atTime",
                    "" + timeNow,
                    System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(
                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
                    HelpingVariables.sosaPrefix + "madeBySensor",
                    pList.get(i).getLocation() + "_HumanDetection_Sensor",
                    System.currentTimeMillis());
            this.put(q);
        }

    }

}







/*
New Algorithm for CAREE that handles one shortest path algorithm including interruptions

1: After each timestep, the location of each person is updated in the system.
2: After each timestep, the location of each person is also printed using a C-SPARQL query.
3: After each timestep, the value of each sensor is updated in the system.
4: After each timestep, the value of each sensor is also printed using a C-SPARQL query.



5: As soon as the safety value decreases that the allowed safety value:
    a: The evacuation starts
    b: If the evacuation was already started, generate an interruption.
6: If the safety value of one space gets below the allowed safety, it'll be checked on each timestep
    a: Is it necessary or should we avoid it?
        i: We can't skip to check it safety value as it might unavailable for everyone (equal to 0) in the next step.


7: Once the evacuation process starts.
    a: Get the location of each person.
    b: Find a route for each person from his location to the nearest exit using the latest graph composed of only available nodes and edges
    c: Shortlist and assign the shortest path to the person.
8: Once the routes have been assigned to people. They must start following those routes.
9: Route-traversing strategy has been implemented separately.
10: Once a person traverses his assigned route successfully. His motion status must set to Resting.

11: At any time step, if the safety value of any space gets lower than the allowed capacity of any space, and it doesn't exist in a list data structure.
    a: Initiate an interruption call.


12: An interruption call checks:
    a: People whose status is in Moving.
    b: Check the assign routes of everyone.
    c: Filter the people who have that space (left in the assigned route) whose safety value got decreased.
    d: Run 7b until 10.

13: Some persons who are following a path will only be shown upon the completion of each node traversal of their given path.
    How to reassign them a new path?
    a: Maybe we can get people using there movement status, i.e., Moving?
    b: Then getting the current traversing node and perform 12c, and then 12d.






 */

