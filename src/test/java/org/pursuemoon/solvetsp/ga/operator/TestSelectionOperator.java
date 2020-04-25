package org.pursuemoon.solvetsp.ga.operator;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.ga.operator.RandomGeneratingOperator;
import org.pursuemoon.solvetsp.ga.operator.RouletteSelectionOperator;

import java.util.*;
import java.util.stream.Collectors;

public class TestSelectionOperator {

    private RandomGeneratingOperator randomGeneratingOperator = new RandomGeneratingOperator(100);

    @Test
    public void testRouletteSelectionOperator() {
        RouletteSelectionOperator operation = new RouletteSelectionOperator(100);
        int populationSize = 20, targetSize = 10000;
        int yes = 0, no = 0;
        for (int time = 0; time < 80; ++time) {
            List<Solution> originalList = new ArrayList<>();
            for (int i = 0; i < populationSize; ++i) {
                Solution newOne = randomGeneratingOperator.generate();
                originalList.add(newOne);
            }
            List<Solution> sList = operation.select(originalList, targetSize);
            Assert.assertEquals(sList.size(), targetSize);

            Map<Double, Long> cntMap = new LinkedHashMap<>();
            sList.stream()
                    .collect(Collectors.groupingBy(Solution::getDistance, Collectors.counting()))
                    .entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(entry -> cntMap.put(entry.getKey(), entry.getValue()));
            originalList.sort(Comparator.reverseOrder());

            Solution winner = originalList.get(0);
            Solution loser = originalList.get(populationSize - 1);
            if (cntMap.get(winner.getDistance()) > cntMap.get(loser.getDistance()))
                yes++;
            else
                no++;
        }
        Assert.assertTrue(yes >= 10 * no);
    }
}
