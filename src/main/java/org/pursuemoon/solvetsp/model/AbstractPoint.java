package org.pursuemoon.solvetsp.model;

/**
 * Skeletal class representing point in TSP.
 */
public abstract class AbstractPoint {

    /**
     * Calculates the distance from this {@code AbstractPoint} to another one.
     *
     * @param o another {@code AbstractPoint}
     * @return the distance between the two points
     */
    public abstract double distanceTo(AbstractPoint o);

    /**
     * Compares the specified object with this {@code AbstractPoint} for equality.
     *
     * @param o the object to be compared for equality with this {@code AbstractPoint}
     * @return true if the specified object is equal to this {@code AbstractPoint}
     */
    public abstract boolean equals(Object o);

    /**
     * Returns the hash code value for this object.
     *
     * @return the hash code value for this object
     */
    public abstract int hashCode();

    /**
     * Returns the string representation.
     *
     * @return the string representation
     */
    public abstract String toString();
}
