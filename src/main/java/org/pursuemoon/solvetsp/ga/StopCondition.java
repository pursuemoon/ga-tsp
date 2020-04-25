package org.pursuemoon.solvetsp.ga;

import org.pursuemoon.ai.ga.Condition;

/**
 * Class of compound stop condition of ga-tsp.
 */
public final class StopCondition extends Condition {

    private Condition.MaxGenerationCondition maxGenerationCondition;
    private Condition.BestStayGenerationCondition bestStayGenerationCondition;
    private Condition.BestWorstDifferenceCondition bestWorstDifferenceCondition;

    public StopCondition(int maxGeneration, int bestStayGeneration, double difference) {
        maxGenerationCondition = Condition.ofMaxGenerationCondition(maxGeneration);
        bestStayGenerationCondition = Condition.ofBestStayGenerationCondition(bestStayGeneration);
        bestWorstDifferenceCondition = Condition.ofBestWorstDifferenceCondition(difference);
    }

    public boolean isMet(int gen, int stayGen, double diff) {
        return maxGenerationCondition.isMet(gen) && bestStayGenerationCondition.isMet(stayGen)
                && bestWorstDifferenceCondition.isMet(diff);
    }
}
