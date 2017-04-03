package visan.statistics;

import visan.common.ClassAndValue;
import visan.common.ClassesCollection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import javax.swing.JOptionPane;
import visan.Visan;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.Distribution;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.distribution.TDistributionImpl;

/**
 *
 * @author Ramzan
 */
public class Tests {

    private static NumberFormat formatter = new DecimalFormat("###.#####");

    public static void performTest(ClassesCollection trainingSet, int[] classesList, int[] featuresList) {
        Integer[] whichTest = StatQuestions.showDialog();
        if (whichTest == null) {
            return;
        }
        try {
            if (whichTest[0] == 0) {
                if (whichTest[1] == 0) {
                    JOptionPane.showMessageDialog(null, "Mann-Whitney U-test will be performed.");
                    utest(trainingSet, classesList, featuresList);
                } else if (whichTest[1] == 1) {
                    JOptionPane.showMessageDialog(null, "Unpaired t test will be performed.");
                    tTestUnpairedEqualVariance(trainingSet, classesList, featuresList);
                } else if (whichTest[1] == 2) {
                    JOptionPane.showMessageDialog(null, "Welch's t test will be performed.");
                    welchTest(trainingSet, classesList, featuresList);
                }
            } else {
                if (whichTest[1] == 0) {
                    JOptionPane.showMessageDialog(null, "Wilcoxon test will be performed.");
                     wilcoxonTest(trainingSet, classesList, featuresList);
                } else if (whichTest[1] == 1) {
                    JOptionPane.showMessageDialog(null, "Paired t test will be performed.");
                    tTestPaired(trainingSet, classesList, featuresList);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Test cannot be performed.");
        }
    }
 public static void wilcoxonTest(ClassesCollection trainingSet, int[] classesList, int[] featuresList) throws MathException {

 }
    public static void tTestPaired(ClassesCollection trainingSet, int[] classesList, int[] featuresList) throws MathException {
        int n = trainingSet.getTrainingClassSize(classesList[0]);
        double d[] = new double[n];
        double dmean = 0;
        for (int i = 0; i < n; i++) {
            d[i] = trainingSet.trainingObject(1, i)[featuresList[0]] - trainingSet.trainingObject(0, i)[featuresList[0]];
            dmean += d[i];
        }
        dmean /= n;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += Math.pow(d[i] - dmean, 2);
        }
        double sd = Math.sqrt(sum / (n - 1));
        double sed = sd / Math.sqrt(n);
        double t = dmean / sed;
        Distribution td = new TDistributionImpl(n - 1);
        Visan.gm.writeToConsole("t value:  " + formatter.format(t) + "\n");
        Visan.gm.writeToConsole("Two-tailed P-value:  " + formatter.format(2 * (td.cumulativeProbability(-Math.abs(t)))) + "\n");
    }

    public static void welchTest(ClassesCollection trainingSet, int[] classesList, int[] featuresList) throws MathException {
        double n1 = trainingSet.getTrainingClassSize(classesList[0]);
        double n2 = trainingSet.getTrainingClassSize(classesList[1]);
        double mean[] = new double[2];
        for (int c = 0; c < 2; c++) {
            for (int i = 0; i < trainingSet.getTrainingClassSize(classesList[c]); i++) {
                mean[c] += trainingSet.trainingObject(classesList[c], i)[featuresList[0]];
            }
        }
        for (int c = 0; c < 2; c++) {
            mean[c] /= trainingSet.getTrainingClassSize(classesList[c]);
        }
        double sd[] = new double[2];
        for (int c = 0; c < 2; c++) {
            double sum = 0;
            for (int i = 0; i < trainingSet.getTrainingClassSize(classesList[c]); i++) {
                sum += square(trainingSet.trainingObject(classesList[c], i)[featuresList[0]] - mean[c]);
            }
            sd[c] = sqrt(sum / (trainingSet.getTrainingClassSize(classesList[c]) - 1));
        }
        double unbiasedEstimator = Math.sqrt(sd[0] * sd[0] / n1 + sd[1] * sd[1] / n2);
        double t = (mean[0] - mean[1]) / unbiasedEstimator;
        double df = square(square(sd[0]) / n1 + square(sd[1]) / n2) / (square(square(sd[0]) / n1) / (n1 - 1) + square(square(sd[1]) / n2) / (n2 - 1));
        Distribution td = new TDistributionImpl(df);
        Visan.gm.writeToConsole("t value:  " + formatter.format(t) + "\n");
        Visan.gm.writeToConsole("Two-tailed P-value:  " + formatter.format(2 * (td.cumulativeProbability(-Math.abs(t)))) + "\n");
    }

    public static void tTestUnpairedUnequalVariance(ClassesCollection trainingSet, int[] classesList, int[] featuresList) throws MathException {
        double n1 = trainingSet.getTrainingClassSize(classesList[0]);
        double n2 = trainingSet.getTrainingClassSize(classesList[1]);
        double mean[] = new double[2];
        for (int c = 0; c < 2; c++) {
            for (int i = 0; i < trainingSet.getTrainingClassSize(classesList[c]); i++) {
                mean[c] += trainingSet.trainingObject(classesList[c], i)[featuresList[0]];
            }
        }
        for (int c = 0; c < 2; c++) {
            mean[c] /= trainingSet.getTrainingClassSize(classesList[c]);
        }
        double sd[] = new double[2];
        for (int c = 0; c < 2; c++) {
            double sum = 0;
            for (int i = 0; i < trainingSet.getTrainingClassSize(classesList[c]); i++) {
                sum += Math.pow(trainingSet.trainingObject(classesList[c], i)[featuresList[0]] - mean[c], 2);
            }
            sd[c] = Math.sqrt(sum / (trainingSet.getTrainingClassSize(classesList[c]) - 1));
        }
        double estimator = Math.sqrt(square(sd[0]) / n1 + square(sd[1]) / n2);
        double t = abs((mean[0] - mean[1]) / estimator);
        NormalDistribution nd = new NormalDistributionImpl(0, 1);
        Visan.gm.writeToConsole("t value:  " + formatter.format(t) + "\n");
        Visan.gm.writeToConsole("Two-tailed P-value:  " + formatter.format(2 * nd.cumulativeProbability(-t)) + "\n");
    }

    public static void tTestUnpairedEqualVariance(ClassesCollection trainingSet, int[] classesList, int[] featuresList) throws MathException {
        double n1 = trainingSet.getTrainingClassSize(classesList[0]);
        double n2 = trainingSet.getTrainingClassSize(classesList[1]);
        double mean[] = new double[2];
        for (int c = 0; c < 2; c++) {
            for (int i = 0; i < trainingSet.getTrainingClassSize(classesList[c]); i++) {
                mean[c] += trainingSet.trainingObject(classesList[c], i)[featuresList[0]];
            }
        }
        for (int c = 0; c < 2; c++) {
            mean[c] /= trainingSet.getTrainingClassSize(classesList[c]);
        }
        double sd[] = new double[2];
        for (int c = 0; c < 2; c++) {
            double sum = 0;
            for (int i = 0; i < trainingSet.getTrainingClassSize(classesList[c]); i++) {
                sum += square(trainingSet.trainingObject(classesList[c], i)[featuresList[0]] - mean[c]);
            }
            sd[c] = sqrt(sum / (trainingSet.getTrainingClassSize(classesList[c]) - 1));
        }
        double estimator = sqrt(0.5 * (sd[0] * sd[0] + sd[1] * sd[1]));
        double t = abs((mean[0] - mean[1]) / (estimator * Math.sqrt(1 / n1 + 1 / n2)));
        double df = square(square(sd[0]) / n1 + square(sd[1]) / n2) / (square(square(sd[0]) / n1) / (n1 - 1) + square(square(sd[1]) / n2) / (n2 - 1));
        Distribution td = new TDistributionImpl(df);
        Visan.gm.writeToConsole("t value:  " + formatter.format(t) + "\n");
        Visan.gm.writeToConsole("Two-tailed P-value:  " + formatter.format(2 * (td.cumulativeProbability(-t))) + "\n");
    }

    public static void utest(ClassesCollection trainingSet, int[] classesList, int[] featuresList) throws MathException {
        int n1 = trainingSet.getTrainingClassSize(classesList[0]);
        int n2 = trainingSet.getTrainingClassSize(classesList[1]);
        ClassAndValue a[] = new ClassAndValue[n1 + n2];
        for (int i = 0; i < n1; i++) {
            a[i] = new ClassAndValue(0, trainingSet.trainingObject(classesList[0], i)[featuresList[0]]);
        }
        for (int i = 0; i < n2; i++) {
            a[n1 + i] = new ClassAndValue(1, trainingSet.trainingObject(classesList[1], i)[featuresList[0]]);
        }
        Arrays.sort(a);
        double R1 = 0;
        for (int i = 0; i < a.length;) {
            int j = 1;
            while (i + j < a.length && a[i].value == a[i + j].value) {
                j++;
            }
            if (j == 1) {
                a[i].value = i + 1;
            } else {
                for (int t = 0; t < j; t++) {
                    a[i + t].value = i + 1 + (double) (j - 1) / 2;
                }
            }
            i += j;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i].classIndex == 0) {
                R1 += a[i].value;
            }
        }
        double U = n1 * n2 + (n1 * (n1 + 1) / 2) - R1;
        Visan.gm.writeToConsole("U: " + formatter.format(U) + "\n");
        //wrong ties
        double m = (n1 * n2) / 2;
        double sd = Math.sqrt((double) (n1 * n2 * (n1 + n2 + 1)) / 12);
        double z = (U - m) / sd;
        double lp = pzscore(Math.abs(z));
        double rp = 1 - lp;
        double tp = 2 * rp;
        //NormalDistribution nd = new NormalDistributionImpl(m, sd);
        //nd.cumulativeProbability(-Math.abs(z));
        Visan.gm.writeToConsole("Two-tailed P-value:  " + tp + "\n");
    }

    static double pzscore(double z) {
        double y, x, w;
        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y > (6 * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                        - 0.001075204047) * w + 0.005198775019) * w
                        - 0.019198292004) * w + 0.059054035642) * w
                        - 0.151968751364) * w + 0.319152932694) * w
                        - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                        + 0.000152529290) * y - 0.000019538132) * y
                        - 0.000676904986) * y + 0.001390604284) * y
                        - 0.000794620820) * y - 0.002034254874) * y
                        + 0.006549791214) * y - 0.010557625006) * y
                        + 0.011630447319) * y - 0.009279453341) * y
                        + 0.005353579108) * y - 0.002141268741) * y
                        + 0.000535310849) * y + 0.999936657524;
            }
        }
        return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
    }

    private static double sqrt(double d) {
        return Math.sqrt(d);
    }

    private static double square(double d) {
        return d * d;
    }

    private static double abs(double d) {
        return Math.abs(d);
    }
}
