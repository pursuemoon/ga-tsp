package org.pursuemoon.solvetsp.ga.operator;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.util.DataExtractor;

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
        for (Solution solution : offspring) {
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
    public void testSectionCrossoverOperator() {
        SectionCrossoverOperator operator = new SectionCrossoverOperator(100);
        Solution p1 = randomGeneratingOperator.generate();
        Solution p2 = randomGeneratingOperator.generate();
        List<Solution> offspring = operator.crossover(p1, p2);
        for (Solution solution : offspring) {
            Assert.assertTrue(checkIfSolutionLegal(solution));
        }
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

    @Test
    public void testForComparison() {
        for (int i = 0; i < 5; ++i) {
            DataExtractor.instance.getNextTsp();
        }
        Solution p1 = randomGeneratingOperator.generate();
        Solution p2 = randomGeneratingOperator.generate();
        double d = (p1.getDistance() + p2.getDistance()) / 2;
//        System.out.println("Init Average Distance: " + d);

        SectionCrossoverOperator section = new SectionCrossoverOperator(100);
        NearestNeighborCrossoverOperator nearest = new NearestNeighborCrossoverOperator(100);

        final int times = 10;
        Solution f1 = p1;
        Solution f2 = p2;
        for (int i = 1; i <= times; ++i) {
            List<Solution> offspring = section.crossover(f1, f2);
            f1 = offspring.get(0);
            f2 = offspring.get(1);
            if (i % 2 == 0) {
                d = (f1.getDistance() + f2.getDistance()) / 2;
//                System.out.println(i + " Section Average Distance: " + d);
            }
        }

        f1 = p1;
        f2 = p2;
        for (int i = 1; i <= times; ++i) {
            List<Solution> offspring = nearest.crossover(f1, f2);
            f1 = offspring.get(0);
            f2 = offspring.get(1);
            if (i % 2 == 0) {
                d = (f1.getDistance() + f2.getDistance()) / 2;
//                System.out.println(i + " Nearest Average Distance: " + d);
            }
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
