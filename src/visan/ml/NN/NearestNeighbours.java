package visan.ml.NN;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import visan.common.Kernel;
import visan.common.MDouble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import visan.Visan;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;

public class NearestNeighbours implements Serializable {

    private Thread thread;
    private ClassesCollection trainingSet;
    private MDouble decisionThreshold = new MDouble();
    private int multiHistogramCount, testHistogramCount;
    private int n;
    private Kernel kernel;
    private boolean dual;

    public void setNewTrainingSet(ClassesCollection trainingSet) {
        this.trainingSet = trainingSet;
    }

    public NearestNeighbours() {
        n = 1;
        kernel = new Kernel();
    }

    public void DrawTestAccuracyHistogram(final int[] classes, final int[] features) {
        if (threadIsBusy()) {
            return;
        }
        if (!Visan.io.chooseTestingSet(false)) {
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
        } else {
            thread = new Thread() {

                @Override
                public void run() {
                    MultiTestHistogram(classes, features);
                }
            };
            thread.start();
        }
    }

    private void TestHistogram(int[] classes, int[] features) {
        ClassAndValue[] cv = new ClassAndValue[trainingSet.getTestingClassSize(classes[0])
                + trainingSet.getTestingClassSize(classes[1])];
        int q = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < trainingSet.getTestingClassSize(i); j++) {
                cv[q] = new ClassAndValue(classes[i], classify(trainingSet.testingObject(classes[i], j), classes, features).value);
                q++;
            }
        }
        Arrays.sort(cv);
        Visan.gm.AccuracyHistogram(cv, classes, features, "NN Testing Set", ++testHistogramCount, decisionThreshold);
    }

    private void MultiTestHistogram(int[] classes, int[] features) {
        int[][] v = new int[classes.length][classes.length];
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
                int pc = classify(trainingSet.testingObject(classes[i], j), classes, features).classIndex;
                v[i][pc]++;
            }
        }
        Visan.gm.MultiClassHistogram(v, classes, features, "NN Testing Set", ++multiHistogramCount);
    }

    private ClassAndValue classify(double[] classObject, int[] classes, int[] features) {
        ClassAndValue cv;
        RealVector x = new ArrayRealVector(classObject);
        if (classes.length == 2) {
            double[][] min = new double[2][n];
            for (int c = 0; c < 2; c++) {
                for (int i = 0; i < n; i++) {
                    min[c][i] = Double.MAX_VALUE;
                }
            }

            double value;
            int predictedClass;
            for (int c = 0; c < 2; c++) {
                for (int i = 0; i < trainingSet.getTrainingClassSize(classes[c]); i++) {

                    double s = 0;
                    if (!dual) {
                    	double[] to = trainingSet.trainingObject(c, i);
                        for (int f = 0; f < features.length; f++) {
                            s += Math.pow(classObject[f] - to[f], 2);
                        }
                        s = Math.sqrt(s);
                    } else {
                        RealVector xi = new ArrayRealVector(trainingSet.trainingObject(c, i));
                        s = kernel.value(x, x) + kernel.value(xi, xi) - 2 * kernel.value(x, xi);
                    }
                    testInsert(min[c], s);
                }
            }

            value = sum(min[0]) - sum(min[1]);
            if (value > decisionThreshold.value) {
                predictedClass = 1;
            } else {
                predictedClass = 0;
            }
            cv = new ClassAndValue(predictedClass, value);
        } else {
            double[][] min = new double[classes.length][n];
            for (int c = 0; c < classes.length; c++) {
                for (int i = 0; i < n; i++) {
                    min[c][i] = Double.MAX_VALUE;
                }
            }
            for (int i = 0; i < classes.length; i++) {
                for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
                    double s = 0;
                    if (!dual) {
                    	double[] to = trainingSet.trainingObject(i, j);
                        for (int f = 0; f < features.length; f++) {
                            s += Math.pow(classObject[f] - to[f], 2);
                        }
                        s = Math.sqrt(s);
                    } else {
                        RealVector xi = new ArrayRealVector(trainingSet.trainingObject(i, j));
                        s = kernel.value(x, x) + kernel.value(xi, xi) - 2 * kernel.value(x, xi);
                    }
                    testInsert(min[i], s);
                }
            }
            int c = 0;
            double m = Double.MAX_VALUE;
            for (int i = 0; i < classes.length; i++) {
                if (sum(min[i]) < m) {
                    c = i;
                    m = sum(min[i]);
                }
            }
            cv = new ClassAndValue(c, m);
        }
        return cv;
    }

    public void classify(ArrayList<double[]> a, int[] classes, int[] features) {
        Visan.gm.writeToConsole("\n");
        for (int j = 0; j < a.size(); j++) {
            //for (int q = 0; q < features.length; q++) {
             //   VISAN.gm.writeToConsole(a.get(j)[q] + "  ");
           // }
            Visan.gm.writeToConsole("Predicted Class:" + trainingSet.getClassesNames()[classify(a.get(j), classes, features).classIndex]+"\n");
        }
    }

    private void testInsert(double[] a, double min) {
        for (int i = 0; i < a.length; i++) {
            if (min < a[i]) {
                double value = a[i];
                a[i] = min;
                testInsert(a, value);
                return;
            }
        }
    }

    private double sum(double[] a) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    public void chooseN() {
        if (threadIsBusy()) {
            return;
        }
        try {
            n = Integer.parseInt(JOptionPane.showInputDialog("Set N:"));
        } catch (Exception e) {
        }
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
	
    public void chooseKernel() {
        if (threadIsBusy()) {
            return;
        }
        kernel.showDialog();
    }

    public boolean setDual(boolean b) {
        if (threadIsBusy()) {
            return false;
        }
        dual = b;
        if (dual) {
            chooseKernel();
        } else {
            kernel.setMethod(0);
        }
        return true;
    }
}
