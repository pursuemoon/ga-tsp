package org.pursuemoon.solvetsp.ga.operator;

import org.pursuemoon.ai.ga.operator.WeightedOperator;
import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;
import org.pursuemoon.solvetsp.util.geometry.Euc2DPoint;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.util.geometry.ComputationalGeometryUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Convex-Hull-Constriction-Strategy implementation of {@code GeneratingOperator}.
 *
 * This algorithm constructs a convex hull of all points first. And then repeats the
 * following operation until a complete loop is generated: finds at most k best plans which
 * aiming to insert a certain point into a certain edge; if a plan causes less distance
 * increment, it is better; randomly picks one to carry out and removes the inserted point
 * from the remaining points' set.
 */
public final class ConvexHullConstrictionGeneratingOperator
        extends WeightedOperator.WeightedGeneratingOperator<Integer, Solution> {

    private int k;
    private Random random;

    public ConvexHullConstrictionGeneratingOperator(Integer weight, Integer k) {
        super(weight);
        this.k = k;
        random = new Random();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Solution generate() {
        List<? extends AbstractPoint> pList = TspSolver.getPoints();
        List<? extends AbstractPoint> ch;
        if (pList.get(0) instanceof Euc2DPoint) {
            ch = constructConvexHull((List<? extends Euc2DPoint>) pList);
        } else {
            ch = constructApproximateConvexHull(pList);
        }
        int[] gene = constrict(ch, pList);
        return new Solution(gene, true);
    }

    private List<? extends AbstractPoint> constructConvexHull(List<? extends Euc2DPoint> pList) {
        return ComputationalGeometryUtils.getConvexHull(pList);
    }

    private List<? extends AbstractPoint> constructApproximateConvexHull(List<? extends AbstractPoint> pList) {
        return ComputationalGeometryUtils.getApproximateConvexHull(pList);
    }

    private int[] constrict(List<? extends AbstractPoint> convexHull, List<? extends AbstractPoint> pList) {
        /* Accelerates computing distances. */
        TspSolver.fullyCalDistArray();
        double[][] distArray = TspSolver.getDistArray();

        int len = pList.size();
        int[] gene = new int[len];
        List<Integer> remainingOrder = pList.stream().map(AbstractPoint::getOrder).collect(Collectors.toList());
        List<Integer> chOrder = convexHull.stream().map(AbstractPoint::getOrder).collect(Collectors.toList());
        remainingOrder.removeAll(chOrder);
        int size = convexHull.size();
        LinkedList<Integer> geneList = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            geneList.add(chOrder.get(i));
        }

        while (!remainingOrder.isEmpty()) {
            PriorityQueue<PointDistanceIncrement> queue = new PriorityQueue<>();
            for (int i : remainingOrder) {
                int idx = 0;
                ListIterator<Integer> fromIter = geneList.listIterator(idx);
                ListIterator<Integer> toIter = geneList.listIterator(idx + 1);
                double bestDistIncrement = Double.MAX_VALUE;
                int bestIdx = 0;
                while (fromIter.hasNext()) {
                    int j = fromIter.next();
                    int k = toIter.hasNext() ? toIter.next() : geneList.getFirst();
                    double distIncrement = distArray[j - 1][i - 1] + distArray[i - 1][k - 1] - distArray[j - 1][k - 1];
                    if (Double.compare(distIncrement, bestDistIncrement) < 0) {
                        bestIdx = idx;
                        bestDistIncrement = distIncrement;
                    }
                    idx++;
                }
                queue.offer(new PointDistanceIncrement(i, bestIdx, (bestIdx + 1) % size, bestDistIncrement));
            }
            List<PointDistanceIncrement> list = new ArrayList<>();
            for (int i = 0; i < k && queue.peek() != null; ++i) {
                list.add(queue.remove());
            }
            PointDistanceIncrement pdi = list.get(random.nextInt(list.size()));
            remainingOrder.remove(pdi.pi);
            int pkIndex = pdi.pkIndex;
            geneList.add(pkIndex, pdi.pi);
            size++;
        }
        int idx = 0;
        for (Integer integer : geneList) {
            gene[idx++] = integer;
        }
        return gene;
    }

    /**
     * An object of this class means, if a point ordered by {@code pi} was inserted between the two
     * points orders of which were represented as {@code gene[pjIndex]} and {@code gene[pkIndex]},
     * it will cause a distance increment represented by {@code distanceIncrement}.
     */
    private static class PointDistanceIncrement implements Comparable<PointDistanceIncrement> {

        private Integer pi, pjIndex, pkIndex;
        private Double distanceIncrement;

        public PointDistanceIncrement(Integer piOrder, Integer pjIndex, Integer pkIndex, Double distanceIncrement) {
            this.pi = piOrder;
            this.pjIndex = pjIndex;
            this.pkIndex = pkIndex;
            this.distanceIncrement = distanceIncrement;
        }

        @Override
        public int compareTo(PointDistanceIncrement o) {
            return Double.compare(distanceIncrement, o.distanceIncrement);
        }
    }
}
