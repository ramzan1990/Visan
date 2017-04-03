package visan.visualisation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class TrendFile extends DataComponent {

	private int maxCount;
	private double max, min;
	private ArrayList<ArrayList<Double>> arrays;
	private ArrayList<String> names;

	public TrendFile(ArrayList<ArrayList<Double>> arrays,
			ArrayList<String> names) {
		super("trend");
		this.arrays = arrays;
		this.names = names;
		refresh();
	}

	private void refresh() {
		maxCount = -Integer.MAX_VALUE;
		max = -Double.MAX_VALUE;
		min = Double.MAX_VALUE;
		for (int i = 0; i < arrays.size(); i++) {
			if (arrays.get(i).size() > maxCount) {
				maxCount = arrays.get(i).size();
			}
			for (int j = 0; j < arrays.get(i).size(); j++) {
				double n = arrays.get(i).get(j);
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

		// x-axis
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

		// y-axis
		yPoint = (max - min) / ((double) (height / step));
		yStep = step / yPoint;
		for (int i = height - step; i > -3; i -= step) {
			g2d.setColor(gridColor);
			g2d.drawLine(step, i, width, i);
			g2d.setColor(textColor);
			double val = Round(min + yPoint * (height - i) / step, n);
			String value = String.valueOf(val);
			if (value.endsWith(".0")) {
				value = value.substring(0, value.length() - 2);
			}
			g2d.drawString(value, step - 25 - n * 5, i + 5);

		}
		g2d.setColor(gridColor);
		g2d.drawLine(0, height, width, height);
		g2d.drawLine(step, height + step, step, 0);
		g2d.drawLine(0, height + step - 1, width, height + step - 1);
		// drawing margin
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, height + step, width, margin);
		// drawing labels
		g2d.setColor(textColor);
		for (int i = 0; i < arrays.size(); i++) {
			g2d.setColor(colors[i % colors.length]);
			g2d.drawString(names.get(i), 5 + (i / 2) * 60, height - 30 + margin
					+ 20 * (i % 2) + step);
			for (int j = 0; j < arrays.get(i).size(); j++) {
				double v = arrays.get(i).get(j);
				g2d.fillRect((int) (j * xStep + step) - dotSize / 2, height
						- (int) Math.round(yStep * (v - min)) - dotSize / 2,
						dotSize, dotSize);
				if (j < arrays.get(i).size() - 1) {
					double v2 = arrays.get(i).get(j + 1);
					g2d.drawLine((int) (j * xStep + step),
							height - (int) Math.round(yStep * (v - min)),
							(int) ((j + 1) * xStep + step),
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
