package org.pursuemoon.solvetsp.model;

import static java.lang.Math.*;

/**
 * Representation of a point in geographical latitude and longitude system expressed in radians.
 */
public class GeoPoint extends AbstractPoint implements Comparable<GeoPoint> {

    /** The radius of the earth, which uses kilometer as unit. */
    private static final double R_EARTH = 6378.388;

    private int order;
    private double latitude;
    private double longitude;

    private GeoPoint() {
        throw new RuntimeException("GeoPoint object must be initialized with specified latitude and longitude.");
    }

    public GeoPoint(double latitude, double longitude) {
        this(0, latitude, longitude, false);
    }

    public GeoPoint(int order, double latitude, double longitude) {
        this(order, latitude, longitude, false);
    }

    public GeoPoint(int order, double latitude, double longitude, boolean inRadians) {
        if (!inRadians) {
            latitude = degrees2Radians(latitude);
            longitude = degrees2Radians(longitude);
        }
        this.order = order;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public double distanceTo(AbstractPoint o) {
        if (!(o instanceof GeoPoint))
            throw new RuntimeException("Different type points are being calculated their distance.");
        GeoPoint p = (GeoPoint) o;

        /*
         * Haversine Formula.
         * Easy to derive, but has accuracy problems.
         */
//        double deltaLatitude = abs(latitude - p.latitude);
//        double deltaLongitude = abs(longitude - p.longitude);
//        double inside = pow(sin(0.5 * deltaLatitude), 2) +
//                cos(latitude) * cos(p.latitude) * pow(sin(0.5 * deltaLongitude), 2);
//        double distance = 2 * R_EARTH * asin(sqrt(inside));

        /*
         * A formula on the FAQ of TSPLIB.
         */
        double q1 = cos(longitude - p.longitude);
        double q2 = cos(latitude - p.latitude);
        double q3 = cos(latitude + p.latitude);
        double distance = R_EARTH * acos(0.5 * ((1 + q1) * q2 - (1 - q1) * q3));

        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GeoPoint))
            return false;
        GeoPoint p = (GeoPoint) o;
        return (latitude == p.latitude && longitude == p.longitude);
    }

    @Override
    public int hashCode() {
        int hash = Double.hashCode(latitude);
        hash = hash * 31 + Double.hashCode(longitude);
        return hash;
    }

    @Override
    public int compareTo(GeoPoint o) {
        int result = Double.compare(latitude, o.latitude);
        if (result == 0) {
            result = Double.compare(longitude, o.longitude);
        }
        return result;
    }

    /**
     * Converts a value from an angular representation to a radian representation.
     *
     * Pay attention! 23 degrees 28 minutes should be expressed as 23.28 here because of human habits.
     * Then this method could work correctly.
     *
     * @param degrees a value in degrees
     * @return a value in radians
     */
    private static double degrees2Radians(double degrees) {
        int integerPart = (int) degrees;
        double fractionalPart = degrees - integerPart;
        return PI * (integerPart + 5 * fractionalPart / 3) / 180;
    }

    @Override
    public String toString() {
        return String.format("GeoPoint [%d](%f, %f)", order, latitude, longitude);
    }
}
