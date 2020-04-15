package org.pursuemoon.solvetsp.util.model.operator;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.Solution;
import org.pursuemoon.solvetsp.operator.MultiPointMutationOperator;
import org.pursuemoon.solvetsp.operator.RandomGeneratingOperator;

public class TestMutationOperator {

    private RandomGeneratingOperator randomGeneratingOperator = new RandomGeneratingOperator(100);

    @Test
    public void testMultiPointMutationOperator() {
        MultiPointMutationOperator operator = new MultiPointMutationOperator(100, 10);
        Solution oldSolution = randomGeneratingOperator.generate();
        Solution newSolution = operator.mutate(oldSolution);
        Assert.assertNotEquals(oldSolution, newSolution);
    }
}
