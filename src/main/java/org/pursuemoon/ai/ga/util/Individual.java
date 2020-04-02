package org.pursuemoon.ai.ga.util;

/**
 * The interface representing an individual in Genetic Algorithm.
 * An individual consists of a gene sequence, also called genotype, which may mutate sometimes.
 */
public interface Individual extends Comparable<Individual> {

    /**
     * Returns the fitness value of itself. This value can be calculated by obtaining
     * the corresponding phenotype from genotype.
     *
     * The fitness must meet the following requirements: single value, continuous, non-negative, maximized.
     *
     * @return fitness value
     */
    double getFitness();

    /**
     * Compares the specified object with this individual for equality.
     *
     * @param o the object to be compared for equality with this individual
     * @return true if the specified object is equal to this individual
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this list.
     *
     * @return the hash code value for this individual
     */
    int hashCode();

    /**
     * Returns a string representation of this individual.
     *
     * @return the string representation of this individual
     */
    String toString();

    /**
     * Compare two {@code Individual} objects by comparing their fitness.
     *
     * @param o the {@code Individual} to be compared
     * @return  the value 0 if the fitness {@code o} is equal to that of this {@code Individual};
     *          a value less than 0 if the fitness of this {@code Individual} is less than that of {@code o};
     *          and a value greater than 0 if the fitness of this {@code Individual} is greater than that of {@code o}.
     */
    default int compareTo(Individual o) {
        return Double.compare(getFitness(), o.getFitness());
    }
}
