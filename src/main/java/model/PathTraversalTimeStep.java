package model;

import entities.EventTimer;
import entities.ITimeStepListener;
import model.graph.Edge;
import model.graph.IAccessibility;
import operations.CommonOperations;
import streamers.SpaceSensorsStreamer;

import java.util.ArrayList;
import java.util.List;

public class PathTraversalTimeStep implements ITimeStepListener {

    private List<String> path = new ArrayList<>();
    private Edge currentEdge;
    private long cumulativeEdgeTraversalTime = 0;
    private float timeElapsed = 0;
    private int index = 0; // index shows current edge's destination node's index A-B-C
    private Person person; //testing purposes
    private IAccessibility accessibility;
    private IPathTraversal pathTraversal;

    /**
     * Set path will assign the route and start the traversal of person on the path.
     * @param person person who is traversing the path
     * @param path path the person should follow
     * @param accessibility accessibility interface for checking accessibility of nodes and edges
     * @param pathTraversal pathTraversal interface for path related operations
     */
    public void setPath(Person person, List<String> path, IAccessibility accessibility, IPathTraversal pathTraversal) {
        this.path = path;
        this.accessibility = accessibility;
        this.pathTraversal = pathTraversal;
        this.person = person;
        this.index = 0;
        this.timeElapsed = 0;

////        // Adds listener on path traversal for timeStep
        EventTimer.Instance().addTimeStepListener(this);
        EventTimer.Instance().updateTimeStepListener(this);

        if (!path.isEmpty()) {

            // *** Testing Block Start ***
            System.out.println(person.getReadableName() + " started to follow the provided route.");
            // *** Testing Block End ***

            if (hasNextNode()) {
                currentEdge = new Edge(path.get(index), path.get(index + 1));
                cumulativeEdgeTraversalTime = currentEdge.getCost();

                // *** Testing Block Start ***
                System.out.println(person.getReadableName()
                        + " Traversing from "
                        + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getOrigin()).getReadableName()
                        + " to "
                        + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getDestination()).getReadableName());
                // *** Testing Block End ***

            } else { // if the person is already located at one of the exits.
                pathTraversal.onPathComplete();
            }
        }
    }

    @Override
    public void onTimeStep(long timeStep) {

        if (currentEdge == null) {
            return;
        }

        // Keep checking the Remaining path after every timestep that whether it is safe or not
        if(isPathSafe(path.subList(index, path.size()))) {

            // If the time has passed more than time required to traverse the edge (i.e., cost in our case).
            // Otherwise, do nothing.
            if (timeElapsed >= cumulativeEdgeTraversalTime) {
                pathTraversal.onEdgeTraversed(currentEdge.getOrigin(), currentEdge.getDestination());
                index++;
                if (hasNextNode()) {

                    // Creating an edge of the current node and the next node.
                    currentEdge = new Edge(path.get(index), path.get(index + 1));

                    //                    // Check if the NEW destination node is safe for this person.
                    //                    if (!isNodeSafe(currentEdge.getDestination())) {
                    //                        onPathInterrupt(currentEdge.getDestination());
                    //                    } else {

                    // *** Testing Block Start ***
                    System.out.println(person.getReadableName()
                            + " Traversing from "
                            + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getOrigin()).getReadableName()
                            + " to "
                            + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getDestination()).getReadableName());
                    // *** Testing Block End ***

                    cumulativeEdgeTraversalTime += currentEdge.getCost();
                    //                    }
                } else {
                    pathTraversal.onPathComplete();
                }
            }
            timeElapsed += timeStep; // time stepping for testing purposes.
//                timeElapsed += System.currentTimeMillis(); //Real time stepping
        }

    }

    private boolean isPathSafe(List<String> remainingPath) {
        // It checks both nodes and edges
        boolean safe;

        for (String s : remainingPath) {
            safe = accessibility.isNodeAccessible(s);
            if (!safe) {
                pathTraversal.onPathInterrupt(s);
                return false;
            }
        }

        for (int i = 0; i < remainingPath.size()-1; i++) {
            String origin = remainingPath.get(i);
            String destination = remainingPath.get(i+1);
            safe = accessibility.isEdgeAccessible(origin, destination);
            if (!safe){
                pathTraversal.onPathInterrupt(CommonOperations.createEdge(origin, destination));
                return false;
            }
        }
        return true;
    }

    private boolean hasNextNode() {
        return this.path.size() - 1 > this.index;
    }
}
