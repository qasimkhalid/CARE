import java.util.ArrayList;
import java.util.Arrays;
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

    final static private int SEED = 12;

    private List<IEvacuationPlan> routeFinderAlgorithms = Arrays.asList(
            new AStarPlan(),
            new DFSPlan(),
            new BFSPlan());
    private IEvacuationPlan currentPlan = null;

    public void Start() {
        for (IEvacuationPlan plan : routeFinderAlgorithms) {
            currentPlan = plan;
            // note the current system time here
            EvacuateUsingFinder(plan);
            // note the current system time here and
            // compare it with previous time to get the actual time taken by this plan
            ReportTime(plan);
        }
    }

    private void ReportTime(IEvacuationPlan plan) {
        // generate time
    }

    private void EvacuateUsingFinder(IEvacuationPlan plan) {
        // here do all the rubish and play with finder to evacuate all persons
        plan.Execute();
        while (plan.inProgress()) {
            // wait until next timestep
            plan.Update(0);

            // SomelogicToDetectInterrupt();
            // based on a flag if interrupt happens
            plan.Interrupt();
        }
    }
}
