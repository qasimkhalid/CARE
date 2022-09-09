package model;

import java.util.*;

import graph.INodeAccessibility;
import helper.AutomatedOperations;
import helper.EvacuationController;
import helper.EventTimer;
import helper.RouteFinder;
import streamers.SpaceSensorsStreamer;

public class PersonController implements INodeAccessibility {

    private final Person person;
    private float allowedSafetyValue;

    private final PathTraversal pathTraversalListener = new PathTraversal() {
        @Override
        public void onEdgeTraversed(String personOldLocation, String personNewLocation) {
            // update destination of this person with new destination where he has landed!
            AutomatedOperations.updatePersonLocationOnSuccessfulPathTraversal(getName(), personOldLocation, personNewLocation);
            person.setLocation(personNewLocation);

            System.out.println(getReadableName() + " Path Traversal: " +
                    SpaceSensorsStreamer.getSpacesInfo().get(personOldLocation).getReadableName() +
                    " --> " +
                    SpaceSensorsStreamer.getSpacesInfo().get(personNewLocation).getReadableName());
        }

        @Override
        public void onPathComplete() {
            // update destination of person with personId -> as he
            // has completed the path

            AutomatedOperations.updateModelWhenPersonCompletesPath(getName());

            // Remove listener on path traversal which will stop onTimeStep method calls
            EventTimer.Instance().removeTimeStepListener(pathTraversalListener.listener);

            System.out.println(getReadableName() + " has successfully evacuated.");
        }

        @Override
        public void onRouteNotPossible() {
            // Excluding the person from the expected evacuated persons' list.
            EvacuationController.evacueesCounter -= 1;
            int x = 0;

        }

        @Override
        public void onPathInterrupt() {
            /*
            * We don't update the location of a person if there is an interruption in the path.
            * Here we assume that while following the path, if there is an interruption in his path,
            * The person has to start to follow another path from last node he left.
            */
//            AutomatedOperations.updatePersonLocationOnCompletePathTraversal(getName(), origin, destination);

            // Remove listener on path traversal which will stop onTimeStep method calls
            EventTimer.Instance().removeTimeStepListener(pathTraversalListener.listener);

            // Evacuation will be started from the previous checkpoint node of the person using a new calculated path.
            evacuate();
        }

        @Override
        public boolean isNodeSafe(String node) {
            return SpaceSensorsStreamer.getSpacesInfo().get(node).getSafetyValue() > allowedSafetyValue;
        }
    };

    public PersonController(Person person, float allowedSafetyValue) {
        this.person = person;
        this.allowedSafetyValue = allowedSafetyValue;
    }

    public void evacuate() {
        List<String> route = findRoute();

        if (route != null) {
//            for (String str : route) {
//                System.out.println(getReadableName() + " in " + person.getLocation().split("#")[1]);
//            }
            System.out.println(getReadableName() + " has " + Arrays.toString(route.toArray()));
        } else {
            System.out.println(getReadableName() + " cannot evacuate the building.");


        }
        this.followRoute(route);
    }

    @Override
    public boolean isNodeAccessible(String nodeName) {
        return SpaceSensorsStreamer.getSpacesInfo().get(nodeName).getSafetyValue() > allowedSafetyValue;
    }

    private List<String> findRoute() {

        String personLocation = person.getLocation();
        List<String> availableExits = getAvailableExits();

        Route minRoute = null;
        for (String exit : availableExits) {
            Route route = RouteFinder.findPath(personLocation, exit, this);
            if (minRoute == null || minRoute.getCost() > route.getCost()) {
                minRoute = route;
            }
        }

        return minRoute != null ? minRoute.getPath() : null;
    }

    private List<String> getAvailableExits() {
        List<String> exits = AutomatedOperations.getExits();
        List<String> availableExits = new ArrayList<>();

        // Check every exit in space streamer
        for (String exit : exits) {
            if (SpaceSensorsStreamer.getSpacesInfo().get(exit).getSafetyValue() > allowedSafetyValue){
                availableExits.add(exit);
            }
        }

        return availableExits;
    }

    public void followRoute(List<String> routeAssigned) {
        if (routeAssigned == null) {
            pathTraversalListener.onRouteNotPossible();
            return;
        }

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

    public float getAllowedSafetyValue() {
        return allowedSafetyValue;
    }

    public String getReadableName() {
        return person.getName().split("#")[1];
    }

    public void setAllowedSafetyValue(float allowedSafetyValue) {
        this.allowedSafetyValue = allowedSafetyValue;
    }
}
