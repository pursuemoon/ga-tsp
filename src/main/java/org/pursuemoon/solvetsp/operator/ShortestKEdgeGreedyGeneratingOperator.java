package org.pursuemoon.solvetsp.operator;

import org.pursuemoon.ai.ga.util.operator.WeightedOperator;
import org.pursuemoon.solvetsp.TspSolver;
import org.pursuemoon.solvetsp.Solution;

import java.util.*;

/**
 * Shortest-Edge-Greedy-Strategy implementation of {@code GeneratingOperator}.
 *
 * Every time this algorithm chooses k shortest edge that could be chosen and
 * randomly add one of them to the result path, until a complete loop is generated.
 */
public final class ShortestKEdgeGreedyGeneratingOperator
        extends WeightedOperator.WeightedGeneratingOperator<Integer, Solution> {

    private int k;

    private Random random;

    public ShortestKEdgeGreedyGeneratingOperator(Integer weight, Integer k) {
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
        int[] next = new int[size];
        BitSet fromSet = new BitSet(size);
        BitSet toSet = new BitSet(size);
        DisjointSet pathSet = new DisjointSet(size);
        for (int time = 0; time < size; ++time) {
            PriorityQueue<Edge> queue = new PriorityQueue<>(Comparator.reverseOrder());
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (i == j) continue;
                    if (fromSet.get(i) || toSet.get(j)) continue;
                    if (time != size - 1 && pathSet.isInSameSet(i, j)) continue;
                    double d = distArray[i][j];
                    Edge edge = new Edge(i, j, d);
                    if (queue.size() < k)
                        queue.offer(edge);
                    else {
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
            int fromOrder = list.get(idx).from;
            int toOrder = list.get(idx).to;
            next[fromOrder] = toOrder;
            pathSet.union(fromOrder, toOrder);
            fromSet.set(fromOrder);
            toSet.set(toOrder);
        }
        int t = 0, p = 0;
        while (t < size) {
            gene[t] = p + 1;
            p = next[p];
            t++;
        }
        return new Solution(gene, true);
    }

    private static class Edge implements Comparable<Edge> {

        private int from;
        private int to;
        private double distance;

        public Edge(int from, int to, double distance) {
            this.from = from;
            this.to = to;
            this.distance = distance;
        }

        @Override
        public int compareTo(Edge o) {
            return Double.compare(distance, o.distance);
        }
    }

    private static class DisjointSet {

        private int[] father;

        public DisjointSet(int size) {
            father = new int[size];
            for (int i = 0; i < size; ++i) {
                father[i] = i;
            }
        }

        public int findFather(int x) {
            return (x == father[x] ? x : (father[x] = findFather(father[x])));
        }

        public boolean isInSameSet(int a, int b) {
            int pa = findFather(a);
            int pb = findFather(b);
            return (pa == pb);
        }

        public void union(int a, int b) {
            int pa = findFather(a);
            int pb = findFather(b);
            if (pa == pb)
                return;
            father[pa] = pb;
        }
    }
}
