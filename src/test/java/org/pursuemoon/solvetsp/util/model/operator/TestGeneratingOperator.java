package org.pursuemoon.solvetsp.util.model.operator;

import org.junit.Test;
import org.pursuemoon.solvetsp.model.Solution;
import org.pursuemoon.solvetsp.model.operator.ConvexHullGeneratingOperator;
import org.pursuemoon.solvetsp.model.operator.GreedyGeneratingOperator;
import org.pursuemoon.solvetsp.model.operator.RandomGeneratingOperator;

public class TestGeneratingOperator {

    @Test
    public void testRandomGeneratingOperator() {
        RandomGeneratingOperator operator = new RandomGeneratingOperator(100);
        Solution solution = operator.generate();
    }

    @Test
    public void testGreedyGeneratingOperator() {
        GreedyGeneratingOperator operator = new GreedyGeneratingOperator(100);
        // 还没测
        Solution solution = operator.generate();
    }

    @Test
    public void testConvexHullGeneratingOperator() {
        ConvexHullGeneratingOperator operator = new ConvexHullGeneratingOperator(100);
        // 还没测
        Solution solution = operator.generate();
    }
}
