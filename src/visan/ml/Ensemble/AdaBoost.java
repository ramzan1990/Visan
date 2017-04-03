package visan.ml.Ensemble;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import visan.common.MDouble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import visan.Visan;

/**
 * @author Ramzan
 */
public class AdaBoost implements Serializable {

    private ClassesCollection trainingSet;
    private ArrayList<Forest> forests;
    public static int numTrees = 80;
    private int testHistogramCount;
    private MDouble decisionThreshold;
    private static int maxDepth = 40;
    private static int branchingFactor = 3;
    private static double maxA = 100;

    public static void setBranchingFactor() {
        try {
            int n = Integer.parseInt(JOptionPane.showInputDialog("Branching factor of a tree (Min 2): "));
            if (n >= 2) {
                branchingFactor = n;
            }
        } catch (Exception ex) {
        }
    }

    public void setTrainingSet(ClassesCollection trainingSet) {
        this.trainingSet = trainingSet;
        forests = new ArrayList<Forest>();
        decisionThreshold = new MDouble();
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
        final Forest forest = new Forest(classInd, featureInd);
        DT dt = new DT();
        dt.setDepth(maxDepth);
        dt.setBranchingFactor(branchingFactor);
        double[][] D = new double[classInd.length][];
        int m = trainingSet.getTrainingElementsSum(classInd);
        for (int t = 0; t < numTrees; t++) {
            ArrayList<ArrayList<TrainingObject>> data = new ArrayList<ArrayList<TrainingObject>>(classInd.length);
            for (int i = 0; i < classInd.length; i++) {
                int classSize = trainingSet.getTrainingClassSize(classInd[i]);
                data.add(new ArrayList<TrainingObject>());
                if (t == 0) {
                    D[i] = new double[classSize];
                    for (int q = 0; q < classSize; q++) {
                        D[i][q] = 1.0 / m;
                    }
                }
                for (int q = 0; q < classSize; q++) {
                    Double d[] = new Double[featureInd.length];
                    for (int j = 0; j < d.length; j++) {
                        d[j] = trainingSet.trainingObject(classInd[i], q, featureInd)[j];
                    }
                    data.get(i).add(new TrainingObject(d, D[i][q]));
                }
            }
            dt.setData(data);
            Tree tree = dt.build(classInd, featureInd);
            double error = 0;
            int[][] y = new int[classInd.length][];
            for (int i = 0; i < classInd.length; i++) {
                int classSize = trainingSet.getTrainingClassSize(classInd[i]);
                y[i] = new int[classSize];
                for (int q = 0; q < classSize; q++) {
                    y[i][q] = tree.classify(trainingSet.trainingObject(classInd[i], q, featureInd));
                    if (i != y[i][q]) {
                        error++;
                    }
                }
            }
            error = error / m;
            double a;
            if (error != 0) {
                a = 0.5 * Math.log((double) (1 - error) / error);
                if (a > maxA) {
                    a = maxA;
                }
            } else {
                a = maxA;
            }
            tree.weight = a;
            double sum = 0;
            for (int i = 0; i < classInd.length; i++) {
                int classSize = trainingSet.getTrainingClassSize(classInd[i]);
                for (int q = 0; q < classSize; q++) {
                    D[i][q] = D[i][q] * Math.exp(-a * (y[i][q] - 1) * (i - 1));
                    sum += D[i][q];
                }
            }
            for (int i = 0; i < classInd.length; i++) {
                int classSize = trainingSet.getTrainingClassSize(classInd[i]);
                for (int q = 0; q < classSize; q++) {
                    D[i][q] /= sum;
                }
            }

            forest.forest.add(tree);
            if (error == 0) {
                break;
            }
            Visan.gm.writeToConsole((t + 1) + " ");
        }

        int ind;
        ind = indexOf(classInd, featureInd);
        if (ind != -1) {
            forests.remove(ind);
        }
        forests.add(forest);
        Visan.gm.writeToConsole("\nFinished\n");
    }

