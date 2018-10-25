package de.bastard.frameworks.pathfinding;

import de.bastard.frameworks.util.Area;
import de.bastard.frameworks.util.Position;

public interface Map {

    boolean isAccessible(int x, int y);
    int getMoveCost(int x, int y);

    int getWidth();
    int getHeight();
    Area getDimension();

    int getHeuristicDistance(Position p1, Position p2);

}
