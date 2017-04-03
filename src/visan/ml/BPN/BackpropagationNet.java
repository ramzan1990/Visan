package visan.ml.BPN;

import visan.common.ClassAndValue;
import visan.common.MDouble;
import visan.common.ObjectAndClass;

import java.io.Serializable;
import java.util.ArrayList;

import visan.Visan;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;

public final class BackpropagationNet implements Comparable, Serializable {

    RealVector weights[][], tempv[][], neuronLayer[], prevWeights[][], prevWeightsTesting[][];
    double a[], bias[][], tempb[][], prevBias[][], prevBiasTesting[][];
    int[] classInd, featureInd, classSizes;
    int netSize[], f, testStop, numberOfImprovementTries = 5;
    double momentum;
    double tdp = 2 / Math.PI;
    private double cof = 1, minCof = 0.001;
    private double target = 1, decRate;
    private boolean noImprovementsStop, useTest, userStop;
    private int error[];
    private MDouble decisionThreshold;
    ArrayList<ObjectAndClass> tx;
    private int improvementTries, testingImprovementTries;
    public int result;

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

    public void setImprovementsStop(boolean b) {
        noImprovementsStop = b;
    }

    public BackpropagationNet(int[] classInd, int[] featureInd, int[] netSize, int[] classSizes, double[] learningRate, MDouble decisionThreshold) {
        this.decisionThreshold = decisionThreshold;
        this.netSize = netSize;
        this.classInd = classInd;
        this.featureInd = featureInd;
        this.classSizes = classSizes;
        a = new double[learningRate.length];
        setLearningRate(learningRate);
        neuronLayer = new RealVector[netSize.length];
        weights = new RealVector[netSize.length - 1][];
        for (int i = 0; i < netSize.length; i++) {
            neuronLayer[i] = new ArrayRealVector(netSize[i]);
        }
        randWeights(0.5);
        for (int i = 0; i < a.length; i++) {
            a[i] = learningRate[classInd[i]];
        }
    }

