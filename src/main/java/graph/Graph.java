package graph;

import helper.HelpingVariables;
import model.ODPair;
import model.Space;

import java.util.*;

public class Graph {
    private final Map<String, Node> nodeMap = new HashMap<>();

    public Graph() {

        for (Space space : HelpingVariables.spaceInfoList){
            if (space.getType().equals("node")) {
                nodeMap.put(space.getName(), new Node(space.getName()));
            }
        }

        int x = 0;
        for (ODPair od : HelpingVariables.odPairList){
            nodeMap.get(od.getOrigin()).addDestination(nodeMap.get(od.getDestination()), od.getCost());
            nodeMap.get(od.getDestination()).addDestination(nodeMap.get(od.getOrigin()), od.getCost());
        }
    }

    public List<Node> getShortestPathToSourceFromNode(String personLocation, String exit, INodeAccessibility nodeAccessibility) {
        Node personNode = nodeMap.get(personLocation);
        Dijkstra.calculateShortestPathFromSource(personNode, nodeAccessibility);
        Node n = nodeMap.get(exit);
        List<Node> path = n.getShortestPath();
        if (!path.contains(n)) {
            path.add(n);
        }

        return path;
    }
}