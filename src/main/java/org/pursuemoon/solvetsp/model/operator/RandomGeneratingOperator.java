package org.pursuemoon.solvetsp.model.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.model.AbstractPoint;
import org.pursuemoon.solvetsp.model.Solution;

import java.util.List;
import java.util.Random;

/**
 * Random-Strategy implementation of {@code GeneratingOperator}.
 */
public final class RandomGeneratingOperator extends WeightedOperator.WeightedGeneratingOperator<Integer, Solution> {

    private Random random;

    public RandomGeneratingOperator(Integer weight) {
        super(weight);
        random = new Random();
    }

    @Override
    public Solution generate() {
        List<AbstractPoint> pList = TspSolver.getPoints();
        int len = pList.size();
        int[] gene = new int[len];
        for (int i = 0; i < len; ++i)
            gene[i] = i + 1;
        for (int t = len - 1; t >= 0; --t) {
            int pos = random.nextInt(len);
            int temp = gene[pos];
            gene[pos] = gene[t];
            gene[t] = temp;
        }
        return new Solution(gene, true);
    }
}
