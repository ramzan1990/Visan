/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visan.ml.Ensemble;

import visan.common.ClassAndValue;
import visan.common.MDouble;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Ramzan
 */
public class Forest implements Serializable{

    int[] classInd, featureInd;
    ArrayList<Tree> forest;

    public Forest(int[] classInd, int[] featureInd) {
        this.classInd = classInd;
        this.featureInd = featureInd;
        forest = new ArrayList<Tree>();
    }

    public ClassAndValue classify(double[] trainingObject, MDouble decisionThreshold) {
        ClassAndValue cav;
        int bias = (int) Math.round(decisionThreshold.value);
        int[] results = new int[classInd.length];
        for (Tree tree : forest) {
            results[tree.classify(trainingObject)]++;
        }
        if (results.length == 2) {
            int value = results[1] - results[0];
            if (bias < 0) {
                results[1] += Math.abs(bias);
            } else {
                results[0] += bias;
            }
            int max = -1;
            int c = -1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    max = results[i];
                    c = i;
                }
            }
            cav = new ClassAndValue(classInd[c], value);
        } else {
            int max = -1;
            int c = -1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    max = results[i];
                    c = i;
                }
            }
            cav = new ClassAndValue(classInd[c], 1);
        }
        return cav;
    }

    public ClassAndValue classifyWithWeight(double[] trainingObject, MDouble decisionThreshold) {
        int bias = (int) Math.round(decisionThreshold.value);
        double[] results = new double[classInd.length];
        for (Tree tree : forest) {
            results[tree.classify(trainingObject)] += tree.weight;
        }
        double value = results[1] - results[0];
        if (bias < 0) {
            results[1] += Math.abs(bias);
        } else {
            results[0] += bias;
        }
        double max = -1;
        int c = -1;
        for (int i = 0; i < results.length; i++) {
            if (results[i] > max) {
                max = results[i];
                c = i;
            }
        }
        ClassAndValue cav = new ClassAndValue(classInd[c], value);
        return cav;
    }
}
