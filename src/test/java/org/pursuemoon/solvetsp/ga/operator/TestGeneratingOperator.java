package org.pursuemoon.solvetsp.ga.operator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.DataExtractor;
import org.pursuemoon.solvetsp.util.Painter;

import java.util.*;

import static java.lang.Double.*;

public class TestGeneratingOperator {

    @BeforeClass
    public static void setTspCh130() {
        for (int i = 0; i < 5; i++) {
            DataExtractor.instance.getNextTsp();
        }
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

    @Test
    public void testConvexHullDivisionGeneratingOperator() {
        ConvexHullDivisionGeneratingOperator operator = new ConvexHullDivisionGeneratingOperator(100);
        Solution solution = operator.generate();
        Assert.assertTrue(checkIfSolutionLegal(solution));
    }

    @Test
    public void testForComparison() {
        ConvexHullDivisionGeneratingOperator division = new ConvexHullDivisionGeneratingOperator(100);
        final int times = 20;
        double minDistance = MAX_VALUE;
        double sum = 0;
        for (int i = 1; i <= times; ++i) {
            Solution solution = division.generate();
            double distance = solution.getDistance();
            if (distance <= minDistance) {
                minDistance = distance;
                Painter.paint("division", false, TspSolver.getPoints(), solution);
            }
            sum += solution.getDistance();
        }
        double average = sum / times;
//        System.out.println("\nDivision Average: " + average);
//        System.out.println("Division Best: " + minDistance);

        ConvexHullConstrictionGeneratingOperator constriction = new ConvexHullConstrictionGeneratingOperator(100, 1);
        minDistance = MAX_VALUE;
        sum = 0;
        for (int i = 1; i <= times; ++i) {
            Solution solution = constriction.generate();
            double distance = solution.getDistance();
            if (distance <= minDistance) {
                minDistance = distance;
                Painter.paint("constriction", false, TspSolver.getPoints(), solution);
            }
            sum += solution.getDistance();
        }
        average = sum / times;
//        System.out.println("\nConstriction Average: " + average);
//        System.out.println("Constriction Best: " + minDistance);

        NearestKNeighborsGreedyGeneratingOperator nearest = new NearestKNeighborsGreedyGeneratingOperator(100, 1);
        minDistance = MAX_VALUE;
        sum = 0;
        for (int i = 1; i <= times; ++i) {
            Solution solution = nearest.generate();
            double distance = solution.getDistance();
            if (distance <= minDistance) {
                minDistance = distance;
                Painter.paint("nearest", false, TspSolver.getPoints(), solution);
            }
            sum += solution.getDistance();
        }
        average = sum / times;
//        System.out.println("\nNearest Average: " + average);
//        System.out.println("Nearest Best: " + minDistance);
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
