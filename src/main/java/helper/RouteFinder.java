package helper;

import graph.Dijkstra;
import graph.Graph;
import graph.INodeAccessibility;
import graph.Node;
import model.Route;

import java.util.List;

public class RouteFinder {

    //public static Graph
    public static Graph GRAPH;

    public static void initializeGraph() {
        GRAPH = new Graph();
    }

    public static Route findPath(String personLocation, String exit, INodeAccessibility nodeAccessibility) {
        List<Node> shortestPath = GRAPH.getShortestPathToSourceFromNode(personLocation, exit, nodeAccessibility);
        return new Route(shortestPath);
    }
}
