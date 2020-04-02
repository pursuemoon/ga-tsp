package org.pursuemoon.solvetsp.model;

import static java.lang.Math.*;

/**
 * Representation of a point in 2-D Euclidean coordinates system.
 */
public class Euc2DPoint extends AbstractPoint implements Comparable<Euc2DPoint> {

    private int order;
    private double x;
    private double y;

    private Euc2DPoint() {
        throw new RuntimeException("Euc2DPoint object must be initialized with specified coordinates.");
    }

    public Euc2DPoint(double x, double y) {
        this(0, x, y);
    }

    public Euc2DPoint(int order, double x, double y) {
        this.order = order;
        this.x = x;
        this.y = y;
    }

    @Override
    public double distanceTo(AbstractPoint o) {
        if (!(o instanceof Euc2DPoint))
            throw new RuntimeException("Different type points are being calculated their distance.");
        Euc2DPoint p = (Euc2DPoint) o;
        return sqrt(pow(x - p.x, 2.0) + pow(y - p.y, 2));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Euc2DPoint))
            return false;
        Euc2DPoint p = (Euc2DPoint) o;
        return (x == p.x && y == p.y);
    }

    @Override
    public int hashCode() {
        int hash = Double.hashCode(x);
        hash = hash * 31 + Double.hashCode(y);
        return hash;
    }

    @Override
    public int compareTo(Euc2DPoint o) {
        int result = Integer.compare(order, o.order);
        if (result == 0) {
            result = Double.compare(x, o.x);
        }
        if (result == 0) {
            result = Double.compare(y, o.y);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Euc2DPoint [%d](%f, %f)", order, x, y);
    }
}
