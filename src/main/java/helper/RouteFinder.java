package helper;

import model.graph.Dijkstra;
import model.graph.IAccessibility;
import model.graph.Node;
import model.graph.ODPair;
import model.Route;
import model.Space;

import java.util.*;

public class RouteFinder {

    private static final Map<String, Node> nodeMap = new HashMap<>();
    public static  Map<Float, Map<String, Node>> multipleNodeMaps = new HashMap<>();

    public static Route findPath(String personLocation, String exit, IAccessibility accessibility, float personAllowedSafetyValue) {
        List<Node> shortestPath = getShortestPathToSourceFromNode(personLocation, exit, accessibility, personAllowedSafetyValue);
        return new Route(shortestPath);
    }

    public static List<Node> getShortestPathToSourceFromNode(String personLocation, String exit, IAccessibility accessibility, float personAllowedSafetyValue) {
        List<Node> path = new ArrayList<>();
        Node personNode;
        Node exitNode;
        if(personLocation.equals(exit)){
            path.add(new Node (exit, 0L));
        } else {
            if (multipleNodeMaps.containsKey(personAllowedSafetyValue)) {
                personNode = multipleNodeMaps.get(personAllowedSafetyValue).get(personLocation);
                exitNode = multipleNodeMaps.get(personAllowedSafetyValue).get(exit);
            } else {
                getCustomizedNodeMap(accessibility);
                multipleNodeMaps.put(personAllowedSafetyValue, nodeMap);
                personNode = multipleNodeMaps.get(personAllowedSafetyValue).get(personLocation);
                exitNode = multipleNodeMaps.get(personAllowedSafetyValue).get(exit);
                Dijkstra.calculateShortestPathFromSource(exitNode);
            }
            path = personNode.getShortestPath();
            if (!path.isEmpty() && !path.contains(personNode)) {
                path.add(personNode);
            }
        }
//            getCustomizedNodeMap(accessibility);
//            Node personNode = nodeMap.get(personLocation);
//            Node exitNode = nodeMap.get(exit);
//            Dijkstra.calculateShortestPathFromSource(exitNode);
//
//
//            if (!multipleNodeMaps.containsKey(personAllowedSafetyValue)) {
//                multipleNodeMaps.put(personAllowedSafetyValue, nodeMap);
//            }
//            path = personNode.getShortestPath();

        Collections.reverse(path);
        return path;
    }


    private static void getCustomizedNodeMap(IAccessibility accessibility) {
        if(nodeMap.isEmpty()){
            createNodeMapSkeleton();
        } else {
            nodeMap.replaceAll((k, v) -> new Node(k));
        }
        for(int i=0; i < HelpingVariables.odPairList.size(); i++){
            if (accessibility.isEdgeAccessible(HelpingVariables.odPairList.get(i).getDestination(), HelpingVariables.odPairList.get(i).getOrigin()) && accessibility.isNodeAccessible(HelpingVariables.odPairList.get(i).getOrigin())){
                    nodeMap.get(HelpingVariables.odPairList.get(i).getDestination()).addDestination(nodeMap.get(HelpingVariables.odPairList.get(i).getOrigin()), HelpingVariables.odPairList.get(i).getCost());
            }
        }
    }

    public static void createNodeMapSkeleton() {
        for (Space space : HelpingVariables.spaceInfoList){
            if (space.getType().equals("node")) {
                nodeMap.put(space.getName(), new Node(space.getName()));
            }
        }
    }

}