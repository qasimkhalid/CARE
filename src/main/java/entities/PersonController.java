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
    private String interruptedNode = null;
    private boolean isStuck = false;
    private boolean isInterrupt = false;
    private IEvacuationCallback evacuationCallback;

    public PersonController(Person person, float allowedSafetyValue) {
        this.person = person;
        this.person.setAllowedSafetyValue(allowedSafetyValue);
        this.allowedSafetyValue = allowedSafetyValue;
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
        AutomatedOperations.updateModelWhenPersonTraversesPathSuccessfully(person.getName(), personOldLocation, personNewLocation);
        person.setLocation(personNewLocation);
    }

    @Override
    public void onPathComplete() {
        AutomatedOperations.updateModelWhenPersonCompletesPath(person.getName());
        evacuated = true;
    }

    @Override
    public void onPathInterrupt(String interruptedSpace) {
        /*
         * We don't update the location of a person if there is an interruption in the path.
         * Here we assume that while following the path, if there is an interruption in his path,
         * The person has to start to follow another path from last node he left.
         */
        isInterrupt = true;
        interruptedNode = interruptedSpace.split("#")[1];

        if (person.getReadableLocation().equals(interruptedNode)) {
            isStuck = true;
            evacuationCallback.evacuationEnded(this);
        }
    }
    /* ----------------------------------- */

    public void evacuate(IEvacuationCallback evacuationCallback) {
        this.evacuationCallback = evacuationCallback;
        this.isInterrupt = false;

        List<String> route = findRoute();

        if (route != null) {
            this.followRoute(route);
        } else {
            isStuck = true;
            evacuationCallback.evacuationEnded(this);
        }


        AutomatedOperations.updateModelWhenARouteIsAssignedToPerson(person.getName(), route);
    }

    public void followRoute(List<String> routeAssigned) {
        this.assignedRoute = routeAssigned;
        pathTraversalTimeStep.setPath(routeAssigned, this, this);
    }

    private List<String> findRoute() {

        String personLocation = person.getLocation();
        List<String> availableExits = getAvailableExits();

        Route minRoute = null;
        long timeInitial = System.currentTimeMillis();
        System.out.println("Route Calculation Started for " + person.getReadableName() + ":" + timeInitial);
        for (String exit : availableExits) {
            // Check if Space Safety of the person's location is greater than the allowed safety value.
            // Otherwise, the route is not provided to the person.
            if(SpaceSensorsStreamer.getSpacesInfo().get(personLocation).getSafetyValue() > allowedSafetyValue){
                Route route = RouteFinder.findPath(personLocation, exit, this, allowedSafetyValue);
                if (!route.getPath().isEmpty() && (minRoute == null || minRoute.getCost() > route.getCost())) {
                    minRoute = route;
                }
            }
        }
        System.out.println("Route Calculation Ended for " + person.getReadableName() + ":" + Math.subtractExact(System.currentTimeMillis(),timeInitial));
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

    public String getLastInterrupt() {
        return this.interruptedNode;
    }

    public void update(long timeStep) {
        if (isInterrupt) {

            // If there is an interrupt, i.e., safety value reduces from the allowed safety value of a specific type,
            // the graph for that type is removed. As a result, a new graph will be created for them.
            RouteFinder.multipleNodeMaps.remove(allowedSafetyValue);

            // Evacuation is started again
            evacuate(evacuationCallback);
            return; // returns the flow and next person is called
        }
        pathTraversalTimeStep.onTimeStep(timeStep);
    }

    public boolean isStuck() {
        return isStuck;
    }
}
