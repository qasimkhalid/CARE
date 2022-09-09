package helper;

import model.CareeInfModel;
import model.PersonController;

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

public class EvacuationController {
    private final long timestep;
    private final List<PersonController> personControllers;
    public static int evacueesCounter;
    private int iterationCounter = 0;


    public EvacuationController(List<PersonController> personControllers, long timestep) {
//        this.personControllers = personControllers.subList(2,3); //WheelChair person
        this.personControllers = personControllers.subList(3,5); // Normal person
//        this.personControllers = personControllers;
        this.timestep = timestep;
        evacueesCounter = this.personControllers.size();
    }

    public void start() {

        // Creating a Graph from the building data
        RouteFinder.initializeGraph();

        for (PersonController pc : personControllers) {
            System.out.println(pc.getReadableName() + " in " + pc.getPerson().getLocation().split("#")[1]);
            pc.evacuate();
        }

//        while (inProgress(evacueesCounter)) {
        while (true) {
            EventTimer.Instance().doTimeStep(this.timestep);

            // Printing the Location of persons (Fix it)
//            EvacuationStreamer.detectPersonLocationUsingIdQuadrupleGenerator();
            System.out.println("--------------------" + iterationCounter + "---------------------------");

            //for (PersonController pc : personControllers) {
            //    System.out.println(pc.getReadableName() + " in " + pc.getPerson().getLocation().split("#")[1]);
            //}
            //System.out.println("-----------------------------------------------");

            try {
                Thread.sleep(this.timestep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            iterationCounter++;
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