    public void outputTrainingStats(int[] classes, int[] features) {
        if (!ensure(classes, features)) {
            return;
        }
        int er = 0;
        for (int i = 0; i < 2; i++) {
            int e = 0;
            for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
                int pc = classify(trainingSet.trainingObject(classes[i], j), classes, features).classIndex;
                if (pc != i) {
                    e++;
                }
            }
            Visan.gm.writeToConsole("Error in class " + i + " is " + e + " out of " + trainingSet.getTrainingClassSize(classes[i]) + "\n");
            double acc = (double) (trainingSet.getTrainingClassSize(classes[i]) - e) / trainingSet.getTrainingClassSize(classes[i]);
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
                int pc = classify(trainingSet.testingObject(classes[i], j), classes, features).classIndex;
                if (pc != i) {
                    e++;
                }
            }
            Visan.gm.writeToConsole("Error in class " + i + " is " + e + " out of " + trainingSet.getTestingClassSize(classes[i]) + "\n");
            double acc = (double) (trainingSet.getTestingClassSize(classes[i]) - e) / trainingSet.getTestingClassSize(classes[i]);
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
                JOptionPane.showMessageDialog(null, "You need to select at least two classes.");
            } else if (features.length == 0) {
                JOptionPane.showMessageDialog(null, "You need to select at least one feature.");
            } else if (classes.length == 2) {
                TestHistogram(classes, features);
            }
        }
    }

    private void TestHistogram(int[] classInd, int[] featureInd) {

        ClassAndValue[] ldfv = new ClassAndValue[trainingSet.getTestingClassSize(classInd[0])
                + trainingSet.getTestingClassSize(classInd[1])];
        int q = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < trainingSet.getTestingClassSize(i); j++) {
                ldfv[q] = new ClassAndValue(classInd[i], classify(trainingSet.testingObject(classInd[i], j), classInd, featureInd).value);
                q++;
            }
        }
        Arrays.sort(ldfv);
        Visan.gm.AccuracyHistogram(ldfv, classInd, featureInd, "AdaBoost Testing Set", ++testHistogramCount, decisionThreshold);

    }

    public void DrawAccuracyHistogram(int[] classes, int[] features) {
        if (ensure(classes, features)) {
            if (classes.length < 2) {
                JOptionPane.showMessageDialog(null, "You need to select at least two classes.");
            } else if (features.length == 0) {
                JOptionPane.showMessageDialog(null, "You need to select at least one feature.");
            } else if (classes.length == 2) {
                AccuracyHistogram(classes, features);
            }
        }
    }

    private void AccuracyHistogram(int[] classInd, int[] featureInd) {

        ClassAndValue[] ldfv = new ClassAndValue[trainingSet.getTrainingClassSize(classInd[0])
                + trainingSet.getTrainingClassSize(classInd[1])];
        int q = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < trainingSet.getTrainingClassSize(i); j++) {
                ldfv[q] = new ClassAndValue(classInd[i], classify(trainingSet.trainingObject(classInd[i], j), classInd, featureInd).value);
                q++;
            }
        }
        Arrays.sort(ldfv);
        Visan.gm.AccuracyHistogram(ldfv, classInd, featureInd, "AdaBoost Training Set", ++testHistogramCount, decisionThreshold);
    }

    private int indexOf(int[] classes, int[] features) {
        for (int i = 0; i < forests.size(); i++) {
            if (Arrays.equals(forests.get(i).classInd, classes) && Arrays.equals(forests.get(i).featureInd, features)) {
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

    private ClassAndValue classify(double[] trainingObject, int[] classes, int[] features) {
        if (ensure(classes, features)) {
            Forest rf = forests.get(indexOf(classes, features));
            return rf.classifyWithWeight(trainingObject, decisionThreshold);
        } else {
            return null;
        }
    }

    public void clear() {
        forests.clear();
    }

    public void classify(ArrayList<double[]> x, int[] classesList, int[] featuresList) {
        Visan.gm.writeToConsole("\n");
        if (ensure(classesList, featuresList)) {
            for (double[] x1 : x) {
                int r = classify(x1, classesList, featuresList).classIndex;
                Visan.gm.writeToConsole("Predicted Class:" + trainingSet.getClassesNames()[r] + "\n");
            }
        }
    }

    public static void setMaxDepth() {
        try {
            int n = Integer.parseInt(JOptionPane.showInputDialog("Maximum depth of a tree: "));
            if (n > 0) {
                maxDepth = n;
            }
        } catch (Exception ex) {

        }
    }

    public static void setNumTrees() {
        try {
            int n = Integer.parseInt(JOptionPane.showInputDialog("Number of trees: "));
            if (n > 0) {
                numTrees = n;
            }
        } catch (Exception ex) {

        }
    }

}
