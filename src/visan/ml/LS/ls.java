package visan.ml.LS;

import java.io.Serializable;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

/**
 *
 * @author Ramzan
 */
public class ls implements Serializable{

    public RealMatrix constantPart, X;
    public RealVector w;
    public int[] classes, features;

    public ls(int[] classes, int[] features) {
        this.classes = classes;
        this.features = features;
    }
}
