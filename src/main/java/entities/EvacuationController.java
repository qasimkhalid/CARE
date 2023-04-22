package entities;

import helper.ConsoleColors;
import helper.HelpingVariables;
import model.CareeCsparqlEngineImpl;
import model.CareeInfModel;
import streamers.SpaceSensorsStreamer;

import java.util.ArrayList;
import java.util.List;

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

        for (PersonController pc : personControllers) {
            pc.evacuate(this);
        }

        while (inProgress(evacueesCounter)) {

            for (PersonController pc : personControllers) {
                if (pc.isStuck() || pc.isEvacuated())
                    continue;

                pc.update(this.timestep);
            }

            try {
                Thread.sleep(this.timestep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            printPersons();
            iterationCounter++;
            System.out.println("iteration Reference Time= " + iterationCounter);
            // special case for interrupt generation
            if (iterationCounter==referenceSavedForThisScenario+47){
                SpaceSensorsStreamer.getSpacesInfo().get(HelpingVariables.exPrefix+"OE3").setSafetyValue(0.1);
            }
            if (iterationCounter==referenceSavedForThisScenario+60){
                SpaceSensorsStreamer.getSpacesInfo().get(HelpingVariables.exPrefix+"J1").setSafetyValue(0.1);
            }
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
}
