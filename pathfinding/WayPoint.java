package de.bastard.frameworks.pathfinding;

import de.bastard.frameworks.network.Context;
import de.bastard.frameworks.network.Data;
import de.bastard.frameworks.network.Transferable;
import de.bastard.game.concept.World;
import de.bastard.game.standard.ID;

import de.bastard.frameworks.util.Position;

public class WayPoint extends Position implements Transferable {

    public boolean flagged = false;

    public WayPoint() {
    }

    public WayPoint(Position point) {
        x = point.x;
        y = point.y;
    }

    public Direction getDirection(Position p2) {
        return new Direction(this, p2);
    }

    @Override
    public int getTypeId() {
        return ID.MOD_ID_ADD | ID.WAY_POINT;
    }

    @Override
    public void setContext(Context context) { }

    @Override
    public void storeData(Data data) {
        flagged = data.storeBoolean(flagged);
        x = data.storeShort((short)x);
        y = data.storeShort((short)x);
    }
}
