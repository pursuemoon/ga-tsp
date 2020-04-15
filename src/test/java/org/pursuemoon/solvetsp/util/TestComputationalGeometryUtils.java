package org.pursuemoon.solvetsp.util;

import org.junit.Assert;
import org.junit.Test;
import org.pursuemoon.solvetsp.util.geometry.ComputationalGeometryUtils;
import org.pursuemoon.solvetsp.util.geometry.Euc2DPoint;

import java.util.ArrayList;
import java.util.List;

public class TestComputationalGeometryUtils {

    @Test
    public void testGetConvexHull() {
        List<Euc2DPoint> chList = new ArrayList<>();
        chList.add(new Euc2DPoint(1, 1));
        chList.add(new Euc2DPoint(1, 0));
        chList.add(new Euc2DPoint(1, -1));
        chList.add(new Euc2DPoint(-1, 1));
        chList.add(new Euc2DPoint(-1, 0));
        chList.add(new Euc2DPoint(-1, -1));
        chList.add(new Euc2DPoint(0, 1));
        chList.add(new Euc2DPoint(0, -1));

        List<Euc2DPoint> pList = new ArrayList<>(chList);
        pList.add(new Euc2DPoint(0, 0));
        pList.add(new Euc2DPoint(0.5, 0.5));
        pList.add(new Euc2DPoint(-0.9, 0.8));
        pList.add(new Euc2DPoint(0.7, -0.4));
        pList.add(new Euc2DPoint(-0.1, -0.999));
        List<Euc2DPoint> convexHull = ComputationalGeometryUtils.getConvexHull(pList);
        Assert.assertEquals(chList.size(), convexHull.size());
    }
}
