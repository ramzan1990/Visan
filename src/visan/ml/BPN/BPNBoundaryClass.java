package visan.ml.BPN;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import visan.common.MDouble;
import visan.common.ObjectAndClass;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JOptionPane;

import visan.Visan;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;

public class BPNBoundaryClass implements Serializable {

	public ArrayList<BackpropagationNet> computedBPN;
	public double momentum, decrRate, minCoeficient, defLearningRate[];
	public int function, allowedEpochs, allowedTime, defTest, netSize[];
	public boolean noImprovementsStop, useTestingSetStop, stop;
	public MDouble decisionThreshold;
	private BackpropagationNet ref;
	private ClassesCollection trainingSet;
	private static Thread thread;
	private int accHistogramCount, multiHistogramCount, testHistogramCount, numberOfImprovementTries, numberOfLearns;

	public void setTrainingSet(ClassesCollection trainingSet) {
		computedBPN = new ArrayList<BackpropagationNet>();
		this.trainingSet = trainingSet;
		defLearningRate = new double[trainingSet.getSize()];
		for (int i = 0; i < defLearningRate.length; i++) {
			defLearningRate[i] = 0.5;
		}
	}

	public BPNBoundaryClass() {
		momentum = 0;
		decrRate = 0.999;
		minCoeficient = 0.0002;
		function = 0;
		allowedEpochs = 1000000;
		allowedTime = 120000;
		defTest = 1;
		noImprovementsStop = true;
		numberOfImprovementTries = 10;
		numberOfLearns = 1;
		stop = true;
		decisionThreshold = new MDouble();
		computedBPN = new ArrayList<BackpropagationNet>();
	}

