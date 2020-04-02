package org.pursuemoon.solvetsp.model;

import org.pursuemoon.ai.ga.util.Individual;
import org.pursuemoon.solvetsp.TspSolver;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public final class Solution implements Individual {

    /** The specific fitness function. */
    private static UnaryOperator<Double> fitnessFunction = t -> 1 / (t + 1e-3);

    /** The genotype, represented by a Hamiltonian path, not a cycle, since we can get a cycle from a path. */
    private int[] gene;

    /** The fitness of this solution, using lazy loading. */
    private double fitness = -1.0;

    /** The distance of this solution. */
    private double distance;

    public Solution(int[] path, boolean beginWith1) {
        gene = (beginWith1 ? cloneAndConvertToBeginWith1(path) : path.clone());
    }

    public Solution(Integer[] path, boolean beginWith1) {
        gene = new int[path.length];
        for (int i = 0; i < path.length; ++i)
            gene[i] = path[i];
        if (beginWith1)
            gene = cloneAndConvertToBeginWith1(gene);
    }

    /**
     * Gets a cloned genotype.
     *
     * @return the cloned genotype
     */
    public int[] getClonedGene() {
        return gene.clone();
    }

    /**
     * Make the gene array begin with the number 1 which represents the point with order 1.
     */
    public void beginWith1() {
        gene = cloneAndConvertToBeginWith1(gene);
    }

    /**
     * Returns the fitness of this {@code Solution} which loads lazily.
     *
     * @return the fitness value of this {@code Solution}
     */
    @Override
    public double getFitness() {
        if (fitness < 0)
            fitness = calFitness();
        return fitness;
    }

    /**
     * Compares the specified object with this individual for equality.
     * Symmetric solutions will be judged as unequal if their first point is not the same.
     *
     * @param o the object to be compared for equality with this individual
     * @return true if the specified object is equal to this individual
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Solution))
            return false;
        Solution solution = (Solution) o;
        if (gene.length != solution.gene.length)
            return false;
        for (int i = 0; i < gene.length; ++i)
            if (gene[i] != solution.gene[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(gene);
    }

    @Override
    public String toString() {
        return String.format("{distance=%f, fitness=%f, gene=%s}", getDistance(), getFitness(), Arrays.toString(gene));
    }

    @Override
    public int compareTo(Individual o) {
        if (!(o instanceof Solution))
            throw new RuntimeException("Different type individuals are being compared.");
        return Double.compare(getFitness(), o.getFitness());
    }

    /**
     * Distance getter.
     *
     * @return the distance of this TSP solution
     */
    public double getDistance() {
        if (fitness < 0)
            fitness = calFitness();
        return distance;
    }

    /**
     * Calculates the fitness by getting the distance of the Hamiltonian cycle.
     *
     * @return fitness value
     */
    private double calFitness() {
        if (gene == null || gene.length == 0)
            throw new RuntimeException("Illegal genotype.");

        List<AbstractPoint> pList = TspSolver.getPoints();
        int size = pList.size();
        distance = pList.get(gene[size - 1] - 1).distanceTo(pList.get(gene[0] - 1));
        for (int i = 1; i < size; ++i)
            distance += pList.get(gene[i - 1] - 1).distanceTo(pList.get(gene[i] - 1));
        return fitnessFunction.apply(distance);
    }

    /**
     * Clones and converts the input int array begin with 1.
     *
     * @param ints the input int array
     * @return the output int array that starts with 1
     */
    private static int[] cloneAndConvertToBeginWith1(int[] ints) {
        if (ints[0] == 1)
            return ints.clone();
        int idx, len = ints.length;
        for (idx = 0; idx < len; ++idx) {
            if (ints[idx] == 1)
                break;
        }
        if (idx == len)
            throw new RuntimeException("There is no 1 in this array.");
        int[] gene = new int[len];
        System.arraycopy(ints, idx, gene, 0, len - idx);
        System.arraycopy(ints, 0, gene, len - idx, idx);
        return gene;
    }
}
