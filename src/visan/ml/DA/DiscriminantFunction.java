package visan.ml.DA;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

public class DiscriminantFunction implements Serializable{

    public RealMatrix pC;
    public ArrayList<RealMatrix> C;
    public ArrayList<RealVector> mi, miC;
    public ArrayList<Double> MD;
    public double mDistTwoClass;
    public RealVector P;
    int[] classInd, featureInd;

    public DiscriminantFunction(int[] classInd, int[] featureInd) {
        this.classInd = classInd;
        this.featureInd = featureInd;
        int fnumber = featureInd.length;
        int classes = classInd.length;
        
    }

    public String ClassIndices() {
        String t = "";
        for (int i = 0; i < classInd.length; i++) {
            t += " " + classInd[i];
        }
        return t;
    }

    public String FeatureIndices() {
        String t = "";
        for (int i = 0; i < featureInd.length; i++) {
            t += " " + featureInd[i];
        }
        return t;
    }
}
