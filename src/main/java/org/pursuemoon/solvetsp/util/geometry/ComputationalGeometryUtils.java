package org.pursuemoon.solvetsp.util.geometry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A utility class for computational geometry.
 */
public final class ComputationalGeometryUtils {

    /**
     * Gets the convex hull of points collected in {@code pList} by Andrew Algorithm.
     * This algorithm will not change the original list.
     *
     * @param pList scattered points
     * @param <T> type of points
     * @return the convex hull, collinear points excluded
     */
    public static <T extends Euc2DPoint> List<T> getConvexHull(List<T> pList) {
        if (pList.isEmpty()) {
            throw new RuntimeException("The original point list is empty.");
        }

        /* Clones the original list. */
        List<T> all = new ArrayList<>(pList);
        all.sort(Comparator.naturalOrder());

        List<T> ans = new ArrayList<>();
        for (T p : all) {
            int size = ans.size();
            if (size > 1) {
                Vector a = new Vector(ans.get(size - 2), ans.get(size - 1));
                Vector b = new Vector(ans.get(size - 2), p);
                while (size > 1 && a.det(b) <= 0) {
                    ans.remove(--size);
                    if (size > 1) {
                        a = new Vector(ans.get(size - 2), ans.get(size - 1));
                        b = new Vector(ans.get(size - 2), p);
                    }
                }
            }
            ans.add(p);
        }
        int k = ans.size();
        for (int i = all.size() - 2; i >= 0; i--) {
            T p = all.get(i);
            int size = ans.size();
            if (size > k) {
                Vector a = new Vector(ans.get(size - 2), ans.get(size - 1));
                Vector b = new Vector(ans.get(size - 2), p);
                while (size > k && a.det(b) <= 0) {
                    ans.remove(--size);
                    if (size > k) {
                        a = new Vector(ans.get(size - 2), ans.get(size - 1));
                        b = new Vector(ans.get(size - 2), p);
                    }
                }
            }
            ans.add(p);
        }
        if (all.size() > 1) {
            ans.remove(ans.size() - 1);
        }
        return ans;
    }

    /**
     * Gets an approximate convex hull points collected in {@code pList} by constructing a
     * triangle with the largest diameter.
     * This algorithm will not change the original list.
     *
     * @param pList scattered points
     * @param <T> type of points
     * @return an approximate convex hull
     */
    public static <T extends AbstractPoint> List<T> getApproximateConvexHull(List<T> pList) {
        if (pList.isEmpty()) {
            throw new RuntimeException("The original point list is empty.");
        }

        List<T> ch = new ArrayList<>();
        if (pList.size() < 2) {
            ch.add(pList.get(0));
            return ch;
        } else {
            int size = pList.size();
            double maxDistance = -1;
            int iOrder = 0, jOrder = 0;
            for (int i = 0; i < size; ++i) {
                T pi = pList.get(i);
                for (int j = 0; j < size; ++j) {
                    if (i == j) continue;
                    T pj = pList.get(j);
                    double d = pi.distanceTo(pj);
                    if (d > maxDistance) {
                        iOrder = i;
                        jOrder = j;
                        maxDistance = d;
                    }
                }
            }
            T pi = pList.get(iOrder);
            T pj = pList.get(jOrder);
            ch.add(pi);
            ch.add(pj);
            if (size <= 2) {
                return ch;
            } else {
                int kOrder = 0;
                double maxDistSum = -1;
                for (int i = 0; i < size; ++i) {
                    if (i == iOrder || i == jOrder) continue;
                    T pk = pList.get(i);
                    double d = pi.distanceTo(pk) + pk.distanceTo(pj);
                    if (d > maxDistSum) {
                        kOrder = i;
                        maxDistSum = d;
                    }
                }
                ch.add(pList.get(kOrder));
                return ch;
            }
        }
    }
}
