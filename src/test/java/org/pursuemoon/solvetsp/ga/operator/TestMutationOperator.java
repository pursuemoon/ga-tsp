package org.pursuemoon.solvetsp.ga.operator;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.ga.Solution;

public class TestMutationOperator {

    private RandomGeneratingOperator randomGeneratingOperator = new RandomGeneratingOperator(100);

    @Test
    public void testMultiPointMutationOperator() {
        MultiPointMutationOperator operator = new MultiPointMutationOperator(100, 10);
        Solution oldSolution = randomGeneratingOperator.generate();
        Solution newSolution = operator.mutate(oldSolution);
        for (int i = 1; i <= 10; ++i) {
            newSolution = operator.mutate(newSolution);
        }
        Assert.assertNotEquals(oldSolution, newSolution);
    }

    @Test
    public void testRangeReversingMutationOperator() {
        RangeReversingMutationOperator operator = new RangeReversingMutationOperator(100, 100);
        Solution oldSolution = randomGeneratingOperator.generate();
        Solution newSolution = operator.mutate(oldSolution);
        for (int i = 1; i <= 10; ++i) {
            newSolution = operator.mutate(newSolution);
        }
        Assert.assertNotEquals(oldSolution, newSolution);
    }
}
