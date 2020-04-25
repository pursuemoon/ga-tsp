package org.pursuemoon.ai.ga;

/**
 * The interface representing the population in Genetic Algorithm.
 * A population consists of all individuals under study.
 *
 * In Genetic Algorithm, a population is designed to complete some kind of
 * training, or to get an approximate optimal solution to some optimization problem.
 * In general, the process is to initialize the population, evolve the population,
 * and finally obtain the best individual.
 *
 * @param <T> type of individual in the population
 */
public interface Population<T extends Individual> {

    /**
     * Set the crossover probability.
     *
     * The effect of the crossover probability is to limit the probability that
     * each two parents will cross.
     *
     * @param pc crossover probability
     */
    void setCrossoverProbability(double pc);

    /**
     * Set the mutation probability.
     *
     * The effect of the mutation probability is to limit the probability that
     * mutation happens to each individual.
     *
     * @param pm mutation probability
     */
    void setMutationProbability(double pm);

    /**
     * Initializes the population.
     */
    void initialize();

    /**
     * Makes the population evolve until the stop condition is met.
     *
     * @param stopCondition the stop condition which needs to be met top stop the evolution
     */
    void evolve(Condition stopCondition);

    /**
     * Returns the best individual which has the highest fitness to the problem.
     *
     * @return the best individual after evolution
     */
    T getBest();
}
