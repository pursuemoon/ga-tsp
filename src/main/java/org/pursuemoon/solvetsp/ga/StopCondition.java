package org.pursuemoon.solvetsp.ga;

import org.pursuemoon.ai.ga.Condition;

/**
 * Class of compound stop condition of ga-tsp.
 */
public final class StopCondition extends Condition {

    private MinGenerationCondition minGenerationCondition;
    private int maxGeneration;
    private Condition.BestStayGenerationCondition bestStayGenerationCondition;
    private Condition.BestWorstDifferenceCondition bestWorstDifferenceCondition;

    public StopCondition(int minGeneration, int maxGeneration, int bestStayGeneration, double difference) {
        if (minGeneration > maxGeneration) {
            throw new RuntimeException("StopCondition could not be constructed with minGeneration greater than maxGeneration.");
        }
        minGenerationCondition = Condition.ofMinGenerationCondition(minGeneration);
        this.maxGeneration = maxGeneration;
        bestStayGenerationCondition = Condition.ofBestStayGenerationCondition(bestStayGeneration);
        bestWorstDifferenceCondition = Condition.ofBestWorstDifferenceCondition(difference);
    }

    public boolean isMet(int gen, int stayGen, double diff) {
        return (gen >= maxGeneration) ||
                (minGenerationCondition.isMet(gen) && bestStayGenerationCondition.isMet(stayGen)
                && bestWorstDifferenceCondition.isMet(diff));
    }
}
