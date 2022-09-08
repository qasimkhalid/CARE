package algorithm;

import helper.HelpingVariables;
import model.ODPair;
import model.Space;
import streamers.SpaceSensorsStreamer;

import java.util.*;

public class Dijkstra {

    private Map<String, Graph> multipleGraphs; // This map is store number of various graphs for each type of persons
//    private static Map<String, List<Node>> adjacencyMatrix;
    private double allowedSafetyValue;
    private String source;
    private Graph graph;


    public Map<String, List<Node>> getAdjacencyMatrix() {
        return createAdjacentMatrix();
    }

    public void assignRoute(String type, String source, double allowedSafetyValue){
        this.allowedSafetyValue = allowedSafetyValue;
        this.source = source;

        if(multipleGraphs.containsKey(type)) {
            graph = multipleGraphs.get(type);
        } else {
            graph = new Graph();
            multipleGraphs.put(type, graph);

            // Get only those nodes and edges whose instantaneous Safety Value is greater than or equal to this.safetyvalue variable


        // Get All Exits from the query


        graph.getShortestPath(source, "exit");

        }

    }

    public static Map<String, List<Node>> createAdjacentMatrix() {

        Map<String, List<Node>> adjacencyMatrix = new HashMap<>();
        // Creating an adjacency Matrix from o-d pairs
        for (ODPair od : HelpingVariables.odPairList){

            if(!adjacencyMatrix.containsKey(od.getOrigin())){
                adjacencyMatrix.put(od.getOrigin(), new ArrayList<Node>());
            }
            adjacencyMatrix.get(od.getOrigin()).add(new Node(od.getDestination(), od.getCost()));

            if(!adjacencyMatrix.containsKey(od.getDestination())){
                adjacencyMatrix.put(od.getDestination(), new ArrayList<Node>());
            }
            adjacencyMatrix.get(od.getOrigin()).add(new Node(od.getOrigin(), od.getCost()));
        }
        return adjacencyMatrix;
    }

    public void updateMultipleGraphsOnInterruption(String type, String node){
       if (this.multipleGraphs.containsKey(type)) {
           this.multipleGraphs.get(type).removeNode(node);
       }
    }


    /* Strategy:

        To find a route we have to create a graph.

    1: Create a new graph every time for each person

        or

    2:  * Create a graph for each type of persons
        * Keep updating the graphs upon each .

          But graph is being updated every second.

           Every type of persons have their own unique graph.

        So when we need to give a route to people:

            Only the relevant and updated graph should be searched.





    */




}

