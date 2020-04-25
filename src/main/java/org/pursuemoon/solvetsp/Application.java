package org.pursuemoon.solvetsp;

import org.apache.log4j.Logger;
import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.DataExtractor;

import java.util.List;

public class Application {

    private static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) {
        if (args.length == 0) {

        }
        if (args.length == 1) {
            if (args[0].equals("--case")) {
                printCases();
            }
            if (args[0].equals("--test")) {
                testAll();
            }
        }
        if (args.length == 2) {
            if (args[0].equals("--test")) {
                if (args[1].equals("all")) {
                    testAll();
                } else {
                    int index = Integer.parseInt(args[1]);
                    testCase(index);
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

    private static void testAll() {
        // TODO : 全算例计算
        DataExtractor dataExtractor = DataExtractor.instance;
        int size = 2;//dataExtractor.getTestDirList().size();
        for (int i = 0; i < size; ++i) {
            testCase(i);
        }
    }

    private static void testCase(int index) {
        try {
            TspSolver tspSolver = new TspSolver(index, 3);
            Thread thread = new Thread(tspSolver);
            thread.start();
            thread.join();
            System.out.println(tspSolver.getReport());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
