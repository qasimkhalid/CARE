package model;

import helper.TimeStepListener;
import streamers.SpaceSensorsStreamer;

import java.util.ArrayList;
import java.util.List;

public abstract class PathTraversal {

    private List<String> path = new ArrayList<>();
    private Edge currentEdge;
    private long cumulativeEdgeTraversalTime = 0;
    private float timeElapsed = 0;
    private int index = 0; // index shows current edge's destination node's index A-B-C

    public abstract void onEdgeTraversed(String origin, String destination);

    public abstract boolean isNodeSafe(String string);

    public abstract void onPathInterrupt();

    public abstract void onPathComplete();

    public abstract void onRouteNotPossible();

    public TimeStepListener listener = new TimeStepListener() {

        @Override
        public void onTimeStep(long timeStep) {

            if (currentEdge == null)
                return;

            timeElapsed += timeStep;

            // insert a logic:
            // Stop working for a time if the route of the person has been found null.
            // Because it starts to give the same route to another person after some iterations.

            if (timeElapsed >= cumulativeEdgeTraversalTime) {
                onEdgeTraversed(currentEdge.getOrigin(), currentEdge.getDestination());

                if (hasNextNode()) {

                    currentEdge = new Edge(path.get(index), path.get(index + 1));
                    if (!isNodeSafe(currentEdge.getDestination())) {
                        System.out.println(currentEdge.getDestination()+ " got inaccessible with Space Safety Value = "
                                + SpaceSensorsStreamer.getSpacesInfo().get(currentEdge.getDestination()).getSafetyValue());
                        onPathInterrupt();
                    } else {
                        cumulativeEdgeTraversalTime += currentEdge.getCost();
                        index++;
                    }
                } else {
                    onPathComplete();
                }
            }
        }
    };

    public void SetPath(List<String> path) {
        this.path = path;
        index = 0;
        timeElapsed = 0;

        if (hasNextNode()) {
            currentEdge = new Edge(path.get(index), path.get(index + 1));
            cumulativeEdgeTraversalTime = currentEdge.getCost();
        }
    }

    private boolean hasNextNode() {
        return this.path.size() - 1 > this.index;
    }
}
