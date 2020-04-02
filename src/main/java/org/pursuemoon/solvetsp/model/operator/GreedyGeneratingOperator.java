package org.pursuemoon.solvetsp.model.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.model.AbstractPoint;
import org.pursuemoon.solvetsp.model.Solution;

import java.util.List;

/**
 * Closest-Greedy-Strategy implementation of {@code GeneratingOperator}.
 */
/**
 * Random-strategy implementation of {@code GeneratingOperator}.
 */
public final class GreedyGeneratingOperator extends WeightedOperator.WeightedGeneratingOperator<Integer, Solution> {

    public GreedyGeneratingOperator(Integer weight) {
        super(weight);
    }

    @Override
    public Solution generate() {
        List<AbstractPoint> pList = TspSolver.getPoints();
        int[] gene = new int[pList.size()];
        // TODO : 贪心生成算法
        return new Solution(gene, true);
    }
}
