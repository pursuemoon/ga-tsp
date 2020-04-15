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
     * This algorithm will now change the original list.
     *
     * @param pList scattered points
     * @param <T> type of points
     * @return the convex hull, collinear points included
     */
    public static <T extends Euc2DPoint> List<T> getConvexHull(List<T> pList) {
        /* Clones the original list. */
        List<T> all = new ArrayList<>(pList);
        all.sort(Comparator.naturalOrder());

        List<T> ans = new ArrayList<>();
        for (T p : all) {
            int size = ans.size();
            if (size > 1) {
                Vector a = new Vector(ans.get(size - 2), ans.get(size - 1));
                Vector b = new Vector(ans.get(size - 2), p);
                while (size > 1 && a.det(b) < 0) {
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
                while (size > k && a.det(b) < 0) {
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
}
