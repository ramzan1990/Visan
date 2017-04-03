package visan.ml.DA;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import visan.common.MDouble;
import visan.common.Round;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import visan.Visan;

import org.apache.commons.math.linear.*;
import org.apache.commons.math.stat.correlation.Covariance;

public class DiscriminantAnalysis implements Serializable {

	private Thread thread;
	public ArrayList<DiscriminantFunction> computedDA;
	public MDouble decisionThreshold;
	public boolean useP;
	public int method;
	private ClassesCollection trainingSet;
	private int accHistogramCount, multiHistogramCount, testHistogramCount;
	private double mDistTwoClass;

	public DiscriminantAnalysis() {
		computedDA = new ArrayList<DiscriminantFunction>();
		decisionThreshold = new MDouble();
		useP = true;
	}

	private void analyse(int[] classInd, int[] featureInd) {
		int fnumber = featureInd.length;
		// number of groups
		int classes = classInd.length;
		// global mean vector, that is mean of the whole data set
		RealVector m = new ArrayRealVector(fnumber);
		// mean corrected data, that is the features data for group i, minus the
		// global mean vector
		ArrayList<RealMatrix> xo = new ArrayList<RealMatrix>();
		// covariance matrix of group i
		ArrayList<RealMatrix> Ci = new ArrayList<RealMatrix>();
		// pooled within group covariance matrix. It is calculated for each
		// entry in the matrix.
		RealMatrix C = new Array2DRowRealMatrix(fnumber, fnumber);
		ArrayList<RealVector> mi = new ArrayList<RealVector>();
		int numberOfObjects = 0;
		for (int i = 0; i < classes; i++) {
			RealVector tmi = new ArrayRealVector(fnumber);
			int classSize = trainingSet.getTrainingClassSize(classInd[i]);
			RealMatrix temp = new Array2DRowRealMatrix(classSize, fnumber);
			numberOfObjects += classSize;
			for (int q = 0; q < classSize; q++) {
				temp.setRow(q,
						trainingSet.trainingObject(classInd[i], q, featureInd));
				for (int j = 0; j < fnumber; j++) {
					tmi.setEntry(
							j,
							tmi.getEntry(j)
									+ trainingSet
											.trainingObject(classInd[i], q)[featureInd[j]]);
					m.setEntry(
							j,
							m.getEntry(j)
									+ trainingSet
											.trainingObject(classInd[i], q)[featureInd[j]]);
				}
			}
			xo.add(temp);
			for (int j = 0; j < fnumber; j++) {
				tmi.setEntry(j, tmi.getEntry(j) / classSize);
			}
			mi.add(tmi);
		}
		for (int j = 0; j < fnumber; j++) {
			m.setEntry(j, m.getEntry(j) / numberOfObjects);
		}
		for (int i = 0; i < xo.size(); i++) {
			for (int q = 0; q < xo.get(i).getRowDimension(); q++) {
				for (int j = 0; j < xo.get(i).getColumnDimension(); j++) {
					xo.get(i).setEntry(q, j,
							xo.get(i).getEntry(q, j) - m.getEntry(j));
				}
			}
		}
		try {
			for (int i = 0; i < classes; i++) {
				Covariance c = new Covariance(xo.get(i));
				RealMatrix mc = c.getCovarianceMatrix();
				Ci.add(mc);
			}
		} catch (Exception e) {
			Ci = new ArrayList<RealMatrix>();
			for (int i = 0; i < classes; i++) {
				RealMatrix temp = xo.get(i).transpose().multiply(xo.get(i));
				temp = temp.scalarMultiply((double) 1
						/ trainingSet.getTrainingClassSize(classInd[i]));
				Ci.add(temp);
			}
		}
		for (int i = 0; i < C.getRowDimension(); i++) {
			for (int j = 0; j < C.getColumnDimension(); j++) {
				for (int q = 0; q < Ci.size(); q++) {
					C.setEntry(
							i,
							j,
							C.getEntry(i, j)
									+ trainingSet
											.getTrainingClassSize(classInd[q])
									* Ci.get(q).getEntry(i, j));
				}
				C.setEntry(i, j, C.getEntry(i, j) / (numberOfObjects));
			}
		}
		C = new LUDecompositionImpl(C).getSolver().getInverse();
		if (classes == 2) {
			RealVector t1 = mi.get(0).subtract(mi.get(1));
			RealVector t2 = C.preMultiply(t1);
			double t3 = Math.sqrt(t2.dotProduct(t1));
			mDistTwoClass = t3;
		}
		DiscriminantFunction temp = new DiscriminantFunction(classInd,
				featureInd);
		int s = 0;
		for (int i = 0; i < classes; i++) {
			s += trainingSet.getTrainingClassSize(classInd[i]);
		}
		temp.P = new ArrayRealVector(classes);
		for (int i = 0; i < classes; i++) {
			temp.P.setEntry(i,
					(double) trainingSet.getTrainingClassSize(classInd[i]) / s);
		}
		temp.C = Ci;
		temp.pC = C;
		temp.mi = mi;
		temp.miC = new ArrayList<RealVector>();
		temp.MD = new ArrayList<Double>();
		for (int i = 0; i < classes; i++) {
			temp.miC.add(C.preMultiply(mi.get(i)));
			temp.MD.add(temp.miC.get(i).dotProduct(mi.get(i)));
		}
		int i = indexOf(classInd, featureInd);
		if (i != -1) {
			computedDA.remove(i);
		}
		computedDA.add(temp);
		Visan.gm.writeToConsole("DA Analysis Finished. \n\n");
		
		
	}

