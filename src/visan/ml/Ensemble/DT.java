/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visan.ml.Ensemble;

import visan.common.MDouble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Ramzan
 */
public class DT implements Serializable{

    public MDouble decisionThreshold;
    private int maxDepth = 40;
    private int branchingFactor = 3;
    private final int nDStep = 400;
    ArrayList<ArrayList<TrainingObject>> dataSet;

    public DT() {
        decisionThreshold = new MDouble();
    }

    public void setData(ArrayList<ArrayList<TrainingObject>> data) {
        dataSet = data;
    }

    public void setDepth(int md) {
        maxDepth = md;
    }

    public void setBranchingFactor(int branchingFactor) {
        this.branchingFactor = branchingFactor;
    }

    public Tree build(int[] classInd, int[] featureInd) {
        //Step 1, getting all the required data
        double[][] cut = new double[featureInd.length][];
        ArrayList<Integer> remainingFeatures = new ArrayList<Integer>();
        ArrayList<double[]> featureValues = new ArrayList<double[]>();

        int objects = 0;
        for (ArrayList<TrainingObject> class1 : dataSet) {
            objects += class1.size();
        }

        for (int i = 0; i < featureInd.length; i++) {
            remainingFeatures.add(i);
            featureValues.add(new double[objects]);
        }
        int counter = 0;

        for (int i = 0; i < classInd.length; i++) {
            int classSize = dataSet.get(i).size();
            for (int q = 0; q < classSize; q++) {
                for (int f = 0; f < featureInd.length; f++) {
                    featureValues.get(f)[counter] = dataSet.get(i).get(q).data[f];
                }
                counter++;
            }
        }
        double mins[] = new double[featureInd.length];
        double maxs[] = new double[featureInd.length];
        for (int f = 0; f < featureInd.length; f++) {
            double max = -Double.MAX_VALUE, min = Double.MAX_VALUE;
            for (int i = 0; i < featureValues.get(f).length; i++) {
                if (featureValues.get(f)[i] > max) {
                    max = featureValues.get(f)[i];
                }
                if (featureValues.get(f)[i] < min) {
                    min = featureValues.get(f)[i];
                }
            }
            maxs[f] = max;
            mins[f] = min;
        }

        //sample size
        double ss = 0;
        for (int i = 0; i < classInd.length; i++) {
            int classSize = dataSet.get(i).size();
            for (int q = 0; q < classSize; q++) {
                ss += dataSet.get(i).get(q).weight;
            }
        }
        //Random cut points selection
        if (nDStep > 0) {
            for (int f = 0; f < featureInd.length; f++) {
                double currentBest = Double.MAX_VALUE;
                double[] bestCut = null;
                Random rand = new Random();
                for (int d = 0; d < nDStep; d++) {
                    //int numCuts = rand.nextInt((max - min) + 1) + min;
                    int numCuts = branchingFactor - 1;
                    double[] temp = new double[numCuts];
                    for (int b = 0; b < numCuts; b++) {
                        temp[b] = mins[f] + (rand.nextDouble()) * (maxs[f] - mins[f]);
                    }
                    Arrays.sort(temp);
                    double totalEntropy = 0;
                    for (int b = 0; b < numCuts + 1; b++) {
                        double entropy = 0;
                        double rem[] = new double[classInd.length];
                        double ptotal = 0;
                        for (int i = 0; i < classInd.length; i++) {
                            double p = 0;
                            int classSize = dataSet.get(i).size();
                            for (int q = 0; q < classSize; q++) {
                                double value = dataSet.get(i).get(q).data[f];
                                if (b < numCuts) {
                                    if (value < temp[b]
                                            && !(b > 0 && value < temp[b - 1])) {
                                        p += dataSet.get(i).get(q).weight;
                                    }
                                } else {
                                    if (value > temp[b - 1]) {
                                        p += dataSet.get(i).get(q).weight;
                                    }
                                }

                            }
                            rem[i] = p;
                            ptotal += p;
                        }
                        double sum = 0;
                        for (int i = 0; i < rem.length; i++) {
                            sum += rem[i];
                        }
                        for (int i = 0; i < classInd.length; i++) {
                            if (sum != 0) {
                                double px = (rem[i] / sum);
                                if (px != 0) {
                                    entropy += px * Math.log(px);
                                }
                            }
                        }
                        entropy = -entropy;
                        double pt = ptotal / ss;
                        totalEntropy += pt * entropy;
                    }
                    if (totalEntropy < currentBest) {
                        currentBest = totalEntropy;
                        bestCut = temp;
                    }
                }
                cut[f] = bestCut;
            }
        }
        //Step 2, building tree
        Node root = new Node();
        growNode(root, 0, dataSet, remainingFeatures, cut, classInd, featureInd);
        Tree t = new Tree(classInd, featureInd, root);
        return t;
    }

