package model;

public interface IPathTraversal {
    void onEdgeTraversed(String origin, String destination);
    void onPathInterrupt(String interruptedSpace);
    void onPathComplete();
}
