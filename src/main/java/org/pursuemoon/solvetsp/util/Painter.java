package org.pursuemoon.solvetsp.util;

import org.pursuemoon.solvetsp.ga.Solution;
import org.pursuemoon.solvetsp.util.geometry.AbstractPoint;
import org.pursuemoon.solvetsp.util.geometry.Euc2DPoint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public final class Painter {

    private static final int CANVAS_WIDTH = 1200;
    private static final int CANVAS_HEIGHT = 800;

    private static final int FRAME_MARGIN = 50;
    private static final int FRAME_WIDTH = CANVAS_WIDTH + FRAME_MARGIN * 2;
    private static final int FRAME_HEIGHT = CANVAS_HEIGHT + FRAME_MARGIN * 2;

    private static final int MAGNITUDE_BASE = 2;

    /**
     * Paints the solution of a TSP whose point set is {@code pList}.
     *
     * @param caseName name of the case
     * @param visible whether the frame of painting is visible after painting
     * @param pList point set of a TSP
     * @param solution solution to be painted
     * @throws UnsupportedOperationException if the type of point is not supported being painted
     */
    public static void paint(String caseName, boolean visible, List<? extends AbstractPoint> pList, Solution solution)
            throws UnsupportedOperationException {
        new Frame(caseName, visible, pList, solution);
    }

    private static class Frame extends JFrame {

        Frame(String name, boolean visible, List<? extends AbstractPoint> pList, Solution solution) {
            super(name);
            setSize(FRAME_WIDTH, FRAME_HEIGHT + FRAME_MARGIN);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(visible);

            Container container = getContentPane();
            container.setLayout(new BorderLayout());

            Canvas canvas = new Canvas(pList, solution);
            container.add(BorderLayout.CENTER, canvas);

            /* Saves the painting of result as a file. */
            BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            canvas.printAll(g2d);
            try {
                File dir = new File("imgs");
                boolean dirExists = (dir.exists() || dir.mkdirs());
                if (dirExists) {
                    File file = new File(String.format("imgs/%s.jpg", name));
                    ImageIO.write(image, "jpg", file);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static class Canvas extends JPanel {

        private double markX, markY;
        private double scale;
        private int exp;
        private int pointRadius;

        List<? extends AbstractPoint> pList;
        Solution solution;

        Canvas(List<? extends AbstractPoint> pList, Solution solution) {
            super();
            this.pList = pList;
            this.solution = solution;
            setSize(FRAME_WIDTH, FRAME_HEIGHT);
            setVisible(true);
            Color color = new Color(225, 225, 225);
            setBackground(color);
            calScale();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawRectOnCanvas(g, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            drawAxes(g);
            int size = pList.size();
            int[] gene = solution.getClonedGene();
            for (int i = 0; i < size; ++i) {
                int j = (i + 1) % size;
                int orderI = gene[i] - 1;
                int orderJ = gene[j] - 1;
                Euc2DPoint pi = (Euc2DPoint) pList.get(orderI);
                Euc2DPoint pj = (Euc2DPoint) pList.get(orderJ);
                drawLine(g, pi.getX(), pi.getY(), pj.getX(), pj.getY());
            }
            for (int i = 0; i < size; ++i) {
                Euc2DPoint p = (Euc2DPoint) pList.get(i);
                double x = p.getX(), y = p.getY();
                drawPoint(g, x, y);
            }
        }

        private void drawPoint(Graphics g, double x, double y) {
            int intX = (int) ((x - markX) / scale);
            int intY = (int) ((y - markY) / scale);
            drawPointOnCanvas(g, intX, intY, pointRadius);
        }

        private void drawLine(Graphics g, double x1, double y1, double x2, double y2) {
            int intX1 = (int) ((x1 - markX) / scale);
            int intY1 = (int) ((y1 - markY) / scale);
            int intX2 = (int) ((x2 - markX) / scale);
            int intY2 = (int) ((y2 - markY) / scale);
            drawLineOnCanvas(g, intX1, intY1, intX2, intY2);
        }

        private void drawStringOnCanvas(Graphics g, String s, int x, int y) {
            Color color = Color.BLACK;
            g.setColor(color);
            g.drawString(s, (int) (FRAME_MARGIN * 1.5) + x, (int) (FRAME_MARGIN * 0.6) + y);
        }

        private void drawLineOnCanvas(Graphics g, int x1, int y1, int x2, int y2) {
            Color color = Color.BLACK;
            g.setColor(color);
            g.drawLine((int) (FRAME_MARGIN * 1.5) + x1, (int) (FRAME_MARGIN * 0.6) + y1,
                    (int) (FRAME_MARGIN * 1.5) + x2, (int) (FRAME_MARGIN * 0.6)+ y2);
        }

        private void drawRectOnCanvas(Graphics g, int x, int y, int width, int height) {
            Color color = Color.GRAY;
            g.setColor(color);
            g.drawRect((int) (FRAME_MARGIN * 1.5) + x, (int) (FRAME_MARGIN * 0.6) + y, width, height);
        }

        private void drawPointOnCanvas(Graphics g, int x, int y, int pointRadius) {
            Color color = Color.RED;
            g.setColor(color);
            g.fillOval((int) (FRAME_MARGIN * 1.5) - pointRadius + x, (int) (FRAME_MARGIN * 0.6) - pointRadius + y,
                    pointRadius * 2, pointRadius * 2);
        }

        private void calScale() {
            pointRadius = (pList.size() >= 1500 ? 3 : 5);

            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (AbstractPoint o : pList) {
                Euc2DPoint p = (Euc2DPoint) o;
                minX = Math.min(minX, p.getX());
                minY = Math.min(minY, p.getY());
                maxX = Math.max(maxX, p.getX());
                maxY = Math.max(maxY, p.getY());
            }

            double deltaX = maxX - minX;
            double deltaY = maxY - minY;
            if (deltaX * 2 >= deltaY * 3) {
                exp = getMagnitude(deltaX);
                scale = Math.pow(MAGNITUDE_BASE, exp) / CANVAS_WIDTH;
            } else {
                exp = getMagnitude(deltaY);
                scale = Math.pow(MAGNITUDE_BASE, exp) / CANVAS_HEIGHT;
            }
            double canvasWidth = scale * CANVAS_WIDTH;
            double canvasHeight = scale * CANVAS_HEIGHT;
            markX = minX - (canvasWidth - deltaX) / 2;
            markY = minY - (canvasHeight - deltaY) / 2;
        }

        private void drawAxes(Graphics g) {
            final int margin = 10;
            drawLineOnCanvas(g, 0, CANVAS_HEIGHT + margin, CANVAS_WIDTH, CANVAS_HEIGHT + margin);
            drawLineOnCanvas(g, -margin, 0, -margin, CANVAS_HEIGHT);

            final int seg = 5;
            final int count = 32;   // Needs to be the power of MAGNITUDE_BASE
            double calibration = Math.pow(MAGNITUDE_BASE, exp) / count;
            double span = Math.min(1, calibration);

            int standardX = (int) markX;
            double endX = markX + scale * CANVAS_WIDTH;
            boolean flag = true;

            for (double i = standardX; i <= endX; i += span) {
                if (i % calibration == 0) {
                    if (markX <= i && i <= endX) {
                        int intX = (int) ((i - markX) / scale);
                        drawLineOnCanvas(g, intX, CANVAS_HEIGHT + margin, intX, CANVAS_HEIGHT + margin + seg);
                        if (flag) {
                            String s = "" + (int) i;
                            int x = (int) (intX - s.length() * 3.5);
                            int y = CANVAS_HEIGHT + margin + seg * 4;
                            drawStringOnCanvas(g, s, x, y);
                        }
                    }
                    flag = !flag;
                }
            }

            int standardY = (int) markY;
            double endY = markY + scale * CANVAS_HEIGHT;
            flag = true;
            for (double i = standardY; i <= endY; i += span) {
                if (i % calibration == 0) {
                    if (markY <= i && i <= endY) {
                        int intY = (int) ((i - markY) / scale);
                        drawLineOnCanvas(g, -margin, intY, -margin - seg, intY);
                        if (flag) {
                            String s = "" + (int) i;
                            int x = -3 * margin - (int) (s.length() * 4.5);
                            int y = intY + seg;
                            drawStringOnCanvas(g, s, x, y);
                        }
                    }
                    flag = !flag;
                }
            }
        }

        private static int getMagnitude(double num) {
            int l = 1, r = 310, ans = r - 1;
            while (l < r) {
                int mid = (l + r) >>> 1;
                double get = num * Math.pow(MAGNITUDE_BASE, -mid);
                if (get < 1) {
                    ans = mid;
                    r = mid;
                } else {
                    l = mid + 1;
                }
            }
            return ans;
        }
    }
}
