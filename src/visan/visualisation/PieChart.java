package visan.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class PieChart extends DataComponent {

    double[][] data;

    public PieChart(double[][] data) {
        super("PieChart");
        this.data = data;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        height = getHeight() - step;
        width = getWidth();
        int size = Math.min(width -2*step, height -2*step);
        g2d.setRenderingHints(renderHints);
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height + step);
        g2d.setColor(Color.BLACK);
        for (int c = 0; c < data.length; c++) {
            double total = 0;
            for (int i = 0; i < data[0].length; i++) {
                total += data[c][i];
            }
            double curValue = 0.0D;
            int startAngle;
            for (int i = 0; i < data[0].length; i++) {
                startAngle = (int) (curValue * 360 / total);
                int arcAngle = (int) (data[c][i] * 360 / total);
                if (i == data[0].length - 1) {
                    arcAngle = 360 - startAngle;
                }
                g2d.setColor(colors[i % colors.length]);
                g.fillArc(step + 300 * c, height / 2 - size/2, size, size, startAngle, arcAngle);
                curValue += data[c][i];
            }
        }
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