	public String toString(int[] classInd, int[] featureInd){
		String exp = "";
		ensure(classInd, featureInd);
		int i = indexOf(classInd, featureInd);
		DiscriminantFunction temp = computedDA.get(i);
		exp += decisionThreshold.value + "\n";
		for (int t = 0; t < 2; t++) {
			for (int k = 0; k < temp.miC.get(t).getDimension(); k++) {
				exp +=temp.miC.get(t).getEntry(k) + " ";
			}
			exp +="\n";
		}
		exp +="\n";
		for (int t = 0; t < 2; t++) {
			exp +=temp.MD.get(t).toString() + "\n";
		}
		exp +="\n";
		for (int t = 0; t < 2; t++) {
			exp +=temp.P.getEntry(t) + "\n";
		}
		return exp;
	}
	
	
	public void ensure(int[] classInd, int[] featureInd) {
		if (indexOf(classInd, featureInd) == -1) {
			analyse(classInd, featureInd);
		}
	}

	public int classify(double[] x, int[] classesList, int[] featuresList) {
		ensure(classesList, featuresList);
		int predictedClass;
		if (classesList.length == 2) {
			double df = DFValue(x, classesList, featuresList);
			if (df > decisionThreshold.value) {
				predictedClass = 1;
			} else {
				predictedClass = 0;
			}
			return predictedClass;
		} else {
			double temp;
			int max = 0;
			double maxV = 0;
			for (int i = 0; i < classesList.length; i++) {
				temp = classVal(x, i, classesList, featuresList);
				if (i == 0) {
					max = i;
					maxV = temp;
				} else if (temp > maxV) {
					max = i;
					maxV = temp;
				}
			}
			return max;
		}
	}

	public void classify(ArrayList<double[]> x, int[] classesList,
			int[] featuresList) {
		Visan.gm.writeToConsole("\n");
		for (int j = 0; j < x.size(); j++) {
			// for (int q = 0; q < featuresList.length; q++) {
			// VISAN.gm.writeToConsole(x.get(j)[q] + "  ");
			// }
			int predictedClass = classify(x.get(j), classesList, featuresList);
			if (classesList.length == 2) {
				// <editor-fold desc="probability">
				double pgx, pxg, epxg = 0, pg;
				pg = (double) trainingSet.getTrainingClassSize(predictedClass)
						/ trainingSet.getTrainingElementsSum(classesList);
				pxg = Pxg(predictedClass, x.get(j), classesList, featuresList);
				for (int k = 0; k < classesList.length; k++) {
					epxg += Pxg(k, x.get(j), classesList, featuresList)
							* ((double) trainingSet.getTrainingClassSize(k) / trainingSet
									.getTrainingElementsSum(classesList));
				}
				pgx = pxg * pg / epxg;
				// </editor-fold>
				Visan.gm.writeToConsole("Predicted Class: "
						+ trainingSet.getClassesNames()[predictedClass]
						+ ". Probability: " + Round.sRound(pgx) * 100 + "%\n");
			} else {
				Visan.gm.writeToConsole("Predicted Class:"
						+ trainingSet.getClassesNames()[predictedClass] + "\n");
			}
		}
	}

