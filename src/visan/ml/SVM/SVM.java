package visan.ml.SVM;

import visan.common.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import visan.Visan;



import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;


public class SVM implements Serializable{

    private ClassesCollection trainingSet;
    private MDouble decisionThreshold;
    private int accHistogramCount, multiHistogramCount, testHistogramCount;
    private ArrayList<SVMFunction> computedSVM;
    private Kernel kernel;
    private static double threshold = 0.01;
    private static int C = 10;
    private boolean busy;
    double toleranceFeas=0.1;
    double tolerance=0.1;
    private Thread thread;

     public void setToleranceFeas() {
        try {
            toleranceFeas = Double.parseDouble(JOptionPane.showInputDialog("Input tolerance feasibility (default 0.1):"));
        } catch (Exception e) {
            Visan.gm.wrongInput();
        }
    }
      public void setTolerance() {
        try {
            tolerance = Double.parseDouble(JOptionPane.showInputDialog("Input tolerance (default 0.1):"));
        } catch (Exception e) {
            Visan.gm.wrongInput();
        }
    }
    public void setThreshold() {
        try {
            threshold = Double.parseDouble(JOptionPane.showInputDialog("Input threshold (default 0.01):"));
        } catch (Exception e) {
            Visan.gm.wrongInput();
        }
    }

    public void setC() {
        try {
            C = Integer.parseInt(JOptionPane.showInputDialog("Input C (default 10):"));
        } catch (Exception e) {
            Visan.gm.wrongInput();
        }
    }

    public void DrawAccuracyHistogram(int[] classes, int[] features) {
        if (classes.length < 2) {
            JOptionPane.showMessageDialog(null, "You need to select at least two classes!");

        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");

        } else if (classes.length == 2) {
            AccuracyHistogram(classes, features);
        } else {
            MultiHistogram(classes, features);
        }
    }

    public void DrawTestAccuracyHistogram(int[] classes, int[] features) {
        if (classes.length < 2) {
            JOptionPane.showMessageDialog(null, "You need to select at least two classes!");
        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");
        } else if (classes.length == 2) {
            TestHistogram(classes, features);
        } else {
            MultiTestHistogram(classes, features);
        }
    }

    public void learn(int[] classes, int[] features) {
        prepareSVM(classes, features);
    }

