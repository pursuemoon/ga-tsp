package org.pursuemoon.solvetsp;

import org.pursuemoon.ai.ga.util.Individual;

import java.util.Arrays;
import java.util.function.UnaryOperator;

public final class Solution implements Individual {

    /** The genotype, represented by a Hamiltonian path, not a cycle, since we can get a cycle from a path. */
    private int[] gene;

    /** The fitness of this solution, using lazy loading. */
    private double fitness = -1.0;

    /** The distance of this solution, using lazy loading. */
    private double distance = -1.0;

    public Solution(int[] path, boolean beginWith1) {
        if (beginWith1) {
            gene = (path[0] == 1 ? path : cloneAndConvertToBeginWith1(path));
        } else {
            gene = path;
        }
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
        // FIXME : 精度问题
        if (distance < 0) {
            /* Accelerates computing distances. */
            TspSolver.fullyCalDistArray();
            double[][] distArray = TspSolver.getDistArray();

            int size = distArray.length;
            int from = gene[size - 1] - 1, to = gene[0] - 1;
            distance = distArray[from][to];
            for (int i = 1; i < size; ++i) {
                from = gene[i - 1] - 1;
                to = gene[i] - 1;
                distance += distArray[from][to];
            }
        }
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
        UnaryOperator<Double> fitnessFunction = TspSolver.getFitnessFunction();
        distance = getDistance();
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
