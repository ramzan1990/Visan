package visan.visualisation;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import visan.common.MDouble;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import visan.Visan;

public class AccuracyHistogram extends DataComponent {

	private int numberOfBins, maxCount, tp, tn, fp, fn, class1Len, class2Len,
			te;
	private static boolean histogramShape;
	private int[][] points;
	private double maxX, minX, binSize, cc, sn, sp, pc, fs, r1, r2, p1, p2;
	private ClassAndValue cav[];
	private MDouble decThreshold;
	private static boolean roc;

	public AccuracyHistogram(ClassesCollection cc, ClassAndValue[] ldf,
			int[] classInd, int[] featureInd, MDouble l) {
		super("accuracyHistogram", cc);
		this.cav = ldf;
		this.classes = classInd;
		this.features = featureInd;
		this.decThreshold = l;
		addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					click(e.getX());
				} else {
					decThreshold.value = 0;
					Visan.gm.updateAccuracyHistograms();
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
	}

	public void setBins(int n) {
		numberOfBins = n + 1;
		points = new int[2][numberOfBins];
		refresh();
	}

	public void updateSpecificity() {
		tp = tn = fp = fn = 0;
		cc = sn = sp = fs = 0;
		r1 = r2 = p1 = p2 = 0;
		for (int i = 0; i < cav.length; i++) {
			int c;
			if (cav[i].classIndex == classes[0]) {
				c = 0;
			} else {
				c = 1;
			}
			if (cav[i].value > decThreshold.value) {
				if (c == 0) {
					fp++;
				} else {
					tp++;
				}
			} else {
				if (c == 0) {
					tn++;
				} else {
					fn++;
				}
			}
		}
		
		//tp = 1;
		//fn=49;
		//tn=49;
		//fp=1; 
		
		
		
		sn = (double) tp / (tp + fn);
		sn = Round(sn, 3);
		sp = (double) tn / (tn + fp);
		sp = Round(sp, 3);
		long a1, a2, a3, a4;
		a1 = (long) tp * tn - (long) fp * fn;
		a2 = (long) (tp + fp) * (long) (tp + fn);
		a3 = (long) (tn + fp) * (long) (tn + fn);
		a4 = (long) Math.sqrt(a2) * (long) Math.sqrt(a3);
		cc = (double) a1 / a4;
		cc = Round(cc, 3);
		fs = ((double) 2 * tp) / ((double) (2 * tp + fp + fn));
		fs = Round(fs, 3);
		te = fp + fn;
		pc = 100 * (tp + tn) / (tp + tn + fp + fn);

		r1 = Round(100 * (double) tp / (tp + fn), 1);
		p1 = Round(100 * (double) tp / (tp + fp), 1);

		r2 = Round(100 * (double) tn / (tn + fp), 1);
		p2 = Round(100 * (double) tn / (tn + fn), 1);
		//VISAN.gm.writeToConsole(tp + "  " + fn + "  " + tn + "  " + fp + "  " + sn
		//		+ "  " + sp + "  " + fs + "  " + cc + "  " + p1);
	}

