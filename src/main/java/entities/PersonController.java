package entities;

import java.util.*;

import model.*;
import model.graph.IAccessibility;
import helper.*;
import operations.AutomatedOperations;
import operations.CommonOperations;
import streamers.SpaceSensorsStreamer;

public class PersonController implements IAccessibility, IPathTraversal {

    private final Person person;
    private float allowedSafetyValue;
    private final PathTraversalTimeStep pathTraversalTimeStep = new PathTraversalTimeStep();
    private List<String> assignedRoute;
    private boolean evacuated = false;

    public PersonController(Person person, float allowedSafetyValue) {
        this.person = person;
        this.person.setAllowedSafetyValue(allowedSafetyValue);
    }

    public void setAllowedSafetyValue(float allowedSafetyValue) {
        this.allowedSafetyValue = allowedSafetyValue;
    }

    public Person getPerson() {
        return person;
    }

    /* Imp IAccessibility interface */
    @Override
    public boolean isNodeAccessible(String nodeName) {
        return SpaceSensorsStreamer.getSpacesInfo().get(nodeName).getSafetyValue() > person.getAllowedSafetyValue();
    }

    @Override
    public boolean isEdgeAccessible(String origin, String destination) {
        if (SpaceSensorsStreamer.getSpacesInfo().containsKey(CommonOperations.createEdge(origin, destination))){
            return SpaceSensorsStreamer.getSpacesInfo().get(CommonOperations.createEdge(origin, destination)).getSafetyValue() > person.getAllowedSafetyValue();
        } else {
            return SpaceSensorsStreamer.getSpacesInfo().get(CommonOperations.createEdge(destination, origin)).getSafetyValue() > person.getAllowedSafetyValue();
        }
    }

    /* Imp IPathTraversal interface */
    @Override
    public void onEdgeTraversed(String personOldLocation, String personNewLocation) {
        // update destination of this person with new destination where he has landed!
        AutomatedOperations.updatePersonLocationOnSuccessfulPathTraversal(person.getName(), personOldLocation, personNewLocation);
        person.setLocation(personNewLocation);

//        System.out.println(person.getReadableName() + " Successfully Traversed an Edge from: " +
//                SpaceSensorsStreamer.getSpacesInfo().get(personOldLocation).getReadableName() +
//                " --> " +
//                SpaceSensorsStreamer.getSpacesInfo().get(personNewLocation).getReadableName());
    }

    @Override
    public void onPathComplete() {
        // Remove listener on path traversal which will stop onTimeStep method calls
        EventTimer.Instance().removeTimeStepListener(pathTraversalTimeStep);

        AutomatedOperations.updateModelWhenPersonCompletesPath(person.getName());

        //System.out.println(person.getReadableName() + " has successfully evacuated.");
        evacuated = true;
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
                + person.getReadableName()
                + " with Space Safety Value = "
                + SpaceSensorsStreamer.getSpacesInfo().get(interruptedSpace).getSafetyValue());
        // *** Testing Block End ***

//            // Remove listener on path traversal which will stop onTimeStep method calls
        EventTimer.Instance().removeTimeStepListener(pathTraversalTimeStep);

        // Evacuation will be started from the previous checkpoint node of the person using a new calculated path.
        evacuate();
    }
    /* ----------------------------------- */

    public void evacuate() {
        List<String> route = findRoute();

        // *** Debugging Block Start***

        if (route != null) {
            List<String> routeForDebuggingPurposed = new ArrayList<>();
            for (String s: route) {
                routeForDebuggingPurposed.add(s.split("#")[1]);
            }
            System.out.println(person.getReadableName() + " Route : " + Arrays.toString(routeForDebuggingPurposed.toArray()));
        } else {
            System.out.println(person.getReadableName() + " cannot evacuate the building.");
            // *** Debugging Block End***
//
//            // Remove listener on path traversal which will stop onTimeStep method calls
//            EventTimer.Instance().removeTimeStepListener(pathTraversalListener.listener);

        }
        this.followRoute(route);
    }

    public void followRoute(List<String> routeAssigned) {
        if (routeAssigned == null) {
            // Route not possible
            EvacuationController.evacueesCounter -= 1;
            return;
        }
        this.assignedRoute = routeAssigned;
        pathTraversalTimeStep.setPath(person, routeAssigned, this, this);
    }

    private List<String> findRoute() {

        String personLocation = person.getLocation();
        List<String> availableExits = getAvailableExits();

        Route minRoute = null;
        for (String exit : availableExits) {

            // Check if Space Safety of the person's location is greater than the allowed safety value.
            // Otherwise, the route is not provided to the person.
            if(SpaceSensorsStreamer.getSpacesInfo().get(personLocation).getSafetyValue() > allowedSafetyValue){
                Route route = RouteFinder.findPath(personLocation, exit, this);
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

    public List<String> getAssignedRoute() {
        return this.assignedRoute;
    }

    public boolean isEvacuated() {
        return this.evacuated;
    }
}
