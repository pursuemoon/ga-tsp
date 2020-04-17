package org.pursuemoon.solvetsp.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;
import org.pursuemoon.solvetsp.util.geometry.Euc2DPoint;
import org.pursuemoon.solvetsp.Solution;
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
    public Solution generate() {
        List<AbstractPoint> pList = TspSolver.getPoints();
        List<? extends AbstractPoint> ch;
        if (pList.get(0) instanceof Euc2DPoint) {
            ch = constructConvexHull(pList);
        } else {
            ch = constructApproximateConvexHull(pList);
        }
        int[] gene = constrict(ch, pList);
        return new Solution(gene, true);
    }

    private List<? extends AbstractPoint> constructConvexHull(List<? extends AbstractPoint> pList) {
        @SuppressWarnings("unchecked")
        List<? extends AbstractPoint> ch = ComputationalGeometryUtils.getConvexHull((List<Euc2DPoint>) pList);
        return ch;
    }

    private List<? extends AbstractPoint> constructApproximateConvexHull(List<? extends AbstractPoint> pList) {
        int size = pList.size();
        double maxDistance = -1;
        int iOrder = 0, jOrder = 0;
        for (int i = 0; i < size ; ++i) {
            AbstractPoint pi = pList.get(i);
            for (int j = 0; j < size; ++j) {
                if (i == j) continue;
                AbstractPoint pj = pList.get(j);
                double d = pi.distanceTo(pj);
                if (d > maxDistance) {
                    iOrder = i;
                    jOrder = j;
                    maxDistance = d;
                }
            }
        }
        List<AbstractPoint> ch = new ArrayList<>();
        AbstractPoint pi = pList.get(iOrder);
        AbstractPoint pj = pList.get(jOrder);
        ch.add(pi);
        ch.add(pj);
        if (size <= 2) {
            return ch;
        } else {
            int kOrder = 0;
            double maxDistSum = -1;
            for (int i = 0; i < size; ++i) {
                if (i == iOrder || i == jOrder) continue;
                AbstractPoint pk = pList.get(i);
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

    private int[] constrict(List<? extends AbstractPoint> convexHull, List<AbstractPoint> pList) {
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
