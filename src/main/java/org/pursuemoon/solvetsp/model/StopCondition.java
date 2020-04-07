package org.pursuemoon.solvetsp.model;

import org.pursuemoon.ai.ga.util.Condition;

/**
 * Class of compound stop condition of ga-tsp.
 */
public final class StopCondition extends Condition {

    private Condition.MaxGenerationCondition maxGenerationCondition;
    private Condition.BestWorstDifferenceCondition bestWorstDifferenceCondition;

    public StopCondition(int maxGeneration, double difference) {
        maxGenerationCondition = Condition.ofMaxGenerationCondition(maxGeneration);
        bestWorstDifferenceCondition = Condition.ofBestWorstDifferenceCondition(difference);
    }

    public boolean isMet(int gen, double diff) {
        return maxGenerationCondition.isMet(gen) && bestWorstDifferenceCondition.isMet(diff);
    }
}
