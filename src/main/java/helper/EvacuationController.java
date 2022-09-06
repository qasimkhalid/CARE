package helper;

import model.PersonController;

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
    private long timestep;
    private List<PersonController> personControllers;

    public EvacuationController(List<PersonController> personControllers, long timestep) {
        this.personControllers = personControllers;
        this.timestep = timestep;
    }

    public void start() throws InterruptedException {

        this.personControllers.forEach(p -> p.evacuate());

        while (inProgress()) {
            EventTimer.Instance().doTimeStep(this.timestep);
            Thread.sleep(this.timestep);
            EventTimer.Instance().doPostTimeStep(this.timestep);
        }
    }

    private boolean inProgress() {
        // Todo: put the logic here for checking in progress logic
        return false;
    }
}
