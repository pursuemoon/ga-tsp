package org.pursuemoon.solvetsp.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.Solution;
import org.pursuemoon.solvetsp.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Section-Strategy implementation of {@code CrossoverOperator}.
 */
public final class SectionCrossoverOperator extends WeightedOperator.WeightedCrossoverOperator<Integer, Solution> {

    private Random random;

    public SectionCrossoverOperator(Integer weight) {
        super(weight);
        random = new Random();
    }

    @Override
    public List<Solution> crossover(Solution o1, Solution o2) {
        List<Solution> offspring = new ArrayList<>();
        int[] gene1 = o1.getClonedGene();
        int[] gene2 = o2.getClonedGene();
        int begin = random.nextInt(gene1.length);
        for (int i = begin; i < gene1.length; ++i) {
            int idx1 = ArrayUtils.indexOfUnique(gene1, gene2[i]);
            int idx2 = ArrayUtils.indexOfUnique(gene2, gene1[i]);
            int temp;
            temp = gene1[i];
            gene1[i] = gene2[i];
            gene2[i] = temp;
            temp = gene1[idx1];
            gene1[idx1] = gene2[idx2];
            gene2[idx2] = temp;
        }
        offspring.add(new Solution(gene1, true));
        offspring.add(new Solution(gene2, true));
        return offspring;
    }
}
