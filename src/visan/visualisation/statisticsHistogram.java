package visan.visualisation;

import visan.common.ClassesCollection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public final class statisticsHistogram extends DataComponent {

    private int numberOfBins, maxCount, total;
    private int[] points;
    private double[] normalPoints;
    private double maxX, minX, binSize, range;
    private double mean;
    private double median;
    private NormalDistribution nd;

    public statisticsHistogram(ClassesCollection cc, int[] classInd, int[] featureInd, double mean, double sd, double median) {
        super("histogram", cc);
        this.classes = classInd;
        this.features = featureInd;
        nd = new NormalDistributionImpl(mean, sd);
        total = col.getTrainingClassSize(classes[0]);
        this.mean = mean;
        this.median = median;
        setBins(Round(Math.sqrt(total)));
    }

    public void setBins(int n) {
        numberOfBins = n;
        points = new int[numberOfBins];
        normalPoints = new double[numberOfBins];
        refresh();
    }

    private void refresh() {
        if (numberOfBins % 2 == 0) {
            setBins(numberOfBins + 1);
            return;
        }
        maxX = -Double.MAX_VALUE;
        minX = Double.MAX_VALUE;
        maxCount = 0;
        for (int j = 0; j < total; j++) {
            double x = col.trainingObject(classes[0], j)[features[0]];
            if (x > maxX) {
                maxX = x;
            }
            if (x < minX) {
                minX = x;
            }
        }
        binSize = (maxX - minX) / numberOfBins;
        for (int j = 0; j < total; j++) {
            double x = col.trainingObject(classes[0], j)[features[0]];
            int t = (int) ((x - minX) / binSize);
            if (t == numberOfBins) {
                t--;
            }
            points[t]++;
            if (points[t] > maxCount) {
                maxCount = points[t];
            }
        }
        for (int j = 0; j < numberOfBins; j++) {
            try {
                normalPoints[j] = total * (nd.cumulativeProbability(minX + (j + 1) * binSize) - nd.cumulativeProbability(minX + j * binSize));
            } catch (MathException ex) {
            }
        }
        //increase to fit normal distribution
        range = (maxX - minX);
        applySizePref();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        height = getHeight() - margin - step;
        width = getWidth();
        g2d.setRenderingHints(renderHints);
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height + step);
        g2d.setColor(Color.BLACK);
        xPoint = (range) / ((width - step) / step);
        xStep = step / xPoint;

        int n = scale / 2 + 2;
        if (maxX - minX < 1) {
            n = 2 + scale / 2;
        }

        //x-axis
        for (int i = step + step; i < width; i += step) {
            g2d.setColor(gridColor);
            g2d.drawLine(i, height, i, 0);
            g2d.setColor(textColor);
            double val = Round(minX + xPoint * (i - step) / step, n);
            String value = String.valueOf(val);
            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }
            g2d.drawString(value, i - 5 * n, height + 15);
        }

        yPoint = (double) maxCount / ((height) / step);
        if (yPoint != Math.floor(yPoint)) {
            yPoint = Math.floor(yPoint) + 1;
        }
        yStep = step / yPoint;

        //y-axis
        for (int i = height - step; i > -3; i -= step) {
            g2d.setColor(gridColor);
            g2d.drawLine(step, i, width, i);
           g2d.setColor(textColor);
            double val = (-yPoint * (i - height) / step);
            String value = String.valueOf(val);
            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }
            g2d.drawString(value, 5, i + 5);

        }
          g2d.setColor(gridColor);
        g2d.drawLine(0, height, width, height);
        g2d.drawLine(step, height + step, step, 0);
        g2d.drawLine(0, height+step-1, width, height+step-1);
        //drawing margin
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, height + step, width, margin);
        //drawing labels
        g2d.setColor(colors[0]);
        g2d.drawString("mean", 15, height - 30 + margin + step);
        g2d.setColor(colors[1]);
        g2d.drawString("median", 15, height - 15 + margin + step);

        int x, y, x2 = 0, y2 = 0, yn, yn2 = 0, xn, xn2 = 0;
        for (int j = 0; j < numberOfBins; j++) {
            if (j > 0) {
                x = x2;
                y = y2;
                yn = yn2;
                xn = xn2;
            } else {
                x = Round(j * binSize * xStep + step);
                y = height - Round(points[j] * yStep);
                yn = height -  Round(normalPoints[j] * yStep);
                xn = Round(j * binSize * xStep + step);
            }
            if (j < numberOfBins - 1) {
                yn2 = height - Round(normalPoints[j] * yStep);
                xn2 = Round((j + 1) * binSize * xStep + step);
                x2 = Round((j + 1) * binSize * xStep + step);
                y2 = height - Round(points[j + 1] * yStep);
            }
            g2d.setColor(Color.white);
            g2d.fillRect(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
            if (j < numberOfBins - 1) {
                g2d.drawLine(x, y, x2, y2);
            }
            g2d.setColor(Color.MAGENTA);
            //now normal distribution
            g2d.fillRect(xn - dotSize / 2, yn - dotSize / 2, dotSize, dotSize);
            if (j < numberOfBins - 1) {
                g2d.drawLine(xn, yn, xn2, yn2);
            }
        }


        //drawing statistics
        //mean
        g2d.setColor(colors[0]);
        g2d.drawLine(Round((mean - minX) * xStep + step), height, Round((mean - minX) * xStep + step), 0);

        //median
        g2d.setColor(colors[1]);
        g2d.drawLine(Round((median - minX) * xStep + step), height, Round((median - minX) * xStep + step), 0);
    }

    @Override
    public void scaleIncrease() {
        dw = (width / scale) * (scale + 1);
        scale++;
        this.setSize(dw, height);
        applySizePref();
        repaint();
    }

    @Override
    public void scaleDecrease() {
        if (scale > 1) {
            dw = (width / scale) * (scale - 1);
            this.setSize(dw, height);
            applySizePref();
            scale--;
            repaint();
        }

    }
}
