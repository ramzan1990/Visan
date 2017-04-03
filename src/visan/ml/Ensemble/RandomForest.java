package visan.ml.Ensemble;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import visan.common.MDouble;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import visan.Visan;

/**
 *
 * @author Ramzan
 */
public class RandomForest implements Serializable {

	private ClassesCollection trainingSet;
	private ArrayList<Forest> forests;
	public static int numTrees = 1;
	public static int numThreads = 1;
	private int aistogramCount;
	private MDouble decisionThreshold;
	private static double sampleFraction = 0.05;
	private static int numFeatures = -1;
	private int threadTrees;
	private static int maxDepth = 42;
	private static int branchingFactor = 3;
	public static int sco=1;

	public void setTrainingSet(ClassesCollection trainingSet) {
		this.trainingSet = trainingSet;
		forests = new ArrayList<Forest>();
		decisionThreshold = new MDouble();
	}

	public static void setBranchingFactor() {
		try {
			int n = Integer.parseInt(JOptionPane
					.showInputDialog("Branching factor of a tree (Min 2): "));
			if (n >= 2) {
				branchingFactor = n;
			}
		} catch (Exception ex) {
		}
	}

	public void growForestNewThread(final int[] classInd, final int[] featureInd) {
		Thread t = new Thread() {
			public void run() {
				growForest(classInd, featureInd);
			}
		};
		t.start();

	}

	public void growForest(final int[] classInd, final int[] featureInd) {
		final Forest rf = new Forest(classInd, featureInd);
		threadTrees = (numTrees / numThreads);
		for (int c = 0; c < numThreads; c++) {
			new Thread() {
				public void run() {
					Random rand = new Random();
					DT dt = new DT();
					dt.setDepth(maxDepth);
					dt.setBranchingFactor(branchingFactor);
					for (int t = 0; t < threadTrees; t++) {
						// int[] featureInd2 = new int[featureInd.length / 2 +
						// rand.nextInt(featureInd.length / 2)];
						// int sqrt = (int) Math.sqrt(featureInd.length);
						// int[] featureInd2 = new int[12];
						// int[] featureInd2 = new int[sqrt];
						int fnumber = featureInd.length;
						if (numFeatures == 0) {
							fnumber = (int) Math.sqrt(fnumber);
						} else if (numFeatures > 0) {
							fnumber = Math.min(numFeatures, fnumber);
						}
						int[] featureInd2 = new int[fnumber];
						ArrayList<Integer> origFeatures = new ArrayList<Integer>();
						for (int i = 0; i < featureInd.length; i++) {
							origFeatures.add(featureInd[i]);
						}
						for (int i = 0; i < featureInd2.length; i++) {
							//int index = rand.nextInt(origFeatures.size());
							featureInd2[i] = origFeatures.get(i);
							//origFeatures.remove(index);
						}
						ArrayList<ArrayList<TrainingObject>> nData = new ArrayList<ArrayList<TrainingObject>>(
								classInd.length);
						for (int i = 0; i < classInd.length; i++) {
							int classSize = trainingSet
									.getTrainingClassSize(classInd[i]);
							int sampleSize = classSize;
							nData.add(new ArrayList<TrainingObject>());
							if (sampleFraction == -1) {
								sampleSize = (int) Math.sqrt(sampleSize);
							} else if (sampleFraction > 0) {
								sampleSize = (int) Math.floor(sampleFraction
										* sampleSize);
							}
							for (int q = 0; q < sampleSize; q++) {
								int randomElement;
								if (sampleFraction == 1) {
									randomElement = q;
								} else {
									randomElement = rand.nextInt(classSize);
								}
								Double d[] = new Double[featureInd2.length];
								for (int j = 0; j < d.length; j++) {
									d[j] = trainingSet.trainingObject(
											classInd[i], randomElement,
											featureInd2)[j];
								}
								nData.get(i).add(new TrainingObject(d, 1));
							}
						}
						StringBuilder s = new StringBuilder();
						for (ArrayList<TrainingObject> alto : nData) {
							for (TrainingObject to : alto) {
								for (Double d : to.data) {
									s.append(d);
									s.append(", ");
								}
								s.append(nData.indexOf(alto));
								s.append(", ");
								s.append("\n");
							}
						}
						try {
							PrintWriter out = new PrintWriter("tree"+sco+".txt");
							out.println(s.toString());
							out.close();
							sco++;
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						dt.setData(nData);
						rf.forest.add(dt.build(classInd, featureInd2));
						// if (t > counter * (0.1 * numTrees)) {
						// VISAN.gm.writeToConsole(10 * counter + "%");
						// counter++;
						// }
						Visan.gm.writeToConsole(rf.forest.size() + " ");
					}
				}
			}.start();
		}
		while (rf.forest.size() < numThreads * threadTrees) {
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Visan.gm.writeToConsole("100% \n");
		int ind;
		ind = indexOf(classInd, featureInd);
		if (ind != -1) {
			forests.remove(ind);
		}
		forests.add(rf);
	}

	public void outputTrainingStats(int[] classes, int[] features) {
		if (!ensure(classes, features)) {
			return;
		}
		int er = 0;
		for (int i = 0; i < 2; i++) {
			int e = 0;
			for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
				int pc = classify(trainingSet.trainingObject(classes[i], j),
						classes, features).classIndex;
				if (pc != i) {
					e++;
				}
			}
			Visan.gm.writeToConsole("Error in class " + i + " is " + e
					+ " out of " + trainingSet.getTrainingClassSize(classes[i])
					+ "\n");
			double acc = (double) (trainingSet.getTrainingClassSize(classes[i]) - e)
					/ trainingSet.getTrainingClassSize(classes[i]);
			Visan.gm.writeToConsole("Accuracy is " + acc + "\n");
			er += e;
		}
		Visan.gm.writeToConsole("Total error is " + er + "\n\n");
	}

	public void outputTestingStats(int[] classes, int[] features) {
		if (!ensure(classes, features)) {
			return;
		}
		int er = 0;
		for (int i = 0; i < 2; i++) {
			int e = 0;
			for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
				int pc = classify(trainingSet.testingObject(classes[i], j),
						classes, features).classIndex;
				if (pc != i) {
					e++;
				}
			}
			Visan.gm.writeToConsole("Error in class " + i + " is " + e
					+ " out of " + trainingSet.getTestingClassSize(classes[i])
					+ "\n");
			double acc = (double) (trainingSet.getTestingClassSize(classes[i]) - e)
					/ trainingSet.getTestingClassSize(classes[i]);
			Visan.gm.writeToConsole("Accuracy is " + acc + "\n");
			er += e;
		}
		Visan.gm.writeToConsole("Total error is " + er + "\n\n");
	}

