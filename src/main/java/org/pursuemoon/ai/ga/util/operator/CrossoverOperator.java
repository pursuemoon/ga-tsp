package org.pursuemoon.ai.ga.util.operator;


import org.pursuemoon.ai.ga.util.Individual;

import java.util.List;

/**
 * The interface of crossing over two {@code Individual} objects to get some new ones.
 *
 * @param <T> type of individual
 */
@FunctionalInterface
public interface CrossoverOperator<T extends Individual> {

    /**
     * Crosses over two individuals and gets several new individuals collected as a {@code List}.
     *
     * @param o1 the first individual
     * @param o2 the second individual
     * @return crossover results, a list that contains several individuals which size is customarily 2
     */
    List<T> crossover(T o1, T o2);
}
