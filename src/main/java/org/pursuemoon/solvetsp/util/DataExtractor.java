package org.pursuemoon.solvetsp.util;

import org.apache.log4j.Logger;
import org.pursuemoon.solvetsp.model.Euc2DPoint;
import org.pursuemoon.solvetsp.model.GeoPoint;
import org.pursuemoon.solvetsp.model.AbstractPoint;
import org.pursuemoon.solvetsp.model.Solution;

import java.io.*;
import java.util.*;

/**
 * Data reader, aiming to obtain formatted TSP points and optimal solutions.
 */
public final class DataExtractor {

    private static Logger log = Logger.getLogger(DataExtractor.class);

    private static final String defaultDir = Objects.requireNonNull(DataExtractor.class.getClassLoader()
            .getResource("tsp_test/test_EUC_2D")).getPath();

    private List<File> testDirList;
    private int idx;

    public DataExtractor() {
        this(defaultDir);
    }

    /**
     * Constructor with an absolute path of the tsp test directory of one type.
     *
     * By now, the following directories are supported:
     * 1. tsp_test/test_EUC_2D
     * 2. tsp_test/test_GEO
     *
     * @param testTypeDir the absolute path of the test directory of one type
     */
    public DataExtractor(String testTypeDir) {
        File file = new File(testTypeDir);
        testDirList = Arrays.asList(Objects.requireNonNull(file.listFiles(File::isDirectory)));
        if (testDirList.size() == 0) {
            String m = String.format("There is no test directory in dir: %s.", testTypeDir);
            log.error(m);
            throw new RuntimeException(m);
        }
        idx = 0;
    }

    /**
     * Gets the next TSP.
     *
     * The first element is its name.
     * The second element is its points.
     * The third element is its optimal solution if it exists, or {@code null} if not.
     *
     * @return the list that represents a TSP
     */
    public List<Object> nextTsp() {
        if (idx == testDirList.size()) {
            String m = "No more TSP test cases.";
            log.error(m);
            throw new RuntimeException(m);
        }
        File file = testDirList.get(idx++);
        String filePath = file.getPath();
        String fileName = file.getName();
        List<AbstractPoint> pList = extractPoints(String.format("%s/%s.tsp", filePath, fileName));
        Solution solution = extractSolution(String.format("%s/%s.opt.tour", filePath, fileName), true);
        return Arrays.asList(filePath, pList, solution);
    }

    /**
     * Gets the list of points from the specified absolute file path.
     *
     * @param fileDir the specified absolute file path
     * @return the list of points
     */
    public static List<AbstractPoint> extractPoints(String fileDir) {
        List<AbstractPoint> pList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileDir))) {
            String str;
            int cnt = 0, typeOfPoint = 0;
            while ((str = bufferedReader.readLine()) != null) {
                if (++cnt <= 4 || cnt == 6 || typeOfPoint == 2 && cnt == 7)
                    continue;
                if (cnt == 5) {
                    if (str.trim().endsWith("EUC_2D"))
                        typeOfPoint = 1;
                    else if (str.trim().endsWith("GEO"))
                        typeOfPoint = 2;
                    continue;
                }
                if (str.trim().equals("EOF")) break;
                String[] strings = str.split("\\s+", 0);
                int order = Integer.parseInt(!strings[0].isEmpty() ? strings[0] : strings[1]);
                double d1 = Double.parseDouble(!strings[0].isEmpty() ? strings[1] : strings[2]);
                double d2 = Double.parseDouble(!strings[0].isEmpty() ? strings[2] : strings[3]);
                AbstractPoint point = (typeOfPoint == 1 ? new Euc2DPoint(order, d1, d2) : new GeoPoint(order, d1, d2));
                pList.add(point);
            }
        } catch (Exception e) {
            String m = String.format("Extraction of file [%s] failed.", fileDir);
            log.error(m, e);
            throw new RuntimeException(e);
        }
        return pList;
    }

    /**
     * Gets the optimal solution from the specified absolute file path.
     *
     * @param fileDir the specified absolute file path
     * @return the optimal solution if the solution path exists, or {@code null} if not
     */
    public static Solution extractSolution(String fileDir, boolean beginWith1) {
        Solution solution;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileDir))) {
            ArrayList<Integer> integers = new ArrayList<>();
            String str;
            boolean reached = false;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.trim().equals("-1"))
                    break;
                if (reached) {
                    String[] strings = str.split("\\s+");
                    for (String s : strings) {
                        if (s.isEmpty())
                            continue;
                        integers.add(Integer.parseInt(s));
                    }
                }
                if (str.trim().equals("TOUR_SECTION"))
                    reached = true;
            }
            solution = new Solution(integers.toArray(new Integer[0]), true);
        } catch (FileNotFoundException fnfe) {
            String m = String.format("File [%s] was not found, so null will be returned.", fileDir);
            log.warn(m);
            return null;
        } catch (IOException e) {
            String m = String.format("Extraction of file [%s] failed.", fileDir);
            log.error(m, e);
            throw new RuntimeException(e);
        }
        return solution;
    }
}
