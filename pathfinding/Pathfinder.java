package de.bastard.frameworks.pathfinding;

import de.bastard.game.concept.World;

import com.badlogic.gdx.graphics.Color; import de.bastard.frameworks.util.Position;

public class Pathfinder {

    private static final int MAX_LAPS = 50000;

    public static Path findPath(Position start, Position end, Map map) {
        Node[][] nodeMap = new Node[map.getHeight()][map.getWidth()];
        SortedNodes nodes = new SortedNodes();
        nodeMap[start.x][start.y] = new Node(new Position(start.x, start.y), null, 0, map.getHeuristicDistance(start, end));
        nodes.add(nodeMap[start.x][start.y]);
        Node currentNode;
        int laps = 0;
        do {
            laps++;
            if (laps > MAX_LAPS)
                return null;
            currentNode = nodes.getList().remove(0);
            if (currentNode.pos.equals(end))
                return generatePath(currentNode, nodeMap, map);
            expandNode(currentNode, nodeMap, nodes, end, map);
        } while(!nodes.getList().isEmpty());
        return null;
    }

    private static Path generatePath(Node endNode, Node[][] nodeMap, Map map) {
        Path path = new Path(endNode.pos, map);
        Node node = endNode;
        while (node.pre != null) {
            if (path.addFirst(node.pre) == null)
                System.out.println("Path not connected! (Pathfinder)");
            if (node.pre != null)
                node = nodeMap[node.pre.x][node.pre.y];
        }
        return path;
    }

    private static void expandNode(Node node, Node[][] nodeMap, SortedNodes nodes, Position end, Map map) {
        Node currentNode;
        for (int i = 0; i < 4; i++) {
            Position pos = null;
            switch (i) {
                case 0: pos = new Position(node.pos.x - 1, node.pos.y); break;
                case 1: pos = new Position(node.pos.x + 1, node.pos.y); break;
                case 2: pos = new Position(node.pos.x, node.pos.y - 1); break;
                case 3: pos = new Position(node.pos.x, node.pos.y + 1); break;
            }
            if (map.getDimension().contains(pos) && map.isAccessible(pos.x, pos.y)) {
                currentNode = new Node(pos, node.pos, node.cost + map.getMoveCost(pos.x, pos.y), map.getHeuristicDistance(pos, end));
                int f = currentNode.cost + currentNode.h;
                if (nodeMap[pos.x][pos.y] != null) {
                    if (nodeMap[pos.x][pos.y].cost + nodeMap[pos.x][pos.y].h > f) {
                        nodes.getList().remove(nodeMap[pos.x][pos.y]);
                        nodeMap[pos.x][pos.y] = currentNode;
                        nodes.add(currentNode);
                    }
                } else {
                    nodeMap[pos.x][pos.y] = currentNode;
                    nodes.add(currentNode);
                }
            }
        }
    }

}
