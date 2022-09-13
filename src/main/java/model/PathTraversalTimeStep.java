package model;

import model.graph.Edge;
import model.graph.IAccessibility;
import operations.CommonOperations;

import java.util.ArrayList;
import java.util.List;

public class PathTraversalTimeStep {

    private List<String> path = new ArrayList<>();
    private Edge currentEdge;
    private long cumulativeEdgeTraversalTime = 0;
    private float timeElapsed = 0;
    private int index = 0; // index shows current edge's destination node's index A-B-C
    private IAccessibility accessibility;
    private IPathTraversal pathTraversal;

    /**
     * Set path will assign the route and start the traversal of person on the path.
     * @param path path the person should follow
     * @param accessibility accessibility interface for checking accessibility of nodes and edges
     * @param pathTraversal pathTraversal interface for path related operations
     */
    public void setPath(List<String> path, IAccessibility accessibility, IPathTraversal pathTraversal) {
        this.path = path;
        this.accessibility = accessibility;
        this.pathTraversal = pathTraversal;
        this.index = 0;
        this.timeElapsed = 0;

        if (!path.isEmpty()) {

            if (hasNextNode()) {
                currentEdge = new Edge(path.get(index), path.get(index + 1));
                cumulativeEdgeTraversalTime = currentEdge.getCost();
            } else { // if the person is already located at one of the exits.
                pathTraversal.onPathComplete();
            }
        }
    }

    public void onTimeStep(long timeStep) {
        if (currentEdge == null) {
            return;
        }

        // Keep checking the Remaining path after every timestep that whether it is safe or not
        if(index < path.size() && isPathSafe(path.subList(index, path.size()))) {
            // If the time has passed more than time required to traverse the edge (i.e., cost in our case).
            // Otherwise, do nothing.
            if (timeElapsed >= cumulativeEdgeTraversalTime) {
                pathTraversal.onEdgeTraversed(currentEdge.getOrigin(), currentEdge.getDestination());
                index++;
                if (hasNextNode()) {
                    // Creating an edge of the current node and the next node.
                    currentEdge = new Edge(path.get(index), path.get(index + 1));
                    cumulativeEdgeTraversalTime += currentEdge.getCost();
                } else {
                    pathTraversal.onPathComplete();
                }
            }
            timeElapsed += timeStep; // time stepping for testing purposes.
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
