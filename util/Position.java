package de.bastard.frameworks.util;

public class Position {

    public int x, y;

    public Position() {
        x = 0;
        y = 0;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position copy() {
        return new Position(x, y);
    }

    @Override
    public boolean equals(Object var1) {
        if (!(var1 instanceof Position)) {
            return super.equals(var1);
        } else {
            Position var2 = (Position)var1;
            return this.x == var2.x && this.y == var2.y;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + "]";
    }

}
