package model;

import entities.TimeStepListener;
import model.graph.Edge;
import streamers.SpaceSensorsStreamer;

import java.util.ArrayList;
import java.util.List;

public abstract class PathTraversal {

    private List<String> path = new ArrayList<>();
    private Edge currentEdge;
    private long cumulativeEdgeTraversalTime = 0;
    private float timeElapsed = 0;
    private int index = 0; // index shows current edge's destination node's index A-B-C
    private String person; //testing purposes

    public abstract void onEdgeTraversed(String origin, String destination);

    public abstract boolean isNodeSafe(String node);

    public abstract boolean isPathSafe(List<String> remainingPath);

    public abstract void onPathInterrupt(String interruptedSpace);

    public abstract void onPathComplete();

    public abstract void onRouteNotPossible();

    public TimeStepListener listener = new TimeStepListener() {

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
                    onEdgeTraversed(currentEdge.getOrigin(), currentEdge.getDestination());
                    index++;
                    if (hasNextNode()) {

                        // Creating an edge of the current node and the next node.
                        currentEdge = new Edge(path.get(index), path.get(index + 1));

                        //                    // Check if the NEW destination node is safe for this person.
                        //                    if (!isNodeSafe(currentEdge.getDestination())) {
                        //                        onPathInterrupt(currentEdge.getDestination());
                        //                    } else {

                        // *** Testing Block Start ***
                        System.out.println(person
                                + " Traversing from "
                                + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getOrigin()).getReadableName()
                                + " to "
                                + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getDestination()).getReadableName());
                        // *** Testing Block End ***

                        cumulativeEdgeTraversalTime += currentEdge.getCost();
                        //                    }
                    } else {
                        onPathComplete();
                    }
                }

                timeElapsed += timeStep; // time stepping for testing purposes.
//                timeElapsed += System.currentTimeMillis(); //Real time stepping

            }

        }
    };

    public void SetPath(String person, List<String> path) {
        this.path = path;
        this.person = person;
        index = 0;
        timeElapsed = 0;


        if (!path.isEmpty()) {

            // *** Testing Block Start ***
            System.out.println(person + " started to follow the provided route" );
            // *** Testing Block End ***

            if (hasNextNode()) {
                currentEdge = new Edge(path.get(index), path.get(index + 1));
                cumulativeEdgeTraversalTime = currentEdge.getCost();

                // *** Testing Block Start ***
                System.out.println(person
                        + " Traversing from "
                        + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getOrigin()).getReadableName()
                        + " to "
                        + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getDestination()).getReadableName());
                // *** Testing Block End ***

            } else { // if the person is already located at one of the exits.
                onPathComplete();
            }
        }
    }

    private boolean hasNextNode() {
        return this.path.size() - 1 > this.index;
    }
}
