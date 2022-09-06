package model;

import java.util.List;

import helper.AutomatedOperations;
import helper.EventTimer;

public class PersonController {

    private final Person person;
    private float allowedSafetyValue = 0.5f;

    private PathTraversal pathTraversalListener = new PathTraversal() {
        @Override
        public void onEdgeTraversed(String newLocation) {
            // update destination of this person with new destination where he has landed!
            person.setLocation(newLocation);
            AutomatedOperations.updatePersonLocation(getName(), person.getLocation());
        }

        @Override
        public void onPathComplete() {
            // update destination of person with personId -> as he
            // has completed the path

            AutomatedOperations.updateModelWhenPersonCompletesPath(getName());

            // Remove listener on path traversal which will stop onTimeStep method calls
            EventTimer.Instance().removeTimeStepListener(pathTraversalListener.listener);
        }

        @Override
        public void onPathInterrupt() {
            // Remove listener on path traversal which will stop onTimeStep method calls
            EventTimer.Instance().removeTimeStepListener(pathTraversalListener.listener);

            // Evaluate will start path finding from the begining
            evacuate();
        }

        @Override
        public boolean isNodeSafe(String node) {
            return false;
        }
    };

    public PersonController(Person person, float allowedSafetyValue) {
        this.person = person;
        this.allowedSafetyValue = allowedSafetyValue;
    }

    public void evacuate() {
        List<String> route = findRoute();
        this.followRoute(route);
    }

    private List<String> findRoute() {
        // find route, write algorithm that will return a path in the form of
        // list<string>
        return null;
    }

    public void followRoute(List<String> routeAssigned) {
        pathTraversalListener.SetPath(routeAssigned);
        // Adds listener on path traversal for timeStep
        EventTimer.Instance().addTimeStepListener(this.pathTraversalListener.listener);
    }

    public Person getPerson() {
        return person;
    }

    public String getName() {
        return this.person.getName();
    }
}
