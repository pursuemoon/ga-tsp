package org.pursuemoon.ai.ga.util.operator;

import org.pursuemoon.ai.ga.util.Individual;

import java.util.List;

/**
 * The interface of selecting a specified number of {@code Individual} objects from
 * the original individual {@code List}.
 *
 * @param <T> type of individual
 */
@FunctionalInterface
public interface SelectionOperator<T extends Individual> {

    /**
     * Selects as many as {@code targetSize} individuals from the {@code originalList},
     * and collects them as a new list.
     *
     * @param originalList the original list of individuals
     * @param targetSize the size of the target list
     * @return a list of individual after selection
     */
    List<T> select(List<T> originalList, int targetSize);
}
