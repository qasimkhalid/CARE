package model.graph;

public interface IAccessibility {
    boolean isNodeAccessible(String nodeName);
    boolean isEdgeAccessible(String origin, String destination);
}
