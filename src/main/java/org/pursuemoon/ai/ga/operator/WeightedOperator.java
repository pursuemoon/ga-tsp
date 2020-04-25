package org.pursuemoon.ai.ga.operator;

import org.pursuemoon.ai.ga.Individual;

/**
 * An instance of this class owns a weight with it.
 *
 * @param <W> type of weight
 */
public abstract class WeightedOperator<W extends Number> {

    private W weight;

    public WeightedOperator(W weight) {
        this.weight = weight;
    }

    public W getWeight() {
        return weight;
    }

    /**
     * Abstract implementation of {@code SelectionOperator} with weight.
     *
     * @param <W> type of weight
     * @param <T> type of individual
     */
    public static abstract class WeightedSelectionOperator<W extends Number, T extends Individual>
            extends WeightedOperator<W> implements SelectionOperator<T> {

        public WeightedSelectionOperator(W weight) {
            super(weight);
        }
    }

    /**
     * Abstract implementation of {@code GeneratingOperator} with weight.
     *
     * @param <W> type of weight
     * @param <T> type of individual
     */
    public static abstract class WeightedGeneratingOperator<W extends Number, T extends Individual>
            extends WeightedOperator<W> implements GeneratingOperator<T> {

        public WeightedGeneratingOperator(W weight) {
            super(weight);
        }
    }

    /**
     * Abstract implementation of {@code CrossoverOperator} with weight.
     *
     * @param <W> type of weight
     * @param <T> type of individual
     */
    public static abstract class WeightedCrossoverOperator<W extends Number, T extends Individual>
            extends WeightedOperator<W> implements CrossoverOperator<T> {

        public WeightedCrossoverOperator(W weight) {
            super(weight);
        }
    }

    /**
     * Abstract implementation of {@code MutationOperator} with weight.
     *
     * @param <W> type of weight
     * @param <T> type of individual
     */
    public static abstract class WeightedMutationOperator<W extends Number, T extends Individual>
            extends WeightedOperator<W> implements MutationOperator<T> {

        public WeightedMutationOperator(W weight) {
            super(weight);
        }
    }
}
