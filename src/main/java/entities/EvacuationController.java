package entities;

import helper.ConsoleColors;
import helper.HelpingVariables;
import model.CareeCsparqlEngineImpl;
import model.CareeInfModel;
import org.openrdf.query.algebra.Str;
import streamers.SpaceSensorsStreamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller has a set of evacuating algorithms of type IRouteFinder
 * Has a state of all persons in the building including every sensor details
 * like a snapshot of whole building with everything
 * Has a tigger method to start the evacuation
 * Foreach e in evacuating algorithms
 * Set everything to start state
 * Note the start time of evacuation in millis
 * Handle the path finding for all persons in the building
 * Note the end time of all evacuations
 * Generate report
 */

public class EvacuationController implements IEvacuationCallback {
    private final long timestep;
    private final List<PersonController> personControllers;
    private static int evacueesCounter;
    private int iterationCounter = 0;
    private int referenceSavedForThisScenario = 0;
    private String previousScreenBuffer = "";


    public EvacuationController(List<PersonController> personControllers, long timestep, int referenceTimeAlreadyPassed) {
//        this.personControllers = personControllers.subList(1,2); //WheelChair person
        this.personControllers = personControllers;
        this.timestep = timestep;
        evacueesCounter = this.personControllers.size();
        this.iterationCounter = referenceTimeAlreadyPassed;
        this.referenceSavedForThisScenario = referenceTimeAlreadyPassed;
    }

