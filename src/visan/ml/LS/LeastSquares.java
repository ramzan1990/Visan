package visan.ml.LS;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import visan.common.Kernel;
import visan.common.MDouble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import visan.Visan;

import org.apache.commons.math.linear.*;

/**
 *
 * @author Umarov
 */
public class LeastSquares implements Serializable{

    private Thread thread;
    private ClassesCollection trainingSet;
    private MDouble decisionThreshold;
    private int accHistogramCount, testHistogramCount;
    ArrayList<ls> computedFunctions;
    private double a = 1;
    private boolean dual;
    private Kernel kernel;

    public void DrawAccuracyHistogram(final int[] classes, final int[] features) {
        if (threadIsBusy()) {
            return;
        }
        if (classes.length != 2) {
            JOptionPane.showMessageDialog(null, "You need to select two classes!");

        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");

        } else {
            thread = new Thread() {

                @Override
                public void run() {
                    AccuracyHistogram(classes, features);
                }
            };
            thread.start();
        }
    }

    public void DrawTestAccuracyHistogram(final int[] classes, final int[] features) {
        if (threadIsBusy()) {
            return;
        }
        if (classes.length != 2) {
            JOptionPane.showMessageDialog(null, "You need to select two classes!");
        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");
        } else {
            thread = new Thread() {

                @Override
                public void run() {
                    TestHistogram(classes, features);
                }
            };
            thread.start();
        }
    }

    private void AccuracyHistogram(int[] classes, int[] features) {
        ClassAndValue[] cv = new ClassAndValue[trainingSet.getTrainingClassSize(classes[0]) + trainingSet.getTrainingClassSize(classes[1])];
        int q = 0;        
        for (int i = 0; i < 2; i++) {
        	int size = trainingSet.getTrainingClassSize(classes[i]);
            for (int j = 0; j < size; j++) {            	
                cv[q] = new ClassAndValue(classes[i], classify(trainingSet.trainingObject(classes[i],features, j), classes, features).value);
                q++;
            }
        }
        Arrays.sort(cv);
        Visan.gm.AccuracyHistogram(cv, classes, features, "Least Squares Training Set", ++accHistogramCount, decisionThreshold);
    }

