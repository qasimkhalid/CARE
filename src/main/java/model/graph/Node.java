package model.graph;

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
 * Graph model.graph = new Graph();
 *
 * model.graph.addNode(nodeA);
 * model.graph.addNode(nodeB);
 * model.graph.addNode(nodeC);
 * model.graph.addNode(nodeD);
 * model.graph.addNode(nodeE);
 * model.graph.addNode(nodeF);
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
    private Long distance = Long.MAX_VALUE;

    Map<Node, Long> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, long distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, Long distance) {
        this.name = name;
        this.distance = distance;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Map<Node, Long> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setShortestPath(LinkedList<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public List<Node> getShortestPath() {
//        if (!shortestPath.contains(this)) {
//            // add last element as well
//            shortestPath.add(this);
//        }

        return shortestPath;
    }

    public String getName() {
        return name;
    }
}
