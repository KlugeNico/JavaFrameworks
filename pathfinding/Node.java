package de.bastard.frameworks.pathfinding;

import de.bastard.frameworks.util.Position;

class Node {

    Position pos;
    Position pre;
    int cost;
    int h;

    Node(Position pos, Position pre, int cost, int h) {
        this.pos = pos;
        this.pre = pre;
        this.cost = cost;
        this.h = h;
    }

}
