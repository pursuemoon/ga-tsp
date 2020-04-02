package org.pursuemoon.ai.ga.util.operator;

import org.pursuemoon.ai.ga.util.Individual;

import java.util.function.Supplier;

/**
 * The interface of generating an {@code Individual} object.
 *
 * @param <T> type of individual
 */
@FunctionalInterface
public interface GeneratingOperator<T extends Individual> extends Supplier<T> {

    /**
     * Generates an individual.
     *
     * @return an generated individual
     */
    T generate();

    /**
     * Gets a result.
     *
     * @return a result
     */
    default T get() {
        return generate();
    }
}