    private void TestHistogram(int[] classes, int[] features) {
        if (!Visan.io.chooseTestingSet(false)) {
            return;
        }
        ClassAndValue[] cv = new ClassAndValue[trainingSet.getTestingClassSize(classes[0])
                + trainingSet.getTestingClassSize(classes[1])];
        int q = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < trainingSet.getTestingClassSize(i); j++) {
                cv[q] = new ClassAndValue(classes[i], classify(trainingSet.testingObject(classes[i],features, j), classes, features).value);
                q++;
            }
        }
        Arrays.sort(cv);
        Visan.gm.AccuracyHistogram(cv, classes, features, "Least Squares Testing Set", ++testHistogramCount, decisionThreshold);
    }

    private ClassAndValue classify(double[] classObject, int[] classes, int[] features) {
        ensure(classes, features);
        RealVector x = new ArrayRealVector(classObject);
        x = x.append(1);
        ClassAndValue cv;
        int c;
        double v;
        ls temp = computedFunctions.get(indexOf(classes, features));
        if (dual) {
            int n = temp.X.getRowDimension();
            RealMatrix k = new Array2DRowRealMatrix(n, 1);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    RealVector xi = new ArrayRealVector(temp.X.getRow(i));
                    k.setEntry(i, 0, kernel.value(xi, x));
                }
            }
            v = temp.constantPart.multiply(k).getEntry(0, 0);
        } else {
            v = temp.w.dotProduct(x);
        }
        if (v > 0) {
            c = 1;
        } else {
            c = 0;
        }
        cv = new ClassAndValue(c, v);
        return cv;
    }

    public void classify(ArrayList<double[]> x, int[] classesList, int[] featuresList) {
        if (threadIsBusy()) {
            return;
        }
        Visan.gm.writeToConsole("\n");
        for (int j = 0; j < x.size(); j++) {                       
            int predictedClass = classify(x.get(j), classesList, featuresList).classIndex;
            Visan.gm.writeToConsole("Predicted Class: " + trainingSet.getClassesNames()[predictedClass]+"\n");
        }
    }

    private void ensure(int[] classes, int[] features) {
        if (indexOf(classes, features) == -1) {
            findParameters(classes, features);
        }
    }

    private int indexOf(int[] classes, int[] features) {
        for (int i = 0; i < computedFunctions.size(); i++) {
            if (Arrays.equals(computedFunctions.get(i).classes, classes) && Arrays.equals(computedFunctions.get(i).features, features)) {
                return i;
            }
        }
        return -1;
    }

    private void findParameters(int[] classes, int[] features){
        int n = 0, c = 0;
        ls newValues = new ls(classes, features);
        for (int i = 0; i < classes.length; i++) {
            n += trainingSet.getTrainingClassSize(classes[i]);
        }
        RealMatrix X = new Array2DRowRealMatrix(n, features.length + 1), Y = new Array2DRowRealMatrix(n, 1);
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
                for (int f = 0; f < features.length; f++) {
                    X.setEntry(c, f, trainingSet.trainingObject(classes[i], j)[f]);
                }
                X.setEntry(c, features.length, 1);
                if (classes.length == 2) {
                    if (i == 0) {
                        Y.setEntry(c, 0, -1);
                    } else {
                        Y.setEntry(c, 0, 1);
                    }
                } else {
                    Y.setEntry(c, 0, classes[i]);
                }
                c++;
            }
        }
        RealMatrix XX, aI;
        if (dual) {
            RealMatrix K = new Array2DRowRealMatrix(n, n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    RealVector xi = new ArrayRealVector(X.getRow(i));
                    RealVector xj = new ArrayRealVector(X.getRow(j));
                    K.setEntry(i, j, kernel.value(xi, xj));
                }
            }
            aI = MatrixUtils.createRealIdentityMatrix(n).scalarMultiply(a);
            XX = new LUDecompositionImpl(aI.add(K)).getSolver().getInverse();
            RealMatrix constantPart = Y.transpose().multiply(XX);
            newValues.constantPart = constantPart;
            newValues.X = X;
        } else {
            XX = X.transpose().multiply(X);
            aI = MatrixUtils.createRealIdentityMatrix(XX.getRowDimension()).scalarMultiply(a);
            XX = aI.add(XX);
            XX = new LUDecompositionImpl(XX).getSolver().getInverse();
            Y = Y.transpose();
            RealMatrix wd = Y.multiply(X).multiply(XX);
            newValues.w = new ArrayRealVector(wd.getRowVector(0));
        }
        int i = indexOf(classes, features);
        if (i != -1) {
            computedFunctions.remove(i);
        }
        computedFunctions.add(newValues);
        Visan.gm.writeToConsole("LS parameters found.\n");
    }

    public void setNewTrainingSet(ClassesCollection trainingSet) {
        computedFunctions = new ArrayList<ls>();
        this.trainingSet = trainingSet;
        decisionThreshold = new MDouble();
        kernel = new Kernel();
    }

    public void setA() {
        if (threadIsBusy()) {
            return;
        }
        try {
            int t = Integer.parseInt(JOptionPane.showInputDialog("Input a( >= 1):"));
            if (a >= 1) {
                a = t;
            }
        } catch (Exception e) {
            Visan.gm.wrongInput();
        }
    }

    public boolean setDual(boolean b) {
        if (threadIsBusy()) {
            return false;
        }
        computedFunctions = new ArrayList<ls>();
        dual = b;
        if (dual) {
            chooseKernel();
        } else {
            kernel.setMethod(0);
        }
        return true;
    }

    public void startFindingParameters(final int[] classes, final int[] features) {
        if (threadIsBusy()) {
            return;
        }
        if (classes.length != 2) {
            JOptionPane.showMessageDialog(null, "You need to select two classes!");

        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");

        } else {
            thread = new Thread() {

                @Override
                public void run() {
                    findParameters(classes, features);
                }
            };
            thread.start();
        }
    }

    public void chooseKernel() {
        if (threadIsBusy()) {
            return;
        }
        computedFunctions = new ArrayList<ls>();
        kernel.showDialog();
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
}