    public void randWeights(double n) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new RealVector[netSize[i + 1]];
            for (int h = 0; h < netSize[i + 1]; h++) {
                weights[i][h] = new ArrayRealVector(netSize[i]);
                for (int j = 0; j < weights[i][h].getDimension(); j++) {
                    weights[i][h].setEntry(j, RandomBetween(-n, n));
                }
            }
        }
        bias = new double[netSize.length - 1][];
        for (int i = 0; i < bias.length; i++) {
            bias[i] = new double[netSize[i + 1]];
            for (int j = 0; j < bias[i].length; j++) {
                bias[i][j] = RandomBetween(-n, n);
            }
        }
    }

    public BackpropagationNet(int[] classInd, int[] featureInd, int[] netSize, RealVector weights[][], double bias[][],
            MDouble decisionThreshold) {
        this.decisionThreshold = decisionThreshold;
        this.netSize = netSize;
        this.classInd = classInd;
        this.featureInd = featureInd;
        neuronLayer = new RealVector[netSize.length];
        for (int i = 0; i < netSize.length; i++) {
            neuronLayer[i] = new ArrayRealVector(netSize[i]);
        }
        this.weights = weights;
        this.bias = bias;
    }

    public void setMinCof(double d) {
        minCof = d;
    }

    public void mem() {
        tempv = copy2d(weights);
        tempb = copy2d(bias);
    }

    public void setMem() {
        weights = copy2d(tempv);
        bias = copy2d(tempb);
    }

    public void setF(int f) {
        this.f = f;
    }

    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    public void setDecRate(double decRate) {
        this.decRate = decRate;
    }

    public void setLearningRate(double l[]) {
        System.arraycopy(l, 0, a, 0, a.length);
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public void setTestStop(int n) {
        testStop = n;
    }

    public int learn(ArrayList<ObjectAndClass> objects, int lc, int allowedSeconds) {
        result = 0;
        int n = 0, prevError = Integer.MAX_VALUE, tPrevError = Integer.MAX_VALUE;
        improvementTries = 0;
        testingImprovementTries = 0;
        long elapsedTime = 0, st;
        allowedSeconds *= 1000;
        double s = cof;
        boolean stop = false;
        userStop = false;
        RealVector[][] previousWeights1 = null, previousWeights2 = null;
        double[][] previousBias1 = null, previousBias2 = null;
        do {
            st = System.currentTimeMillis();
            n++;
            error = new int[netSize[netSize.length - 1]];
            if (decRate > 0 && s > minCof) {
                s = s * decRate;
            }
            for (int i = 0; i < objects.size(); i++) {
                //feedforward
                double[][] neuronInput = new double[netSize.length - 1][];
                neuronLayer[0] = objects.get(i).object;
                int objectClass = objects.get(i).objectClass;
                for (int h = 0; h < neuronInput.length; h++) {
                    neuronInput[h] = new double[netSize[h + 1]];
                    for (int j = 0; j < neuronInput[h].length; j++) {
                        neuronInput[h][j] = bias[h][j] + neuronLayer[h].dotProduct(weights[h][j]);
                        neuronLayer[h + 1].setEntry(j, F(neuronInput[h][j]));
                    }
                }
                //backpropagation of error                
                double[][] errorInfTerm = new double[netSize.length - 1][];
                errorInfTerm[errorInfTerm.length - 1] = new double[netSize[netSize.length - 1]];
                RealVector[][] dWeights = new RealVector[netSize.length - 1][];
                dWeights[dWeights.length - 1] = new ArrayRealVector[netSize[netSize.length - 1]];
                double[][] dBias = new double[netSize.length - 1][];
                dBias[dBias.length - 1] = new double[netSize[netSize.length - 1]];

                for (int j = 0; j < netSize[netSize.length - 1]; j++) {
                    double t = -target, y = neuronLayer[neuronLayer.length - 1].getEntry(j);
                    if (j == objectClass) {
                        t = target;
                    }
                    errorInfTerm[errorInfTerm.length - 1][j] = (t - y) * fDerivative(y);
                    dWeights[dWeights.length - 1][j] =
                            neuronLayer[neuronLayer.length - 2].mapMultiply(s * a[objectClass] * errorInfTerm[errorInfTerm.length - 1][j]);
                    dBias[dBias.length - 1][j] = s * a[objectClass] * errorInfTerm[errorInfTerm.length - 1][j];
                }
                for (int q = netSize.length - 2; q > 0; q--) {
                    errorInfTerm[q - 1] = new double[netSize[q]];
                    dBias[q - 1] = new double[netSize[q]];
                    dWeights[q - 1] = new ArrayRealVector[netSize[q]];
                    double[] d_in = new double[netSize[q]];
                    for (int j = 0; j < d_in.length; j++) {
                        for (int k = 0; k < netSize[q + 1]; k++) {
                            d_in[j] += errorInfTerm[q][k] * weights[q][k].getEntry(j);
                        }
                        errorInfTerm[q - 1][j] = d_in[j] * fDerivative(neuronInput[q - 1][j]);
                        dWeights[q - 1][j] = neuronLayer[q - 1].mapMultiply(s * a[objectClass] * errorInfTerm[q - 1][j]);
                        dBias[q - 1][j] = s * a[objectClass] * errorInfTerm[q - 1][j];
                    }
                }
                //Update
                for (int p = 0; p < netSize.length - 1; p++) {
                    for (int h = 0; h < netSize[p + 1]; h++) {
                        weights[p][h] = weights[p][h].add(dWeights[p][h]);
                        bias[p][h] += dBias[p][h];
                        if (momentum != 0 && i > 1) {
                            RealVector m = (previousWeights1[p][h].subtract(previousWeights2[p][h])).mapMultiply(momentum);
                            weights[p][h].add(m);
                            bias[p][h] += momentum * (previousBias1[p][h] - previousBias2[p][h]);
                        }
                    }
                }
                if (momentum != 0) {
                    if (i > 0) {
                        previousWeights2 = copy2d(previousWeights1);
                    }
                    previousWeights1 = copy2d(weights);
                    if (i > 0) {
                        previousBias2 = copy2d(previousBias1);
                    }
                    previousBias1 = copy2d(bias);
                }
            }
            if (n % testStop == 0) {
                //Counting Error Number
                Visan.gm.writeToConsole("n:" + n + ", error: ");
                for (int i = 0; i < objects.size(); i++) {
                    if (classify(objects.get(i).object).classIndex != objects.get(i).objectClass) {
                        error[objects.get(i).objectClass]++;
                    }
                }
                for (int i = 0; i < error.length; i++) {
                    Visan.gm.writeToConsole(error[i] + "    ");
                }
                Visan.gm.writeToConsole("\n");
                int curError = 0;
                for (int v = 0; v < error.length; v++) {
                    curError += error[v];
                }
                Visan.gm.writeToConsole("Total Error: " + curError + ". s: " + s + "\n");
                //testing stop conditions
                if (noImprovementsStop) {
                    if (curError == 0) {
                        Visan.gm.writeToConsole("No improvement stop. Final error: " + curError + "\n");
                        break;
                    } else if (improvementTries > numberOfImprovementTries) {
                        result = prevError;
                        weights = copy2d(prevWeights);
                        bias = copy2d(prevBias);
                        Visan.gm.writeToConsole("No improvement stop. Final error: " + prevError + "\n");
                        break;
                    } else if (prevError <= curError) {
                        improvementTries++;
                    } else {
                        prevError = curError;
                        prevWeights = copy2d(weights);
                        prevBias = copy2d(bias);
                        improvementTries = 0;
                    }
                } else if (useTest) {
                    int er = 0;
                    for (int i = 0; i < tx.size(); i++) {
                        if (classify(tx.get(i).object).classIndex != tx.get(i).objectClass) {
                            er++;
                        }
                    }
                    if (er == 0) {
                        result = er;
                        Visan.gm.writeToConsole("No improvement in testing set stop. Final error in testing set: " + er + "\n");
                        break;
                    } else if (testingImprovementTries > numberOfImprovementTries) {
                        result = tPrevError;
                        weights = copy2d(prevWeightsTesting);
                        bias = copy2d(prevBiasTesting);
                        Visan.gm.writeToConsole("No improvement in testing set stop. Final error in testing set: " + tPrevError + "\n");
                        Visan.gm.writeToConsole("Final error in training set: " + prevError + "\n");
                        break;
                    } else if (tPrevError <= er) {
                        testingImprovementTries++;
                    } else {
                        prevError = curError;
                        tPrevError = er;
                        prevWeightsTesting = copy2d(weights);
                        prevBiasTesting = copy2d(bias);
                        testingImprovementTries = 0;
                    }
                }
            }
            elapsedTime += (System.currentTimeMillis() - st);
            if (n > lc) {
                stop = true;
                Visan.gm.writeToConsole("No more allowed epochs stop.\n");
            } else if (allowedSeconds > 0 && elapsedTime > allowedSeconds) {
                stop = true;
                Visan.gm.writeToConsole("Out of time stop.\n");
            } else if (userStop) {
                stop = true;
                Visan.gm.writeToConsole("Learning stopped by user.\n");
            }
        } while (!stop);
        Visan.gm.writeToConsole("Elapsed Time: " + elapsedTime / 1000 + " seconds\n");
        return result;
    }

    private double[][] copy2d(double[][] array) {
        double[][] copy = new double[array.length][];
        for (int i = 0; i < copy.length; i++) {
            double[] member = new double[array[i].length];
            System.arraycopy(array[i], 0, member, 0, array[i].length);
            copy[i] = member;
        }
        return copy;
    }

    private RealVector[][] copy2d(RealVector[][] array) {
        RealVector[][] copy = new RealVector[array.length][];
        for (int i = 0; i < copy.length; i++) {
            RealVector[] member = new ArrayRealVector[array[i].length];
            System.arraycopy(array[i], 0, member, 0, array[i].length);
            copy[i] = member;
        }
        return copy;
    }

    public void useTestingSet(ArrayList<ObjectAndClass> tx) {
        useTest = true;
        this.tx = tx;
        noImprovementsStop = false;
    }

    public ClassAndValue classify(RealVector x) {
        double[][] neuronInput = new double[netSize.length - 1][];
        neuronLayer[0] = x;
        for (int h = 0; h < neuronInput.length; h++) {
            neuronInput[h] = new double[netSize[h + 1]];
            for (int j = 0; j < neuronInput[h].length; j++) {
                neuronInput[h][j] = bias[h][j] + neuronLayer[h].dotProduct(weights[h][j]);
                neuronLayer[h + 1].setEntry(j, F(neuronInput[h][j]));
            }
        }
        int c = 0;
        double v = 0;
        if (netSize[netSize.length - 1] == 2) {
            v = neuronLayer[neuronLayer.length - 1].getEntry(1) - neuronLayer[neuronLayer.length - 1].getEntry(0);
            if (v > decisionThreshold.value) {
                c = 1;
            }
        } else {
            double max = neuronLayer[neuronLayer.length - 1].getEntry(0);
            for (int h = 1; h < neuronLayer[neuronLayer.length - 1].getDimension(); h++) {
                if (neuronLayer[neuronLayer.length - 1].getEntry(h) > max) {
                    c = h;
                    max = neuronLayer[neuronLayer.length - 1].getEntry(h);
                }
            }
        }
        return new ClassAndValue(c, v);
    }

    double bipolarSigmoid(double x) {
        return 2.0 / (1.0 + Math.exp(-x)) - 1.0;
    }

    double bipolarSigmoidDerivative(double x) {
        double bs = bipolarSigmoid(x);
        return 0.5 * (1 + bs) * (1 - bs);
    }

    double arctan(double x) {
        return tdp * Math.atan(x);
    }

    double arctanDerivative(double x) {
        return tdp * (1 / (1 + x * x));
    }

    double flog(double x) {
        if (x > 0) {
            return Math.log(1 + x);
        } else if (x < 0) {
            return -Math.log(1 - x);
        } else {
            return 0;
        }
    }

    double flogDerivative(double x) {
        if (x > 0) {
            return 1 / (1 + x);
        } else if (x < 0) {
            return 1 / (1 - x);
        } else {
            return 0;
        }
    }

	double F(double x) {
		if (f == 0) {
			return arctan(x);
		} else if (f == 1) {
			return bipolarSigmoid(x);
		} else if (f == 2) {
			return flog(x);
		} else {
			return relu(x);
		}
	}



	double fDerivative(double x) {
		if (f == 0) {
			return arctanDerivative(x);
		} else if (f == 1) {
			return bipolarSigmoidDerivative(x);
		} else if (f == 2) {
			return flogDerivative(x);
		} else {
			return reluDerivative(x);
		}
	}
	private double relu(double x) {
		if (Double.isInfinite(Math.exp(x))) {
			return Math.max(x, 0);
		}
		return Math.log(1 + Math.exp(x));
	}
	private double reluDerivative(double x) {
		if (Double.isInfinite(Math.exp(x))) {
			return 1;
		}
		return Math.exp(x) / (Math.exp(x) + 1);
	}

    private double RandomBetween(double min, double max) {
        return min + Math.random() * (max - min);
    }

    public String toString() {
        String r = "Parametres:";
        r += "Lerning Rate: ";
        for (int i = 0; i < a.length; i++) {
            r += a[i] + " ";
        }
        if (momentum != 0) {
            r += ", Momentum:" + momentum;
        }
        r += ", Decreasion Rate:" + decRate;
        r += ", Min decreasion coefficient:" + minCof;
        r += "\nActivation Function:";
        if (f == 0) {
            r += "Arctan";
        } else if (f == 1) {
            r += "Bipolar Sigmoid";
        } else if (f == 2) {
            r += "Logarithm";
        }
        r += "\nNet Size:\n";
        for (int i = 0; i < netSize.length; i++) {
            r += netSize[i] + " ";
        }
        r += "\nWeights\n";
        for (int i = 0; i < weights.length; i++) {
            r += "Layer " + i + ":\n";
            for (int j = 0; j < weights[i].length; j++) {
                String v = "";
                for (int q = 0; q < weights[i][j].getDimension(); q++) {
                    v += weights[i][j].getEntry(q) + " ";
                }
                r += v + "\n";
            }
        }
        r += "Bias\n";
        for (int i = 0; i < bias.length; i++) {
            r += "Layer " + i + ":\n";
            for (int j = 0; j < bias[i].length; j++) {
                r += bias[i][j] + "\n";
            }
        }
        return r;
    }
    
    void stop() {
        userStop = true;
    }

    public int compareTo(Object o) {
        if (o instanceof BackpropagationNet) {
            BackpropagationNet t = (BackpropagationNet) o;
            if (this.result > t.result) {
                return 1;
            } else if (this.result < t.result) {
                return -1;
            }
        }
        return 0;
    }
    
    
 
	

}
