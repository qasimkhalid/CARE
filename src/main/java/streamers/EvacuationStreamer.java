//package streamers;
//
//import entities.PersonController;
//import eu.larkc.csparql.cep.api.RdfQuadruple;
//import eu.larkc.csparql.cep.api.RdfStream;
//import operations.AutomatedOperations;
//import entities.EvacuationController;
//import helper.HelpingVariables;
//import model.*;
//import operations.CommonOperations;
//
//import java.util.*;
//
//public class EvacuationStreamer extends RdfStream implements Runnable {
//
////    private final long timeStep;
////    private float areaPerPersonM2 = 1f;
////    private final boolean freeFlow;
////
////    // Just for testing purpose. Delete it in future:
////    int referenceCounter = 0;
////
//    public EvacuationStreamer(final String iri, long timeStep, boolean freeFlow, float areaPerPersonM2) {
//        super(iri);
////        this.timeStep = timeStep;
////        this.freeFlow = freeFlow;
////        this.areaPerPersonM2 = areaPerPersonM2;
//    }
//
//    @Override
//    public void run() {
//
////        long deltaTime;
////
////        // Get All Persons
////        List<PersonController> personControllers = EvacuationStreamer.GetAllPersonControllers();
////        EvacuationController ec = new EvacuationController(personControllers, timeStep, 15);
////        ec.start();
////        try {
////
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        } finally {
////            // As soon the evacuation has been completed, close the application
////            closeApplication();
////        }
////        closeApplication();
//
//    }
//
//
//
//
//
////    private void closeApplication() {
////        System.out.println("SpaceSensorsStreamer.stop()");
////        SpaceSensorsStreamer.stop();
////
////        System.out.println("Successfully unregistered Evacuation Stream from the engine");
////        CareeCsparqlEngineImpl.Instance().unregisterStream(getIRI());
////        System.out.println("Successfully unregistered Space Sensors Stream from the engine");
////        CareeCsparqlEngineImpl.Instance().unregisterStream(SpaceSensorsStreamer.getStreamIRI());
////
////        System.out.println("About to exit");
////        System.exit(0);
////    }
//
//
////    private void assignRouteToPersonsWhoAreReadyToMove(List<Route> availableRoutes,
////            Map<String, PersonController> personsMap) {
////        List<String> getEachPersonLocationQueryResult = CareeInfModel.Instance()
////                .getQueryResult("data/queries/sparql/GetPersonsLocationWhoAreStanding(ReadyToMove).txt");
////
////        if (!getEachPersonLocationQueryResult.isEmpty()) {
////
////            for (int i = 0; i < getEachPersonLocationQueryResult.size() - 1; i += 2) {
////                String person = getEachPersonLocationQueryResult.get(i);
////                String location = getEachPersonLocationQueryResult.get(i + 1);
////
////                Optional<Route> r = availableRoutes.stream()
////                        .filter(x -> x.getRoute().get(0).equals(location)).findFirst();
////
////                if (r.isPresent()) {
////                    // todo: add conditional assignment, dont assign if previous route is not
////                    // finished
////                    // personsMap.get(person).assignRoute(r.get().getRoute());
////                } else {
////                    System.out.println("Route Starting from person's location has not been found in RouteMap");
////                }
////            }
////        }
////    }
//
//
//
////    public void detectPersonLocationUsingIdQuadrupleGenerator() {
////        RdfQuadruple q;
////        String timeNow = String.valueOf(System.currentTimeMillis());
////        List<Person> pList = new ArrayList<>();
////        List<String> p = CareeInfModel.Instance()
////                .getQueryResult("data/queries/sparql/GetPersonHavingRestingMotionStatus.txt");
////
////        for (int i = 0; i < p.size() - 2; i += 3) {
////            String[] tokens = p.get(i + 2).split("\"");
////            pList.add(new Person(p.get(i + 1), tokens[1] + "^^http://www.w3.org/2001/XMLSchema#integer"));
////        }
////
////        for (int i = 0; i < pList.size(); i++) {
////            String observationCounter = "_" + i;
////            q = new RdfQuadruple(
////                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
////                    HelpingVariables.rdfPrefix + "type",
////                    HelpingVariables.sosaPrefix + "Observation",
////                    System.currentTimeMillis());
////
////            this.put(q);
////            q = new RdfQuadruple(
////                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
////                    HelpingVariables.sosaPrefix + "observedProperty",
////                    HelpingVariables.exPrefix + "HumanDetection",
////                    System.currentTimeMillis());
////            this.put(q);
////
////            q = new RdfQuadruple(
////                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
////                    HelpingVariables.sosaPrefix + "hasSimpleResult", pList.get(i).getId() + "",
////                    System.currentTimeMillis());
////            this.put(q);
////
////            q = new RdfQuadruple(
////                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
////                    HelpingVariables.sbeoPrefix + "atTime",
////                    "" + timeNow,
////                    System.currentTimeMillis());
////            this.put(q);
////
////            q = new RdfQuadruple(
////                    HelpingVariables.exPrefix + "ObsLocation" + timeNow + observationCounter,
////                    HelpingVariables.sosaPrefix + "madeBySensor",
////                    pList.get(i).getLocation() + "_HumanDetection_Sensor",
////                    System.currentTimeMillis());
////            this.put(q);
////        }
////
////    }
////
//}
//
///*
// * New Algorithm for CAREE that handles one shortest path algorithm including
// * interruptions
// *
// * 1: After each timestep, the location of each person is updated in the system.
// * 2: After each timestep, the location of each person is also printed using a
// * C-SPARQL query.
// * 3: After each timestep, the value of each sensor is updated in the system.
// * 4: After each timestep, the value of each sensor is also printed using a
// * C-SPARQL query.
// *
// *
// *
// * 5: As soon as the safety value decreases than the allowed safety value:
// * a: The evacuation starts
// * b: If the evacuation was already started, generate an interruption.
// * 6: If the safety value of one space gets below the allowed safety, it'll be
// * checked on each timestep
// * a: Is it necessary or should we avoid it?
// * i: We can't skip to check it safety value as it might unavailable for
// * everyone (equal to 0) in the next step.
// *
// *
// * 7: Once the evacuation process starts.
// * a: Get the location of each person.
// * b: Find a route for each person from his location to the nearest exit using
// * the latest model.graph composed of only available nodes and edges
// * c: Shortlist and assign the shortest path to the person.
// * 8: Once the routes have been assigned to people. They must start following
// * those routes.
// * 9: Route-traversing strategy has been implemented separately.
// * 10: Once a person traverses his assigned route successfully. His motion
// * status must set to Resting.
// *
// * 11: At any time step, if the safety value of any space gets lower than the
// * allowed capacity of any space, and it doesn't exist in a list data structure.
// * a: Initiate an interruption call.
// *
// *
// * 12: An interruption call checks:
// * a: People whose status is in Moving.
// * b: Check the assign routes of everyone.
// * c: Filter the people who have that space (left in the assigned route) whose
// * safety value got decreased.
// * d: Run 7b until 10.
// *
// * 13: Some persons who are following a path will only be shown upon the
// * completion of each node traversal of their given path.
// * How to reassign them a new path?
// * a: Maybe we can get people using there movement status, i.e., Moving?
// * b: Then getting the current traversing node and perform 12c, and then 12d.
// *
// *
// *
// *
// *
// *
// */
