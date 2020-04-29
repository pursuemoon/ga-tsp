package org.pursuemoon.solvetsp.util;

import org.junit.Test;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;

import java.util.List;

public class TestPainter {

    private static final String relativeEuc2dPointsDir = "tsp_test/test_EUC_2D/pr2392/pr2392.tsp";
    private static final String relativeEuc2dSolutionDir = "tsp_test/test_EUC_2D/pr2392/pr2392.opt.tour";

    @Test
    public void testPaint() {
        List<AbstractPoint> pList = DataExtractor.extractPointsByResource(relativeEuc2dPointsDir);
        int bg = relativeEuc2dPointsDir.lastIndexOf("/") + 1;
        int ed = relativeEuc2dPointsDir.lastIndexOf(".tsp");
        String caseName = relativeEuc2dPointsDir.substring(bg, ed);
        Solution optimal = DataExtractor.extractSolutionByResource(relativeEuc2dSolutionDir, true);
        Painter.paint(caseName, false, pList, optimal);
    }
}
