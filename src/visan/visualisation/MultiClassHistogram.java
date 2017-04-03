package visan.visualisation;

import visan.common.ClassesCollection;
import visan.common.Round;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class MultiClassHistogram extends DataComponent {

    private int maxCount, sum[], data[][], totalError;
    private boolean shape;
    private double sn[], sp[];

    public MultiClassHistogram(ClassesCollection cc, int[] classes, int[] features, int[][] data) {
        super("mchistogram", cc);
        this.classes = classes;
        this.features = features;
        this.data = data;
        int tia = 0, swn = 0, no = 0;
        sum = new int[classes.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                sum[i] += data[i][j];
            }
            if (sum[i] > maxCount) {
                maxCount = sum[i];
            }
        }
        sn = new double[data.length];
        sp = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            no += data[data.length - 1][i];
            sn[i] = Round.sRound((double) data[i][i] / sum[i]);
            int t = 0;
            for (int c = 0; c < data.length; c++) {
                t += data[c][i];
                if (c != i) {
                    totalError += data[c][i];
                }
            }
            sp[i] = Round.sRound((double) data[i][i] / t);
        }

        for (int i = 0; i < data.length - 1; i++) {
            tia += data[i][i];
            for (int c = 0; c < data.length - 1; c++) {
                swn += data[i][c];
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
        g2d.fillRect(0, height + step, width, margin);
        if (shape == true) {
            for (int c = 0; c < classes.length; c++) {
                double total = 0;
                for (int i = 0; i < classes.length; i++) {
                    total += data[c][i];
                }
                double curValue = 0.0D;
                int startAngle;
                for (int i = 0; i < classes.length; i++) {
                    startAngle = (int) (curValue * 360 / total);
                    int arcAngle = (int) (data[c][i] * 360 / total);
                    if (i == classes.length - 1) {
                        arcAngle = 360 - startAngle;
                    }
                    g2d.setColor(colors[i % colors.length]);
                    g.fillArc(step + 300 * c, height / 2 - 65, 140, 140, startAngle, arcAngle);
                    curValue += data[c][i];
                }
            }
        } else {
            yPoint = (double) maxCount / ((height) / step);
            if (yPoint != Math.floor(yPoint)) {
                yPoint = Math.floor(yPoint) + 1;
            }
            yStep = step / yPoint;
            //y-axis
            for (int i = height - step; i > -3; i -= step) {
                g2d.setColor(gridColor);
                g2d.drawLine(step, i, width, i);
                double val = (-yPoint * (i - height) / step);
                String value = String.valueOf(val);
                if (value.endsWith(".0")) {
                    value = value.substring(0, value.length() - 2);
                }
                g2d.setColor(textColor);
                g2d.drawString(value, 5, i + 5);
            }
            g2d.setColor(gridColor);
            g2d.drawLine(0, height, width, height);
            g2d.drawLine(step, height + step, step, 0);
            g2d.drawLine(0, height + step, width, height + step);
            for (int c = 0; c < classes.length; c++) {
                int total = 0;
                for (int i = 0; i < classes.length; i++) {
                    g2d.setColor(colors[i % colors.length]);
                    int h = (int) Math.round(data[c][i] * yStep);
                    total += h;
                    g2d.fillRect(2 * step + 3 * step * c, height - total, step, h);
                }
            }
        }
        for (int c = 0; c < classes.length; c++) {
            g2d.setColor(colors[c % colors.length]);
            int x;
            if (shape == true) {
                x = step + 3 * step * c;
            } else {
                x = 2 * step + 3 * step * c;
            }
            g2d.drawString("Class: " + col.getClassesNames()[classes[c]], x, height + 20);
            g2d.drawString("SN: " + sn[c] + " SP: " + sp[c], x, height + 40);
        }
        //features names on margin
        g2d.setColor(textColor);
        int u = 0;
        String row[] = {"   ", "   "};
        while (f.size() > u) {
            row[0] += f.get(features[u]) + "   ";
            u += 2;
        }
        u = 1;
        while (f.size() > u) {
            row[1] += f.get(features[u]) + "   ";
            u += 2;
        }
        for (int i = 0; i < 2; i++) {
            g2d.drawString(row[i], 40 + (i / 2) * 60, height - 30 + margin + 20 * (i % 2) + step);
        }

        g2d.drawString("Total Error: " + totalError, width - 150, height - 30 + margin + step);

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

    public void setShape(boolean b) {
        shape = b;
    }
}
