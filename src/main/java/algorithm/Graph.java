package algorithm;


import java.util.*;

class Graph {

    private final Map<String, List<Node>> nodes;

    public Graph() {
//        this.nodes = new HashMap<String, List<Node>>();
        this.nodes = Dijkstra.createAdjacentMatrix();
    }

    public void addNode(String String, List<Node> node) {
        this.nodes.put(String, node);
    }

    public void removeNode(String node) {
        this.nodes.remove(node);
        for (Map.Entry<String, List<Node>> entry : this.nodes.entrySet()) {
            List<Node> listOfAdjacentNodes = entry.getValue();
            for (int i=0; i < listOfAdjacentNodes.size();i++){
                if (Objects.equals(node, listOfAdjacentNodes.get(i).getId())){
                    listOfAdjacentNodes.remove(i);
                    break;
                }
            }
        }

    }

    public List<String> getShortestPath(String start, String finish) {
        final Map<String, Integer> distances = new HashMap<String, Integer>();
        final Map<String, Node> previous = new HashMap<String, Node>();
        PriorityQueue<Node> nodes = new PriorityQueue<Node>();

        for(String node : this.nodes.keySet()) {
            if (node == start) {
                distances.put(node, 0);
                nodes.add(new Node(node, 0));
            } else {
                distances.put(node, Integer.MAX_VALUE);
                nodes.add(new Node(node, Integer.MAX_VALUE));
            }
            previous.put(node, null);
        }

        while (!nodes.isEmpty()) {
            Node smallest = nodes.poll();
            if (smallest.getId() == finish) {
                final List<String> path = new ArrayList<String>();
                while (previous.get(smallest.getId()) != null) {
                    path.add(smallest.getId());
                    smallest = previous.get(smallest.getId());
                }
                return path;
            }

            if (distances.get(smallest.getId()) == Integer.MAX_VALUE) {
                break;
            }

            for (Node neighbor : this.nodes.get(smallest.getId())) {
                Integer alt = distances.get(smallest.getId()) + neighbor.getDistance();
                if (alt < distances.get(neighbor.getId())) {
                    distances.put(neighbor.getId(), alt);
                    previous.put(neighbor.getId(), smallest);

                    forloop:
                    for(Node n : nodes) {
                        if (n.getId() == neighbor.getId()) {
                            nodes.remove(n);
                            n.setDistance(alt);
                            nodes.add(n);
                            break forloop;
                        }
                    }
                }
            }
        }

        return new ArrayList<String>(distances.keySet());
    }

}