	public double classVal(double[] x, int classIndex, int[] classInd,
			int[] featureInd) {
		int n = featureInd.length;
		RealVector X = new ArrayRealVector(n);
		for (int i = 0; i < n; i++) {
			X.setEntry(i, x[featureInd[i]]);
		}
		DiscriminantFunction l = computedDA.get(indexOf(classInd, featureInd));
		double r;
		if (method == 0) {
			r = l.miC.get(classIndex).dotProduct(X) - 0.5
					* l.MD.get(classIndex);
			if (useP) {
				r += Math.log(l.P.getEntry(classIndex));
			}
			return r;
		} else {
			RealVector v1 = X.subtract(l.mi.get(classIndex));
			RealVector v2 = new LUDecompositionImpl(l.C.get(classIndex))
					.getSolver().getInverse().preMultiply(v1);
			r = -0.5
					* Math.log(new LUDecompositionImpl(l.C.get(classIndex))
							.getDeterminant()) - v2.dotProduct(v1);
			if (useP) {
				r += Math.log(l.P.getEntry(classIndex));
			}
			return r;
		}
	}

	public double DFValue(double[] classObject, int[] classInd, int[] featureInd) {
		double df1 = classVal(classObject, 0, classInd, featureInd), df2 = classVal(
				classObject, 1, classInd, featureInd);
		return df2 - df1;
	}

	private double Pxg(int g, double[] x, int[] classesList, int[] featuresList) {
		double pxg;
		int n = featuresList.length;
		RealVector xm = new ArrayRealVector(n);
		for (int t = 0; t < n; t++) {
			xm.setEntry(t, x[t]);
		}
		DiscriminantFunction df = computedDA.get(indexOf(classesList,
				featuresList));
		RealVector t1 = xm.subtract(df.mi.get(g));
		RealMatrix Ci = new LUDecompositionImpl(df.C.get(g)).getSolver()
				.getInverse();
		RealVector t2 = Ci.preMultiply(t1);
		double t3 = t2.dotProduct(t1);
		pxg = Math.pow(Math.E, -0.5 * t3);
		pxg /= Math.sqrt(Math.pow(2 * Math.PI, n)
				* (new LUDecompositionImpl(df.C.get(g)).getDeterminant()));
		return pxg;
	}

	public String toString(int i) {
		DiscriminantFunction df = computedDA.get(i);
		String r = df.ClassIndices() + "\n" + df.FeatureIndices() + "\n";
		for (int k = 0; k < df.C.size(); k++) {
			r += "C" + k + ":\n";
			r += df.C.get(k).toString();
		}
		r += "PooledCovarianceInverse:\n";
		r += df.pC.toString();
		r += "mi:\n";
		for (int t = 0; t < df.mi.size(); t++) {
			r += df.mi.get(t).toString() + "\n";

		}
		return r;
	}

	public String getMethodName() {
		if (method == 0) {
			return "LDF";
		} else {
			return "QDF";
		}
	}

