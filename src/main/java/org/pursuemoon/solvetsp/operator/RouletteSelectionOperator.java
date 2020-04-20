package org.pursuemoon.solvetsp.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Roulette-Selection-Strategy implementation of {@code SelectionOperator}.
 */
public final class RouletteSelectionOperator extends WeightedOperator.WeightedSelectionOperator<Integer, Solution> {

    private Random random;

    public RouletteSelectionOperator(Integer weight) {
        super(weight);
        random = new Random();
    }

    @Override
    public List<Solution> select(List<Solution> originalList, int targetSize) {
        List<Solution> sList = new ArrayList<>();
        double sum = originalList.stream().mapToDouble(Solution::getFitness).sum();
        double[] chances = originalList.stream().mapToDouble(solution -> solution.getFitness() / sum).toArray();
        for (int i = 0; i < targetSize; ++i) {
            double p = random.nextDouble();
            boolean flag = false;
            for (int j = 0; j < chances.length; ++j) {
                if (p >= chances[j])
                    p -= chances[j];
                else {
                    sList.add(originalList.get(j));
                    flag = true;
                    break;
                }
            }
            // Guarantees that there is always a solution to be selected.
            if (!flag) {
                int index = random.nextInt(chances.length);
                sList.add(originalList.get(index));
            }
        }
        return sList;
    }
}
