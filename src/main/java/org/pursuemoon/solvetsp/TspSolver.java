package org.pursuemoon.solvetsp;

import org.apache.log4j.Logger;
import org.pursuemoon.solvetsp.operator.*;
import org.pursuemoon.solvetsp.util.DataExtractor;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

/**
 * Main class to solve the TSP.
 */
public final class TspSolver implements Runnable {

    private static Logger log = Logger.getLogger(TspSolver.class);

    /** The atomic integer to initialize threads that solve TSPs. */
    private static AtomicInteger atomicInteger = new AtomicInteger(1);

    /** The data extractor to get points of the TSP which is about to be solved. */
    private static DataExtractor dataExtractor = new DataExtractor();

    /** The id of the current solver thread. */
    public static ThreadLocal<Integer> idLocal = ThreadLocal.withInitial(() -> atomicInteger.getAndAdd(1));

    /** The information about the TSP being solved by the current solver thread. */
    public static ThreadLocal<List<Object>> tspLocal = ThreadLocal.withInitial(() -> dataExtractor.nextTsp());

    /** The path of the base directory of this TSP. */
    private String baseDir;

    /** The optimal solution of this TSP. */
    private Solution optimalSolution;

    /**
     * Default constructor.
     */
    public TspSolver() {
        baseDir = "";
    }

    /**
     * Constructor with {@code baseDir}.
     *
     * @param baseDir the base direction of a specific TSP
     */
    public TspSolver(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Initialize this solver before running it.
     * If the {@code baseDir} is empty, set it to the value from {@code DataExtractor}, and get data of TSP by default.
     * If the {@code baseDir} is not empty, get data of TSP according to it.
     */
    public void init() {
        if (baseDir.isEmpty()) {
            List<Object> tsp = tspLocal.get();
            baseDir = (String) tsp.get(0);
            optimalSolution = (Solution) tsp.get(2);
        } else {
            String name = new File(baseDir).getName();
            List<AbstractPoint> pList = DataExtractor.extractPoints(String.format("%s/%s.tsp", baseDir, name));
            Solution opt = DataExtractor.extractSolution(String.format("%s/%s.opt.tour", baseDir, name), true);
            List<Object> tsp = Arrays.asList(baseDir, pList, opt);
            tspLocal.set(tsp);
            optimalSolution = opt;
        }
        log.info(String.format("The TSP in directory [%s] is being solved.\n" +
                "Solver id is [%d].", baseDir, idLocal.get()));
    }

    /**
     * Gets the optimal solution of this TSP, if it exists; if it doesn't, gets {@code null}.
     *
     * @return the optimal solution of this TSP if exists, or {@code null} if it doesn't
     */
    public Solution getOptimalSolution() {
        return optimalSolution;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        init();
        SolutionGroup solutionGroup = SolutionGroup.Builder.ofNew()
                .populationSize(100).withCrossoverProbability(0.96).withMutationProbability(0.60)
                .withGenerationOperator(new RandomGeneratingOperator(1))
                .withGenerationOperator(new NearestKNeighborsGreedyGeneratingOperator(33, 1))
                .withGenerationOperator(new ShortestKEdgeGreedyGeneratingOperator(33, 2))
                .withGenerationOperator(new ConvexHullConstrictionGeneratingOperator(33, 2))
                .withCrossoverOperator(new SinglePointCrossoverOperator(30, 20))
                .withCrossoverOperator(new SectionCrossoverOperator(70))
                .withMutationOperator(new MultiPointMutationOperator(100, 5))
                .withSelectionOperator(new RouletteSelectionOperator(100))
                .withTopX(5)
                .withTopY(15)
                .withTopZ(10)
                .withBestQueueSize(200)
                .build();
        solutionGroup.initialize();
        try {
            log.info("The evolution is beginning.");
//            solutionGroup.evolve(Condition.ofMaxGenerationCondition(300));
//            solutionGroup.evolve(Condition.ofBestWorstDifferenceCondition(1e-5));
//            solutionGroup.evolve(Condition.ofBestStayGenerationCondition(5000));
            solutionGroup.evolve(new StopCondition(200, 200, 1e-7));
            log.info("The evolution finished.");
        } catch (Exception e) {
            log.error("The evolution stopped because of exception: ", e);
            throw new RuntimeException(e);
        }
        Solution solution = solutionGroup.getBest();
        int genNum = solutionGroup.getGen();
        long endTime = System.currentTimeMillis();
        double usedTime = (double) (endTime - startTime) / 1000 ;

        log.info(String.format("The optimal solution is: %s", optimalSolution));
        log.info(String.format("The best solution is: %s", solution));
        log.info(String.format("The quality of solution is: %.2f%%",
                (solution.getDistance() - optimalSolution.getDistance()) / optimalSolution.getDistance() * 100));
        log.info(String.format("The number of generations this algorithm has gone through: %d", genNum));
        log.info(String.format("This algorithm takes time: %.3fs", usedTime));

        // TODO : 图形化遗传算法结果
    }

    public static void setDataExtractor(String str) {
        dataExtractor = new DataExtractor(str);
    }

    @SuppressWarnings("unchecked")
    public static List<AbstractPoint> getPoints() {
        return (List<AbstractPoint>) TspSolver.tspLocal.get().get(1);
    }

    @SuppressWarnings("unchecked")
    public static UnaryOperator<Double> getFitnessFunction() {
        return (UnaryOperator<Double>) TspSolver.tspLocal.get().get(3);
    }

    public static double[][] getDistArray() {
        return (double[][]) TspSolver.tspLocal.get().get(4);
    }

    public static void main(String[] args) {
        TspSolver tspSolver = new TspSolver();
        Thread thread = new Thread(tspSolver);
        thread.start();

        // 经纬度的测试
//        String testDir = Objects.requireNonNull(TspSolver.class.getClassLoader()
//                .getResource("tsp_test/test_GEO")).getPath();
//        setDataExtractor(testDir);
//        for (int i = 1; i <= 1; i++) {
//            TspSolver tspSolver = new TspSolver();
//            Thread thread = new Thread(tspSolver);
//            thread.start();
//        }
    }
}