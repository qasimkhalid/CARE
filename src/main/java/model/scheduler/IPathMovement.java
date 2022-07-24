package model.scheduler;


import model.Edge;

public interface IPathMovement {
    long GetEdgeCost();
    void OnEdgeTraversed();
    Edge GetCurrentEdge();
    boolean HasNextNode();
    boolean IsResting();
}