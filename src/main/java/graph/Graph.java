package graph;

import helper.HelpingVariables;
import model.ODPair;

import java.util.*;

public class Graph {

    public static Map<String, List<algorithm.Node>> createAdjacentMatrix() {

        Map<String, List<algorithm.Node>> adjacencyMatrix = new HashMap<>();
        // Creating an adjacency Matrix from o-d pairs
        for (ODPair od : HelpingVariables.odPairList){

            if(!adjacencyMatrix.containsKey(od.getOrigin())){
                adjacencyMatrix.put(od.getOrigin(), new ArrayList<algorithm.Node>());
            }
            adjacencyMatrix.get(od.getOrigin()).add(new algorithm.Node(od.getDestination(), od.getCost()));

            if(!adjacencyMatrix.containsKey(od.getDestination())){
                adjacencyMatrix.put(od.getDestination(), new ArrayList<algorithm.Node>());
            }
            adjacencyMatrix.get(od.getOrigin()).add(new algorithm.Node(od.getOrigin(), od.getCost()));
        }
        return adjacencyMatrix;
    }

    private Set<Node> nodes = new HashSet<>();

    public Graph() {

        Map<String, List<String>> adjacencyMatrix = new HashMap<>();
        // Creating an adjacency Matrix from o-d pairs
        for (ODPair od : HelpingVariables.odPairList){

            if(!adjacencyMatrix.containsKey(od.getOrigin())){
                adjacencyMatrix.put(od.getOrigin(), new ArrayList<>());
            }
            adjacencyMatrix.get(od.getOrigin()).add(od.getDestination());

            if(!adjacencyMatrix.containsKey(od.getDestination())){
                adjacencyMatrix.put(od.getDestination(), new ArrayList<>());
            }
            adjacencyMatrix.get(od.getOrigin()).add(od.getOrigin());
        }

        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeF = new Node("F");

        nodeA.addDestination(nodeB, 10);
        nodeA.addDestination(nodeC, 15);

        nodeB.addDestination(nodeD, 12);
        nodeB.addDestination(nodeF, 15);

        nodeC.addDestination(nodeE, 10);

        nodeD.addDestination(nodeE, 2);
        nodeD.addDestination(nodeF, 1);

        nodeF.addDestination(nodeE, 5);

        Graph graph = new Graph();

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);
    }

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters
}