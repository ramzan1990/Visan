package visan.statistics;

import visan.common.ClassesCollection;
import visan.common.Round;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import visan.Visan;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

/**
 *
 * @author Ramzan
 */
public class DataStatistics {

	private static CustomDoubleRenderer customRenderer = new CustomDoubleRenderer();
	private static DefaultTableCellRenderer customRenderer2 = new DefaultTableCellRenderer();
	public static Double mean[], median[], sd[];

	public static JTable produceSummary(ClassesCollection trainingSet, int classIndex, int[] featuresList) {
		int noElements = 8;
		int fNumber = featuresList.length;
		int cSize = trainingSet.getTrainingClassSize(classIndex);
		String[] columnNames = new String[fNumber + 1];
		for (int i = 0; i < fNumber; i++) {
			columnNames[i + 1] = trainingSet.features.get(featuresList[i]);
		}
		columnNames[0] = "Statistic";
		Object[][] data = new Object[noElements][columnNames.length];
		// Mean
		data[0][0] = "Mean";
		mean = new Double[fNumber];
		for (int f = 0; f < fNumber; f++) {
			mean[f] = new Double(0);
		}
		for (int i = 0; i < cSize; i++) {
			for (int f = 0; f < fNumber; f++) {
				mean[f] += trainingSet.trainingObject(classIndex, i)[featuresList[f]];
			}
		}
		for (int f = 0; f < fNumber; f++) {
			mean[f] /= cSize;
		}
		System.arraycopy(mean, 0, data[0], 1, fNumber);
		// Standard Deviation
		data[1][0] = "Standard Deviation";
		sd = new Double[fNumber];
		for (int f = 0; f < fNumber; f++) {
			double sum = 0;
			for (int i = 0; i < cSize; i++) {
				sum += Math.pow(trainingSet.trainingObject(classIndex, i)[featuresList[f]] - mean[f], 2);
			}
			sd[f] = Math.sqrt(sum / (cSize - 1));
		}
		System.arraycopy(sd, 0, data[1], 1, fNumber);
		// Standard error of the mean
		data[2][0] = "Standard error of the mean";
		Double sem[] = new Double[fNumber];
		for (int f = 0; f < fNumber; f++) {
			sem[f] = sd[f] / Math.sqrt(cSize);
		}
		System.arraycopy(sem, 0, data[2], 1, fNumber);
		// 95% confidence interval
		data[3][0] = "95% confidence interval";
		String ci[] = new String[fNumber];
		for (int f = 0; f < fNumber; f++) {
			double a = mean[f] - 1.96 * (sem[f]);
			double b = mean[f] + 1.96 * (sem[f]);
			ci[f] = "(" + a + ", " + b + ")";
		}
		System.arraycopy(sd, 0, data[3], 1, fNumber);
		// Median
		data[4][0] = "Median";
		median = new Double[fNumber];
		int mid;
		if (cSize % 2 == 0) {
			mid = cSize / 2;
		} else {
			mid = (cSize + 1) / 2;
		}
		for (int f = 0; f < fNumber; f++) {
			Double t[] = new Double[cSize];
			for (int i = 0; i < cSize; i++) {
				t[i] = trainingSet.trainingObject(classIndex, i)[featuresList[f]];
			}
			Arrays.sort(t);
			median[f] = t[mid];
		}
		System.arraycopy(median, 0, data[4], 1, fNumber);
		// Normality Test
		try {
			data[5][0] = "Normality Test";
			double chi[] = new double[fNumber];
			double[] binWidth = new double[fNumber];
			int binNumber = 40;
			double l = 3;
			NormalDistribution nd;
			for (int f = 0; f < fNumber; f++) {
				double lLimit = mean[f] - l * sd[f];
				double uLimit = mean[f] + l * sd[f];
				binWidth[f] = ((2 * l) / binNumber) * sd[f];
				nd = new NormalDistributionImpl(mean[f], sd[f]);
				ArrayList<Double> E = new ArrayList<Double>();
				ArrayList<Double> O = new ArrayList<Double>();
				for (int i = 0; i < binNumber; i++) {
					O.add(new Double(0));
				}
				for (int i = 0; i < binNumber / 2; i++) {
					try {
						E.add(cSize * (nd.cumulativeProbability(lLimit + (i + 1) * binWidth[f])
								- nd.cumulativeProbability(lLimit + i * binWidth[f])));
					} catch (MathException ex) {
						JOptionPane.showMessageDialog(null, "Exception!");
					}
				}
				for (int i = 0; i < cSize; i++) {
					double value = trainingSet.trainingObject(classIndex, i)[featuresList[f]];
					if (value > uLimit || value < lLimit) {
						continue;
					}
					for (int b = 1; b <= binNumber; b++) {
						if (value > uLimit - b * binWidth[f]) {
							O.set(binNumber - b, O.get(binNumber - b) + 1);
							break;
						}
					}
				}
				int j = 0;
				int c = 0;
				while (c < binNumber / 2 - 1 && j < E.size()) {
					if (E.get(j) < 5) {
						double temp1 = E.get(j), temp2 = O.get(j), temp3 = O.get(O.size() - 1 - j);
						E.remove(j);
						O.remove(j);
						O.remove(O.size() - 1 - j);
						E.set(j, temp1 + E.get(j));
						O.set(j, temp2 + O.get(j));
						O.set(O.size() - 1 - j, temp3 + O.get(O.size() - 1 - j));
					} else {
						j++;
					}
					c++;
				}
				int s = E.size();
				for (int i = 1; i <= s; i++) {
					E.add(E.get(s - i));
				}
				for (int i = 0; i < E.size(); i++) {
					chi[f] += Math.pow((O.get(i) - E.get(i)), 2) / E.get(i);
				}
				String[] temp = new String[chi.length];
				for (int i = 0; i < chi.length; i++) {
					if (chi[i] < chi_table.check(E.size() - 3)) {
						temp[i] = "Passed";
					} else {
						temp[i] = "Didn't Pass";
					}
				}
				System.arraycopy(temp, 0, data[5], 1, fNumber);

				NumberFormat formatter = new DecimalFormat("###.################");
				Visan.gm.writeToConsole("chi: " + formatter.format(chi[f]) + "   DF:  " + (E.size() - 3) + "\n");
			}
			Visan.gm.writeToConsole("\n");
		} catch (Exception ex) {

		}
		data[6][0] = "Maximum";
		data[7][0] = "Minimum";
		Double max[] = new Double[fNumber];
		Double min[] = new Double[fNumber];
		for (int f = 0; f < fNumber; f++) {
			max[f] = new Double(-Double.MAX_VALUE);
			min[f] = new Double(Double.MAX_VALUE);
		}
		for (int i = 0; i < cSize; i++) {
			for (int f = 0; f < fNumber; f++) {
				double value = trainingSet.trainingObject(classIndex, i)[featuresList[f]];
				if (value > max[f]) {
					max[f] = value;
				} else if (value < min[f]) {
					min[f] = value;
				}
			}
		}
		System.arraycopy(max, 0, data[6], 1, fNumber);
		System.arraycopy(min, 0, data[7], 1, fNumber);

		DataTable m = new DataTable(columnNames, data);
		JTable table = new JTable(m) {

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if (row > 4) {
					return customRenderer2;
				} else if (column == 0) {
					return super.getCellRenderer(row, column);
				} else {
					return customRenderer;
				}
			}
		};
		return table;
	}
}

class CustomDoubleRenderer extends DefaultTableCellRenderer {

	public CustomDoubleRenderer() {
		super();
	}

	@Override
	public void setValue(Object value) {
		double d = (Double) value;
		NumberFormat formatter = new DecimalFormat("###.##########");
		setText((value == null) ? "" : formatter.format(Round.ssRound(d)));
	}
}
