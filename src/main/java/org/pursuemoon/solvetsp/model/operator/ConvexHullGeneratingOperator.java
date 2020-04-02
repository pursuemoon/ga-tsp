package org.pursuemoon.solvetsp.model.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.model.AbstractPoint;
import org.pursuemoon.solvetsp.model.Solution;

import java.util.List;

/**
 * Convex-Hull-Strategy implementation of {@code GeneratingOperator}.
 */
public final class ConvexHullGeneratingOperator extends WeightedOperator.WeightedGeneratingOperator<Integer, Solution> {

    public ConvexHullGeneratingOperator(Integer weight) {
        super(weight);
    }

    @Override
    public Solution generate() {
        List<AbstractPoint> pList = TspSolver.getPoints();
        int[] gene = new int[pList.size()];
        // TODO : 凸包生成算法
        return new Solution(gene, true);
    }
}
