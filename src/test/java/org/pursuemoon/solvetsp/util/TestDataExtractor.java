package org.pursuemoon.solvetsp.util;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.model.AbstractPoint;
import org.pursuemoon.solvetsp.model.Solution;

import java.io.File;
import java.util.List;
import java.util.function.UnaryOperator;

public class TestDataExtractor {

    private static final String euc2dPointsDir = TestDataExtractor.class.getClassLoader()
            .getResource("tsp_test/test_EUC_2D/a280/a280.tsp").getPath();
    private static final String euc2dOptimalSolutionDir = TestDataExtractor.class.getClassLoader()
            .getResource("tsp_test/test_EUC_2D/a280/a280.opt.tour").getPath();

    private static final String geoPointsDir = TestDataExtractor.class.getClassLoader()
            .getResource("tsp_test/test_GEO/gr96/gr96.tsp").getPath();
    private static final String geoOptimalSolutionDir = TestDataExtractor.class.getClassLoader()
            .getResource("tsp_test/test_GEO/gr96/gr96.opt.tour").getPath();

    @Test
    public void testFileExists() {
        File euc2dPointsFile = new File(euc2dOptimalSolutionDir);
        File euc2dOptimalSolutionFile = new File(euc2dOptimalSolutionDir);
        Assert.assertTrue(euc2dPointsFile.exists());
        Assert.assertTrue(euc2dOptimalSolutionFile.exists());

        File geoPointsFile = new File(geoOptimalSolutionDir);
        File geoOptimalSolutionFile = new File(geoOptimalSolutionDir);
        Assert.assertTrue(geoPointsFile.exists());
        Assert.assertTrue(geoOptimalSolutionFile.exists());
    }

    @Test
    public void testDefaultNew() {
        DataExtractor dataExtractor = new DataExtractor();
    }

    @Test
    public void testExtractPoints() {
        List<AbstractPoint> euc2dList = DataExtractor.extractPoints(euc2dPointsDir);
        Assert.assertEquals(280, euc2dList.size());

        List<AbstractPoint> geoList = DataExtractor.extractPoints(geoPointsDir);
        Assert.assertEquals(96, geoList.size());
    }

    @Test
    public void testExtractSolution() {
        Solution euc2dSolution = DataExtractor.extractSolution(euc2dOptimalSolutionDir, true);
        Assert.assertEquals(280, euc2dSolution.getClonedGene().length);

        Solution geoSolution = DataExtractor.extractSolution(geoOptimalSolutionDir, true);
        Assert.assertEquals(96, geoSolution.getClonedGene().length);
    }

    @Test
    public void testFitnessFunction() {
        UnaryOperator<Double> f = TspSolver.getFitnessFunction();
        double a = f.apply(40000d), b = f.apply(39999d);
        Assert.assertTrue(a < b);
    }
}
