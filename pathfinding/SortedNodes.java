package de.bastard.frameworks.pathfinding;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SortedNodes {

    List<Node> list = new LinkedList<Node>();

    public void add(Node node) {
        ListIterator<Node> iterator = list.listIterator();
        Node next;
        while (iterator.hasNext()) {
            next = iterator.next();
            if (node.h + node.cost < next.h + next.cost) {
                iterator.previous();
                iterator.add(node);
                return;
            }
        }

        iterator.add(node);
    }

    public List<Node> getList() {
        return list;
    }

}