	public void learn(int[] classes, int[] features) {
		Visan.gm.lockBPNOptions();
		stop = false;
		if (!noImprovementsStop && !useTestingSetStop) {
			numberOfLearns = 1;
		}
		if (netSize == null) {
			netSize = new int[] { features.length, features.length, classes.length };
		}
		netSize[0] = features.length;
		netSize[netSize.length - 1] = classes.length;
		// prepairing training set
		ArrayList<ObjectAndClass> x = new ArrayList<ObjectAndClass>();
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
				x.add(new ObjectAndClass());
				x.get(x.size() - 1).object = new ArrayRealVector(features.length);
				x.get(x.size() - 1).objectClass = i;
				for (int f = 0; f < features.length; f++) {
					double c = trainingSet.trainingObject(classes[i], j)[features[f]];
					x.get(x.size() - 1).object.setEntry(f, c);
				}
			}
		}
		Collections.shuffle(x);
		BackpropagationNet bpn = new BackpropagationNet(classes, features, netSize, trainingSet.getTrainingClassSizes(),
				defLearningRate, decisionThreshold);
		// setting parametres
		bpn.setMinCof(minCoeficient);
		bpn.setMomentum(momentum);
		bpn.setF(function);
		bpn.setDecRate(decrRate);
		bpn.setTestStop(defTest);
		bpn.setImprovementsStop(noImprovementsStop);
		bpn.numberOfImprovementTries = numberOfImprovementTries;
		// testing set
		if (useTestingSetStop) {
			ArrayList<ObjectAndClass> tx = new ArrayList<ObjectAndClass>();
			for (int i = 0; i < classes.length; i++) {
				for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
					tx.add(new ObjectAndClass());
					tx.get(tx.size() - 1).object = new ArrayRealVector(features.length);
					tx.get(tx.size() - 1).objectClass = i;
					for (int f = 0; f < features.length; f++) {
						double c = trainingSet.testingObject(classes[i], j)[features[f]];
						tx.get(tx.size() - 1).object.setEntry(f, c);
					}
				}
			}
			bpn.useTestingSet(tx);
		}
		ref = bpn;
		if (numberOfLearns > 1) {
			int min = Integer.MAX_VALUE;
			for (int i = 0; i < numberOfLearns; i++) {
				Visan.gm.setConsole(false);
				int result = bpn.learn(x, allowedEpochs, allowedTime);
				Visan.gm.setConsole(true);
				if (result < min) {
					bpn.mem();
					min = result;
				}
				bpn.randWeights(0.5);
				Visan.gm.writeToConsole(
						"Result:" + result + ". " + (numberOfLearns - i - 1) + " left. Current minimum: " + min + "\n");
				if (stop) {
					break;
				}
			}
			bpn.setMem();
		} else {
			stop = true;
			bpn.learn(x, allowedEpochs, allowedTime);
		}
		int i = indexOf(classes, features);
		if (i != -1) {
			computedBPN.remove(i);
		}
		computedBPN.add(bpn);
		Visan.gm.lockBPNOptions();
		stop = true;
	}

	public void AccuracyHistogram(int[] classes, int[] features) {
		ensure(classes, features);
		ClassAndValue[] bpncv = new ClassAndValue[trainingSet.getTrainingClassSize(classes[0])
				+ trainingSet.getTrainingClassSize(classes[1])];
		int q = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
				RealVector v = new ArrayRealVector(features.length);
				double[] to = trainingSet.trainingObject(classes[i], j);
				for (int t = 0; t < features.length; t++) {
					v.setEntry(t, to[features[t]]);
				}
				bpncv[q] = new ClassAndValue(classes[i], classify(classes, features, v).value);
				q++;
			}
		}
		Arrays.sort(bpncv);
		Visan.gm.AccuracyHistogram(bpncv, classes, features, "BPN Training Set", ++accHistogramCount, decisionThreshold);
	}

	public void classify(ArrayList<double[]> x, int[] classesList, int[] featuresList) {
		Visan.gm.writeToConsole("\n");
		for (int j = 0; j < x.size(); j++) {
			RealVector v = new ArrayRealVector(x.get(j));
			int r = classify(classesList, featuresList, v).classIndex;
			Visan.gm.writeToConsole("Predicted Class:" + trainingSet.getClassesNames()[r] + "\n");
		}
	}

	public ClassAndValue classify(int[] classesList, int[] featuresList, RealVector v) {
		ensure(classesList, featuresList);
		int n = featuresList.length;
		// RealVector X = new ArrayRealVector(n);
		// for (int i = 0; i < n; i++) {
		// X.setEntry(i, v.getEntry(featuresList[i]));
		// }
		return computedBPN.get(indexOf(classesList, featuresList)).classify(v);
	}

	public void ensure(int[] classInd, int[] featureInd) {
		if (indexOf(classInd, featureInd) == -1) {
			learn(classInd, featureInd);
		}
	}

	public void stopLearning() {
		if (!stop) {
			stop = true;
		} else {
			ref.stop();
		}
	}

	private int indexOf(int[] classes, int[] features) {
		for (int i = 0; i < computedBPN.size(); i++) {
			if (Arrays.equals(computedBPN.get(i).classInd, classes)
					&& Arrays.equals(computedBPN.get(i).featureInd, features)) {
				return i;
			}
		}
		return -1;
	}

	public boolean threadIsBusy() {
		if (thread != null && thread.isAlive()) {
			JOptionPane.showMessageDialog(null, "Wait until current operation is over!");
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

	public void setImprovementStop() {
		if (threadIsBusy()) {
			return;
		}
		try {
			noImprovementsStop = true;
			useTestingSetStop = false;
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setAllowedEpochs() {
		if (threadIsBusy()) {
			return;
		}
		try {
			allowedEpochs = Integer.parseInt(JOptionPane.showInputDialog("Input allowed epochs number:"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setAllowedTime() {
		if (threadIsBusy()) {
			return;
		}
		try {
			allowedTime = Integer.parseInt(JOptionPane.showInputDialog("Input allowed lerning time:"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setHiddenLayers() {
		if (threadIsBusy()) {
			return;
		}
		try {
			int numberOfHiddenLayers = Integer.parseInt(JOptionPane.showInputDialog("Input Number Of Hidden Layers:"));
			netSize = new int[numberOfHiddenLayers + 2];
			for (int i = 0; i < netSize.length - 2; i++) {
				netSize[i + 1] = Integer
						.parseInt(JOptionPane.showInputDialog("Input Units Number For Hidden Layer " + i + ":"));
			}
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setTest() {
		if (threadIsBusy()) {
			return;
		}
		try {
			defTest = Integer.parseInt(JOptionPane.showInputDialog("Input N:"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setFunc() {
		if (threadIsBusy()) {
			return;
		}
		try {
			function = Integer.parseInt(JOptionPane.showInputDialog("Input 0 for arctan, 1 for bipolar, 2 for log, 3 for relu:"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setMinCoef() {
		if (threadIsBusy()) {
			return;
		}
		try {
			minCoeficient = Double.parseDouble(JOptionPane.showInputDialog("Input Min Lerning Rate Coefficient:"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setDecRate() {
		if (threadIsBusy()) {
			return;
		}
		try {
			decrRate = Double.parseDouble(JOptionPane.showInputDialog("Input Decreasion Rate:"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setMomentum() {
		if (threadIsBusy()) {
			return;
		}
		try {
			momentum = Double.parseDouble(JOptionPane.showInputDialog("Input Momentum(0 to turn off):"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setLerningRate() {
		if (threadIsBusy()) {
			return;
		}
		for (int i = 0; i < defLearningRate.length; i++) {
			try {
				defLearningRate[i] = Double.parseDouble(JOptionPane
						.showInputDialog("Input Lerning Rate For Class:" + trainingSet.getClassesNames()[i]));
			} catch (Exception e) {
				Visan.gm.wrongInput();
			}
		}
	}

	public void setNumberOfLearns() {
		if (threadIsBusy()) {
			return;
		}
		try {
			numberOfLearns = Integer.parseInt(JOptionPane.showInputDialog("Number Of Learns:"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}

	}

	public void useTestingSet() {
		if (threadIsBusy()) {
			return;
		}
		try {
			if (!Visan.io.chooseTestingSet(false)) {
				useTestingSetStop = false;
			} else {
				useTestingSetStop = true;
				noImprovementsStop = false;
			}
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void startLearning(final int[] classesList, final int[] featuresList) {
		if (threadIsBusy()) {
			return;
		}
		if (classesList.length < 2) {
			JOptionPane.showMessageDialog(null, "You need to select at least two classes!");

		} else if (featuresList.length < 1) {
			JOptionPane.showMessageDialog(null, "You need to select at least one feature!");
		} else {
			thread = new Thread() {

				@Override
				public void run() {
					learn(classesList, featuresList);
				}
			};
			thread.start();
		}
	}

	public void DrawHistogram(final int[] classes, final int[] features) {
		if (threadIsBusy()) {
			return;
		}
		if (classes.length < 2) {
			JOptionPane.showMessageDialog(null, "You need to select at least two classes!");

		} else if (features.length == 0) {
			JOptionPane.showMessageDialog(null, "You need to select at least one feature!");

		} else if (classes.length == 2) {
			thread = new Thread() {

				@Override
				public void run() {
					AccuracyHistogram(classes, features);
				}
			};
			thread.start();
		} else if (classes.length > 2) {
			thread = new Thread() {

				@Override
				public void run() {
					MultiHistogram(classes, features);
				}
			};
			thread.start();
		}
	}

	public void DrawTestHistogram(final int[] classes, final int[] features) {
		if (threadIsBusy()) {
			return;
		}
		if (classes.length < 2) {
			JOptionPane.showMessageDialog(null, "You need to select at least two classes!");

		} else if (features.length == 0) {
			JOptionPane.showMessageDialog(null, "You need to select at least one feature!");

		} else if (classes.length == 2) {
			thread = new Thread() {

				@Override
				public void run() {
					TestHistogram(classes, features);
				}
			};
			thread.start();
		} else if (classes.length > 2) {
			thread = new Thread() {

				@Override
				public void run() {
					TestMultiHistogram(classes, features);
				}
			};
			thread.start();
		}
	}

	private void TestHistogram(int[] classes, int[] features) {
		if (!Visan.io.chooseTestingSet(false)) {
			return;
		}
		ensure(classes, features);
		ClassAndValue[] bpncv = new ClassAndValue[trainingSet.getTestingClassSize(classes[0])
				+ trainingSet.getTestingClassSize(classes[1])];
		int q = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
				RealVector v = new ArrayRealVector(features.length);
				double[] to = trainingSet.testingObject(classes[i], j);
				for (int t = 0; t < features.length; t++) {
					v.setEntry(t, to[features[t]]);
				}
				bpncv[q] = new ClassAndValue(classes[i], classify(classes, features, v).value);
				q++;
			}
		}
		Arrays.sort(bpncv);
		Visan.gm.AccuracyHistogram(bpncv, classes, features, "BPN Testing Set", ++testHistogramCount, decisionThreshold);
	}

	private void TestMultiHistogram(int[] classes, int[] features) {
		if (!Visan.io.chooseTestingSet(false)) {
			return;
		}
		ensure(classes, features);
		int[][] v = new int[classes.length][classes.length];
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
				RealVector x = new ArrayRealVector(trainingSet.testingObject(classes[i], j));
				int pc = classify(classes, features, x).classIndex;
				v[i][pc]++;
			}
		}
		Visan.gm.MultiClassHistogram(v, classes, features, "BPN Testing Set", ++multiHistogramCount);
	}

	private void MultiHistogram(int[] classes, int[] features) {
		ensure(classes, features);
		int[][] v = new int[classes.length][classes.length];
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
				RealVector x = new ArrayRealVector(trainingSet.trainingObject(classes[i], j));
				int pc = classify(classes, features, x).classIndex;
				v[i][pc]++;
			}
		}
		Visan.gm.MultiClassHistogram(v, classes, features, "BPN Training Set", ++multiHistogramCount);
	}

	public String toString(int[] classes, int[] features) {
		ensure(classes, features);
		return computedBPN.get(indexOf(classes, features)).toString();
	}

	public void importBPN(int[] classes, int[] features, File f) throws Exception {
		Scanner scan = null;
		try {
			scan = new Scanner(f);
			int pos = indexOf(classes, features);
			if (pos != -1) {
				computedBPN.remove(pos);
			}
			String af;
			RealVector[][] weights;
			double[][] bias;
			scan.nextLine();
			af = scan.nextLine();
			af = af.split(":")[1];
			scan.nextLine();
			ArrayList<Integer> ns = new ArrayList<Integer>();
			while (scan.hasNextInt()) {
				ns.add(scan.nextInt());
			}
			netSize = new int[ns.size()];
			for (int i = 0; i < ns.size(); i++) {
				netSize[i] = ns.get(i);
			}
			scan.nextLine();
			weights = new RealVector[netSize.length - 1][];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = new RealVector[netSize[i + 1]];
				System.out.println("weights[" + i + "] = new double[" + netSize[i + 1] + "][];");
				scan.nextLine();
				scan.nextLine();
				for (int h = 0; h < netSize[i + 1]; h++) {
					weights[i][h] = new ArrayRealVector(netSize[i]);
					System.out.println("weights[" + i + "][" + h + "] = new double[" + netSize[i] + "];");
					for (int j = 0; j < weights[i][h].getDimension(); j++) {
						weights[i][h].setEntry(j, scan.nextDouble());
						System.out.println(
								"weights[" + i + "][" + h + "][" + j + "] = " + weights[i][h].getEntry(j) + ";");
					}
				}
			}
			scan.nextLine();
			bias = new double[netSize.length - 1][];
			for (int i = 0; i < bias.length; i++) {
				bias[i] = new double[netSize[i + 1]];
				System.out.println("bias[" + i + "] = new double[" + netSize[i + 1] + "];");
				scan.nextLine();
				scan.nextLine();
				for (int j = 0; j < bias[i].length; j++) {
					bias[i][j] = scan.nextDouble();
					System.out.println("bias[" + i + "][" + j + "] = " + bias[i][j] + ";");
				}
			}
			BackpropagationNet bpn = new BackpropagationNet(classes, features, netSize, weights, bias,
					decisionThreshold);
			if (af.equals("Arctan")) {
				bpn.setF(0);
			} else if (af.equals("Bipolar Sigmoid")) {
				bpn.setF(1);
			} else {
				bpn.setF(2);
			}
			computedBPN.add(bpn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}



	public void setNumberOfImprovementTries() {
		if (threadIsBusy()) {
			return;
		}
		try {
			numberOfImprovementTries = Integer.parseInt(JOptionPane.showInputDialog("Set Number Of Improvement Tries"));
		} catch (Exception e) {
			Visan.gm.wrongInput();
		}
	}

	public void setManualStop(boolean b) {
		noImprovementsStop = false;
		useTestingSetStop = false;
	}
}
