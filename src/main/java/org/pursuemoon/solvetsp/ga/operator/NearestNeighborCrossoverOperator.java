package org.pursuemoon.solvetsp.ga.operator;

import org.pursuemoon.ai.ga.operator.WeightedOperator;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.ArrayUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * Nearest-Neighbor-Strategy implementation of {@code CrossoverOperator}.
 */
public class NearestNeighborCrossoverOperator extends WeightedOperator.WeightedCrossoverOperator<Integer, Solution> {

    private Random random;

    public NearestNeighborCrossoverOperator(Integer weight) {
        super(weight);
        random = new Random();
    }

    @Override
    public List<Solution> crossover(Solution o1, Solution o2) {
        /* Accelerates computing distances. */
        TspSolver.fullyCalDistArray();
        double[][] distArray = TspSolver.getDistArray();

        List<Solution> offspring = new ArrayList<>();
        int size = distArray.length;
        int[] gene1 = o1.getClonedGene();
        int[] gene2 = o2.getClonedGene();
        for (int time = 0; time < 2; time++) {
            int[] gene = new int[size];
            BitSet bitSet = new BitSet(size);
            int begin = random.nextInt(size) + 1;
            gene[0] = begin;
            bitSet.set(begin);
            for (int i = 1; i < size; ++i) {
                int last = gene[i - 1];
                int idx1 = ArrayUtils.indexOfUnique(gene1, last);
                int idx2 = ArrayUtils.indexOfUnique(gene2, last);
                int prev1 = gene1[prev(idx1, size)];
                int prev2 = gene2[prev(idx2, size)];
                int next1 = gene1[next(idx1, size)];
                int next2 = gene2[next(idx2, size)];
                double dist = Double.MAX_VALUE;
                int next = -1;
                if (!bitSet.get(prev1)) {
                    double d = distArray[last - 1][prev1 - 1];
                    if (d < dist) {
                        next = prev1;
                        dist = d;
                    }
                }
                if (!bitSet.get(prev2)) {
                    double d = distArray[last - 1][prev2 - 1];
                    if (d < dist) {
                        next = prev2;
                        dist = d;
                    }
                }
                if (!bitSet.get(next1)) {
                    double d = distArray[last - 1][next1 - 1];
                    if (d < dist) {
                        next = next1;
                        dist = d;
                    }
                }
                if (!bitSet.get(next2)) {
                    double d = distArray[last - 1][next2 - 1];
                    if (d < dist) {
                        next = next2;
                        dist = d;
                    }
                }
                if (next == -1) {
                    for (int nxt = 1; nxt <= size; ++nxt) {
                        if (!bitSet.get(nxt)) {
                            double d = distArray[last - 1][nxt - 1];
                            if (d < dist) {
                                next = nxt;
                                dist = d;
                            }
                        }
                    }
                }
                gene[i] = next;
                bitSet.set(next);
            }
            Solution newOne = new Solution(gene, true);
            offspring.add(newOne);
        }

        return offspring;
    }

    private static int prev(int index, int size) {
        return (index - 1 < 0 ? size - 1 : index - 1);
    }

    private static int next(int index, int size) {
        return (index + 1 >= size ? 0 : index + 1);
    }
}
