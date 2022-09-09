package helper;

import graph.Graph;
import model.CareeCsparqlEngineImpl;
import model.CareeInfModel;
import model.PersonController;
import streamers.EvacuationStreamer;
import streamers.SpaceSensorsStreamer;

import java.util.List;
import java.util.function.Consumer;

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

public class EvacuationController {
    private final long timestep;
    private final List<PersonController> personControllers;

    public EvacuationController(List<PersonController> personControllers, long timestep) {
        this.personControllers = personControllers.subList(0,1);
        this.timestep = timestep;
    }

    public void start() {

        RouteFinder.initializeGraph();

        for (PersonController pc : personControllers) {
            System.out.println("Person " + pc.getName() + " in Room " + pc.getPerson().getLocation());
            pc.evacuate();
        }

        while (true) {
            EventTimer.Instance().doTimeStep(this.timestep);

            // Printing the Location of persons (Fix it)
//            EvacuationStreamer.detectPersonLocationUsingIdQuadrupleGenerator();
            System.out.println("-----------------------------------------------");

            for (PersonController pc : personControllers) {
                System.out.println("Person " + pc.getName() + " in Room " + pc.getPerson().getLocation());
            }
            System.out.println("-----------------------------------------------");

            try {
                Thread.sleep(this.timestep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            EventTimer.Instance().doPostTimeStep(this.timestep);
        }
    }

    private boolean inProgress(int totalPersons) {
        // a query that returns all the persons located in the building other than the
        // exits
        /* e.g. return BuildingUtils.hasAnyPerson(); */
        List<String> personsWhoHaveNotEvacuated = CareeInfModel.Instance()
                .getQueryResult("data/Queries/sparql/PersonsWhoHaveEvacuated.txt");
        return personsWhoHaveNotEvacuated.size() != totalPersons;
    }
}
