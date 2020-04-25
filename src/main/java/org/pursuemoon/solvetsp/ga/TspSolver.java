package org.pursuemoon.solvetsp.ga;

import org.apache.log4j.Logger;
import org.pursuemoon.solvetsp.ga.operator.*;
import org.pursuemoon.solvetsp.util.DataExtractor;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

/**
 * Main class to solve the TSP.
 */
public final class TspSolver implements Runnable {

    private static Logger log = Logger.getLogger(TspSolver.class);

    /** The atomic integer to initialize threads that solve TSPs. */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    /** The data extractor to get points of the TSP which is about to be solved. */
    private static DataExtractor dataExtractor = DataExtractor.instance;

    /** The id of the current solver thread. */
    private static ThreadLocal<Integer> idLocal = ThreadLocal.withInitial(() -> atomicInteger.getAndAdd(1));

    /** The information about the TSP being solved by the current solver thread. */
    private static ThreadLocal<List<Object>> tspLocal = ThreadLocal.withInitial(() -> dataExtractor.getNextTsp());

    /** The index of a TSP case found in default resource. */
    private int index;

    /** Number of calculations for the same TSP. */
    private int calTime;

    /* Parameters of traditional GA. */

    private int populationSize;
    private double crossoverProbability;
    private double mutationProbability;

    /* Parameters of improved GA. */

    private int topX;
    private int topY;
    private int topZ;

    /* Parameters of stop condition. */

    private int leastGenerationNumber;
    private int bestQueueSize;
    private int leastBestStayGeneration;
    private double maxFitnessDifferenceBetweenBestAndWorst;

    /** The list of {@code SolutionReport} that contains approximate optimal solution obtained at each attempt. */
    private List<SolutionReport> solutionReportList;

    /** Average distance of all results. */
    private double averageDistance;

    /** Average generation number of all results. */
    private double averageGenerationNumber;

    /** Average initialization cost time in seconds. */
    private double averageInitUsedTime;

    /** Average evolution cost time in seconds. */
    private double averageEvolutionUsedTime;

    /** The optimal solution of this TSP, given by TSPLIB. */
    private Solution optimalSolution;

    /** The report of solving current TSP case. */
    private String report;

    /**
     * Default constructor.
     */
    public TspSolver() {
        this(-1, 3);
    }

    /**
     * Default constructor with {@code baseDir}.
     *
     * @param index the index of the TSP going to solve; -1 means next TSP in default order
     * @param calTime the number of calculation time
     */
    public TspSolver(int index, int calTime) {
        this(calTime,
                35, 0.96, 0.66,
                2, 5, 3,
                2000, 500, 1000, 1e-6);
        this.index = index;
    }

    /**
     * Constructor with {@code baseDir} and other specific parameters.
     *
     * @param calTime the number of calculation time
     *
     * @param populationSize the population size of ga
     * @param crossoverProbability the probability of crossover of ga
     * @param mutationProbability the probability of mutation of ga
     *
     * @param topX the number of solutions before crossover
     * @param topY the number of solutions before mutation
     * @param topZ the number of solutions before selection
     * @param leastGenerationNumber the least number of evolution generation
     *
     * @param bestQueueSize one of stop condition,
     *                      the size of the queue of best individuals
     * @param leastBestStayGeneration one of stop condition,
     *                                the least number of stay generation of the best individual
     * @param maxFitnessDifferenceBetweenBestAndWorst one of stop condition,
     *                                                the max difference of fitness of the best and worst individual in the best queue
     */
    public TspSolver(int calTime,
                     int populationSize, double crossoverProbability, double mutationProbability,
                     int topX, int topY, int topZ,
                     int leastGenerationNumber, int bestQueueSize, int leastBestStayGeneration, double maxFitnessDifferenceBetweenBestAndWorst) {
        this.calTime = calTime;
        this.populationSize = populationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.topX = topX;
        this.topY = topY;
        this.topZ = topZ;
        this.leastGenerationNumber = leastGenerationNumber;
        this.bestQueueSize = bestQueueSize;
        this.leastBestStayGeneration = leastBestStayGeneration;
        this.maxFitnessDifferenceBetweenBestAndWorst = maxFitnessDifferenceBetweenBestAndWorst;
        this.solutionReportList = new ArrayList<>();
    }

    /**
     * Initialize this solver before running it.
     * If the {@code baseDir} is empty, set it to the value from {@code DataExtractor}, and get data of TSP by default.
     * If the {@code baseDir} is not empty, get data of TSP according to it.
     */
    private void init() {
        if (index == -1) {
            List<Object> tsp = tspLocal.get();
            optimalSolution = (Solution) tsp.get(2);
        } else {
            List<Object> tsp = DataExtractor.instance.getTspByIndex(index);
            tspLocal.set(tsp);
            optimalSolution = (Solution) tsp.get(2);
            /* Makes solver id equal to index. */
            idLocal.set(index);
        }
        String dirName = (String) tspLocal.get().get(0);
        log.info(String.format("[%d] The TSP [%s] is being solved.", idLocal.get(), dirName));
    }

