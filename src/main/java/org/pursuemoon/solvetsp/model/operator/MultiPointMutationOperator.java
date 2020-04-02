package org.pursuemoon.solvetsp.model.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.model.Solution;

import java.util.*;

/**
 * Random-Multi-Point-Mutation-Strategy implementation of {@code MutationOperator}.
 */
public final class MultiPointMutationOperator extends WeightedOperator.WeightedMutationOperator<Integer, Solution> {

    private Random random;
    private Integer numberOfLoci;

    public MultiPointMutationOperator(Integer weight, Integer numberOfLoci) {
        super(weight);
        random = new Random();
        this.numberOfLoci = numberOfLoci;
    }

    @Override
    public Solution mutate(Solution o) {
        int[] gene = o.getClonedGene();
        if (numberOfLoci > gene.length)
            numberOfLoci = gene.length;
        Set<Integer> loci = new HashSet<>();
        Set<Integer> geneSet = new HashSet<>();
        for (int i = 0; i < numberOfLoci; ++i) {
            int index = random.nextInt(gene.length);
            loci.add(index);
            geneSet.add(gene[index]);
        }
        if (loci.size() >= 2) {
            List<Integer> geneList = new ArrayList<>(geneSet);
            for (Integer locus : loci) {
                int index = random.nextInt(geneList.size());
                int toBe = geneList.get(index);
                geneList.remove(index);
                gene[locus] = toBe;
            }
        }
        return new Solution(gene, true);
    }
}
