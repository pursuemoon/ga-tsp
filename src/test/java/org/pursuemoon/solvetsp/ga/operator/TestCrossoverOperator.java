package org.pursuemoon.solvetsp.ga.operator;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.ga.operator.NearestNeighborCrossoverOperator;
import org.pursuemoon.solvetsp.ga.operator.RandomGeneratingOperator;
import org.pursuemoon.solvetsp.ga.operator.SinglePointCrossoverOperator;

import java.util.BitSet;
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
        for (Solution solution: offspring) {
            Assert.assertTrue(checkIfSolutionLegal(solution));
        }
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

    @Test
    public void testNearestNeighborCrossoverOperator() {
        NearestNeighborCrossoverOperator operator = new NearestNeighborCrossoverOperator(100);
        Solution p1 = randomGeneratingOperator.generate();
        Solution p2 = randomGeneratingOperator.generate();
        List<Solution> offspring = operator.crossover(p1, p2);
        for (Solution solution: offspring) {
            Assert.assertTrue(checkIfSolutionLegal(solution));
        }
    }

    private static boolean checkIfSolutionLegal(Solution solution) {
        int[] gene = solution.getClonedGene();
        BitSet bitSet = new BitSet(gene.length);
        for (int i : gene) {
            bitSet.flip(i - 1);
        }
        boolean flag = true;
        for (int i = 0; i < gene.length && flag; ++i) {
            flag = bitSet.get(i);
        }
        return flag;
    }
}
