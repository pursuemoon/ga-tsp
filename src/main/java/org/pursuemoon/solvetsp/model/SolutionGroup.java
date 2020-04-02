package org.pursuemoon.solvetsp.model;

import org.pursuemoon.ai.ga.util.Condition;
import org.pursuemoon.ai.ga.util.Population;
import org.pursuemoon.ai.ga.util.operator.WeightedOperator;

import java.util.*;

public final class SolutionGroup implements Population<Solution> {

    private int populationSize;
    private double crossoverProbability;
    private double mutationProbability;

    private int topX;
    private int topY;
    private int topZ;

    /**
     * The list containing {@code WeightedOperator.WeightedGeneratingOperator}s needed,
     * and their chances of being used.
     */
    private List<WeightedOperator.WeightedGeneratingOperator<Integer, Solution>> generatingOperators;
    private double[] generatingChances;

    /**
     * The list containing {@code WeightedOperator.WeightedSelectionOperator}s needed,
     * and their chances of being used.
     */
    private List<WeightedOperator.WeightedSelectionOperator<Integer, Solution>> selectionOperators;
    private double[] selectionChances;

    /**
     * The list containing {@code WeightedOperator.WeightedCrossoverOperator}s needed,
     * and their chances of being used.
     */
    private List<WeightedOperator.WeightedCrossoverOperator<Integer, Solution>> crossoverOperators;
    private double[] crossoverChances;

    /**
     * The list containing {@code WeightedOperator.WeightedMutationOperator}s needed,
     * and their chances of being used.
     */
    private List<WeightedOperator.WeightedMutationOperator<Integer, Solution>> mutationOperators;
    private double[] mutationChances;

    /** The solutions of this solution group. */
    private List<Solution> solutions;

    /** Pseudorandom number generator. */
    private Random random;

    private SolutionGroup() {
        solutions = new ArrayList<>();
        random = new Random();
    }

    @Override
    public void setCrossoverProbability(double pc) {
        crossoverProbability = pc;
    }

    @Override
    public void setMutationProbability(double pm) {
        mutationProbability = pm;
    }

    @Override
    public void initialize() {
        generatingChances = getChancesByWeights(generatingOperators);
        selectionChances = getChancesByWeights(selectionOperators);
        crossoverChances = getChancesByWeights(crossoverOperators);
        mutationChances = getChancesByWeights(mutationOperators);
        for (int i = 0; i < populationSize; ++i) {
            int index = randIndexByGeneratingChances();
            Solution solution = generatingOperators.get(index).generate();
            solutions.add(solution);
        }
    }

    private int randIndexByGeneratingChances() {
        double p = random.nextDouble();
        for (int i = 0; i < generatingChances.length; ++i) {
            if (p >= generatingChances[i])
                p -= generatingChances[i];
            else
                return i;
        }
        return 0;
    }

    private int randIndexBySelectionChances() {
        double p = random.nextDouble();
        for (int i = 0; i < selectionChances.length; ++i) {
            if (p >= selectionChances[i])
                p -= selectionChances[i];
            else
                return i;
        }
        return 0;
    }

    private int randIndexByCrossoverChances() {
        double p = random.nextDouble();
        for (int i = 0; i < crossoverChances.length; ++i) {
            if (p >= crossoverChances[i])
                p -= crossoverChances[i];
            else
                return i;
        }
        return 0;
    }

    private int randIndexByMutationChances() {
        double p = random.nextDouble();
        for (int i = 0; i < mutationChances.length; ++i) {
            if (p >= mutationChances[i])
                p -= mutationChances[i];
            else
                return i;
        }
        return 0;
    }

