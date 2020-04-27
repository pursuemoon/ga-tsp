package org.pursuemoon.solvetsp;

import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.DataExtractor;

import java.util.List;

public class Application {

    private static final int DEFAULT_CAL_TIME = 10;

    private static final int DEFAULT_POPULATION_SIZE = 35;
    private static final double DEFAULT_CROSSOVER_PROBABILITY = 0.96;
    private static final double DEFAULT_MUTATION_PROBABILITY = 0.66;

    private static final int DEFAULT_TOP_X = 2;
    private static final int DEFAULT_TOP_Y = 5;
    private static final int DEFAULT_TOP_Z = 3;
    private static final int DEFAULT_LEAST_GENERATION_NUMBER = 2000;
    private static final int DEFAULT_BEST_QUEUE_SIZE = 500;
    private static final int DEFAULT_LEAST_BEST_STAY_GENERATION = 1000;
    private static final double DEFAULT_MAX_FITNESS_DIFFERENCE_BETWEEN_BEST_AND_WORST = 1e-7;

    public static void main(String[] args) {
        int size = args.length;
        if (size == 0) {
            printHelp();
            return;
        }

        String index = "";
        int calTime = DEFAULT_CAL_TIME;
        int populationSize = DEFAULT_POPULATION_SIZE;
        double crossoverProbability = DEFAULT_CROSSOVER_PROBABILITY;
        double mutationProbability = DEFAULT_MUTATION_PROBABILITY;
        int topX = DEFAULT_TOP_X;
        int topY = DEFAULT_TOP_Y;
        int topZ = DEFAULT_TOP_Z;
        int leastGenerationNumber = DEFAULT_LEAST_GENERATION_NUMBER;
        int bestQueueSize = DEFAULT_BEST_QUEUE_SIZE;
        int leastBestStayGeneration = DEFAULT_LEAST_BEST_STAY_GENERATION;
        double maxFitnessDifferenceBetweenBestAndWorst = DEFAULT_MAX_FITNESS_DIFFERENCE_BETWEEN_BEST_AND_WORST;

        for (int i = 0; i < size; ++i) {
            String s = args[i];
            if (s.equals("--help")) {
                printHelp();
                return;
            }
            if (s.equals("--cases")) {
                printCases();
                return;
            }
            if (s.equals("--test")) {
                index = args[++i];
                continue;
            }
            if (s.equals("--times")) {
                try {
                    calTime = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--size")) {
                try {
                    populationSize = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--pc")) {
                try {
                    crossoverProbability = Double.parseDouble(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--pm")) {
                try {
                    mutationProbability = Double.parseDouble(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--topx")) {
                try {
                    topX = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--topy")) {
                try {
                    topY = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--topz")) {
                try {
                    topZ = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--gen")) {
                try {
                    leastGenerationNumber = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--queue")) {
                try {
                    bestQueueSize = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--stay")) {
                try {
                    leastBestStayGeneration = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            if (s.equals("--diff")) {
                try {
                    maxFitnessDifferenceBetweenBestAndWorst = Double.parseDouble(args[++i]);
                } catch (NumberFormatException ne) {
                    String para = s + " " + args[i];
                    wrongParaHalt(para);
                }
                continue;
            }
            wrongParaHalt(s);
        }
        if (index.equals("all")) {
            testAll(calTime, populationSize, crossoverProbability, mutationProbability, topX, topY, topZ,
                    leastGenerationNumber, bestQueueSize, leastBestStayGeneration, maxFitnessDifferenceBetweenBestAndWorst);
        } else {
            try {
                int kase = Integer.parseInt(index);
                if (kase >= size) {
                    System.out.println("No such test case exists.");
                    return;
                }
                testCase(kase, calTime, populationSize, crossoverProbability, mutationProbability, topX, topY, topZ,
                        leastGenerationNumber, bestQueueSize, leastBestStayGeneration, maxFitnessDifferenceBetweenBestAndWorst);
            } catch (NumberFormatException ne) {
                String para = "--test " + index;
                wrongParaHalt(para);
            }
        }
    }

    private static void wrongParaHalt(String para) {
        String m = String.format("Unknown parameter: %s", para);
        System.out.println(m);
        System.exit(-1);
    }

    private static void printHelp() {
        String help = "";
        help += "Usage: java -jar JarArchiveName.jar [Parameters]\n";
        help += "***************** Here is all of optional parameters ****************\n";
        help += "--help                 get the help manual like this\n";
        help += "--cases                get all test cases with their order number\n";
        help += "--test all             indicate that all cases should be tested\n";
        help += "--test Number        designate the case to be tested\n";
        help += "--times Number       designate the number of calculation time \n";
        help += "--size Number        designate the size of population\n";
        help += "--pc Number          designate the probability of crossover\n";
        help += "--pm Number          designate the probability of mutation\n";
        help += "--topx Number        designate the number of individuals to remain\n" +
                "                   before every crossover\n";
        help += "--topy Number        designate the number of individuals to remain\n" +
                "                   before every mutation\n";
        help += "--topz Number        designate the number of individuals to remain\n" +
                "                   before every selection\n";
        help += "--queue Number       designate the number of best individuals\n" +
                "                   maintained in the priority queue while in\n" +
                "                   evolution\n";
        help += "--gen Number         designate the number of generations which\n" +
                "                   means evolution should iterate for at least so\n" +
                "                   many generations\n";
        help += "--stay Number        designate the number of generations which\n" +
                "                   means the best obtained individual should stay\n" +
                "                   in the priority queue for at least so many\n" +
                "                   generations\n";
        help += "--diff Number        designate the difference which means the\n" +
                "                   difference between the fitness of best individual\n" +
                "                   and that of the worst one should not be greater\n" +
                "                   than this value\n";
        help += "**************** Above is all of optional parameters ****************\n";
        help += "You can type the following command to start a test case:\n" +
                "java -jar JarArchiveName.jar --test CaseNumber [Parameters]";
        System.out.println(help);
    }

    private static void printCases() {
        DataExtractor dataExtractor = DataExtractor.instance;
        List<String> testDirList = dataExtractor.getTestDirList();
        System.out.println("***************** Here is all of test cases ****************");
        int size = testDirList.size();
        for (int i = 0; i < size; ++i) {
            String s = testDirList.get(i);
            System.out.println(String.format("[%d] %s", i, s));
        }
        System.out.println("**************** Above is all of test cases ****************");
    }

    private static void testAll(int calTime,
                                int populationSize, double crossoverProbability, double mutationProbability,
                                int topX, int topY, int topZ,
                                int leastGenerationNumber, int bestQueueSize, int leastBestStayGeneration, double maxFitnessDifferenceBetweenBestAndWorst) {
        DataExtractor dataExtractor = DataExtractor.instance;
        int size = dataExtractor.getTestDirList().size();
        for (int i = 0; i < size; ++i) {
            testCase(i, calTime, populationSize, crossoverProbability, mutationProbability, topX, topY, topZ,
                    leastGenerationNumber, bestQueueSize, leastBestStayGeneration, maxFitnessDifferenceBetweenBestAndWorst);
        }
    }

    private static void testCase(int index, int calTime,
                                 int populationSize, double crossoverProbability, double mutationProbability,
                                 int topX, int topY, int topZ,
                                 int leastGenerationNumber, int bestQueueSize, int leastBestStayGeneration, double maxFitnessDifferenceBetweenBestAndWorst) {
        try {
            TspSolver tspSolver = new TspSolver(index, calTime,
                    populationSize, crossoverProbability, mutationProbability, topX, topY, topZ,
                    leastGenerationNumber, bestQueueSize, leastBestStayGeneration, maxFitnessDifferenceBetweenBestAndWorst);
            Thread thread = new Thread(tspSolver);
            thread.start();
            thread.join();
            System.out.println(tspSolver.getReport());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

