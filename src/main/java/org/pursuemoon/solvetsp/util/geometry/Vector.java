package org.pursuemoon.solvetsp.util.geometry;

public class Vector {

    protected double x;
    protected double y;

    public Vector() {
        x = y = 0;
    }

    public Vector(Euc2DPoint fromPoint, Euc2DPoint toPoint) {
        x = toPoint.x - fromPoint.x;
        y = toPoint.y - fromPoint.y;
    }

    public double det(Vector o) {
        return x * o.y - y * o.x;
    }

    public double dot(Vector o) {
        return x * o.x + y * o.y;
    }
}
