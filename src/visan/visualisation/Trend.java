package visan.visualisation;

import visan.common.ClassesCollection;

import java.awt.*;

public class Trend extends DataComponent {

    private int maxCount;
    private double max, min;

    public Trend(ClassesCollection cc, int[] classes, int[] features) {
        super("trend", cc);
        this.classes = classes;
        this.features = features;
        refresh();

    }

    private void refresh() {
        maxCount = -Integer.MAX_VALUE;
        max = -Double.MAX_VALUE;
        min = Double.MAX_VALUE;
        for (int i = 0; i < classes.length; i++) {
            if (col.getTrainingClassSize(classes[i]) > maxCount) {
                maxCount = col.getTrainingClassSize(classes[i]);
            }
            for (int j = 0; j < col.getTrainingClassSize(classes[i]); j++) {
                double n = col.trainingObject(classes[i], j)[features[0]];
                if (n > max) {
                    max = n;
                }
                if (n < min) {
                    min = n;
                }
            }
        }
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

        int n = scale / 2 + 1;

        //x-axis
        xPoint = (double) maxCount / ((width - step) / step);
        if (xPoint != Math.floor(xPoint)) {
            xPoint = Math.floor(xPoint) + 1;
        }
        xStep = step / xPoint;
        for (int i = 2 * step; i < width; i += step) {
            g2d.setColor(gridColor);
            g2d.drawLine(i, height, i, 0);
            g2d.setColor(textColor);
            double val = (xPoint * (i - step) / step);
            String value = String.valueOf(val);
            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }
            g2d.drawString(value, i - 5, height + 15);
        }

        //y-axis
        yPoint = (max - min) / ((double) (height / step));
        yStep = step / yPoint;
        for (int i = height - step; i > -3; i -= step) {
            g2d.setColor(gridColor);
            g2d.drawLine(step, i, width, i);
            g2d.setColor(textColor);
            double val = Round(min + yPoint * (height - i) / step,  2);
            String value = String.valueOf(val);
            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }
            g2d.drawString(value, step - 25 - 5, i + 5);

        }
        g2d.setColor(gridColor);
        g2d.drawLine(0, height, width, height);
        g2d.drawLine(step, height + step, step, 0);
        g2d.drawLine(0, height + step - 1, width, height + step - 1);
        //drawing margin
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, height + step, width, margin);
        //drawing labels
        g2d.setColor(textColor);
        g2d.drawString(f.get(features[0]), width - 60, height - 30 + margin + step);
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < classes.length; i++) {
            g2d.setColor(colors[i % colors.length]);
            g2d.drawString(col.getClassesNames()[classes[i]], 5 + (i / 2) * 60, height - 30 + margin + 20 * (i % 2) + step);
            for (int j = 0; j < col.getTrainingClassSize(classes[i]); j++) {
                double v = col.trainingObject(classes[i], j)[features[0]];
                g2d.fillRect((int) (j * xStep + step) - dotSize / 2,
                        height - (int) Math.round(yStep * (v - min)) - dotSize / 2, dotSize, dotSize);
                if (j < col.getTrainingClassSize(classes[i]) - 1) {
                    double v2 = col.trainingObject(classes[i], j + 1)[features[0]];
                    g2d.drawLine((int) (j * xStep + step), height - (int) Math.round(yStep * (v - min)), (int) ((j + 1) * xStep + step),
                            height - (int) Math.round(yStep * (v2 - min)));
                }

            }
        }
    }

    @Override
    public void scaleIncrease() {
        if (xPoint > 1) {
            dw = (width / scale) * (scale + 1);
            scale++;
            this.setSize(dw, height);
            applySizePref();
            repaint();
        }
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
