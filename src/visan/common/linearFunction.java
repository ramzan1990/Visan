
package visan.common;

import org.apache.commons.math.linear.RealVector;

/**
 *
 * @author Umarov
 */
public class linearFunction {
    public RealVector w;
    public double d;
    public int[] classes, features;
    public linearFunction(int[] classes, int[] features) {
        this.classes = classes;
        this.features = features;
    } 
}
