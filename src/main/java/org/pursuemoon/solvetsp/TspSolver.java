package org.pursuemoon.solvetsp;

import org.apache.log4j.Logger;
import org.pursuemoon.solvetsp.operator.*;
import org.pursuemoon.solvetsp.util.DataExtractor;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        log.info(String.format("[%d] The TSP in directory [%s] is being solved.", idLocal.get(), baseDir));
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
                .populationSize(30).withCrossoverProbability(0.96).withMutationProbability(0.60)
                .withGenerationOperator(new RandomGeneratingOperator(4))
                .withGenerationOperator(new NearestKNeighborsGreedyGeneratingOperator(36, 1))
                .withGenerationOperator(new ShortestKEdgeGreedyGeneratingOperator(12, 2))
                .withGenerationOperator(new ConvexHullConstrictionGeneratingOperator(36, 3))
                .withGenerationOperator(new ConvexHullDivisionGeneratingOperator(12))
                .withCrossoverOperator(new SinglePointCrossoverOperator(2, 20))
                .withCrossoverOperator(new SectionCrossoverOperator(3))
                .withCrossoverOperator(new NearestNeighborCrossoverOperator(95))
                .withMutationOperator(new MultiPointMutationOperator(20, 5))
                .withMutationOperator(new RangeReversingMutationOperator(30, 65))
                .withMutationOperator(new RangeReversingMutationOperator(50, 15))
                .withSelectionOperator(new RouletteSelectionOperator(100))
                .withTopX(2)
                .withTopY(4)
                .withTopZ(3)
                .withBestQueueSize(200)
                .build();
        solutionGroup.initialize();

        long initEndTime = System.currentTimeMillis();
        double initUsedTime = (double) (initEndTime - startTime) / 1000;
        log.info(String.format("[%d] Population initialization finished. It took time %ss.", idLocal.get(), initUsedTime));
        try {
            solutionGroup.evolve(new StopCondition(500, 500, 1e-7));

            long evolutionEndTime = System.currentTimeMillis();
            double EvolutionUsedTime = (double) (evolutionEndTime - initEndTime) / 1000;
            log.info(String.format("[%d] Population evolution finished. It took time %ss.", idLocal.get(), EvolutionUsedTime));
        } catch (Exception e) {
            log.error(String.format("[%d] The evolution stopped because of exception: ", idLocal.get()), e);
            throw new RuntimeException(e);
        }
        Solution solution = solutionGroup.getBest();
        int genNum = solutionGroup.getGen();
        log.info(String.format("[%d] The optimal solution is: %s", idLocal.get(), optimalSolution));
        log.info(String.format("[%d] The best solution is: %s", idLocal.get(), solution));
        log.info(String.format("[%d] The quality of solution is: %.2f%%", idLocal.get(),
                (solution.getDistance() - optimalSolution.getDistance()) / optimalSolution.getDistance() * 100));
        log.info(String.format("[%d] The number of generations this algorithm has gone through: %d", idLocal.get(), genNum));

        // TODO : 图形化遗传算法结果
    }

    public static void setDataExtractor(String str) {
        dataExtractor = new DataExtractor(str);
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

    public static void main(String[] args) {
        for (int i = 1; i <= 1; ++i) {
            TspSolver tspSolver = new TspSolver();
            Thread thread = new Thread(tspSolver);
            thread.start();
        }

        // 经纬度的测试
//        String testDir = Objects.requireNonNull(TspSolver.class.getClassLoader()
//                .getResource("tsp_test/test_GEO")).getPath();
//        setDataExtractor(testDir);
//        for (int i = 1; i <= 5; i++) {
//            TspSolver tspSolver = new TspSolver();
//            Thread thread = new Thread(tspSolver);
//            thread.start();
//        }
    }
}