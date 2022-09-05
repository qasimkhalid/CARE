package helper;

import helper.plans.*;
import model.PersonController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private Map<String, PersonController> personControllerMap;
    private long timestep;

    public EvacuationController (Map<String, PersonController> personControllerMap, long timestep){
        this.personControllerMap = personControllerMap;
        this.timestep = timestep;
    }

    private List<IEvacuationPlan> routeFinderAlgorithms = Arrays.asList(
//            new AStarPlan(),
//            new DFSPlan(),
//            new BFSPlan(),
//            new BestFirstSearch(),
//            new ChannelBasedPlan()
            new DijkstraPlan()
            );

    private IEvacuationPlan currentPlan = null;

    public void start() {
        for (IEvacuationPlan plan : routeFinderAlgorithms) {
            currentPlan = plan;
            EvacuateUsingFinder(plan);
        }
    }

    private void EvacuateUsingFinder(IEvacuationPlan plan) {
        // here do all the rubish and play with finder to evacuate all persons
        plan.Execute();
        while (plan.inProgress(personControllerMap.size())) {

            // wait until next timestep
            plan.Update(timestep);

            // SomelogicToDetectInterrupt();
            // based on a flag if interrupt happens
            plan.Interrupt();

            try {
                long ts = timestep;
                Thread.sleep(ts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
