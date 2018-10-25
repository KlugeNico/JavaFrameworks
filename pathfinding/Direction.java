package de.bastard.frameworks.pathfinding;

import de.bastard.frameworks.util.Position;

class Direction {

    public final static int FORWARD = 0;
    public final static int BACK = 2;

    public final static int UP = 0;
    public final static int RIGHT = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;

    int d;

    public Direction(int d) {
        this.d = d % 4;
    }

    public Direction(Position p1, Position p2) {
        d = -1;
        if (p2.x == p1.x) {
            if((p2.y - p1.y) == 1)
                d = 0;
            else if((p2.y - p1.y) == -1)
                d = 2;
        }
        else if (p2.y == p1.y) {
            if((p2.x - p1.x) == 1)
                d = 1;
            else if((p2.x - p1.x) == -1)
                d = 3;
        }

        if (d == -1)
            System.out.print("Points are not connected! (Direction)");
    }

    public Direction(Direction d1, Direction d2) {
        if (d1 != null && d2 != null)
            d = (d2.d - d1.d + 4) % 4;
        else {
            d = 0;
            System.out.print("Direction is null! (Direction)");
        }
    }

    public float toRotation() {
        return -d * 90;
    }

}