	private void refresh() {
		maxX = cav[0].value;
		minX = maxX;
		maxCount = 0;
		class1Len = class2Len = 0;
		for (int i = 0; i < cav.length; i++) {
			if (cav[i].value > maxX) {
				maxX = cav[i].value;
			}
			if (cav[i].value < minX) {
				minX = cav[i].value;
			}
		}
		binSize = (maxX - minX) / (numberOfBins - 1);
		for (int i = 0; i < cav.length; i++) {
			int c;
			if (cav[i].classIndex == classes[0]) {
				c = 0;
				class1Len++;
			} else {
				c = 1;
				class2Len++;
			}
			int t = (int) ((cav[i].value - minX) / binSize);
			if (t == numberOfBins) {
				t--;
			}
			points[c][t]++;
			if (points[c][t] > maxCount) {
				maxCount = points[c][t];
			}
		}
		updateSpecificity();
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
		xPoint = (maxX - minX) / ((width - step) / step);
		xStep = step / xPoint;

		int n = scale / 4 + 1;
		if (maxX - minX < 1) {
			n = 2 + scale / 4;
		}

		// x-axis
		for (int i = 2 * step; i < width; i += step) {
			g2d.setColor(gridColor);
			g2d.drawLine(i, height, i, 0);
			g2d.setColor(textColor);
			// g2d.fillRect(i - 1, height - 3, 3, 7);
			double val = Round(minX + xPoint * (i - step) / step, n);
			String value = String.valueOf(val);
			if (value.endsWith(".0")) {
				value = value.substring(0, value.length() - 2);
			}
			g2d.drawString(value, i - 5, height + 15);
		}

		yPoint = (double) maxCount / ((height) / step);
		if (yPoint != Math.floor(yPoint)) {
			yPoint = Math.floor(yPoint) + 1;
		}
		yStep = step / yPoint;

		// y-axis
		for (int i = height - step; i > -3; i -= step) {
			g2d.setColor(gridColor);
			g2d.drawLine(step, i, width, i);
			g2d.setColor(textColor);
			// g2d.fillRect(step - 3, i - 1, 7, 3);
			double val = (-yPoint * (i - height) / step);
			String value = String.valueOf(val);
			if (value.endsWith(".0")) {
				value = value.substring(0, value.length() - 2);
			}
			g2d.drawString(value, step - 35, i + 5);

		}
		// drawing axis
		g2d.setColor(gridColor);
		g2d.drawLine(0, height, width, height);
		g2d.drawLine(step, height + step, step, 0);
		g2d.drawLine(0, height + step - 1, width, height + step - 1);
		// drawing margin
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, height + step, width, margin);
		// classes names on margin
		for (int i = 0; i < 2; i++) {
			g2d.setColor(colors[i % colors.length]);
			g2d.drawString(col.getClassesNames()[classes[i]], 5 + (i / 2) * 60,
					height - 30 + margin + 20 * (i % 2) + step);
		}
		// features names on margin
		g2d.setColor(textColor);
		int u = 0;
		String row[] = { "   ", "   " };
		while (features.length > u) {
			row[0] += f.get(features[u]) + "   ";
			u += 2;
		}
		u = 1;
		while (features.length > u) {
			row[1] += f.get(features[u]) + "   ";
			u += 2;
		}
		for (int k = 0; k < 2; k++) {
			g2d.drawString(row[k], 65 + (k / 2) * 60, height - 30 + margin + 20
					* (k % 2) + step);
		}
		int gap = 80;
		int in = 550;
		g2d.drawString("TE: " + te, width - (in), height - 30 + margin + step);
		g2d.drawString("ACC: " + Round(pc) + "%", width - (in), height - 10
				+ margin + step);
		g2d.drawString("TP: " + tp, width - (in - gap), height - 30 + margin
				+ step);
		g2d.drawString("FP: " + fp, width - (in - gap), height - 10 + margin
				+ step);
		g2d.drawString("FN: " + fn, width - (in - 2 * gap), height - 30
				+ margin + step);
		g2d.drawString("TN: " + tn, width - (in - 2 * gap), height - 10
				+ margin + step);
		g2d.drawString("R1: " + r1, width - (in - 3 * gap), height - 30
				+ margin + step);
		g2d.drawString("P1: " + p1, width - (in - 3 * gap), height - 10
				+ margin + step);
		g2d.drawString("R2: " + r2, width - (in - 4 * gap), height - 30
				+ margin + step);
		g2d.drawString("P2: " + p2, width - (in - 4 * gap), height - 10
				+ margin + step);
		g2d.drawString("CC: " + cc, width - (in - 5 * gap), height - 30
				+ margin + step);
		g2d.drawString("F1: " + fs, width - (in - 5 * gap), height - 10
				+ margin + step);
		g2d.drawString("DT: " + Round(decThreshold.value, 3), width
				- (in - 6 * gap), height - 30 + margin + step);
		if (!histogramShape) {
			for (int i = 0; i < 2; i++) {
				Color cc = colors[i % colors.length];
				g2d.setColor(cc);
				int xPoints[];
				int yPoints[];
				for (int j = 0; j < numberOfBins; j++) {
					if (points[i][j] > 0) {
						g2d.setColor(cc);
						g2d.fillRect(Round(j * binSize * xStep + step)
								- dotSize / 2, height
								- Round(points[i][j] * yStep) - dotSize / 2,
								dotSize, dotSize);
						if (j < numberOfBins - 1) {
							int x1 = Round(j * binSize * xStep + step);
							int y1 = height - Round(points[i][j] * yStep);
							int x2 = Round((j + 1) * binSize * xStep + step);
							int y2 = height - Round(points[i][j + 1] * yStep);
							g2d.drawLine(x1, y1, x2, y2);
							xPoints = new int[] { x1, x1, x2, x2 };
							yPoints = new int[] { height, y1, y2, height };
							g2d.setColor(new Color(cc
									.getRGBColorComponents(null)[0], cc
									.getRGBColorComponents(null)[1], cc
									.getRGBColorComponents(null)[2], 0.2f));
							g2d.fillPolygon(xPoints, yPoints, 4);
						}

					}
				}

			}
		} else {
			for (int j = 0; j < numberOfBins; j++) {
				int y0 = Round(points[0][j] * yStep);
				int y1 = Round(points[1][j] * yStep);
				if (y0 > y1) {
					g2d.setColor(colors[0]);
					g2d.fillRect(Round(j * binSize * xStep + step),
							height - y0, Round(binSize * xStep), y0);
					g2d.setColor(colors[1]);
					g2d.fillRect(Round(j * binSize * xStep + step),
							height - y1, Round(binSize * xStep), y1);
				} else {
					if (points[1][j] > 0) {
						g2d.setColor(colors[1]);
						g2d.fillRect(Round(j * binSize * xStep + step), height
								- y1, Round(binSize * xStep), y1);
						g2d.setColor(colors[0]);
						g2d.fillRect(Round(j * binSize * xStep + step), height
								- y0, Round(binSize * xStep), y0);
					}
				}
			}
		}
		g2d.setColor(decThresholdColor);
		int d = (int) Round((decThreshold.value - minX) * xStep + step, 1);
		g2d.setStroke(new BasicStroke(2));
		g2d.drawLine(d, 0, d, height);
		if (roc) {
			// ROC curve
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(Color.BLACK);
			int numberOfPoints1, numberOfPoints2, c1 = 0, c2 = 0;
			int x0, x, y0, y, xd = 0, yd = 0;
			double min;
			x0 = x = width - 10;
			y0 = y = 10;
			min = Double.POSITIVE_INFINITY;
			numberOfPoints1 = Math.max(class1Len / (width / 4), class1Len
					/ (height / 4));
			numberOfPoints2 = Math.max(class2Len / (width / 4), class2Len
					/ (height / 4));
			if (numberOfPoints1 == 0) {
				numberOfPoints1 = 1;
			}
			if (numberOfPoints2 == 0) {
				numberOfPoints2 = 1;
			}
			for (int i = 0; i < cav.length; i++) {
				if (cav[i].classIndex == classes[0]) {
					c1++;
				}
				if (cav[i].classIndex == classes[1]) {
					c2++;
				}
				if (c1 == numberOfPoints1) {
					c1 = 0;
					g2d.drawLine(--x, y, x, y);
				} else if (c2 == numberOfPoints2) {
					c2 = 0;
					g2d.drawLine(x, ++y, x, y);
				}
				if (Math.abs(cav[i].value - decThreshold.value) < min) {
					xd = x;
					yd = y;
					min = Math.abs(cav[i].value - decThreshold.value);
				}
			}
			// point
			g2d.setColor(Color.RED);
			g2d.drawLine(xd, y0, xd, y);
			g2d.setColor(Color.BLUE);
			g2d.drawLine(x, yd, x0, yd);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawRect(x, y0, x0 - x, y - y0);
			// diagonal
			g2d.drawLine(x, y, x0, y0);
			g2d.setColor(textColor);
			g2d.drawString("0", x - 10, y + 10);
			g2d.drawString("1", x - 10, y0);
			g2d.drawString("1", x0, y + 10);
			g2d.drawString("TPF", x - 25, y - (y - y0) / 2);
			g2d.drawString("FPF", x + (x0 - x) / 2, y + 15);
		}
	}

	public void click(int ex) {
		decThreshold.value = (ex - step) / xStep + minX;
		Visan.gm.updateAccuracyHistograms();
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

	public static void setROC(boolean b) {
		roc = b;
	}

	public static void setShape(boolean s) {
		histogramShape = s;
	}
}