    private void AccuracyHistogram(int[] classes, int[] features) {
        if (!ensure(classes, features)) {
            return;
        }
        ClassAndValue[] cv = new ClassAndValue[trainingSet.getTrainingClassSize(classes[0]) + trainingSet.getTrainingClassSize(classes[1])];
        int q = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
                cv[q] = new ClassAndValue(classes[i], classify(trainingSet.trainingObject(classes[i], j), classes, features).value);
                q++;
            }
        }
        Arrays.sort(cv);
        Visan.gm.AccuracyHistogram(cv, classes, features, "SVM Training Set", ++accHistogramCount, decisionThreshold);
    }

    private void TestHistogram(int[] classes, int[] features) {
        if (!ensure(classes, features)) {
            return;
        }
        if (!Visan.io.chooseTestingSet(false)) {
            return;
        }
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
        Visan.gm.AccuracyHistogram(cv, classes, features, "SVM Testing Set", ++testHistogramCount, decisionThreshold);
    }

    private void MultiTestHistogram(int[] classes, int[] features) {
        if (!ensure(classes, features)) {
            return;
        }
        if (!Visan.io.chooseTestingSet(false)) {
            return;
        }
        int[][] v = new int[classes.length][classes.length];
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < trainingSet.getTestingClassSize(classes[i]); j++) {
                int pc = classify(trainingSet.testingObject(classes[i], j), classes, features).classIndex;
                v[i][pc]++;
            }
        }
        Visan.gm.MultiClassHistogram(v, classes, features, "SVM Testing Set", ++multiHistogramCount);
    }

    private void MultiHistogram(int[] classes, int[] features) {
        if (!ensure(classes, features)) {
            return;
        }
        int[][] v = new int[classes.length][classes.length];
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
                int pc = classify(trainingSet.trainingObject(classes[i], j), classes, features).classIndex;
                v[i][pc]++;
            }
        }
        Visan.gm.MultiClassHistogram(v, classes, features, "SVM Training Set", ++multiHistogramCount);
    }

    private ClassAndValue classify(double[] x, int[] classes, int[] features) {
        if (!ensure(classes, features)) {
            return null;
        }
        int c;
        SVMFunction svmf = computedSVM.get(indexOf(classes, features));
        double s = 0;
        for (int sl = 0; sl < svmf.sol.length; sl++) {
            double[] x2 = svmf.X[sl];
            double KT = svmf.kernel.value(new ArrayRealVector(x2), new ArrayRealVector(x));
            s = s + svmf.sol[sl] * svmf.Y[sl] * KT;
        }
        s = 0.5 * s;
        s = s + svmf.avgD;
        double y = s;
        if (y > 0) {
            c = 0;
        } else {
            c = 1;
        }
        return new ClassAndValue(c, -s);
    }

    public void classify(ArrayList<double[]> x, int[] classes, int[] features) {
        if (!ensure(classes, features)) {
            return;
        }
        Visan.gm.writeToConsole("\n");
        for (int j = 0; j < x.size(); j++) {
            int r = classify(x.get(j), classes, features).classIndex;
            Visan.gm.writeToConsole("Predicted Class:" + trainingSet.getClassesNames()[r] + "\n");
        }
    }

    private boolean ensure(int[] classes, int[] features) {
        if (indexOf(classes, features) == -1) {
            JOptionPane.showMessageDialog(null, "Find parameters first!");
            return false;
        } else {
            return true;
        }
    }

    private int indexOf(int[] classes, int[] features) {
        for (int i = 0; i < computedSVM.size(); i++) {
            if (Arrays.equals(computedSVM.get(i).classes, classes) && Arrays.equals(computedSVM.get(i).features, features)) {
                return i;
            }
        }
        return -1;
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
	
    private void prepareSVM(final int[] classes, final int[] features) {
        Visan.gm.blockSVM(true);
        Visan.gm.writeToConsole("Finding SVM parameters...\n");
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    int n = trainingSet.getTrainingElementsSum(classes);
                    int c = 0;

                    double[][] X = new double[n][features.length];
                    double[] Y = new double[n];
                    for (int i = 0; i < classes.length; i++) {
                        for (int j = 0; j < trainingSet.getTrainingClassSize(classes[i]); j++) {
                            for (int f = 0; f < features.length; f++) {
                                X[c][f] = trainingSet.trainingObject(classes[i], j)[f];
                            }
                            if (i == 0) {
                                Y[c] = 1;
                            } else {
                                Y[c] = -1;
                            }
                            c++;
                        }
                    }

                    RealMatrix Xm = new Array2DRowRealMatrix(X);
                    RealMatrix K = new Array2DRowRealMatrix(n, n);
                    kernel = new Kernel();
                    kernel.showDialog();
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            RealVector xi = new ArrayRealVector(Xm.getRow(i));
                            RealVector xj = new ArrayRealVector(Xm.getRow(j));
                            K.setEntry(i, j, kernel.value(xi, xj));
                        }
                    }
                    
                    RealMatrix H = new Array2DRowRealMatrix(n, n);
                    
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            H.setEntry(i, j, 0.5*K.getEntry(i, j) * Y[i] * Y[j]);
                        }
                    }
                    //q or f
                    double[] qVector = new double[n];
                    for (int i = 0; i < n; i++) {
                        qVector[i] = -1;
                    }
                    double[][] data = new double[n][n];
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            data[i][j] = H.getEntry(i, j);
                            if (i == j) {
                                data[i][j] += 1;
                            }
                        }
                    }
                    //PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(data, qVector, 0);
                    //equalities
                    double[][] A = new double[1][n];
                    for (int i = 0; i < n; i++) {
                        A[0][i] = Y[i];
                    }
                    double[] b = new double[]{0};

                    //optimization problem
                    //OptimizationRequest or = new OptimizationRequest();
                    //or.setF0(objectiveFunction);
                    //or.setInitialPoint(new double[]{0.1, 0.9});
                    //or.setFi(inequalities); //if you want x>0 and y>0
                    //or.setA(A);
                    //or.setB(b);
                    //or.setToleranceFeas(toleranceFeas);
                    //or.setTolerance(tolerance);

                    //optimization
                    //JOptimizer opt = new JOptimizer();
                   // opt.setOptimizationRequest(or);
                    //int returnCode = opt.optimize();

                    //double[] sol = opt.getOptimizationResponse().getSolution();
                    double[] sol = null;
                    for (int i = 0; i < sol.length; i++) {
                        System.out.println(sol[i]);
                    }

                    int numSupport = 0;
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    for (int i = 0; i < sol.length; i++) {
                        if (threshold < sol[i] && sol[i] < (C - threshold)) {
                            numSupport = numSupport + 1;
                            temp.add(new Integer(i));
                        }
                    }
                    double d = 0, avgD = 0;
                    for (int j = 0; j < temp.size(); j++) {
                        d = Y[temp.get(j)];
                        double s = 0;
                        for (int i = 0; i < sol.length; i++) {
                            s = s + sol[i] * Y[i] * K.getEntry(temp.get(j), i);
                        }
                        s = 0.5 * s;
                        d = d - s;
                        avgD = avgD + d;
                    }
                    avgD = avgD / numSupport;
                    SVMFunction svmf = new SVMFunction(classes, features);
                    svmf.avgD = avgD;
                    svmf.kernel = kernel;
                    svmf.sol = sol;
                    svmf.X = X;
                    svmf.Y = Y;
                    int ind;
                    ind = indexOf(classes, features);
                    if(ind != -1){
                        computedSVM.remove(ind);
                    }
                    computedSVM.add(svmf);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Not positive definite matrix");
                }
                Visan.gm.blockSVM(false);
                Visan.gm.writeToConsole("SVM parameters found \n");
            }
        };
        thread.start();

    }

    public void setNewTrainingSet(ClassesCollection trainingSet) {
        computedSVM = new ArrayList<SVMFunction>();
        this.trainingSet = trainingSet;
        decisionThreshold = new MDouble();
    }
}
