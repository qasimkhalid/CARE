package model;

import model.graph.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {

    private final List<String> path = new ArrayList<>();
    private final int length;
    private long cost = 0;

    public Route(List<Node> shortestPath) {
        this.length = shortestPath.size();
        if (!shortestPath.isEmpty()) {
            this.cost = shortestPath.get(shortestPath.size() - 1).getDistance();
            for (Node node : shortestPath) {
                path.add(node.getName());
            }
            Collections.reverse(path);
        }
    }

    public List<String> getPath() {
        return path;
    }

    public long getCost() {
        return cost;
    }
}
