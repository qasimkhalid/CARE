package helper;

import java.util.ArrayList;
import java.util.List;

public class RouteFinder {

    public class Node {
        public String Id;
        public Node Parent;
        public List<Node> Childs;

        /**
         *
         * @param id - id of the node
         * @param parent - parent of the node whose id is given as a first argument
         */
        public Node(String id, Node parent) {
            Parent = parent;
            Id = id;
        }
    }

    private List<String> visitedNodes = new ArrayList<>();

    /**
     *
     * @param p - find
     */
    public void find(Node p) {
        List<Node> neighbors = getSortedNeighborsByWeights(p);
        for (Node n : neighbors) {
            if (visitedNodes.contains(n.Id))
                continue;

            List<String> listOfPersons = GetPersonsOnNode(n);
            if (listOfPersons != null && listOfPersons.size() > 0) {
                AddToPersonEscapingMap(n, listOfPersons);
            }
            visitedNodes.add(n.Id);
            find(n);
        }
    }

    /**
     *
     * @param n - node whose neighbours are supposed to be found
     * @return list of neighbours of node n in a sorted manner
     */
    private List<Node> getSortedNeighborsByWeights(Node n) {
        // Read data of that particular space name and its connected
        // spaces from you model
        List<String> childs = GetEdges(n.Id);
        if (childs == null || childs.size() == 0)
            return null;

        n.Childs = new ArrayList<>();
        for (int i = 0; i < childs.size(); i++) {
            n.Childs.add(new Node(childs.get(i), n));
        }
        n.Childs.sort(null);

        return n.Childs;
    }

    private List<String> GetPersonsOnNode(Node n) {
        // get persons on Node n
        return null;
    }

    private void AddToPersonEscapingMap(Node n, List<String> listOfPersons) {
        // add persons to a map, will discuss!
    }

    private List<String> GetEdges(String nodeId) {
        return null;
    }
}
