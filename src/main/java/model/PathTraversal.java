package model;

import helper.AutomatedOperations;

import java.util.ArrayList;
import java.util.List;

public abstract class PathTraversal {

    private List<String> path = new ArrayList<>();
    private Edge currentEdge;
    private long cumulativeEdgeTraversalTime = 0;
    private long timeElapsed = 0;
    private int index = 0; // index shows current edge's destination node's index A-B-C

    public boolean isResting = false;

    public abstract void onEdgeTraversed(String destination);

    public abstract void onPathInterrupt();

    public abstract void onPathComplete();

    public PathTraversal(List<String> path) {
        this.path = path;
    }

    public void move(long dt) {
        if (this.isResting) {
            return;
        }

        this.timeElapsed += dt;

        if (this.timeElapsed >= this.cumulativeEdgeTraversalTime) {
            this.onEdgeTraversed(this.currentEdge.getDestination());

            if (this.hasNextNode()) {

                if (this.IsPathInterrupt()) {
                    this.onPathInterrupt();
                }
                this.currentEdge = new Edge(path.get(this.index), path.get(this.index + 1));
                this.cumulativeEdgeTraversalTime += this.currentEdge.getCost();
                this.index++;
            } else {
                this.isResting = true;
                this.onPathComplete();
            }
        }
    }

    private boolean hasNextNode() {
        return this.path.size() - 1 > this.index;
    }

    private boolean IsPathInterrupt() {
        // Todo: write logic here to check if the current path has been interrupted or
        // not!
        return false;
    }
}
