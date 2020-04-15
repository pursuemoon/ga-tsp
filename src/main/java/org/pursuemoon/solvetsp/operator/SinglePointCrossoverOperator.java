package org.pursuemoon.solvetsp.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.Solution;
import org.pursuemoon.solvetsp.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Random-Single-Point-Strategy implementation of {@code CrossoverOperator}.
 */
public final class SinglePointCrossoverOperator extends WeightedOperator.WeightedCrossoverOperator<Integer, Solution> {

    private Random random;
    private Integer numberOfLoci;

    /**
     * Constructor.
     *
     * @param weight the weight of this operator
     * @param numberOfLoci the limit which means there mustn't be more than so many loci to be changed
     */
    public SinglePointCrossoverOperator(Integer weight, Integer numberOfLoci) {
        super(weight);
        random = new Random();
        this.numberOfLoci = numberOfLoci;
    }

    @Override
    public List<Solution> crossover(Solution o1, Solution o2) {
        List<Solution> offspring = new ArrayList<>();
        if (o1.equals(o2)) {
            offspring.add(o1);
            offspring.add(o2);
            return offspring;
        }
        int[] gene1 = o1.getClonedGene();
        int[] gene2 = o2.getClonedGene();
        if (gene1.length != gene2.length) {
            String m = String.format("Sizes of gene between two solutions is different: %d != %d", gene1.length, gene2.length);
            throw new RuntimeException(m);
        }
        if (numberOfLoci > gene1.length)
            numberOfLoci = gene1.length;
        int idx1, idx2, temp;
        do {
            idx1 = random.nextInt(gene1.length);
        } while (gene1[idx1] == gene2[idx1]);
        idx2 = ArrayUtils.indexOfUnique(gene2, gene1[idx1]);
        for (int i = 1; i <= numberOfLoci; ++i) {
            if (i == numberOfLoci) {
                temp = gene1[idx1];
                gene1[idx1] = gene2[idx2];
                gene2[idx2] = temp;
                break;
            }
            temp = gene1[idx1];
            gene1[idx1] = gene2[idx1];
            gene2[idx1] = temp;
            if (idx1 == idx2) break;
            for (int f = 0; f < gene1.length; ++f) {
                if (f == idx1) continue;
                if (gene1[f] == gene1[idx1]) {
                    idx1 = f;
                    break;
                }
            }
        }
        offspring.add(new Solution(gene1, true));
        offspring.add(new Solution(gene2, true));
        return offspring;
    }
}