	public void DrawAccuracyHistogram(int[] classes, int[] features) {
		if (threadIsBusy()) {
			return;
		}
		ensure(classes, features);
		if (classes.length < 2) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least two classes.");

		} else if (features.length == 0) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least one feature.");

		} else if (classes.length == 2) {
			AccuracyHistogram(classes, features);
		} else {
			MultiHistogram(classes, features);
		}
	}

	public void DrawTestAccuracyHistogram(int[] classes, int[] features) {
		if (threadIsBusy()) {
			return;
		}
		if (!Visan.io.chooseTestingSet(false)) {
			JOptionPane.showMessageDialog(null, "Load testing set first.");
			return;
		}
		ensure(classes, features);
		if (classes.length < 2) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least two classes.");
		} else if (features.length == 0) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least one feature.");
		} else if (classes.length == 2) {
			TestHistogram(classes, features);
		} else {
			MultiTestHistogram(classes, features);
		}
	}

	private void AccuracyHistogram(int[] classes, int[] features) {
		ClassAndValue[] ldfv = new ClassAndValue[trainingSet
				.getTrainingClassSize(classes[0])
				+ trainingSet.getTrainingClassSize(classes[1])];
		int q = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
				ldfv[q] = new ClassAndValue(classes[i], DFValue(
						trainingSet.trainingObject(classes[i], j), classes,
						features));
				q++;
			}
		}
		Arrays.sort(ldfv);
		Visan.gm.AccuracyHistogram(ldfv, classes, features, getMethodName()
				+ " Training Set", ++accHistogramCount, decisionThreshold);
	}

	private void TestHistogram(int[] classInd, int[] featureInd) {
		ClassAndValue[] ldfv = new ClassAndValue[trainingSet
				.getTestingClassSize(classInd[0])
				+ trainingSet.getTestingClassSize(classInd[1])];
		int q = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < trainingSet.getTestingClassSize(i); j++) {
				ldfv[q] = new ClassAndValue(classInd[i], DFValue(
						trainingSet.testingObject(classInd[i], j), classInd,
						featureInd));
				q++;
			}
		}
		Arrays.sort(ldfv);
		Visan.gm.AccuracyHistogram(ldfv, classInd, featureInd, getMethodName()
				+ " Testing Set", ++testHistogramCount, decisionThreshold);
	}

	private void MultiTestHistogram(int[] classes, int[] features) {
		int[][] v = new int[classes.length][classes.length];
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
				int pc = classify(trainingSet.testingObject(classes[i], j),
						classes, features);
				v[i][pc]++;
			}
		}
		Visan.gm.MultiClassHistogram(v, classes, features, getMethodName()
				+ " Testing Set", ++multiHistogramCount);
	}

	private void MultiHistogram(int[] classes, int[] features) {
		int[][] v = new int[classes.length][classes.length];
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
				int pc = classify(trainingSet.trainingObject(classes[i], j),
						classes, features);
				v[i][pc]++;
			}
		}
		Visan.gm.MultiClassHistogram(v, classes, features, getMethodName()
				+ " Training Set", ++multiHistogramCount);
	}

	private int indexOf(int[] classes, int[] features) {
		for (int i = 0; i < computedDA.size(); i++) {
			if (Arrays.equals(computedDA.get(i).classInd, classes)
					&& Arrays.equals(computedDA.get(i).featureInd, features)) {
				return i;
			}
		}
		return -1;
	}

	private void analyseSignificance(int[] classesList, int[] featuresList) {
		Visan.gm.setConsole(false);
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i = 0; i < featuresList.length; i++) {
			a.add(featuresList[i]);
		}
		ArrayList<Integer> b = new ArrayList<Integer>();
		ArrayList<Double> d = new ArrayList<Double>();
		
		
		String t = "";
		
		while (a.size() >= 1) {
			int index = 0;
			double max = -Double.MAX_VALUE;
			int[] features = new int[b.size() + 1];
			for (int i = 0; i < b.size(); i++) {
				features[i] = b.get(i);
			}
			for (int i = 0; i < a.size(); i++) {
				features[features.length - 1] = a.get(i);
				analyse(classesList, features);
				if(a.size() == featuresList.length){
					t+=trainingSet.features.get(a.get(i)) + "     " +Round.round(mDistTwoClass, 4) +"\n";
				}
				if (mDistTwoClass > max) {
					max = mDistTwoClass;
					index = i;
				}
			}
			
			d.add(max);
			b.add(a.get(index));
			System.out.println(trainingSet.features.get(a.get(index)));
			a.remove(index);
		}
		Visan.gm.setConsole(true);
		String f = "";
		for (int i = 0; i < b.size(); i++) {
			f += trainingSet.features.get(b.get(i));
			if (i + 1 < b.size()) {
				f += "    ";
			}
			Visan.gm.writeToConsole("Features: " + f + "\n");
			Visan.gm.writeToConsole("Mahalanobis Distance: "
					+ Round.round(d.get(i), 4) + "\n");
		}
		Visan.gm.writeToConsole(t);
	}

	public void setTrainingSet(ClassesCollection trainingSet) {
		computedDA = new ArrayList<DiscriminantFunction>();
		this.trainingSet = trainingSet;
	}

	public void beginAnalysis(final int[] classesList, final int[] featuresList) {
		if (threadIsBusy()) {
			return;
		}
		if (classesList.length < 2) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least two classes!");

		} else if (featuresList.length < 1) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least one feature!");
		} else {
			thread = new Thread() {

				public void run() {
					try {
						analyse(classesList, featuresList);
					} catch (Exception e) {
						JOptionPane
								.showMessageDialog(null,
										"DA cannot be done. Singular matrix obtained. ");
					}
				}
			};
			thread.start();
		}
	}

	public void beginSignificanceAnalysis(final int[] classesList,
			final int[] featuresList) {
		if (threadIsBusy()) {
			return;
		}
		if (classesList.length < 2) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least two classes!");

		} else if (featuresList.length < 1) {
			JOptionPane.showMessageDialog(null,
					"You need to select at least one feature!");
		} else {
			thread = new Thread() {

				public void run() {
					try {
						analyseSignificance(classesList, featuresList);
					} catch (Exception e) {
						JOptionPane
								.showMessageDialog(null,
										"DA cannot be done. Singular matrix obtained. ");
					}
				}
			};
			thread.start();
		}
	}

	public boolean threadIsBusy() {
		if (thread != null && thread.isAlive()) {
			JOptionPane.showMessageDialog(null,
					"Wait until current operation is over!");
			return true;
		}
		return false;
	}

	public boolean isBusy() {
		if (thread != null) {
			if (thread.isAlive()) {
				return true;
			} else {
				thread = null;
			}
		}
		return false;
	}
}
