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
        int len = pList.size();
        int[] gene = new int[len];
        if (pList.get(0) instanceof Euc2DPoint) {
            convexHullConstrictionMethod(gene, pList);
        } else {
            approximateConvexHullConstrictionMethod(gene, pList);
        }
        return new Solution(gene, true);
    }

    private void convexHullConstrictionMethod(int[] gene, List<? extends AbstractPoint> pList) {
        @SuppressWarnings("unchecked")
        List<Euc2DPoint> ch = ComputationalGeometryUtils.getConvexHull((List<Euc2DPoint>) pList);
        List<Integer> remainingOrder = pList.stream().map(AbstractPoint::getOrder).collect(Collectors.toList());
        List<Integer> chOrder = ch.stream().map(AbstractPoint::getOrder).collect(Collectors.toList());
        remainingOrder.removeAll(chOrder);
        int size = ch.size();
        for (int i = 0; i < size; ++i) {
            gene[i] = chOrder.get(i);
        }
        while (!remainingOrder.isEmpty()) {
            PriorityQueue<PointDistanceIncrement> queue = new PriorityQueue<>();
            for (int i : remainingOrder) {
                AbstractPoint pi = pList.get(i - 1);
                for (int idx = 0; idx < size; ++idx) {
                    int j = gene[idx];
                    int k = gene[(idx + 1) % size];
                    AbstractPoint pj = pList.get(j - 1);
                    AbstractPoint pk = pList.get(k - 1);
                    double distIncrement = pj.distanceTo(pi) + pi.distanceTo(pk) - pj.distanceTo(pk);
                    queue.offer(new PointDistanceIncrement(i, idx, (idx + 1) % size, distIncrement));
                }
            }
            List<PointDistanceIncrement> list = new ArrayList<>();
            for (int i = 0; i < k && queue.peek() != null; ++i) {
                list.add(queue.remove());
            }
            PointDistanceIncrement pdi = list.get(random.nextInt(list.size()));
            remainingOrder.remove(pdi.pi);
            int pkIndex = pdi.pkIndex;
            if (pkIndex == 0) {
                gene[size++] = pdi.pi;
            } else {
                System.arraycopy(gene, pkIndex, gene, pkIndex + 1, size - pkIndex);
                gene[pkIndex] = pdi.pi;
                size++;
            }
        }
    }

    private void approximateConvexHullConstrictionMethod(int[] gene, List<AbstractPoint> pList) {

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
