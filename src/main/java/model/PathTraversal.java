package model;

import helper.AutomatedOperations;

import java.util.ArrayList;
import java.util.List;


public abstract class PathTraversal {

    private List<String> path = new ArrayList<>();
    private Edge currentEdge;
    private long requiredCumulativeCurrentEdgeCost = 0;
    private long timeElapsed = 0;
    private int index = 0; // index shows current edge's destination node's index A-B-C
    private boolean edgeTraversed = false;

    public boolean isResting = false;


    public abstract void onEdgeTraversed(String destination);

    public abstract void onLeavingLastDestination();

    public abstract void onPathTraversed();

    public PathTraversal(List<String> path)
    {
        this.path = path;
    }

    public void move(long dt)
    {
        if (this.isResting) {
            return;
        }

        if (this.edgeTraversed)
        {
            this.edgeTraversed = false;
            this.onLeavingLastDestination();
        }

        this.timeElapsed += dt;

        if (this.timeElapsed >= this.requiredCumulativeCurrentEdgeCost)
        {
            if (this.hasNextNode()) {
                this.currentEdge = new Edge(path.get(this.index), path.get(this.index + 1));
                this.requiredCumulativeCurrentEdgeCost += this.currentEdge.getCost();
                this.index++;
                this.onEdgeTraversed(this.currentEdge.getDestination());
                this.edgeTraversed = true;
            } else {
                this.isResting = true;
                this.onPathTraversed();
            }
        }
    }

    private long getCurrentEdgeCost() {
        try {
            return AutomatedOperations.getODPairCostInSeconds(currentEdge.getOrigin(), currentEdge.getDestination());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean hasNextNode() {
        return this.path.size() - 1 > this.index;
    }
}
