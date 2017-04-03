package visan.common;

import java.io.Serializable;

import javax.swing.JOptionPane;

import visan.Visan;

import org.apache.commons.math.linear.RealVector;

/**
 *
 * @author Ramzan
 */
public class Kernel implements Serializable{

    private int method = 0;
    private double degree = 1, gamma = 1;

    public double value(RealVector xi, RealVector xj) {
        double r;
        if (method == 0) {
            r =xi.dotProduct(xj);
        } else if (method == 1) {
            r = Math.pow(xi.dotProduct(xj) + 1, degree);
        } else {
            double result = 0;
            for (int i = 0; i < xi.getDimension(); i++) {
                double temp = xi.getEntry(i) - xj.getEntry(i);
                result += temp * temp;
            }
            r = Math.exp(-gamma * result);
        }
        return r;
    }

    public void setDegree() {
        try {
            int t = Integer.parseInt(JOptionPane.showInputDialog("Input degree(>=1):"));
            if (t >= 1) {
                degree = t;
            }
        } catch (Exception e) {
            Visan.gm.wrongInput();
        }
    }

    public void setMethod(int i) {
        method = i;
    }

    public void showDialog() {
        Object[] v;
        String[] choices = new String[3];
        choices[0] = "Dot Product";
        choices[1] = "Polynomial";
        choices[2] = "Radial";
        v = KernelPicker.showDialog(choices);
        if (v == null) {
            return;
        }
       method = (Integer)v[0];
       if(method==1){
           degree = (Double)v[1];
       } else if (method==2){
           gamma = (Double)v[1];
       }
    }
}