    @Override
    public void run() {
        /* Initializes this TSP solver. */
        init();
        int numberOfLoci = getPoints().size();

        for (int time = 1; time <= calTime; ++time) {
            SolutionGroup solutionGroup = SolutionGroup.Builder.ofNew()
                    /* Traditional GA parameters. */
                    .populationSize(populationSize)
                    .withCrossoverProbability(crossoverProbability)
                    .withMutationProbability(mutationProbability)
                    /* Generating strategy. */
                    .withGenerationOperator(new RandomGeneratingOperator(4))
                    .withGenerationOperator(new NearestKNeighborsGreedyGeneratingOperator(18, 1))
                    .withGenerationOperator(new ShortestKEdgeGreedyGeneratingOperator(12, 2))
                    .withGenerationOperator(new ConvexHullConstrictionGeneratingOperator(48, 3))
                    .withGenerationOperator(new ConvexHullDivisionGeneratingOperator(18))
                    /* Crossover strategy. */
                    .withCrossoverOperator(new SinglePointCrossoverOperator(10, (int) (numberOfLoci * 0.050)))
                    .withCrossoverOperator(new SinglePointCrossoverOperator(10, (int) (numberOfLoci * 0.100)))
                    .withCrossoverOperator(new SinglePointCrossoverOperator(10, (int) (numberOfLoci * 0.300)))
                    .withCrossoverOperator(new SectionCrossoverOperator(10))
                    .withCrossoverOperator(new NearestNeighborCrossoverOperator(60))
                    /* Mutation strategy. */
                    .withMutationOperator(new MultiPointMutationOperator(10, (int) (numberOfLoci * 0.050)))
                    .withMutationOperator(new MultiPointMutationOperator(10, (int) (numberOfLoci * 0.125)))
                    .withMutationOperator(new RangeReversingMutationOperator(10, (int) (numberOfLoci * 0.125)))
                    .withMutationOperator(new RangeReversingMutationOperator(20, (int) (numberOfLoci * 0.250)))
                    .withMutationOperator(new RangeReversingMutationOperator(50, (int) (numberOfLoci * 0.650)))
                    /* Selection strategy. */
                    .withSelectionOperator(new RouletteSelectionOperator(100))
                    .withTopX(topX)
                    .withTopY(topY)
                    .withTopZ(topZ)
                    .withBestQueueSize(bestQueueSize)
                    .build();

            long beforeInit = System.currentTimeMillis();

            /* Initializes population. */
            solutionGroup.initialize();

            long afterInit = System.currentTimeMillis();
            double initUsedTime = (double) (afterInit - beforeInit) / 1000;
            log.info(String.format("[%d] Population-%d initialization finished. It took time %ss.", idLocal.get(), time, initUsedTime));

            try {
                /* Evolution. */
                solutionGroup.evolve(new StopCondition(leastGenerationNumber, leastBestStayGeneration, maxFitnessDifferenceBetweenBestAndWorst));

                long afterEvolution = System.currentTimeMillis();
                double evolutionUsedTime = (double) (afterEvolution - afterInit) / 1000;
                log.info(String.format("[%d] Population-%d evolution finished. It took time %ss.", idLocal.get(), time, evolutionUsedTime));

                /* Obtains the approximate optimal solution. */
                Solution solution = solutionGroup.getBest();
                int generationNumber = solutionGroup.getGen();
                SolutionReport report = new SolutionReport(time, solution, generationNumber, initUsedTime, evolutionUsedTime);
                solutionReportList.add(report);

                /* Records the result. */
                String result = reportSolution(solution, generationNumber);
                log.info(String.format("[%d] Population-%d result is: %s", idLocal.get(), time, result));
            } catch (Exception e) {
                log.error(String.format("[%d] Population-%d evolution stopped because of exception: ", idLocal.get(), time), e);
                throw new RuntimeException(e);
            }
        }

        solutionReportList.sort(Comparator.reverseOrder());
        Solution bestSolution = solutionReportList.get(0).solution;
        double bestDistance = bestSolution.getDistance();
        double bestQuality = (bestDistance - optimalSolution.getDistance()) / optimalSolution.getDistance();
        averageDistance = solutionReportList.stream()
                .map(SolutionReport::getDistance)
                .reduce(0d, Double::sum) / calTime;
        averageGenerationNumber = solutionReportList.stream()
                .map(SolutionReport::getGenerationNumber)
                .map(t -> (double)t)
                .reduce(0d, Double::sum) / calTime;
        averageInitUsedTime = solutionReportList.stream()
                .map(SolutionReport::getInitUsedTime)
                .reduce(0d, Double::sum) / calTime;
        averageEvolutionUsedTime = solutionReportList.stream()
                .map(SolutionReport::getEvolutionUsedTime)
                .reduce(0d, Double::sum) / calTime;
        double averageQuality = (averageDistance - optimalSolution.getDistance()) / optimalSolution.getDistance();
        double averageAlgorithmUsedTime = averageInitUsedTime + averageEvolutionUsedTime;

        String dirName = (String) tspLocal.get().get(0);

        log.info(String.format("[%d] Evolution of all populations finished.", idLocal.get()));

        report = String.format("Here is the report of the improved genetic algorithm of solving [%s]:\n" +
                        "calculation times: %d\n" +
                        "average generation number: %.2f\n" +
                        "average initialization cost time: %.2fs\n" +
                        "average evolution cost time: %.2fs\n" +
                        "average overall cost time: %.2fs\n" +
                        "average distance: %.3f [%.2f%%]\n" +
                        "best obtained distance: %.3f [%.2f%%]\n" +
                        "best obtained solution: %s\n" +
                        "true optimal distance: %.3f [%.2f%%]\n" +
                        "true optimal solution: %s",
                dirName, calTime, averageGenerationNumber,
                averageInitUsedTime, averageEvolutionUsedTime, averageAlgorithmUsedTime,
                averageDistance, averageQuality * 100,
                bestDistance, bestQuality * 100,
                bestSolution,
                optimalSolution.getDistance(), 0d,
                optimalSolution);

        log.info(String.format("[%d] %s", idLocal.get(), report));
    }

