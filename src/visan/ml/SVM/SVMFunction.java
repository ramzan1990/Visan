package visan.ml.SVM;

import visan.common.Kernel;
import org.apache.commons.math.linear.RealVector;

public class SVMFunction {
double avgD;
Kernel kernel;
double[] sol;
double[][] X;
double[] Y;
    public SVMFunction(int[] classes, int[] features) {
        this.classes = classes;
        this.features = features;
    }
    public RealVector w;
    public double d;
    int[] classes, features;

    
}
