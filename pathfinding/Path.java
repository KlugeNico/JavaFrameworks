package de.bastard.frameworks.pathfinding;

import de.bastard.frameworks.network.Context;
import de.bastard.frameworks.network.Data;
import de.bastard.frameworks.network.Transferable;

import de.bastard.frameworks.util.Position;

import java.util.Iterator;
import java.util.LinkedList;

public class Path implements Transferable {

    private Map map;
    private LinkedList<WayPoint> path;
    private int length;
    private WayPoint start;

    // Only must be called in combination with storeData
    public Path() {
        start = new WayPoint(new Position());
        path = new LinkedList<>();
    }

    public Path(Position start, Map map) {
        this(new WayPoint(start), map);
    }

    public Path(WayPoint start, Map map) {
        this.start = start;
        this.map = map;
        this.path = new LinkedList<>();
        calcLength();
    }

    public Path(Path path) {
        this.start = path.start;
        this.map = path.map;
        this.path = new LinkedList<>();
        this.path.addAll(path.path);
        calcLength();
    }

    public Position addFirst(Position point) {
        if (areConnected(point, start)) {
            path.addFirst(start);
            length += map.getMoveCost(start.x, start.y);
            start = new WayPoint(point);
            return point;
        }
        return null;
    }

    public Position addLast(Position point) {
        if (path.isEmpty())
            return addPointAfter(point, start);
        else
            return addPointAfter(point, getLast());
    }

    private Position addPointAfter(Position point, WayPoint last) {
        if (areConnected(point, last)) {
            path.addLast(new WayPoint(point));
            length += map.getMoveCost(point.x, point.y);
            return point;
        }
        return null;
    }

    private boolean areConnected(Position p1, Position p2) {
        return (((p1.y==p2.y) && ((p1.x==p2.x-1) ^ (p1.x==p2.x+1))) ^
                ((p1.x==p2.x) && ((p1.y==p2.y-1) ^ (p1.y==p2.y+1)))
        && map.isAccessible(p1.x, p2.y));
    }

    private void calcLength() {
        length = 0;
        for (WayPoint pos : path) {
            length += map.getMoveCost(pos.x, pos.y);
        }
    }

    public Path append(Path appendage) {
        if (appendage != null) {
            for (WayPoint point : appendage.path) {
                if (addLast(point) == null) {
                    System.out.println("Paths not connected! (Path)");
                    return this;
                }
            }
        }
        return this;
    }

    public int getLength() {
        return length;
    }

    public WayPoint getStart() {
        return start;
    }

    public WayPoint getLast() {
        if (path.isEmpty())
            return start;
        return path.getLast();
    }

    public LinkedList<WayPoint> getList() {
        return path;
    }

    public WayPoint getFirst() {
        if (path.isEmpty())
            return start;
        return path.getFirst();
    }

    public WayPoint removeFirst() {
        start = path.removeFirst();
        calcLength();
        return start;
    }

    // returns true if cut succeeded (pos lays on path)
    public boolean cutToStart(Position pos) {
        if (pos.equals(start))
            return true;
        int amount = 0;
        WayPoint newStart = null;
        for (WayPoint point : path) {
            if (pos.equals(point)) {
                newStart = point;
                break;
            }
            amount++;
        }
        if (newStart != null) {
            Iterator<WayPoint> iterator = path.iterator();
            for (int i = 0; i < amount; i++) {
                iterator.next();
                iterator.remove();
            }
            start = iterator.next();
            iterator.remove();
            calcLength();
            return true;
        }
        calcLength();
        return false;
    }

    public Position firstFlag() {
        if (path.isEmpty())
            return start;
        for (WayPoint point : path) {
            if (point.flagged) {
                return point;
            }
        }
        return path.getLast();
    }

    public Path cutFromFirstFlag() {
        Iterator<WayPoint> iterator = path.iterator();
        WayPoint point = start;
        while (iterator.hasNext()) {
            point = iterator.next();
            if (!point.flagged) {
                iterator.remove();
            } else {
                start = point;
                calcLength();
                return this;
            }
        }
        start = point;
        calcLength();
        return this;
    }

    public void flagLast() {
        if (!path.isEmpty())
            path.getLast().flagged = true;
        else
            start.flagged = true;
    }

    @Override
    public int getTypeId() {
        return MISSING;
    }

    @Override
    public void setContext(Context context) {
        context = context;
    }

    @Override
    public void storeData(Data data, Context context) {
        start.x = data.storeShort((short)start.x);
        start.y = data.storeShort((short)start.y);
        path = (LinkedList<WayPoint>) data.storeCollection(path, context);
        calcLength();
    }

    //   map.game.batch.draw(CONFIG.MARKED_TEXTURE, , );

}
