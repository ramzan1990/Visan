/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visan.ml.Ensemble;

import java.io.Serializable;

/**
 *
 * @author Ramzan
 */
public class Tree implements Serializable{

    int[] classInd, featureInd;
    Node root;
    double weight;

    public Tree(int[] classInd, int[] featureInd, Node root) {
        this.classInd = classInd;
        this.featureInd = featureInd;
        this.root = root;
    }

    int classify(double[] trainingObject) {
        return lookForValue(root, trainingObject);
    }

    private int lookForValue(Node node, double[] trainingObject) {
        if (node.nodeClass != -1) {
            return node.nodeClass;
        } else {
            for (int i = 0; i < node.cut.length; i++) {
                if (trainingObject[node.feature] < node.cut[i]) {
                    return lookForValue(node.getChild(i), trainingObject);
                }
            }
            return lookForValue(node.getChild(node.cut.length), trainingObject);
        }
    }

}
