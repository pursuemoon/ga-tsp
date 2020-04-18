package org.pursuemoon.solvetsp.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.Solution;

import java.util.Random;

/**
 * Range-Reversing-Mutation-Strategy implementation of {@code MutationOperator}.
 */
public class RangeReversingMutationOperator extends WeightedOperator.WeightedMutationOperator<Integer, Solution> {

    private Random random;
    private Integer rangeWidth;

    public RangeReversingMutationOperator(Integer weight, Integer rangeWidth) {
        super(weight);
        this.rangeWidth = rangeWidth;
        random = new Random();
    }

    @Override
    public Solution mutate(Solution o) {
        int[] gene = o.getClonedGene();
        if (rangeWidth > gene.length)
            rangeWidth = gene.length;
        /* Both left and right are included. */
        int left = random.nextInt(gene.length);
        int right = random.nextInt(Math.min(gene.length - left, rangeWidth)) + left;
        int i = left, j = right;
        while (i < j) {
            int tmp = gene[i];
            gene[i] = gene[j];
            gene[j] = tmp;
            i++;
            j--;
        }
        return new Solution(gene, true);
    }
}
