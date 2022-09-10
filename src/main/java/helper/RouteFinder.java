package helper;

import model.graph.Dijkstra;
import model.graph.IEdgeAccessibility;
import model.graph.INodeAccessibility;
import model.graph.Node;
import model.graph.ODPair;
import model.Route;
import model.Space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteFinder {

    private static final Map<String, Node> nodeMap = new HashMap<>();

    public static void createNodeMapSkeleton() {
        for (Space space : HelpingVariables.spaceInfoList){
            if (space.getType().equals("node")) {
                nodeMap.put(space.getName(), new Node(space.getName()));
            }
        }
    }

    public static Route findPath(String personLocation, String exit, INodeAccessibility nodeAccessibility, IEdgeAccessibility edgeAccessibility) {
        List<Node> shortestPath = getShortestPathToSourceFromNode(personLocation, exit, nodeAccessibility, edgeAccessibility);
        return new Route(shortestPath);
    }


    public static List<Node> getShortestPathToSourceFromNode(String personLocation, String exit, INodeAccessibility nodeAccessibility, IEdgeAccessibility edgeAccessibility) {
        List<Node> path = new ArrayList<>();

        if(personLocation.equals(exit)){
            path.add(new Node (exit, 0L));
        } else {
            getCustomizedNodeMap(nodeAccessibility, edgeAccessibility);
            Node personNode = nodeMap.get(personLocation);
            Dijkstra.calculateShortestPathFromSource(personNode);
            Node n = nodeMap.get(exit);
            path = n.getShortestPath();
            if (!path.isEmpty() && !path.contains(n)) {
                path.add(n);
            }
        }

        return path;
    }

    private static void getCustomizedNodeMap(INodeAccessibility nodeAccessibility, IEdgeAccessibility edgeAccessibility) {
        if(nodeMap.isEmpty()){
            createNodeMapSkeleton();
        } else {
            nodeMap.replaceAll((k, v) -> new Node(k));
        }
        for (ODPair od : HelpingVariables.odPairList){
            if (edgeAccessibility.isEdgeAccessible(od.getOrigin(), od.getDestination()) && nodeAccessibility.isNodeAccessible(od.getDestination())){
                    nodeMap.get(od.getOrigin()).addDestination(nodeMap.get(od.getDestination()), od.getCost());
            }
            if (edgeAccessibility.isEdgeAccessible(od.getDestination(), od.getOrigin()) && nodeAccessibility.isNodeAccessible(od.getOrigin())){
                    nodeMap.get(od.getDestination()).addDestination(nodeMap.get(od.getOrigin()), od.getCost());
            }
        }
    }
}


//** Testing Purpose **
//if (edgeAccessibility.isEdgeAccessible(od.getDestination(), od.getOrigin())){
//                if ( nodeAccessibility.isNodeAccessible(od.getOrigin())){
//        nodeMap.get(od.getDestination()).addDestination(nodeMap.get(od.getOrigin()), od.getCost());
//                } else {
//                    System.out.println(SpaceSensorsStreamer.getSpacesInfo().get(od.getOrigin()).getReadableName()
//                            + " is not Accessible from "
//                            + SpaceSensorsStreamer.getSpacesInfo().get(od.getDestination()).getReadableName()
//                            + ". Thus, "
//                            + SpaceSensorsStreamer.getSpacesInfo().get(od.getOrigin()).getReadableName()
//                            + " has not been added in the node Map. " );
//                }
//            }
//            else {
//                System.out.println("Edge between "
//                        + SpaceSensorsStreamer.getSpacesInfo().get(od.getDestination()).getReadableName()
//                        + " and "
//                        + SpaceSensorsStreamer.getSpacesInfo().get(od.getOrigin()).getReadableName()
//                        + " is not accessible. Thus, "
//                        + SpaceSensorsStreamer.getSpacesInfo().get(od.getOrigin()).getReadableName()
//                        + " has not been added in the node Map. " );
//        }