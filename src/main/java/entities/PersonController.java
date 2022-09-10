package entities;

import java.util.*;

import model.graph.IEdgeAccessibility;
import model.graph.INodeAccessibility;
import helper.*;
import model.PathTraversal;
import model.Person;
import model.Route;
import operations.AutomatedOperations;
import operations.CommonOperations;
import streamers.SpaceSensorsStreamer;

public class PersonController implements INodeAccessibility, IEdgeAccessibility {

    private final Person person;
    private float allowedSafetyValue;

    private final PathTraversal pathTraversalListener = new PathTraversal() {
        @Override
        public void onEdgeTraversed(String personOldLocation, String personNewLocation) {
            // update destination of this person with new destination where he has landed!
            AutomatedOperations.updatePersonLocationOnSuccessfulPathTraversal(getName(), personOldLocation, personNewLocation);
            person.setLocation(personNewLocation);

            System.out.println(getReadableName() + " Successfully Traversed an Edge from: " +
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

        }

        @Override
        public void onPathInterrupt(String interruptedSpace) {
            /*
            * We don't update the location of a person if there is an interruption in the path.
            * Here we assume that while following the path, if there is an interruption in his path,
            * The person has to start to follow another path from last node he left.
            */
//            AutomatedOperations.updatePersonLocationOnCompletePathTraversal(getName(), origin, destination);

            // *** Testing Block Start ***
            // Printing the Details of the Interrupted Space
            System.out.println(SpaceSensorsStreamer.getSpacesInfo().get(interruptedSpace).getReadableName()
                    + " got inaccessible for "
                    + getReadableName()
                    + " with Space Safety Value = "
                    + SpaceSensorsStreamer.getSpacesInfo().get(interruptedSpace).getSafetyValue());
            // *** Testing Block End ***

//            // Remove listener on path traversal which will stop onTimeStep method calls
            EventTimer.Instance().removeTimeStepListener(pathTraversalListener.listener);

            // Evacuation will be started from the previous checkpoint node of the person using a new calculated path.
            evacuate();
        }

        @Override
        public boolean isNodeSafe(String node) {
            return SpaceSensorsStreamer.getSpacesInfo().get(node).getSafetyValue() > allowedSafetyValue;
        }

        @Override
        public boolean isPathSafe(List<String> remainingPath) {
            // It checks both nodes and edges

            boolean safe;

            for (String s : remainingPath) {
                safe = isNodeAccessible(s);
                if (!safe) {
                    onPathInterrupt(s);
                    return false;
                }
            }

            for (int i = 0; i < remainingPath.size()-1; i++) {
                String origin = remainingPath.get(i);
                String destination = remainingPath.get(i+1);
                safe = isEdgeAccessible(origin, destination);
                if (!safe){
                    onPathInterrupt(CommonOperations.createEdge(origin, destination));
                    return false;
                }
            }
            return true;
        }
    };

    public PersonController(Person person, float allowedSafetyValue) {
        this.person = person;
        this.allowedSafetyValue = allowedSafetyValue;
    }

    public void evacuate() {
        List<String> route = findRoute();

        // *** Debugging Block Start***

        if (route != null) {
            List<String> routeForDebuggingPurposed = new ArrayList<>();
            for (String s: route) {
                routeForDebuggingPurposed.add(s.split("#")[1]);
            }
            System.out.println(getReadableName() + " Route : " + Arrays.toString(routeForDebuggingPurposed.toArray()));
        } else {
            System.out.println(getReadableName() + " cannot evacuate the building.");
            // *** Debugging Block End***
//
//            // Remove listener on path traversal which will stop onTimeStep method calls
//            EventTimer.Instance().removeTimeStepListener(pathTraversalListener.listener);

        }
        this.followRoute(route);
    }

    private List<String> findRoute() {

        String personLocation = person.getLocation();
        List<String> availableExits = getAvailableExits();

        Route minRoute = null;
        for (String exit : availableExits) {

            // Check if Space Safety of the person's location is greater than the allowed safety value.
            // Otherwise, the route is not provided to the person.
            if(SpaceSensorsStreamer.getSpacesInfo().get(personLocation).getSafetyValue() > allowedSafetyValue){
                Route route = RouteFinder.findPath(personLocation, exit, this, this);
                if (!route.getPath().isEmpty() && (minRoute == null || minRoute.getCost() > route.getCost())) {
                    minRoute = route;
                }
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

        pathTraversalListener.SetPath(getReadableName(), routeAssigned);

////        // Adds listener on path traversal for timeStep
        EventTimer.Instance().addTimeStepListener(this.pathTraversalListener.listener);
        EventTimer.Instance().updateTimeStepListener(this.pathTraversalListener.listener);

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


    @Override
    public boolean isNodeAccessible(String nodeName) {
        return SpaceSensorsStreamer.getSpacesInfo().get(nodeName).getSafetyValue() > allowedSafetyValue;
    }


    @Override
    public boolean isEdgeAccessible(String origin, String destination) {
        if (SpaceSensorsStreamer.getSpacesInfo().containsKey(CommonOperations.createEdge(origin, destination))){
            return SpaceSensorsStreamer.getSpacesInfo().get(CommonOperations.createEdge(origin, destination)).getSafetyValue() > allowedSafetyValue;
        } else {
            return SpaceSensorsStreamer.getSpacesInfo().get(CommonOperations.createEdge(destination, origin)).getSafetyValue() > allowedSafetyValue;
        }
    }
}
