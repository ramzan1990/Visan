package visan.visualisation;


import visan.common.ClassesCollection;
import visan.common.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Plot extends DataComponent {

    private boolean selectionMode, multipleSelectionMode;
    private ArrayList<Point> selectedPoints;
    private ArrayList<Point> multSelPoints;
    private double maxX, minX, maxY, minY, s;
    private Color axisColor;
    private int pex, pey;

    public Plot(ClassesCollection cc, int[] classInd, int[] featureInd) {
        super("plot", cc);
        selectedPoints = new ArrayList<Point>();
        this.classes = classInd;
        this.features = featureInd;
        addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    click(e.getX(), e.getY());
                } else {
                    rightClick(e.getX(), e.getY());
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        axisColor = Color.black;

        maxX = col.trainingObject(classInd[0], 0)[featureInd[0]];
        minX = maxX;
        maxY = col.trainingObject(classInd[0], 0)[featureInd[1]];
        minY = maxY;
        for (int i = 0; i < classInd.length; i++) {
            for (int j = 0; j < col.getTrainingClassSize(classInd[i]); j++) {
                double x = col.trainingObject(classInd[i], j)[featureInd[0]];
                if (x > maxX) {
                    maxX = x;
                }
                if (x < minX) {
                    minX = x;
                }
                double y = col.trainingObject(classInd[i], j)[featureInd[1]];
                if (y > maxY) {
                    maxY = y;
                }
                if (y < minY) {
                    minY = y;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        height = getHeight() - margin;
        width = getWidth();
        g2d.setRenderingHints(renderHints);
        // <editor-fold desc="grid">
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(gridColor);
        g2d.drawLine(step, height + step, step, 0);
        g2d.drawLine(0, height - step, width, height - step);
         g2d.drawLine(0, height-1, width, height-1);
        
        for (int i = step; i < this.getWidth(); i += step) {
            g2d.drawLine(step + i, height - step, step + i, 0);
        }
        for (int i = step; i < this.getHeight(); i += step) {
            g2d.drawLine(step, height - step - i, width, height - step - i);
        }

        double p;
        if ((maxX - minX) / width > (maxY - minY) / height) {
            p = (maxX - minX) / ((width - step) / step - 1);
        } else {
            p = (maxY - minY) / ((height - step) / step - 1);
        }
        s = step / p;
        int n = scale / 2 + 1;
        if ((maxY - minY) < 1 || (maxX - minX) < 1) {
            n = 2 + scale / 2;
        }
        g2d.setColor(textColor);
        for (int i = 0; i < width; i += step) {
            double val = Round(minX + (p * i) / step, n);
            String value = String.valueOf(val);
            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }
            g2d.drawString(value, i + 2 * step - 3, height - step + 20);
        }
        for (int i = height; i > -3; i -= step) {
            double val = Round(minY + p * (height - i) / step, n);
            String value = String.valueOf(val);
            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }
            g2d.drawString(value, step - 35 - 5 * n, i - 2 * step + 5);
        }// </editor-fold>
        //drawing margin
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, height, width, margin);
        //drawing labels
        g2d.setColor(textColor);
        g2d.drawString(f.get(features[0]), width - 120, height - 30 + margin);
        g2d.drawString(f.get(features[1]), width - 120, height - 10 + margin);
        // <editor-fold  desc="drawing points">
        for (int i = 0; i < classes.length; i++) {
            g2d.setColor(colors[i % colors.length]);
            g2d.drawString(col.getClassesNames()[classes[i]], 5 + (i / 2) * 60, height - 30 + margin + 20 * (i % 2));
            for (int j = 0; j < col.getTrainingClassSize(classes[i]); j++) {
                double xd = col.trainingObject(classes[i], j)[features[0]],
                        yd = col.trainingObject(classes[i], j)[features[1]];
                int x = (int) Math.round((xd - minX) * s + 2 * step),
                        y = (int) Math.round(height - (yd - minY) * s - 2 * step);
                if (selectionMode) {
                    if (selectedPoints.contains(new Point(classes[i], j))) {
                        g2d.drawLine(x, y - (scale + dotSize), x, y + (scale + dotSize));
                        g2d.drawLine(x - (scale + dotSize), y, x + (scale + dotSize), y);
                    }
                }
                g2d.fillOval(Math.round(x - dotSize / 2), Math.round(y - dotSize / 2), dotSize, dotSize);
            }
            if (multSelPoints != null) {
                for (int u = 0; u < multSelPoints.size(); u++) {
                    g2d.setColor(Color.GREEN);
                    int msx = (int) Math.round((multSelPoints.get(u).x - minX) * s + 2 * step),
                            msy = (int) Math.round(height - (multSelPoints.get(u).y - minY) * s - 2 * step);
                    g2d.fillOval(msx - 4, msy - 4, 8, 8);
                    g2d.setColor(Color.RED);
                    if (u + 1 < multSelPoints.size()) {
                        g2d.drawLine(msx, msy, (int) Math.round((multSelPoints.get(u + 1).x - minX) * s + 2 * step),
                                (int) Math.round(height - (multSelPoints.get(u + 1).y - minY) * s - 2 * step));
                    }
                }
            }
        }// </editor-fold>

    }

    public void selectionModeOn() {
        selectedPoints = new ArrayList<Point>();
        selectionMode = true;
    }

    public void selectionModeOff() {
        multipleSelectionMode = false;
        multSelPoints = null;
        selectedPoints = null;
        selectionMode = false;
    }

    public ArrayList<Point> getSelectedPoints() {
        return selectedPoints;
    }

    @Override
    public void scaleIncrease() {
        dw = ((getWidth() / scale) * (scale + 1));
        dh = margin + (((getHeight() - margin) / scale) * (scale + 1));
        scale++;
        applySizePref();
        repaint();
    }

    @Override
    public void scaleDecrease() {
        if (scale > 1) {
            dw = ((getWidth() / scale) * (scale - 1));
            dh = margin + (((getHeight() - margin) / scale) * (scale - 1));
            scale--;
            applySizePref();
            repaint();
        }
    }

    private Point translate(int ex, int ey) {
        double x1 = (ex - 2 * step) / s + minX, y1 = -(ey - (getHeight() - margin) + 2 * step) / s + minY;
        return new Point(x1, y1);
    }

    private void rightClick(int ex, int ey) {
        if (selectionMode) {
            Point p = translate(ex, ey);
            if (!multipleSelectionMode) {
                multipleSelectionMode = true;
                multSelPoints = new ArrayList<Point>();
                multSelPoints.add(p);
                pex = ex;
                pey = ey;
            } else {
                if ((Math.sqrt(Math.pow(pey - ey, 2) + Math.pow(pex - ex, 2))) < 10) {
                    finishMultiSelection();
                } else {
                    multSelPoints.add(p);
                }
            }
            repaint();
        }

    }

    private void finishMultiSelection() {
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < col.getTrainingClassSize(classes[i]); j++) {
                double x = col.trainingObject(classes[i], j)[features[0]],
                        y = col.trainingObject(classes[i], j)[features[1]];
                if (isInsidePoly(multSelPoints, x, y)) {
                    selectedPoints.add(new Point(classes[i], j));
                }
            }
        }
        multipleSelectionMode = false;
        multSelPoints = null;
        repaint();
    }

    private void click(int ex, int ey) {
        if (selectionMode) {
            Point p = translate(ex, ey);
            double min = 0;
            int sx = 0, sy = 0;
            for (int i = 0; i < classes.length; i++) {
                for (int j = 0; j < col.getTrainingClassSize(classes[i]); j++) {
                    double x2 = col.trainingObject(classes[i], j)[features[0]],
                            y2 = col.trainingObject(classes[i], j)[features[1]];
                    if ((i == 0 && j == 0) || (min > (Math.sqrt(Math.pow(y2 - p.y, 2) + Math.pow(x2 - p.x, 2))) && !selectedPoints.contains(new Point(x2, y2)))) {
                        min = Math.sqrt(Math.pow(y2 - p.y, 2) + Math.pow(x2 - p.x, 2));
                        sx = classes[i];
                        sy = j;
                    }
                }
            }
            selectedPoints.add(new Point(sx, sy));
            repaint();
        }
    }

    private boolean isInsidePoly(ArrayList<Point> p, double x, double y) {
        int i, j;
        boolean c = false;
        for (i = 0, j = p.size() - 1; i < p.size(); j = i++) {
            if ((((p.get(i).y <= y) && (y < p.get(j).y))
                    || ((p.get(j).y <= y) && (y < p.get(i).y)))
                    && (x < (p.get(j).x - p.get(i).x) * (y - p.get(i).y) / (p.get(j).y - p.get(i).y) + p.get(i).x)) {
                c = !c;
            }
        }
        return c;
    }
}