package org.pursuemoon.solvetsp;

import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.DataExtractor;

import java.util.List;

public class Application {

    private static final int DEFAULT_CAL_TIME = 10;

    public static void main(String[] args) {
        if (args.length == 0) {

        }
        if (args.length == 1) {
            if (args[0].equals("--cases")) {
                printCases();
            }
            if (args[0].equals("--test")) {
                testAll(DEFAULT_CAL_TIME);
            }
        }
        if (args.length == 2) {
            if (args[0].equals("--test")) {
                if (args[1].equals("all")) {
                    testAll(DEFAULT_CAL_TIME);
                } else {
                    int index = Integer.parseInt(args[1]);
                    testCase(index, DEFAULT_CAL_TIME);
                }
            }
        }
        if (args.length == 4) {
            if (args[0].equals("--test") && args[2].equals("--times")) {
                int times = Integer.parseInt(args[3]);
                if (args[1].equals("all")) {
                    testAll(times);
                } else {
                    int index = Integer.parseInt(args[1]);
                    testCase(index, times);
                }
            }
        }
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
        System.out.println("You can type the following command to start a test case:\n" +
                "java -jar [JarArchive] --test [Number]");
    }

    private static void testAll(int times) {
        DataExtractor dataExtractor = DataExtractor.instance;
        int size = dataExtractor.getTestDirList().size();
        for (int i = 0; i < size; ++i) {
            testCase(i, times);
        }
    }

    private static void testCase(int index, int times) {
        try {
            TspSolver tspSolver = new TspSolver(index, times);
            Thread thread = new Thread(tspSolver);
            thread.start();
            thread.join();
            System.out.println(tspSolver.getReport());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

