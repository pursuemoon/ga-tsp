package org.pursuemoon.solvetsp;

import org.apache.log4j.Logger;
import org.pursuemoon.ai.ga.util.Condition;
import org.pursuemoon.solvetsp.model.AbstractPoint;
import org.pursuemoon.solvetsp.model.Solution;
import org.pursuemoon.solvetsp.model.SolutionGroup;
import org.pursuemoon.solvetsp.model.operator.*;
import org.pursuemoon.solvetsp.util.DataExtractor;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    /** The TSP being solved by the current solver thread. */
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
        log.info(String.format("solver id: [%d]\ndirectory: [%s]\n" +
                "optimal solution: [%s]", idLocal.get(), baseDir, optimalSolution.getDistance()));
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
        // TODO : 计时器
        init();
        SolutionGroup solutionGroup = SolutionGroup.Builder.ofNew()
                .populationSize(100).withCrossoverProbability(0.96).withMutationProbability(0.60)
                .withGenerationOperator(new RandomGeneratingOperator(100))
                .withCrossoverOperator(new SinglePointCrossoverOperator(30, 30))
                .withCrossoverOperator(new SectionCrossoverOperator(70))
                .withMutationOperator(new MultiPointMutationOperator(100, 80))
                .withSelectionOperator(new RouletteSelectionOperator(100))
                .withTopX(5)
                .withTopY(10)
                .withTopZ(15)
                .build();
        solutionGroup.initialize();
        try {
            log.info("The evolution is beginning.");
            solutionGroup.evolve(Condition.ofMaxGenerationCondition(500));
            log.info("The evolution finished.");
        } catch (Exception e) {
            log.error("The evolution stopped because of exception: ", e);
            throw new RuntimeException(e);
        }
        log.info(String.format("The optimal solution is: %s", optimalSolution));
        Solution solution = solutionGroup.getBest();
        log.info(String.format("The best solution is: %s", solution));
        // TODO : 图形化遗传算法结果
    }

    private static void setDataExtractor(String str) {
        dataExtractor = new DataExtractor(str);
    }

    @SuppressWarnings("unchecked")
    public static List<AbstractPoint> getPoints() {
        return (List<AbstractPoint>) TspSolver.tspLocal.get().get(1);
    }

    public static void main(String[] args) {
        TspSolver tspSolver = new TspSolver();
        Thread thread = new Thread(tspSolver);
        thread.start();

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