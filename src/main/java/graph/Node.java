package graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A node can be described with a name, a LinkedList in reference to the shortestPath,
 * a cost from the parent, and an adjacency list named adjacentNodes:
 *
 * Example Usage:
 * --------------
 * Node nodeA = new Node("A");
 * Node nodeB = new Node("B");
 * Node nodeC = new Node("C");
 * Node nodeD = new Node("D");
 * Node nodeE = new Node("E");
 * Node nodeF = new Node("F");
 *
 * nodeA.addDestination(nodeB, 10);
 * nodeA.addDestination(nodeC, 15);
 *
 * nodeB.addDestination(nodeD, 12);
 * nodeB.addDestination(nodeF, 15);
 *
 * nodeC.addDestination(nodeE, 10);
 *
 * nodeD.addDestination(nodeE, 2);
 * nodeD.addDestination(nodeF, 1);
 *
 * nodeF.addDestination(nodeE, 5);
 *
 * Graph graph = new Graph();
 *
 * graph.addNode(nodeA);
 * graph.addNode(nodeB);
 * graph.addNode(nodeC);
 * graph.addNode(nodeD);
 * graph.addNode(nodeE);
 * graph.addNode(nodeF);
 */
public class Node {

    private final String name;

    /**
     * Shortest path from the source
     */
    private List<Node> shortestPath = new LinkedList<>();

    /**
     * distance from the source
     */
    private Integer distance = Integer.MAX_VALUE;

    Map<Node, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(String name) {
        this.name = name;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<Node, Integer> getAdjacentNodes() {
        return null;
    }

    public void setShortestPath(LinkedList<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }

    public String getName() {
        return name;
    }
}
