package org.pursuemoon.solvetsp.util;

import org.apache.log4j.Logger;
import org.pursuemoon.solvetsp.util.geometry.Euc2DPoint;
import org.pursuemoon.solvetsp.util.geometry.GeoPoint;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;
import org.pursuemoon.solvetsp.ga.Solution;

import java.io.*;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Data reader, aiming to obtain formatted TSP points and optimal solutions.
 */
public final class DataExtractor {

    private static Logger log = Logger.getLogger(DataExtractor.class);

    /** Singleton instance. */
    public static DataExtractor instance = new DataExtractor();

    private static final String TSP_TEST_DIR = "tsp_test/";
    private static final String TSP_TEST_EUC_2D_DIR = "tsp_test/test_EUC_2D/";
    private static final String TSP_TEST_GEO_DIR = "tsp_test/test_GEO/";

    /** List of names of test directories. */
    private List<String> testDirList;

    /** Index of test directory being processed now. */
    private int idx;

    private static Random random = new Random();

    /**
     * Constructor of {@code DataExtractor} which initializes {@code testDirList}.
     *
     * Directories for test will be found inside jar archive.
     */
    private DataExtractor() {
        testDirList = new ArrayList<>();
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try (JarFile jarFile = new JarFile(jarPath)) {
            log.info(String.format("Started from jar file: %s", jarPath));
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = entryEnumeration.nextElement();
                if (isSelected(jarEntry)) {
                    String entryName = jarEntry.getName();
                    testDirList.add(entryName);
                    log.info(String.format("Test directory found: %s", entryName));
                }
            }
        } catch (IOException ioe) {
            log.warn("Not started from command: java -jar archive_name.jar.");
            String testPath = Objects.requireNonNull(getClass().getClassLoader().getResource(TSP_TEST_DIR)).getPath();
            File file = new File(testPath);
            File[] types = file.listFiles(File::isDirectory);
            for (File type : Objects.requireNonNull(types)) {
                String typeName = type.getName();
                if (typeName.equals("test_EUC_2D") || typeName.equals("test_GEO")) {
                    File[] dirs = type.listFiles(File::isDirectory);
                    for (File dir : Objects.requireNonNull(dirs)) {
                        String dirName = dir.getName();
                        String entryName = TSP_TEST_DIR + typeName + "/" + dirName + "/";
                        testDirList.add(entryName);
                        log.info(String.format("Test directory found: %s", entryName));
                    }
                }
            }
        } catch (Exception e) {
            String m = "Extraction of test directory failed.";
            log.error(m, e);
            throw new RuntimeException(e);
        }
        if (testDirList.isEmpty()) {
            log.warn("No case was found.");
        } else {
            log.info(String.format("%d test case(s) was found.", testDirList.size()));
        }
        idx = 0;
    }

    private boolean isSelected(JarEntry jarEntry) {
        String entryName = jarEntry.getName();
        return (jarEntry.isDirectory() &&
                !entryName.equals(TSP_TEST_EUC_2D_DIR) && !entryName.equals(TSP_TEST_GEO_DIR) &&
                (entryName.startsWith(TSP_TEST_EUC_2D_DIR) || entryName.startsWith(TSP_TEST_GEO_DIR)));
    }

    /**
     * Gets the list of test cases found.
     *
     * @return list of test cases found
     */
    public List<String> getTestDirList() {
        return testDirList;
    }

    /**
     * Gets the next TSP.
     *
     * The first element is its name.
     * The second element is its points.
     * The third element is its optimal solution if it exists, or {@code null} if not.
     * The forth element is a fitness function which is adapted to this TSP.
     *      Specifically, the fitness function is: fitness(distance) = 1 / (C * distance + 1e-5), and C depends on
     *      the TSP being solved.
     * The fifth element is a two-dimensional array representing distances between each two points.
     *      This array is filled with -1, which means no distance has been calculated.
     * The sixth element is a Boolean object which representing if the distance array fully calculated.
     *      Its default value is {@code false}. If it is fully calculated, it's {@code true}.
     *
     * @return the list that represents a TSP
     */
    public List<Object> getNextTsp() {
        return getTspByIndex(idx++);
    }

    /**
     * Gets a TSP by index.
     *
     * The first element is its name.
     * The second element is its points.
     * The third element is its optimal solution if it exists, or {@code null} if not.
     * The forth element is a fitness function which is adapted to this TSP.
     *      Specifically, the fitness function is: fitness(distance) = 1 / (C * distance + 1e-5), and C depends on
     *      the TSP being solved.
     * The fifth element is a two-dimensional array representing distances between each two points.
     *      This array is filled with -1, which means no distance has been calculated.
     * The sixth element is a Boolean object which representing if the distance array fully calculated.
     *      Its default value is {@code false}. If it is fully calculated, it's {@code true}.
     *
     * @param index index of a TSP in {@code testDirList}
     * @return the list that represents a TSP
     */
    public List<Object> getTspByIndex(int index) {
        if (index >= testDirList.size() || index < 0) {
            String m = String.format("Illegal index: size = %d, index = %d", testDirList.size(), index);
            log.error(m);
            throw new RuntimeException(m);
        }
        String nextDir = testDirList.get(index);
        String testDir = nextDir.substring(0, nextDir.length() - 1);
        String dirName = testDir.substring(testDir.lastIndexOf("/") + 1);
        List<AbstractPoint> pList = extractPointsByResource(String.format("%s/%s.tsp", testDir, dirName));
        Solution solution = extractSolutionByResource(String.format("%s/%s.opt.tour", testDir, dirName), true);
        UnaryOperator<Double> fitnessFunction = calFitnessFunction(pList);
        @SuppressWarnings("all")
        double[][] distArray = new double[pList.size()][pList.size()];
        for (double[] doubles : distArray) {
            Arrays.fill(doubles, -1);
        }
        return Arrays.asList(dirName, pList, solution, fitnessFunction, distArray, Boolean.FALSE);
    }

    /**
     * Gets a fitness function which is adapted to the specific TSP.
     * The fitness function conforms the following form:
     *      fitness(distance) = 1 / (C * distance + 1e-5).
     *      And C here is the reciprocal of the magnitude of the random distance
     *
     * @param pList the list of points of the specified tsp
     * @return the fitness function adapted to the tsp
     */
    private static UnaryOperator<Double> calFitnessFunction(List<AbstractPoint> pList) {
        /* Gets a genotype randomly. */
        int size = pList.size();
        int[] gene = new int[size];
        for (int i = 0; i < size; ++i)
            gene[i] = i + 1;
        for (int t = size - 1; t >= 0; --t) {
            int pos = random.nextInt(size);
            int temp = gene[pos];
            gene[pos] = gene[t];
            gene[t] = temp;
        }
        /* Calculates the constant C which is the reciprocal of the magnitude of the random distance. */
        double distance = 0;
        distance = pList.get(gene[size - 1] - 1).distanceTo(pList.get(gene[0] - 1));
        for (int i = 1; i < size; ++i)
            distance += pList.get(gene[i - 1] - 1).distanceTo(pList.get(gene[i] - 1));
        int l = 1, r = 310, ans = r - 1;
        while (l < r) {
            int mid = (l + r) >>> 1;
            double get = distance * Math.pow(10, -mid);
            if (get < 1) {
                ans = mid;
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        final double C = Math.pow(10, -ans);
        return t -> 1 / (C * t + 1e-5);
    }

    /**
     * Gets the list of points from the specified relative resource path.
     *
     * @param resourceDir the specified relative resource path
     * @return the list of points
     */
    public static List<AbstractPoint> extractPointsByResource(String resourceDir) {
        List<AbstractPoint> pList = new ArrayList<>();
        try (InputStream stream = DataExtractor.class.getClassLoader().getResourceAsStream(resourceDir);
             InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(stream));
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
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
            String m = String.format("Extraction of file [%s] failed.", resourceDir);
            log.error(m, e);
            throw new RuntimeException(e);
        }
        return pList;
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
     * Gets the optimal solution from the specified relative resource path.
     *
     * @param resourceDir the specified relative resource path
     * @param beginWith1 true if the caller wants to let gene array begin with 1;
     *                   false if the caller doesn't need that
     * @return the optimal solution if the solution path exists, or {@code null} if not
     */
    public static Solution extractSolutionByResource(String resourceDir, boolean beginWith1) {
        Solution solution;
        try (InputStream stream = DataExtractor.class.getClassLoader().getResourceAsStream(resourceDir);
             InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(stream));
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
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
            solution = new Solution(integers.toArray(new Integer[0]), beginWith1);
        } catch (FileNotFoundException fnfe) {
            String m = String.format("File [%s] was not found, so null will be returned.", resourceDir);
            log.warn(m);
            return null;
        } catch (IOException e) {
            String m = String.format("Extraction of file [%s] failed.", resourceDir);
            log.error(m, e);
            throw new RuntimeException(e);
        }
        return solution;
    }

    /**
     * Gets the optimal solution from the specified absolute file path.
     *
     * @param fileDir the specified absolute file path
     * @param beginWith1 true if the caller wants to let gene array begin with 1;
     *                   false if the caller doesn't need that
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
            solution = new Solution(integers.toArray(new Integer[0]), beginWith1);
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
