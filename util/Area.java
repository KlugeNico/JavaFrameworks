package de.bastard.frameworks.util;

public class Area {

    public int xStart;
    public int yStart;
    public int xEnd;
    public int yEnd;

    public Area(int xStart, int yStart, int xEnd, int yEnd) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        if (xStart > xEnd || yStart > yEnd)
            throw new IllegalStateException("Area mustn't have negative width or height!!!");
    }

    public Area(Position start, Position end) {
        this.xStart = start.x;
        this.yStart = start.y;
        this.xEnd = end.x;
        this.yEnd = end.y;
    }

    public Area(int x, int y, int d) {
        this(x, y, x + d, y + d);
    }

    public Area(int x, int y, int width, int height, boolean b) {
        this(x, y, x + width, y + height);
    }

    public Area(Position pos, int radius) {
        if (radius < 0)
            throw new IllegalStateException("Negative Radius not allowed!");
        this.xStart = pos.x - radius;
        this.yStart = pos.y - radius;
        this.xEnd = pos.x + radius;
        this.yEnd = pos.y + radius;
    }

    public Area replace(int x, int y) {
        xStart += x;
        yStart += y;
        xEnd += x;
        yEnd += y;
        return this;
    }

    public Area fitIn(Area rim) {
        if (xStart < rim.xStart)
            xStart = rim.xStart;
        if (yStart < rim.yStart)
            yStart = rim.yStart;
        if (xEnd > rim.xEnd)
            xEnd = rim.xEnd;
        if (yEnd > rim.yEnd)
            yEnd = rim.yEnd;
        if (xStart > xEnd || yStart > yEnd)
            throw new IllegalStateException("Area doesn't fit in Rim!!!");
        return this;
    }

    public int getWidth() {
        return xEnd - xStart + 1;
    }

    public int getHeight() {
        return yEnd - yStart + 1;
    }

    public boolean contains(Position pos) {
        return (pos.x >= xStart && pos.x <= xEnd && pos.y >= yStart && pos.y <= yEnd);
    }

    public Position movePointIn(Position position) {
        if (position.x < xStart)
            position.x = xStart;
        if (position.x > xEnd)
            position.x = xEnd;
        if (position.y < yStart)
            position.y = yStart;
        if (position.y > yEnd)
            position.y = yEnd;
        return position;
    }

}