	public void DrawTestAccuracyHistogram(int[] classes, int[] features) {
		if (!Visan.io.chooseTestingSet(false)) {
			JOptionPane.showMessageDialog(null, "Load testing set first.");
			return;
		}
		if (ensure(classes, features)) {
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
	}

	private void MultiTestHistogram(int[] classes, int[] features) {
		int[][] v = new int[classes.length][classes.length];
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
				int pc = classify(trainingSet.testingObject(classes[i], j),
						classes, features).classIndex;
				v[i][pc]++;
			}
		}
		Visan.gm.MultiClassHistogram(v, classes, features, "RFC Testing Set",
				++aistogramCount);
	}

	private void MultiHistogram(int[] classes, int[] features) {
		int[][] v = new int[classes.length][classes.length];
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
				int pc = classify(trainingSet.trainingObject(classes[i], j),
						classes, features).classIndex;
				v[i][pc]++;
			}
		}
		Visan.gm.MultiClassHistogram(v, classes, features, "RFC Training Set",
				++aistogramCount);
	}

	private void TestHistogram(int[] classInd, int[] featureInd) {

		ClassAndValue[] ldfv = new ClassAndValue[trainingSet
				.getTestingClassSize(classInd[0])
				+ trainingSet.getTestingClassSize(classInd[1])];
		int q = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < trainingSet.getTestingClassSize(i); j++) {
				ldfv[q] = new ClassAndValue(classInd[i], classify(
						trainingSet.testingObject(classInd[i], j), classInd,
						featureInd).value);
				q++;
			}
		}
		Arrays.sort(ldfv);
		Visan.gm.AccuracyHistogram(ldfv, classInd, featureInd, "RFC Testing Set",
				++aistogramCount, decisionThreshold);

	}

	public void DrawAccuracyHistogram(int[] classes, int[] features) {
		if (ensure(classes, features)) {
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
	}

	private void AccuracyHistogram(int[] classInd, int[] featureInd) {

		ClassAndValue[] ldfv = new ClassAndValue[trainingSet
				.getTrainingClassSize(classInd[0])
				+ trainingSet.getTrainingClassSize(classInd[1])];
		int q = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < trainingSet.getTrainingClassSize(i); j++) {
				ldfv[q] = new ClassAndValue(classInd[i], classify(
						trainingSet.trainingObject(classInd[i], j), classInd,
						featureInd).value);
				q++;
			}
		}
		Arrays.sort(ldfv);
		Visan.gm.AccuracyHistogram(ldfv, classInd, featureInd, "RFC Training Set",
				++aistogramCount, decisionThreshold);
	}

	private int indexOf(int[] classes, int[] features) {
		for (int i = 0; i < forests.size(); i++) {
			if (Arrays.equals(forests.get(i).classInd, classes)
					&& Arrays.equals(forests.get(i).featureInd, features)) {
				return i;
			}
		}
		return -1;
	}

	public boolean ensure(int[] classInd, int[] featureInd) {
		if (indexOf(classInd, featureInd) == -1) {
			Visan.gm.writeToConsole("Train classifier first.");
			return false;
		}
		return true;
	}

	private ClassAndValue classify(double[] trainingObject, int[] classes,
			int[] features) {
		if (ensure(classes, features)) {
			Forest rf = forests.get(indexOf(classes, features));
			return rf.classify(trainingObject, decisionThreshold);
		} else {
			return null;
		}
	}

	public void clear() {
		forests.clear();
	}

	public void classify(ArrayList<double[]> x, int[] classesList,
			int[] featuresList) {
		Visan.gm.writeToConsole("\n");
		if (ensure(classesList, featuresList)) {
			for (double[] x1 : x) {
				int r = classify(x1, classesList, featuresList).classIndex;
				Visan.gm.writeToConsole("Predicted Class:"
						+ trainingSet.getClassesNames()[r] + "\n");
			}
		}
	}

	public static void setNumThreads(int n) {
		numThreads = n;
	}

	public static void setNumTrees(int n) {
		numTrees = n;
	}

	public static void setSampleFraction(double n) {
		sampleFraction = n;
	}

	public static void setMaxFeatures(int n) {
		numFeatures = n;
	}

	public static void setMaxDepth() {
		try {
			int n = Integer.parseInt(JOptionPane
					.showInputDialog("Maximum depth of a tree: "));
			if (n > 0) {
				maxDepth = n;
			}
		} catch (Exception ex) {

		}
	}
}