    private void growNode(Node node, int depth, ArrayList<ArrayList<TrainingObject>> data, ArrayList<Integer> rm, double[][] modes, final int[] classInd, final int[] featureInd) {
        ArrayList<Integer> remainingFeatures = new ArrayList<Integer>();
        remainingFeatures.addAll(rm);
        if (remainingFeatures.size() > 0 && depth < maxDepth) {
            //check if only one class or empty
            int v = 0;
            int c = -1;
            for (int i = 0; i < classInd.length; i++) {
                int classSize = data.get(i).size();
                if (classSize > 0) {
                    v++;
                    //clasInd i
                    c = i;
                }
            }
            //only one class
            if (v == 1) {
                node.nodeClass = c;
                return;
            }
            //empty area
            if (v == 0) {
                node.nodeClass = 0;
                return;
            }
            //calculating entropies
            //sample size
            double ss = 0;
            for (int i = 0; i < classInd.length; i++) {
                double classSize = 0;
                for (TrainingObject to : data.get(i)) {
                    classSize += to.weight;
                }
                for (int q = 0; q < classSize; q++) {
                    ss += dataSet.get(i).get(q).weight;
                }
            }
            int bestFeature = -1;
            double largestIG = -Double.MAX_VALUE;
            for (Integer remainingFeature : remainingFeatures) {
                //finding entropy
                double entropy = 0;
                for (int i = 0; i < classInd.length; i++) {
                    //here
                    double classSize = 0;
                    for (TrainingObject to : data.get(i)) {
                        classSize += to.weight;
                    }
                    double px = ((double) classSize / (double) ss);
                    if (px != 0) {
                        entropy += px * Math.log(px);
                    }
                }
                entropy = -entropy;
                //now finding thing to substract
                double sumToSubstract = 0;
                int size=0;
                try {
                    size = modes[remainingFeature].length + 1;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                for (int b = 0; b < size; b++) {
                    double bentropy = 0;
                    double ptotal = 0;
                    double rem[] = new double[classInd.length];
                    for (int i = 0; i < classInd.length; i++) {
                        double p = 0;
                        int classSize = data.get(i).size();
                        for (int q = 0; q < classSize; q++) {
                            double value = data.get(i).get(q).data[remainingFeature];
                            if (b < modes[remainingFeature].length) {
                                if (value < modes[remainingFeature][b]
                                        && !(b > 0 && value < modes[remainingFeature][b - 1])) {
                                    p += dataSet.get(i).get(q).weight;
                                }
                            } else {
                                if (value > modes[remainingFeature][b - 1]) {
                                    p += dataSet.get(i).get(q).weight;
                                }
                            }

                        }
                        rem[i] = p;
                        ptotal += p;
                    }
                    double sum = 0;
                    for (int i = 0; i < rem.length; i++) {
                        sum += rem[i];
                    }
                    for (int i = 0; i < classInd.length; i++) {
                        if (sum != 0) {
                            double px = ((double) rem[i] / (double) sum);
                            if (px != 0) {
                                bentropy += px * Math.log(px);
                            }
                        }
                    }
                    bentropy = -bentropy;
                    double pt = (double) ptotal / ss;
                    sumToSubstract += pt * bentropy;
                }
                double IG = entropy - sumToSubstract;
                if (IG > largestIG) {
                    largestIG = IG;
                    bestFeature = remainingFeature;
                }
            }
            if (largestIG == 0) {
                //no point to continue
                double maxP = -1;
                int maxC = -1;
                for (int i = 0; i < classInd.length; i++) {
                    double classSize = 0;
                    for (TrainingObject to : data.get(i)) {
                        classSize += to.weight;
                    }
                    if (classSize > maxP) {
                        maxP = classSize;
                        maxC = i;
                    }
                }
                node.nodeClass = maxC;
            }
            node.cut = modes[bestFeature];
            node.feature = featureInd[bestFeature];

            for (int i = 0; i < remainingFeatures.size(); i++) {
                if (remainingFeatures.get(i) == bestFeature) {
                    remainingFeatures.remove(i);
                    break;
                }
            }
            //new children
            node.setNumberOfChildren(modes[bestFeature].length + 1);
            for (int b = 0; b < modes[bestFeature].length + 1; b++) {
                Node childNode = new Node();
                node.setChild(b, childNode);
                ArrayList<ArrayList<TrainingObject>> newData = new ArrayList<ArrayList<TrainingObject>>(classInd.length);
                for (int i = 0; i < classInd.length; i++) {
                    int classSize = data.get(i).size();
                    newData.add(new ArrayList<TrainingObject>());
                    for (int q = 0; q < classSize; q++) {
                        double value = data.get(i).get(q).data[bestFeature];
                        boolean shouldAdd = false;
                        if (b < modes[bestFeature].length) {
                            if (value < modes[bestFeature][b]
                                    && !(b > 0 && value < modes[bestFeature][b - 1])) {
                                shouldAdd = true;
                            }
                        } else {
                            if (value > modes[bestFeature][b - 1]) {
                                shouldAdd = true;
                            }
                        }
                        if (shouldAdd) {
                            newData.get(i).add(data.get(i).get(q));
                        }

                    }
                }
                growNode(childNode, depth + 1, newData, remainingFeatures, modes, classInd, featureInd);
            }
        } else {
            int maxP = -1;
            int maxC = -1;
            for (int i = 0; i < classInd.length; i++) {
                int classSize = data.get(i).size();
                if (classSize > maxP) {
                    maxP = classSize;
                    maxC = i;
                }
            }
            node.nodeClass = maxC;
        }
    }

}