    private String reportSolution(Solution solution, int generationNumber) {
        double quality = (solution.getDistance() - optimalSolution.getDistance()) / optimalSolution.getDistance() * 100;
        return String.format("true optimal solution: [%.0f], approximate optimal solution: [%.0f], quality: [%.3f%%], generation number: %d, specific information: %s",
                optimalSolution.getDistance(), solution.getDistance(), quality, generationNumber, solution);
    }

    /**
     * Gets the optimal solution of this TSP, if it exists; if it doesn't, gets {@code null}.
     *
     * @return the optimal solution of this TSP if exists, or {@code null} if it doesn't
     */
    public Solution getOptimalSolution() {
        return optimalSolution;
    }

    public String getReport() {
        return report;
    }

    /**
     * Gets the list of points of the TSP being solved by current thread.
     *
     * @return the point list of the TSP being solved by current thread
     */
    @SuppressWarnings("unchecked")
    public static List<? extends AbstractPoint> getPoints() {
        return (List<? extends AbstractPoint>) tspLocal.get().get(1);
    }

    /**
     * Gets the fitness function adapted to the TSP being solved by current thread.
     *
     * @return the fitness function adapted to the TSP being solved by current thread
     */
    @SuppressWarnings("unchecked")
    public static UnaryOperator<Double> getFitnessFunction() {
        return (UnaryOperator<Double>) tspLocal.get().get(3);
    }

    /**
     * Gets the 2-dimensional distance array of the TSP being solved by current thread.
     *
     * @return the 2-dimensional distance array of the TSP being solved by current thread
     */
    public static double[][] getDistArray() {
        return (double[][]) tspLocal.get().get(4);
    }

    /**
     * Fully calculates the distance array of the TSP being solved by the current thread.
     */
    public static void fullyCalDistArray() {
        Boolean full = (Boolean) tspLocal.get().get(5);
        if (!full) {
            List<? extends AbstractPoint> pList = getPoints();
            double[][] distArray = getDistArray();
            int size = pList.size();
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (i == j) continue;
                    distArray[i][j] = pList.get(i).distanceTo(pList.get(j));
                }
            }
            List<Object> list = new ArrayList<>(tspLocal.get());
            list.remove(5);
            list.add(5, Boolean.TRUE);
            tspLocal.set(list);
        }
    }

    private static class SolutionReport implements Comparable<SolutionReport> {

        private int order;
        private Solution solution;
        private int generationNumber;
        private double initUsedTime;
        private double evolutionUsedTime;

        SolutionReport(int order, Solution solution, int generationNumber, double initUsedTime, double evolutionUsedTime) {
            this.order = order;
            this.solution = solution;
            this.generationNumber = generationNumber;
            this.initUsedTime = initUsedTime;
            this.evolutionUsedTime = evolutionUsedTime;
        }

        @Override
        public int compareTo(SolutionReport o) {
            return solution.compareTo(o.solution);
        }

        public int getGenerationNumber() {
            return generationNumber;
        }

        public double getInitUsedTime() {
            return initUsedTime;
        }

        public double getEvolutionUsedTime() {
            return evolutionUsedTime;
        }

        public double getDistance() {
            return solution.getDistance();
        }
    }
}