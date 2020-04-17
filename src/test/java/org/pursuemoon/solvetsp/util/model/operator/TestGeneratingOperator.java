package org.pursuemoon.solvetsp.util.model.operator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pursuemoon.solvetsp.Solution;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.operator.ConvexHullConstrictionGeneratingOperator;
import org.pursuemoon.solvetsp.operator.NearestKNeighborsGreedyGeneratingOperator;
import org.pursuemoon.solvetsp.operator.RandomGeneratingOperator;
import org.pursuemoon.solvetsp.operator.ShortestKEdgeGreedyGeneratingOperator;

import java.util.BitSet;
import java.util.Objects;

public class TestGeneratingOperator {

    @Before
    public void setUpTestedDir() {
//        String testDir = Objects.requireNonNull(TspSolver.class.getClassLoader()
//                .getResource("tsp_test/test_GEO")).getPath();
//        TspSolver.setDataExtractor(testDir);
    }

    @Test
    public void testRandomGeneratingOperator() {
        RandomGeneratingOperator operator = new RandomGeneratingOperator(100);
        Solution solution = operator.generate();
        Assert.assertTrue(checkIfSolutionLegal(solution));
    }

    @Test
    public void testNearestKNeighborGreedyGeneratingOperator() {
        NearestKNeighborsGreedyGeneratingOperator operator = new NearestKNeighborsGreedyGeneratingOperator(100, 1);
        Solution solution = operator.generate();
        Assert.assertTrue(checkIfSolutionLegal(solution));
    }

    @Test
    public void testShortestKEdgeGreedyGeneratingOperator() {
        ShortestKEdgeGreedyGeneratingOperator operator = new ShortestKEdgeGreedyGeneratingOperator(100, 2);
        Solution solution = operator.generate();
        Assert.assertTrue(checkIfSolutionLegal(solution));
    }

    @Test
    public void testConvexHullConstrictionGeneratingOperator() {
        ConvexHullConstrictionGeneratingOperator operator = new ConvexHullConstrictionGeneratingOperator(100, 3);
        Solution solution = operator.generate();
        Assert.assertTrue(checkIfSolutionLegal(solution));
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