    public void start() {

        System.out.println("Routes are being calculated: " + System.currentTimeMillis());
        for (PersonController pc : personControllers) {
            pc.evacuate(this);
        }
        System.out.println("Routes have been calculated: " + System.currentTimeMillis());

        int count = 0;
        while (inProgress(evacueesCounter)) {

            for (PersonController pc : personControllers) {
                if (pc.isStuck() || pc.isEvacuated()) {
                    continue;
                }

                pc.update(this.timestep);
            }

            try {
                Thread.sleep(this.timestep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            printPersons();
            iterationCounter++;
            count++;
            System.out.println("iteration Reference Time = " +
                    referenceSavedForThisScenario +
                    " + " +
                    count +
                    " = " +
                    iterationCounter);

            // Special case for interrupt generation
            HashMap<Integer, HashMap<Double, List<String>>> interruptGenerationTimeLineMap = new HashMap<>();

            //Pattern == SafetyValueX:SpaceA,SpaceB$SafetyY:SpaceC,SpaceD
            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+3, getLocationSafetyValueMap(
                    "0.4:J1"));
//            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+2, getLocationSafetyValueMap(
//                    "0.1:R4,R4_REr4a,REr4a"));
//            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+3, getLocationSafetyValueMap(
//                    "0.39:RE21_REr4a,RE21,RE21_RE22,RE22,RE22_RE23,RE23"));
//            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+4, getLocationSafetyValueMap(
//                    "0.1:Hall3,RE21_REr4a,RE21,RE21_RE22,RE22,RE22_RE23,RE23"));
//            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+7, getLocationSafetyValueMap(
//                            "0.3:RE23_RE24,J10_RE24"));
//            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+8, getLocationSafetyValueMap(
//                    "0.1:RE23_RE24,J10_RE24"));
//            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+9, getLocationSafetyValueMap(
//                    "0.1:RE24"));
//            interruptGenerationTimeLineMap.put(referenceSavedForThisScenario+13, getLocationSafetyValueMap(
//                    "0.1:J10"));


            if(interruptGenerationTimeLineMap.containsKey(iterationCounter)){
                setRelevantSafetyValuesForRelevantSpaces(interruptGenerationTimeLineMap.get(iterationCounter));
            }


//            if (iterationCounter==referenceSavedForThisScenario+7){
//                SpaceSensorsStreamer.getSpacesInfo().get(HelpingVariables.exPrefix+"REw1").setSafetyValue(0.1);
//            }
//            if (iterationCounter==referenceSavedForThisScenario+15){
//                SpaceSensorsStreamer.getSpacesInfo().get(HelpingVariables.exPrefix+"J1").setSafetyValue(0.1);
//            }
//            if (iterationCounter==referenceSavedForThisScenario+24){
//                SpaceSensorsStreamer.getSpacesInfo().get(HelpingVariables.exPrefix+"J2").setSafetyValue(0.1);
//            }
//            if (iterationCounter==referenceSavedForThisScenario+30){
//                SpaceSensorsStreamer.getSpacesInfo().get(HelpingVariables.exPrefix+"J11").setSafetyValue(0.1);
//            }
        }
        closeApplication();
    }

    private void printPersons() {
        StringBuilder newScreenBuffer = new StringBuilder();
        for (PersonController p : personControllers) {
            // name
            newScreenBuffer.append(ConsoleColors.CYAN_BOLD).append(p.getPerson().getReadableName()).append(ConsoleColors.RESET);
            String location = p.getPerson().getReadableLocation();
            String interrupt = p.getLastInterrupt();

            // path
            newScreenBuffer.append(" => [ ");

            List<String> route = p.getAssignedRoute();
            // Added a new feature to mention a person is stuck at the beginning, and no route can be found for him/her.
            if(route==null){
                if(p.isStuck()){
                    newScreenBuffer.append(ConsoleColors.RED_BOLD).append(location).append(" ").append(ConsoleColors.RESET);
                } else {
                    System.out.println("Problem in Route Finding of Person " + p.getPerson().getReadableName() + " at Location " + p.getPerson().getReadableLocation());
                }
            }
            else {
                for(int i = 0; i < route.size(); i++) {
                    String l = route.get(i).split("#")[1];

                    if (l.equals(interrupt)) {
                        newScreenBuffer.append(ConsoleColors.RED_BOLD).append(l).append(ConsoleColors.RESET);
                    } else if (l.equals(location)) {
                        newScreenBuffer.append(ConsoleColors.GREEN_BOLD).append(l).append(ConsoleColors.RESET);
                    } else {
                        newScreenBuffer.append(l).append(ConsoleColors.RESET);
                    }

                    if (i == route.size()-1) {
                        newScreenBuffer.append(" ").append(ConsoleColors.RESET);
                    } else {
                        newScreenBuffer.append(", ").append(ConsoleColors.RESET);
                    }
                }
            }
            newScreenBuffer.append("]");

            // evacuation status
            if (p.isEvacuated()) {
                newScreenBuffer.append(ConsoleColors.GREEN_BOLD).append(" \u2713").append(ConsoleColors.RESET);
            } else if (p.isStuck()) {
                newScreenBuffer.append(ConsoleColors.RED_BOLD).append(" \u2715").append(ConsoleColors.RESET);
            }

            newScreenBuffer.append("\n\r");
        }

        if (!previousScreenBuffer.equals(String.valueOf(newScreenBuffer))) {
            System.out.println(newScreenBuffer);
            previousScreenBuffer = String.valueOf(newScreenBuffer);
        }
    }

    private boolean inProgress(int totalPersons) {
        // a query that returns all the persons located in the building other than the exits
        List<String> personsWhoHaveNotEvacuated = CareeInfModel.Instance()
                .getQueryResult("data/Queries/sparql/PersonsWhoHaveEvacuated.txt");
        return personsWhoHaveNotEvacuated.size() != totalPersons;
    }

    public static void closeApplication() {
        System.out.println("SpaceSensorsStreamer.stop()");
        SpaceSensorsStreamer.stop();

        System.out.println("Successfully unregistered Space Sensors Stream from the engine");
        CareeCsparqlEngineImpl.Instance().unregisterStream(SpaceSensorsStreamer.getStreamIRI());

        System.out.println("About to exit");
        System.exit(0);
    }

    @Override
    public void evacuationEnded(PersonController personController) {
        evacueesCounter -= 1;
    }



    public HashMap<Double, List<String>> getLocationSafetyValueMap(String safetyValAndSpaceName){
        HashMap<Double, List<String>> locationSafetyValueMap = new HashMap<>();
        String[] allOccurrences = safetyValAndSpaceName.split("&");
        for (String occurrence: allOccurrences) {
            String[] safetyValArr = occurrence.split(":");
            Double safetyVal = Double.parseDouble(safetyValArr[0].trim());
            String spaces = safetyValArr[1].trim();
            List<String> allSpaces = Arrays.stream(spaces.split(","))
                    .map(String::trim)
                    .map(s -> HelpingVariables.exPrefix + s)
                    .collect(Collectors.toList());
            locationSafetyValueMap.put(safetyVal, allSpaces);
        }
        return locationSafetyValueMap;
    }

    public void setRelevantSafetyValuesForRelevantSpaces(HashMap<Double, List<String>> hashMap) {
        for (Double key : hashMap.keySet()) {
            Double safetyVal = key;
            List<String> values = hashMap.get(key);
            for (String value : values) {
                String space = value;
                SpaceSensorsStreamer.getSpacesInfo().get(value).setSafetyValue(key);
                //Debugging
                System.out.println("Safety value of "+ space.split("#")[1] + " becomes " + safetyVal );
            }
        }
    }



}
