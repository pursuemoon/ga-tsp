package org.pursuemoon.solvetsp.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;
import org.pursuemoon.solvetsp.Solution;

import java.util.*;

/**
 * Nearest-Neighbor-Greedy-Strategy implementation of {@code GeneratingOperator}.
 *
 * This algorithm randomly chooses a point as start point. And then repeats the following
 * operation until a complete loop is generated: finds at most k nearest neighbor points,
 * and randomly select one of them as the next point to the result path.
 */
public final class NearestKNeighborsGreedyGeneratingOperator
        extends WeightedOperator.WeightedGeneratingOperator<Integer, Solution> {

    private int k;

    private Random random;

    public NearestKNeighborsGreedyGeneratingOperator(Integer weight, Integer k) {
        super(weight);
        this.k = k;
        random = new Random();
    }

    @Override
    public Solution generate() {
        List<AbstractPoint> pList = TspSolver.getPoints();
        int size = pList.size();
        int[] gene = new int[size];
        BitSet bitSet = new BitSet(size);
        int t = 0;
        do {
            if (t == 0) {
                int start = random.nextInt(size) + 1;
                gene[t] = start;
                bitSet.set(start);
            } else {
                AbstractPoint from = pList.get(gene[t - 1] - 1);
                PriorityQueue<Edge> queue = new PriorityQueue<>(Comparator.reverseOrder());
                for (int i = 0; i < size; ++i) {
                    if (!bitSet.get(i + 1)) {
                        AbstractPoint p = pList.get(i);
                        double dist = from.distanceTo(p);
                        Edge edge = new Edge(i, dist);
                        if (queue.size() < k) {
                            queue.offer(edge);
                        } else {
                            Edge worst = queue.element();
                            if (edge.compareTo(worst) < 0) {
                                queue.remove();
                                queue.offer(edge);
                            }
                        }
                    }
                }
                List<Edge> list = new ArrayList<>(queue);
                int idx = random.nextInt(list.size());
                int toOrder = list.get(idx).to + 1;
                gene[t] = toOrder;
                bitSet.set(toOrder);
            }
        } while (++t < size);
        return new Solution(gene, true);
    }

    private static class Edge implements Comparable<Edge> {

        private int to;
        private double distance;

        public Edge(int to, double distance) {
            this.to = to;
            this.distance = distance;
        }

        @Override
        public int compareTo(Edge o) {
            return Double.compare(distance, o.distance);
        }
    }
}
