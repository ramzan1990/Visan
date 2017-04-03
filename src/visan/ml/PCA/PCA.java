package visan.ml.PCA;

import visan.common.ClassesCollection;

import java.io.Serializable;
import java.util.ArrayList;

import visan.Visan;

import org.apache.commons.math.linear.*;
import org.apache.commons.math.stat.correlation.Covariance;
import org.jzy3d.colors.Color;

/**
 *
 * @author Ramzan
 */
public class PCA implements Serializable{

    ClassesCollection data, finalData;
    private RealVector pcaVectors[];
    private double[] eigenvalues;
    private int[] classesList, featuresList, classSizes;
    private RealMatrix rowDataAdjust;
    final static Color[] allColors = {Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.YELLOW};

    public PCA(ClassesCollection data, int[] classInd, int[] featureInd) {
        this.data = data;
        classesList = classInd;
        featuresList = featureInd;
        classSizes = new int[classInd.length];
        for (int i = 0; i < classSizes.length; i++) {
            classSizes[i] = data.getTrainingClassSize(classInd[i]);
        }
        double[] globalMean = new double[featureInd.length];
        rowDataAdjust = new Array2DRowRealMatrix(data.getTrainingElementsSum(classesList), featureInd.length);
        //putting data into matrix
        int counter = 0;
        for (int i = 0; i < classInd.length; i++) {
            for (int j = 0; j < data.getTrainingClassSize(classInd[i]); j++) {
                for (int f = 0; f < featureInd.length; f++) {
                    rowDataAdjust.setEntry(counter, f, data.trainingObject(classInd[i], j)[featureInd[f]]);
                    globalMean[f] += data.trainingObject(classInd[i], j)[featureInd[f]];
                }
                counter++;
            }
        }
        for (int i = 0; i < globalMean.length; i++) {
            globalMean[i] /= counter;
        }
        //subtracting mean
        for (int i = 0; i < rowDataAdjust.getRowDimension(); i++) {
            for (int j = 0; j < rowDataAdjust.getColumnDimension(); j++) {
                rowDataAdjust.setEntry(i, j, rowDataAdjust.getEntry(i, j) - globalMean[j]);
            }
        }
        Covariance c = new Covariance(rowDataAdjust);
        RealMatrix m2 = c.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecompositionImpl(m2, 1);
        eigenvalues = ed.getRealEigenvalues();
        pcaVectors = new RealVector[featureInd.length];
        for (int i = 0; i < featureInd.length; i++) {
            pcaVectors[i] = ed.getEigenvector(i);
        }
        Visan.gm.writeToConsole("PCA complete. \n");
        for (int i = 0; i < eigenvalues.length; i++) {
            Visan.gm.writeToConsole("\nEigenvalue " + i + ": " + eigenvalues[i]);
        }
        Visan.gm.writeToConsole("\n");
    }

    public ClassesCollection graph(int d) {
        RealMatrix m = new Array2DRowRealMatrix(d, pcaVectors[0].getDimension());
        finalData = new ClassesCollection();
        int[] v;
        String[] choices = new String[pcaVectors.length];
        for (int t = 0; t < pcaVectors.length; t++) {
            choices[t] = Integer.toString(t);
        }
        v = ListDialog.showDialog("Choose " + d + " eigenvectors:", "", choices);
        if (v == null || v.length != d) {
            return null;
        }
        finalData.features = new ArrayList<String>();
        for (int i = 0; i < d; i++) {
            m.setRowVector(i, pcaVectors[v[i]]);
            finalData.features.add("eigenvector " + v[i]);
        }
        RealMatrix fd = m.multiply(rowDataAdjust.transpose());
        for (int i = 0; i < classesList.length; i++) {
            finalData.addClass(data.getClassesNames()[classesList[i]]);
        }
        int counter = 0, c = 0;
        for (int j = 0; j < fd.getColumnDimension(); j++) {
            double a[] = new double[fd.getRowDimension()];
            for (int f = 0; f < fd.getRowDimension(); f++) {
                a[f] = fd.getEntry(f, j);
            }
            counter++;
            if (counter > classSizes[c]) {
                c++;
                counter = 1;
            }
            finalData.addTrainingExample(c, a);
        }
        return finalData;
    }

    public int[] getClassesList() {
        return classesList;
    }

    public int[] getFeaturesList() {
        return featuresList;
    }

    public double[] getEigenvalues() {
        return eigenvalues;
    }
}