    @Override
    public void evolve(Condition stopCondition) {
        if (populationSize <= 1) {
            String m = String.format("Population size is not greater than 1: populationSize=%d", populationSize);
            throw new RuntimeException(m);
        }

        int gen = 0;
        boolean stopFlag = false;
        do {
            gen++;

            // TODO : 交叉之前可以保留 x 个优良个体
            List<Solution> topXList = getTopK(solutions, topX);
            List<Solution> afterCrossover = crossoverParents(solutions);

            // TODO : 变异之前可以保留 y 个优良个体
            List<Solution> topYList = getTopK(afterCrossover, topY);
            List<Solution> afterMutation = mutateOffspring(afterCrossover);

            // TODO : 选择之前可以保留 z 个优良个体
            List<Solution> topZList = getTopK(afterMutation, topZ);
            List<Solution> afterSelection = selectParents(afterMutation);

            afterSelection.addAll(topXList);
            afterSelection.addAll(topYList);
            afterSelection.addAll(topZList);
            afterSelection.sort(Comparator.reverseOrder());
            solutions = afterSelection.subList(0, populationSize);

            if (stopCondition instanceof Condition.MaxGenerationCondition) {
                if (gen >= ((Condition.MaxGenerationCondition) stopCondition).getMaxGeneration())
                    stopFlag = true;
            }
        } while (!stopFlag);
    }

    /**
     * Crossover the input parents to get more offspring.
     *
     * @param parents the input parents
     * @return the offspring after crossover
     */
    private List<Solution> crossoverParents(List<Solution> parents) {
        List<Solution> afterCrossover = new ArrayList<>();
        int len = parents.size();
        for (int i = 0; i < len; ++i) {
            int idx;
            do {
                idx = random.nextInt(len);
            } while (idx == i);
            Solution p1 = parents.get(i);
            Solution p2 = parents.get(idx); // p2 must be different from p1
            double rate = random.nextDouble();
            if (rate > crossoverProbability) {
                afterCrossover.add(p1);
                afterCrossover.add(p2);
                continue;
            }
            int index = randIndexByCrossoverChances();
            List<Solution> offspring = crossoverOperators.get(index).crossover(p1, p2);
            afterCrossover.addAll(offspring);
        }
        return afterCrossover;
    }

    /**
     * Mutates the input individuals to have different genotypes.
     *
     * @param originalList the original individuals to be mutated
     * @return mutated offspring, collected as {@code List}
     */
    private List<Solution> mutateOffspring(List<Solution> originalList) {
        List<Solution> afterMutation = new ArrayList<>();
        for (Solution solution : originalList) {
            int index = randIndexByMutationChances();
            double rate = random.nextDouble();
            if (rate > mutationProbability) {
                afterMutation.add(solution);
                continue;
            }
            Solution newOne = mutationOperators.get(index).mutate(solution);
            afterMutation.add(newOne);
        }
        return afterMutation;
    }

    /**
     * Selects as many individuals as {@code populationSize} to be the parents of next generation.
     *
     * @param originalList the original individuals to be selected
     * @return selected parents, collected as {@code List}
     */
    private List<Solution> selectParents(List<Solution> originalList) {
        List<Solution> parent = new ArrayList<>(), sList;
        int cnt = 0;
        for (int i = 0; i < selectionChances.length - 1; ++i) {
            int targetSize = (int) (selectionChances[i] * populationSize);
            sList = selectionOperators.get(i).select(originalList, targetSize);
            parent.addAll(sList);
            cnt += targetSize;
        }
        sList = selectionOperators.get(selectionChances.length - 1).select(originalList, populationSize - cnt);
        parent.addAll(sList);
        return parent;
    }

    @Override
    public Solution getBest() {
        solutions.sort(Comparator.reverseOrder());
        return solutions.get(0);
    }

    /**
     * Gets the corresponding chance array according to the weight list.
     *
     * @param weightedOperatorList the input {@code List<WeightedOperator<Integer>>}
     * @return the corresponding chance array
     */
    private static double[] getChancesByWeights(List<? extends WeightedOperator<Integer>> weightedOperatorList) {
        final int sum = weightedOperatorList.stream().mapToInt(WeightedOperator::getWeight).sum();
        return weightedOperatorList.stream().mapToDouble(operator -> (double) operator.getWeight() / sum).toArray();
    }

