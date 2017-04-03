package visan.common;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ramzan
 */
public class Round {

    public static double round(double Rval, int Rpl) {
        double p = Math.pow(10, Rpl);
        Rval = Rval * p;
        double tmp = Math.round(Rval);
        return tmp / p;
    }

    public static double sRound(double v) {
        if (Math.abs(v) < 1) {
            return round(v, 3);
        } else if (Math.abs(v) < 10) {
            return round(v, 2);
        } else if (Math.abs(v) < 100) {
            return round(v, 1);
        } else {
            return round(v, 0);
        }
    }

    public static double ssRound(double v) {
        if (Math.abs(v) < 0.0001) {
            return round(v, 8);
        } else if (Math.abs(v) < 0.001) {
            return round(v, 7);
        } else if (Math.abs(v) < 0.01) {
            return round(v, 6);
        } else if (Math.abs(v) < 0.01) {
            return round(v, 5);
        } else if (Math.abs(v) < 0.1) {
            return round(v, 4);
        } else if (Math.abs(v) < 1) {
            return round(v, 3);
        } else if (Math.abs(v) < 10) {
            return round(v, 2);
        } else if (Math.abs(v) < 100) {
            return round(v, 1);
        } else {
            return round(v, 0);
        }
    }
}
