package org.pursuemoon.solvetsp.util.model.operator;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.model.Solution;
import org.pursuemoon.solvetsp.model.operator.RandomGeneratingOperator;
import org.pursuemoon.solvetsp.model.operator.SinglePointCrossoverOperator;

import java.util.List;

public class TestCrossoverOperator {

    private RandomGeneratingOperator randomGeneratingOperator = new RandomGeneratingOperator(100);

    @Test
    public void testSinglePointCrossoverOperator() {
        int numberOfLoci = 20;
        SinglePointCrossoverOperator operator = new SinglePointCrossoverOperator(100, numberOfLoci);
        Solution p1 = randomGeneratingOperator.generate();
        Solution p2 = randomGeneratingOperator.generate();
        List<Solution> offspring = operator.crossover(p1, p2);
        int[] geneP1 = p1.getClonedGene();
        int[] geneP2 = p2.getClonedGene();
        int[] gene1 = offspring.get(0).getClonedGene();
        int[] gene2 = offspring.get(1).getClonedGene();
        int cnt1 = 0, cnt2 = 0;
        for (int i = 0; i < gene1.length; ++i) {
            if (gene1[i] != geneP1[i]) cnt1++;
            if (gene2[i] != geneP2[i]) cnt2++;
        }
        Assert.assertEquals(cnt1, cnt2);
        Assert.assertTrue(cnt1 <= numberOfLoci);
        Assert.assertTrue(cnt2 <= numberOfLoci);
    }
}
