package org.pursuemoon.solvetsp.util;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;
import org.pursuemoon.solvetsp.ga.Solution;

import java.io.*;
import java.util.List;
import java.util.function.UnaryOperator;

public class TestDataExtractor {


    private static final String relativeEuc2dPointsDir = "tsp_test/test_EUC_2D/a280/a280.tsp";
    private static final String absoluteEuc2dPointsDir = TestDataExtractor.class.getClassLoader()
            .getResource(relativeEuc2dPointsDir).getPath();
    private static final String relativeEuc2dSolutionDir = "tsp_test/test_EUC_2D/a280/a280.opt.tour";
    private static final String absoluteEuc2dSolutionDir = TestDataExtractor.class.getClassLoader()
            .getResource(relativeEuc2dSolutionDir).getPath();

    private static final String relativeGeoPointsDir = "tsp_test/test_GEO/gr96/gr96.tsp";
    private static final String absoluteGeoPointsDir = TestDataExtractor.class.getClassLoader()
            .getResource(relativeGeoPointsDir).getPath();
    private static final String relativeGeoSolutionDir = "tsp_test/test_GEO/gr96/gr96.opt.tour";
    private static final String absoluteGeoSolutionDir = TestDataExtractor.class.getClassLoader()
            .getResource(relativeGeoSolutionDir).getPath();

    @Test
    public void testFileExists() {
        File euc2dPointsFile = new File(absoluteEuc2dPointsDir);
        File euc2dOptimalSolutionFile = new File(absoluteEuc2dSolutionDir);
        Assert.assertTrue(euc2dPointsFile.exists());
        Assert.assertTrue(euc2dOptimalSolutionFile.exists());

        File geoPointsFile = new File(absoluteGeoPointsDir);
        File geoOptimalSolutionFile = new File(absoluteGeoSolutionDir);
        Assert.assertTrue(geoPointsFile.exists());
        Assert.assertTrue(geoOptimalSolutionFile.exists());
    }

    @Test
    public void testExtractPoints() {
        List<AbstractPoint> eucList1 = DataExtractor.extractPoints(absoluteEuc2dPointsDir);
        List<AbstractPoint> eucList2 = DataExtractor.extractPointsByResource(relativeEuc2dPointsDir);
        Assert.assertEquals(eucList1.size(), eucList2.size());

        List<AbstractPoint> geoList1 = DataExtractor.extractPoints(absoluteGeoPointsDir);
        List<AbstractPoint> geoList2 = DataExtractor.extractPointsByResource(relativeGeoPointsDir);
        Assert.assertEquals(geoList1.size(), geoList2.size());
    }

    @Test
    public void testExtractSolution() {
        Solution euc2dSolution1 = DataExtractor.extractSolution(absoluteEuc2dSolutionDir, true);
        Solution euc2dSolution2 = DataExtractor.extractSolutionByResource(relativeEuc2dSolutionDir, true);
        Assert.assertEquals(euc2dSolution1, euc2dSolution2);

        Solution geoSolution1 = DataExtractor.extractSolution(absoluteGeoSolutionDir, true);
        Solution geoSolution2 = DataExtractor.extractSolutionByResource(relativeGeoSolutionDir, true);
        Assert.assertEquals(geoSolution1, geoSolution2);
    }

    @Test
    public void testFitnessFunction() {
        UnaryOperator<Double> f = TspSolver.getFitnessFunction();
        double a = f.apply(40000d), b = f.apply(39999d);
        Assert.assertTrue(a < b);
    }

    @Test
    public void testDefaultNextTsp() {
        DataExtractor dataExtractor = DataExtractor.instance;
        List<Object> list = dataExtractor.getNextTsp();
        Assert.assertEquals(list.size(), 6);
    }
}