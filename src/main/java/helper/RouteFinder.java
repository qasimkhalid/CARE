package helper;

import model.graph.Dijkstra;
import model.graph.IAccessibility;
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

    public static Route findPath(String personLocation, String exit, IAccessibility accessibility) {
        List<Node> shortestPath = getShortestPathToSourceFromNode(personLocation, exit, accessibility);
        return new Route(shortestPath);
    }

    public static List<Node> getShortestPathToSourceFromNode(String personLocation, String exit, IAccessibility accessibility) {
        List<Node> path = new ArrayList<>();

        if(personLocation.equals(exit)){
            path.add(new Node (exit, 0L));
        } else {
            getCustomizedNodeMap(accessibility);
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

    private static void getCustomizedNodeMap(IAccessibility accessibility) {
        if(nodeMap.isEmpty()){
            createNodeMapSkeleton();
        } else {
            nodeMap.replaceAll((k, v) -> new Node(k));
        }
//        for (ODPair od : HelpingVariables.odPairList){
        for(int i=0; i < HelpingVariables.odPairList.size(); i++){
            if (accessibility.isEdgeAccessible(HelpingVariables.odPairList.get(i).getOrigin(), HelpingVariables.odPairList.get(i).getDestination()) && accessibility.isNodeAccessible(HelpingVariables.odPairList.get(i).getDestination())){
//                Debugging
//                String x = HelpingVariables.odPairList.get(i).getOrigin();
//                Node y = nodeMap.get(HelpingVariables.odPairList.get(i).getDestination());
//                long z = HelpingVariables.odPairList.get(i).getCost();
//                nodeMap.get(x).addDestination(y,z);


//                nodeMap.get(HelpingVariables.odPairList.get(i).getOrigin()).addDestination(nodeMap.get(HelpingVariables.odPairList.get(i).getDestination()), HelpingVariables.odPairList.get(i).getCost());
            }
            if (accessibility.isEdgeAccessible(HelpingVariables.odPairList.get(i).getDestination(), HelpingVariables.odPairList.get(i).getOrigin()) && accessibility.isNodeAccessible(HelpingVariables.odPairList.get(i).getOrigin())){
                    nodeMap.get(HelpingVariables.odPairList.get(i).getDestination()).addDestination(nodeMap.get(HelpingVariables.odPairList.get(i).getOrigin()), HelpingVariables.odPairList.get(i).getCost());
            }
//        if (accessibility.isEdgeAccessible(od.getOrigin(), od.getDestination()) && accessibility.isNodeAccessible(od.getDestination())){
//                    nodeMap.get(od.getOrigin()).addDestination(nodeMap.get(od.getDestination()), od.getCost());
//            }
//            if (accessibility.isEdgeAccessible(od.getDestination(), od.getOrigin()) && accessibility.isNodeAccessible(od.getOrigin())){
//                    nodeMap.get(od.getDestination()).addDestination(nodeMap.get(od.getOrigin()), od.getCost());
//            }
        }
        //Debugging
        int x = 0;
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