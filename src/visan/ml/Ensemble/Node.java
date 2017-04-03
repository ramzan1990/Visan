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
public class Node implements Serializable{

    private Node[] children;
    int feature;
    double[] cut;
    int nodeClass;

    public Node() {
        nodeClass = -1;
    }

    public void setNumberOfChildren(int n) {
        children = new Node[n];
    }

    public void setChild(int n, Node node) {
        children[n] = node;
    }

    public Node getChild(int n) {
        return children[n];
    }

}
