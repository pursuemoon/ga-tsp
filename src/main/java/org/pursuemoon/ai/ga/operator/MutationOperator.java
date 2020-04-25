package org.pursuemoon.ai.ga.operator;

import org.pursuemoon.ai.ga.Individual;

import java.util.function.UnaryOperator;

/**
 * The interface of mutating an {@code Individual} object.
 *
 * @param <T> type of individual
 */
@FunctionalInterface
public interface MutationOperator<T extends Individual> extends UnaryOperator<T> {

    /**
     * Mutates a specified {@code Individual} object, so that it owns a different genotype.
     *
     * @param o the specified individual
     * @return a new individual after mutation
     */
    T mutate(T o);

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    default T apply(T t) {
        return mutate(t);
    }
}
