package org.pursuemoon.solvetsp.ga.operator;

import org.pursuemoon.ai.ga.operator.WeightedOperator;
import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.ga.TspSolver;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;
import org.pursuemoon.solvetsp.util.geometry.ComputationalGeometryUtils;
import org.pursuemoon.solvetsp.util.geometry.Euc2DPoint;

import java.util.*;
import java.util.List;

/**
 * Convex-Hull-Division-Strategy implementation of {@code GeneratingOperator}.
 *
 * This algorithm constructs an outer layer by constructing several convex hulls
 * and unions them together first. Then it reduces the remaining points to one point
 * of the outer layer, according to minimum-distance-increment-strategy. For example, if
 * it gets the minimum distance increment when inserts point k between point i and point i+1,
 * then point k is reduced to point i. So after reduction there would be several point set.
 * Then this algorithm recursively does the same operation to these point sets. If there
 * isn't any remaining points, just return the convex hull as a constructed sequence result.
 * Finally it links all constructed sequence result together and let a specified point be
 * the start point.
 */
public class ConvexHullDivisionGeneratingOperator
        extends WeightedOperator.WeightedGeneratingOperator<Integer, Solution> {

    private Random random;

    public ConvexHullDivisionGeneratingOperator(Integer weight) {
        super(weight);
        random = new Random();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Solution generate() {
        List<? extends AbstractPoint> pList = TspSolver.getPoints();
        List<? extends AbstractPoint> list;
        if (pList.get(0) instanceof Euc2DPoint) {
            list = constructByConvexHull((List<? extends Euc2DPoint>) pList, pList.get(0), random.nextBoolean());
        } else {
            list = constructByApproximateConvexHull(pList, pList.get(0), random.nextBoolean());
        }
        int[] gene = new int[pList.size()];
        for (int i = 0; i < gene.length; ++i) {
            gene[i] = list.get(i).getOrder();
        }
        return new Solution(gene, true);
    }

    @SuppressWarnings("unchecked")
    private List<? extends AbstractPoint> constructByConvexHull(List<? extends Euc2DPoint> originalPoints, AbstractPoint beginPoint, boolean flag) {
        List<? extends Euc2DPoint> ch = ComputationalGeometryUtils.getConvexHull(originalPoints);
        List<? extends Euc2DPoint> remaining = new ArrayList<>(originalPoints);
        for (Euc2DPoint p : ch) {
            int size = remaining.size();
            for (int i = 0; i < size; ++i) {
                if (p == remaining.get(i)) {
                    remaining.remove(i);
                    break;
                }
            }
        }

        /* There may be more insert operations when constructing outer layer. */
        List<AbstractPoint> outerLayer = new LinkedList(ch);
        List<AbstractPoint> beforeArrange = new ArrayList<>();

        if (remaining.isEmpty()) {
            beforeArrange.addAll(outerLayer);
        } else {
            /* Constructs an outer layer. */
            List<AbstractPoint> toInsert = new ArrayList<>();
            int numOfLayers = getNumberOfLayers(originalPoints.size());
            for (int lay = 1; lay < numOfLayers && !remaining.isEmpty(); ++lay) {
                List<? extends Euc2DPoint> convexHull = ComputationalGeometryUtils.getConvexHull(remaining);
                toInsert.addAll(convexHull);
                for (Euc2DPoint p : convexHull) {
                    int size = remaining.size();
                    for (int i = 0; i < size; ++i) {
                        if (p == remaining.get(i)) {
                            remaining.remove(i);
                            break;
                        }
                    }
                }
            }
            insertPointsToOuterLayer(toInsert, outerLayer);

            /* Arranges outer points as a list of point set. */
            List<List<AbstractPoint>> setList = new ArrayList<>();
            for (AbstractPoint p : outerLayer) {
                List<AbstractPoint> list = new ArrayList<>();
                list.add(p);
                setList.add(list);
            }
            /* Reduces the remaining points into a corresponding collection. */
            List<AbstractPoint> outer = new ArrayList<>(outerLayer);
            int size = outer.size();
            for (AbstractPoint pk : remaining) {
                int bestIdx = 0;
                double bestIncrement = Double.MAX_VALUE;
                for (int i = 0; i < size; ++i) {
                    AbstractPoint pi = outer.get(i);
                    AbstractPoint pj = outer.get((i + 1) % size);
                    double increment = pi.distanceTo(pk) + pk.distanceTo(pj) - pi.distanceTo(pj);
                    if (increment < bestIncrement) {
                        bestIdx = i;
                        bestIncrement = increment;
                    }
                }
                setList.get(bestIdx).add(pk);
            }

            /* Gets a permutation recursively. */
            for (List<? extends AbstractPoint> set : setList) {
                List<? extends AbstractPoint> section = constructByConvexHull((List<? extends Euc2DPoint>) set, set.get(0), !flag);
                beforeArrange.addAll(section);
            }
        }

        List<AbstractPoint> afterArrange = new ArrayList<>();
        int size = beforeArrange.size();
        for (int i = 0; i < size; ++i) {
            AbstractPoint p = beforeArrange.get(i);
            if (p == beginPoint) {
                for (int j = i; j < size; ++j) {
                    afterArrange.add(beforeArrange.get(j));
                }
                for (int j = 0; j < i; ++j) {
                    afterArrange.add(beforeArrange.get(j));
                }
                break;
            }
        }

        if (flag) {
            List<AbstractPoint> afterReverse = new ArrayList<>();
            afterReverse.add(afterArrange.get(0));
            for (int i = size - 1; i > 0; --i) {
                afterReverse.add(afterArrange.get(i));
            }
            return afterReverse;
        } else {
            return afterArrange;
        }
    }

    @SuppressWarnings("unchecked")
    private List<? extends AbstractPoint> constructByApproximateConvexHull(List<? extends AbstractPoint> originalPoints, AbstractPoint beginPoint, boolean flag) {
        List<? extends AbstractPoint> ch = ComputationalGeometryUtils.getApproximateConvexHull(originalPoints);
        List<? extends AbstractPoint> remaining = new ArrayList<>(originalPoints);
        for (AbstractPoint p : ch) {
            int size = remaining.size();
            for (int i = 0; i < size; ++i) {
                if (p == remaining.get(i)) {
                    remaining.remove(i);
                    break;
                }
            }
        }

        /* There may be more insert operations when constructing outer layer. */
        List<AbstractPoint> outerLayer = new LinkedList(ch);
        List<AbstractPoint> beforeArrange = new ArrayList<>();

        if (remaining.isEmpty()) {
            beforeArrange.addAll(originalPoints);
        } else {
            /* Constructs an outer layer. */
            List<AbstractPoint> toInsert = new ArrayList<>();
            int numOfLayers = getNumberOfLayers(originalPoints.size());
            for (int lay = 1; lay < numOfLayers && !remaining.isEmpty(); ++lay) {
                List<? extends AbstractPoint> convexHull = ComputationalGeometryUtils.getApproximateConvexHull(remaining);
                toInsert.addAll(convexHull);
                for (AbstractPoint p : convexHull) {
                    int size = remaining.size();
                    for (int i = 0; i < size; ++i) {
                        if (p == remaining.get(i)) {
                            remaining.remove(i);
                            break;
                        }
                    }
                }
            }
            insertPointsToOuterLayer(toInsert, outerLayer);

            /* Arranges outer points as a list of point set. */
            List<List<AbstractPoint>> setList = new ArrayList<>();
            for (AbstractPoint p : outerLayer) {
                List<AbstractPoint> list = new ArrayList<>();
                list.add(p);
                setList.add(list);
            }
            /* Reduces the remaining points into a corresponding collection. */
            List<AbstractPoint> outer = new ArrayList<>(outerLayer);
            int size = outer.size();
            for (AbstractPoint pk : remaining) {
                int bestIdx = 0;
                double bestIncrement = Double.MAX_VALUE;
                for (int i = 0; i < size; ++i) {
                    AbstractPoint pi = outer.get(i);
                    AbstractPoint pj = outer.get((i + 1) % size);
                    double increment = pi.distanceTo(pk) + pk.distanceTo(pj) - pi.distanceTo(pj);
                    if (increment < bestIncrement) {
                        bestIdx = i;
                        bestIncrement = increment;
                    }
                }
                setList.get(bestIdx).add(pk);
            }

            /* Gets a permutation recursively. */
            for (List<? extends AbstractPoint> set : setList) {
                List<? extends AbstractPoint> section = constructByApproximateConvexHull(set, set.get(0), !flag);
                beforeArrange.addAll(section);
            }
        }

        List<AbstractPoint> afterArrange = new ArrayList<>();
        int size = beforeArrange.size();
        for (int i = 0; i < size; ++i) {
            AbstractPoint p = beforeArrange.get(i);
            if (p == beginPoint) {
                for (int j = i; j < size; ++j) {
                    afterArrange.add(beforeArrange.get(j));
                }
                for (int j = 0; j < i; ++j) {
                    afterArrange.add(beforeArrange.get(j));
                }
                break;
            }
        }

        if (flag) {
            List<AbstractPoint> afterReverse = new ArrayList<>();
            afterReverse.add(afterArrange.get(0));
            for (int i = size - 1; i > 0; --i) {
                afterReverse.add(afterArrange.get(i));
            }
            return afterReverse;
        } else {
            return afterArrange;
        }
    }

    private void insertPointsToOuterLayer(List<AbstractPoint> points, List<AbstractPoint> outerLayer) {
        while (!points.isEmpty()) {
            PriorityQueue<PointDistanceIncrement> queue = new PriorityQueue<>();
            int size = outerLayer.size();
            for (AbstractPoint point : points) {
                for (int i = 0; i < size; ++i) {
                    AbstractPoint pi = outerLayer.get(i);
                    AbstractPoint pj = outerLayer.get((i + 1) % size);
                    double increment = pi.distanceTo(point) + point.distanceTo(pj) - pi.distanceTo(pj);
                    queue.offer(new PointDistanceIncrement(point, (i + 1) % size, increment));
                }
            }
            PointDistanceIncrement pdi = queue.remove();
            outerLayer.add(pdi.index, pdi.point);
            points.remove(pdi.point);
        }
    }

    private int getNumberOfLayers(int size) {
        if (size <= 50) {
            return 1 + random.nextInt(2);
        } else if (size <= 100) {
            return 3 + random.nextInt(4);
        } else if (size <= 500) {
            return 5 + random.nextInt(6);
        } else if (size <= 1500) {
            return 7 + random.nextInt(8);
        } else if (size <= 2000) {
            return 9 + random.nextInt(10);
        } else if (size <= 3000) {
            return 11 + random.nextInt(12);
        } else {
            return 25;
        }
    }

    /**
     * An object of this class means, if a point whose refer is {@code point} was inserted
     * before the point at the {@code index} position of some point sequence, it will cause
     * a distance increment represented by {@code distanceIncrement}.
     */
    private static class PointDistanceIncrement implements Comparable<PointDistanceIncrement> {

        private AbstractPoint point;
        private Integer index;
        private Double distanceIncrement;

        public PointDistanceIncrement(AbstractPoint point, Integer index, Double distanceIncrement){
            this.point = point;
            this.index = index;
            this.distanceIncrement = distanceIncrement;
        }

        @Override
        public int compareTo(PointDistanceIncrement o) {
            return Double.compare(distanceIncrement, o.distanceIncrement);
        }
    }
}