    /**
     * Gets the best {@code number} solutions from the {@code list}.
     *
     * @param list the input list
     * @param number the number "k"
     * @return the top k list
     */
    private static List<Solution> getTopK(List<Solution> list, int number) {
        list.sort(Comparator.reverseOrder());
        return list.subList(0, number);
    }

    /**
     * Builder of {@code SolutionGroup}.
     */
    public static class Builder {

        private Integer populationSize;
        private Double crossoverProbability;
        private Double mutationProbability;

        private int topX;
        private int topY;
        private int topZ;

        private List<WeightedOperator.WeightedGeneratingOperator<Integer, Solution>> generatingOperators;
        private List<WeightedOperator.WeightedSelectionOperator<Integer, Solution>> selectionOperators;
        private List<WeightedOperator.WeightedCrossoverOperator<Integer, Solution>> crossoverOperators;
        private List<WeightedOperator.WeightedMutationOperator<Integer, Solution>> mutationOperators;

        private Builder() {
            generatingOperators = new ArrayList<>();
            selectionOperators = new ArrayList<>();
            crossoverOperators = new ArrayList<>();
            mutationOperators = new ArrayList<>();
            topX = topY = topZ = 0;
        }

        public static Builder ofNew() {
            return new Builder();
        }

        public Builder populationSize(int populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        public Builder withCrossoverProbability(double crossoverProbability) {
            this.crossoverProbability = crossoverProbability;
            return this;
        }

        public Builder withMutationProbability(double mutationProbability) {
            this.mutationProbability = mutationProbability;
            return this;
        }

        public Builder withGenerationOperator(WeightedOperator.WeightedGeneratingOperator<Integer, Solution> operator) {
            generatingOperators.add(operator);
            return this;
        }

        public Builder withSelectionOperator(WeightedOperator.WeightedSelectionOperator<Integer, Solution> operator) {
            selectionOperators.add(operator);
            return this;
        }

        public Builder withCrossoverOperator(WeightedOperator.WeightedCrossoverOperator<Integer, Solution> operator) {
            crossoverOperators.add(operator);
            return this;
        }

        public Builder withMutationOperator(WeightedOperator.WeightedMutationOperator<Integer, Solution> operator) {
            mutationOperators.add(operator);
            return this;
        }

        public Builder withTopX(int topX) {
            this.topX = topX;
            return this;
        }

        public Builder withTopY(int topY) {
            this.topY = topY;
            return this;
        }

        public Builder withTopZ(int topZ) {
            this.topZ = topZ;
            return this;
        }

        public SolutionGroup build() {
            SolutionGroup solutionGroup = new SolutionGroup();
            solutionGroup.populationSize = Objects.requireNonNull(populationSize);
            solutionGroup.crossoverProbability = Objects.requireNonNull(crossoverProbability);
            solutionGroup.mutationProbability = Objects.requireNonNull(mutationProbability);
            solutionGroup.generatingOperators = requireNonEmpty(generatingOperators);
            solutionGroup.selectionOperators = requireNonEmpty(selectionOperators);
            solutionGroup.crossoverOperators = requireNonEmpty(crossoverOperators);
            solutionGroup.mutationOperators = requireNonEmpty(mutationOperators);
            solutionGroup.topX = topX;
            solutionGroup.topY = topY;
            solutionGroup.topZ = topZ;
            return solutionGroup;
        }

        private static <T extends List<? extends WeightedOperator<Integer>>> T requireNonEmpty(T operatorList) {
            if (operatorList.isEmpty()) {
                String m = String.format("The instance of [%s] is empty.", operatorList.getClass());
                throw new RuntimeException(m);
            }
            return operatorList;
        }
    }
}
