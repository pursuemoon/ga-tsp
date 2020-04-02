package org.pursuemoon.ai.ga.util;

/**
 * The interface representing a condition that is met so that some event will happen.
 */
public class Condition {

    public static MaxGenerationCondition ofMaxGenerationCondition(int maxGeneration) {
        return new MaxGenerationCondition(maxGeneration);
    }

    public static BestStayGenerationCondition ofBestStayGenerationCondition(int stayGeneration) {
        return new BestStayGenerationCondition(stayGeneration);
    }

    public static BestWorstDifferenceCondition ofBestWorstDifferenceCondition(int difference) {
        return new BestWorstDifferenceCondition(difference);
    }

    /**
     * If the generations of evolution of a population is equals to or greater than
     * the value of {@code maxGeneration}, this condition is met.
     */
    public static class MaxGenerationCondition extends Condition {

        protected int maxGeneration;

        protected MaxGenerationCondition(int maxGeneration) {
            this.maxGeneration = maxGeneration;
        }

        public int getMaxGeneration() {
            return maxGeneration;
        }
    }

    /**
     * If the best individual that ever appeared doesn't change for as much generations
     * as {@code stayGeneration}, this condition is met.
     */
    public static class BestStayGenerationCondition extends Condition {

        protected int stayGeneration;

        protected BestStayGenerationCondition(int stayGeneration) {
            this.stayGeneration = stayGeneration;
        }

        public int getStayGeneration() {
            return stayGeneration;
        }
    }

    /**
     * If the absolute difference between some indicator of the best individual and that
     * of the worst one is equal to or less than {@code difference}, this condition is met.
     */
    public static class BestWorstDifferenceCondition extends Condition {

        protected double difference;

        protected  BestWorstDifferenceCondition(double difference) {
            this.difference = difference;
        }

        public double getDifference() {
            return difference;
        }
    }
}
