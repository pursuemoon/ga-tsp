package org.pursuemoon.solvetsp.ga.operator;

import org.pursuemoon.ai.ga.operator.WeightedOperator;
import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.ga.Solution;

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
        /* Accelerates computing distances. */
        TspSolver.fullyCalDistArray();
        double[][] distArray = TspSolver.getDistArray();

        int size = distArray.length;
        int[] gene = new int[size];
        BitSet bitSet = new BitSet(size);
        int t = 0;
        do {
            if (t == 0) {
                int start = random.nextInt(size) + 1;
                gene[t] = start;
                bitSet.set(start);
            } else {
                int from = gene[t - 1] - 1;
                PriorityQueue<Edge> queue = new PriorityQueue<>(Comparator.reverseOrder());
                for (int to = 0; to < size; ++to) {
                    if (!bitSet.get(to + 1)) {
                        double dist = distArray[from][to];
                        Edge edge = new Edge(to, dist);
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
