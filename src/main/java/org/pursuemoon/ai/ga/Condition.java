package org.pursuemoon.ai.ga;

/**
 * The interface representing a condition that is met so that some event will happen.
 */
public class Condition {

    public boolean isMet() {
        throw new UnsupportedOperationException("The condition check operation is not supported.");
    }

    public static MinGenerationCondition ofMinGenerationCondition(int minGeneration) {
        return new MinGenerationCondition(minGeneration);
    }

    public static BestStayGenerationCondition ofBestStayGenerationCondition(int stayGeneration) {
        return new BestStayGenerationCondition(stayGeneration);
    }

    public static BestWorstDifferenceCondition ofBestWorstDifferenceCondition(double difference) {
        return new BestWorstDifferenceCondition(difference);
    }

    /**
     * If the generations of evolution of a population is equals to or greater than
     * the value of {@code minGeneration}, this condition is met.
     */
    public static class MinGenerationCondition extends Condition {

        protected int minGeneration;

        protected MinGenerationCondition(int minGeneration) {
            this.minGeneration = minGeneration;
        }

        public int getMinGeneration() {
            return minGeneration;
        }

        public boolean isMet(int gen) {
            return gen >= minGeneration;
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

        public boolean isMet(int gen) {
            return gen >= stayGeneration;
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

        public boolean isMet(double diff) {
            return diff <= difference;
        }
    }
}
