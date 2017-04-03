package visan.main;

import visan.Visan;
import visan.ml.PCA.PCA;
import visan.common.*;
import visan.ui.GUI;
import visan.visualisation.*;
import visan.statistics.DataStatistics;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GUIManager {
    private GUI mainWindow;
    private String selectionClassName;
    private ArrayList<DataComponent> componentList;
    private Object selectedComponent;
    public IOManager io;
    public VisState s;

    public GUIManager(VisState s) {
        this.s = s;
        componentList = new ArrayList<DataComponent>();
    }

    public void show(IOManager io) {
        this.io = io;
        mainWindow = new GUI(this);
        mainWindow.setTitle("VISAN - - " + s.projectName);
        mainWindow.setVisible(true);
    }

    public void wrongInput() {
        JOptionPane.showMessageDialog(null, "Wrong Input! Try again.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void prepareLists() {
        String classesModel[] = s.trainingSet.getClassesNames();
        mainWindow.setLists(classesModel, s.trainingSet.features.toArray());
    }

    public void setState(VisState s) {
        this.s = s;
        mainWindow.setTitle("VISAN - - " + s.projectName);
        prepareLists();
        mainWindow.transformGroup.clearSelection();
        switch (s.trainingSet.getTransformation()) {
            case 1:
                mainWindow.normalise.setSelected(true);
                break;
            case 2:
                mainWindow.logarithmScale.setSelected(true);
                break;
            case 3:
                mainWindow.sqrt.setSelected(true);
                break;
            case 4:
                mainWindow.reciprocal.setSelected(true);
                break;
        }
        if (s.bpn.noImprovementsStop) {
            mainWindow.BPNNoImprovementStop.setSelected(true);
        } else if (s.bpn.useTestingSetStop) {
            mainWindow.BPNTestingSetStop.setSelected(true);
        } else {
            mainWindow.BPNManualStop.setSelected(true);
        }
        if (s.blackTheme) {
            DataComponent.setBlackTheme();
        }
        if (s.showName) {
            mainWindow.ShowDataName.setSelected(true);
        }
        if (s.da.useP) {
            mainWindow.UsePriorProbability.setSelected(true);
        }
        mainWindow.DFMethodGroup.clearSelection();
        if (s.da.method == 0) {
            mainWindow.LinearMethod.setSelected(true);
        } else {
            mainWindow.QuadraticMethod.setSelected(true);
        }
    }

    public void setTitle(String projectName) {
        mainWindow.setTitle("VISAN - - " + projectName);
    }

    public void reset() {
        componentList = new ArrayList<DataComponent>();
        mainWindow.disposeAllIFrames();
        mainWindow.consoleArea.setText("");
        prepareLists();
    }

    public static String chooseDelimeter() {
        String d = JOptionPane.showInputDialog("Input Delimeter:");
        if (d.trim().length() != 0) {
            d = d.trim();
            d = "\\s*(" + d + "\\s*)|(\\s*\r*\n)";

        } else {
            d = "\\s+";
        }
        return d;
    }

    public void plot(int[] classes, int[] features) {
        if (classes.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one class!");

        } else if (features.length < 2) {
            JOptionPane.showMessageDialog(null, "You need to select two features!");
        } else {
            if (features.length > 3) {
                JOptionPane.showMessageDialog(null, "Features with smalest index will be used.");
            }
            DrawPlot(s.trainingSet, classes, features, true);
        }
    }

    private void DrawPlot(ClassesCollection data, int[] classes, int[] features, boolean select) {
        if (features.length == 2) {
            Plot pc = new Plot(data, classes, features);
            componentList.add(pc);
            mainWindow.createFrame("Plot " + (++s.plotCount), pc);
        } else {
            mainWindow.createFrame("3D Plot " + (++s.plotCount), new plot3D(data, classes, features, select));
        }
    }


    public void AccuracyHistogram(ClassAndValue[] v, int[] classInd, int[] featureInd, String title, int index,
                                  MDouble decisionThreshold) {
        AccuracyHistogram ahc = new AccuracyHistogram(s.trainingSet, v, classInd, featureInd, decisionThreshold);
        ahc.setType("accuracyHistogram");
        ahc.setBins(s.numberOfBins);
        componentList.add(ahc);
        mainWindow.createFrame("Accuracy Histogram (" + title + ")" + index, ahc);
    }

    public void histogram(int[] classes, int[] features) {
        if (classes.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one class!");

        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select one feature!");

        } else {
            if (features.length > 1) {
                JOptionPane.showMessageDialog(null, "The feature with smalest index will be used.");
            }
            DrawHistogram(classes, features);
        }
    }

    private void DrawHistogram(int[] classInd, int[] featureInd) {
        Histogram hc = new Histogram(s.trainingSet, classInd, featureInd);
        hc.setBins(s.numberOfBins);
        componentList.add(hc);
        mainWindow.createFrame("Histogram " + (++s.histogramCount), hc);
    }

    public void trend(int[] classes, int[] features) {
        if (classes.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one class!");

        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");

        } else {
            if (features.length > 1) {
                JOptionPane.showMessageDialog(null, "Feature with smalest index will be used.");
            }
            DrawTrend(classes, features);
        }
    }

    private void DrawTrend(int[] classInd, int[] featureInd) {
        Trend trc = new Trend(s.trainingSet, classInd, featureInd);
        componentList.add(trc);
        mainWindow.createFrame("Trend " + (++s.trendCount), trc);
    }

    public void dataStatistics(int[] classesList, int[] featuresList) {
        JTabbedPane tp = new JTabbedPane();
        for (int i = 0; i < classesList.length; i++) {
            JTable p = DataStatistics.produceSummary(s.trainingSet, classesList[i], featuresList);
            p.setPreferredSize(new Dimension(600, 800));
            JScrollPane sp = new JScrollPane(p);
            JTabbedPane histogramPane = new JTabbedPane();
            try {
                for (int f = 0; f < featuresList.length; f++) {
                    final statisticsHistogram sh = new statisticsHistogram(s.trainingSet, new int[]{classesList[i]},
                            new int[]{featuresList[f]}, DataStatistics.mean[f], DataStatistics.sd[f],
                            DataStatistics.median[f]);
                    JScrollPane jsp = new JScrollPane(sh);
                    histogramPane.add("Feature: " + s.trainingSet.features.get(featuresList[f]), jsp);
                    histogramPane.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            setSelectedComponent(sh);
                        }
                    });
                    if (f == 0 && i == 0) {
                        setSelectedComponent(sh);
                    }
                }
            } catch (Exception ex) {

            }
            // sp.setPreferredSize(new Dimension(0, 800));
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(sp);
            panel.add(Box.createRigidArea(new Dimension(10, 10)));
            panel.add(histogramPane);
            tp.add("Class: " + s.trainingSet.getClassesNames()[classesList[i]], panel);
        }
        mainWindow.createFrame("Data Summary ", tp);
    }

    public void classify(int method, int[] classesList, int[] featuresList) {
        if (classesList.length < 2) {
            JOptionPane.showMessageDialog(null, "You need to select at least two class!");
        } else if (featuresList.length < 1) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");
        } else {
            Scanner scan = null;
            try {
                FileDialog fd = new FileDialog((Frame) null, "Open File For Classification", FileDialog.LOAD);
                fd.setVisible(true);
                if (fd.getFiles().length > 0) {
                    scan = new Scanner(fd.getFiles()[0]);
                    scan.useDelimiter(chooseDelimeter());
                    ArrayList<double[]> a = new ArrayList<double[]>();
                    while (scan.hasNext()) {
                        double[] x = new double[s.trainingSet.features.size()];
                        int inc = 0;
                        for (int i = 0; i < s.trainingSet.features.size(); i++) {
                            x[i] = s.trainingSet.transform(scan.nextDouble(), i);
                        }
                        a.add(x);
                    }
                    if (method == 0) {
                        s.da.classify(a, classesList, featuresList);
                    } else if (method == 1) {
                        s.bpn.classify(a, classesList, featuresList);
                    } else if (method == 2) {
                        s.nn.classify(a, classesList, featuresList);
                    } else if (method == 3) {
                        s.ls.classify(a, classesList, featuresList);
                    } else if (method == 4) {
                        s.sv.classify(a, classesList, featuresList);
                    } else if (method == 5) {
                        s.rfc.classify(a, classesList, featuresList);
                    } else if (method == 6) {
                        s.ab.classify(a, classesList, featuresList);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Cannot read the file!", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (scan != null) {
                    scan.close();
                }
            }
        }
    }


    public void startSelection() {
        if (selectedComponent == null) {
        } else if (DataComponent.class.isAssignableFrom(selectedComponent.getClass())
                && ((DataComponent) selectedComponent).getType().equals("plot")) {
            Plot pc = (Plot) selectedComponent;
            selectionClassName = JOptionPane.showInputDialog("New class name:");
            pc.selectionModeOn();
        } else if (selectedComponent.getClass().equals(plot3D.class)) {
            selectionClassName = JOptionPane.showInputDialog("New class name:");
            ((plot3D) selectedComponent).setSelectionMode(true);
        }
    }

    public void finishSelection() {
        if (selectionClassName != null && selectedComponent != null) {
            if (DataComponent.class.isAssignableFrom(selectedComponent.getClass())
                    && ((DataComponent) selectedComponent).getType().equals("plot")) {
                s.trainingSet.addClass(selectionClassName);
                Plot pc = (Plot) selectedComponent;
                ArrayList<visan.common.Point> p = pc.getSelectedPoints();
                for (int i = 0; i < p.size(); i++) {
                    s.trainingSet.addTrainingExample(s.trainingSet.getSize() - 1,
                            s.trainingSet.trainingObject((int) p.get(i).x, (int) p.get(i).y));
                }
                pc.selectionModeOff();
            } else if (selectedComponent.getClass().equals(plot3D.class)) {
                s.trainingSet.addClass(selectionClassName);
                plot3D p = (plot3D) selectedComponent;
                int ss = s.trainingSet.getTrainingElementsSum(p.classes);
                int c = 0;
                int cc = 0;
                for (int i = 0; i < ss; i++) {
                    if (p.scatter.getHighlighted(i)) {
                        s.trainingSet.addTrainingExample(s.trainingSet.getSize() - 1,
                                s.trainingSet.trainingObject(p.classes[cc], c));
                        p.scatter.setHighlighted(i, false);
                    }
                    c++;
                    if (c > s.trainingSet.getTrainingClassSize(p.classes[cc])) {
                        cc++;
                        c = 0;
                    }
                }
                p.setSelectionMode(false);
            }
            prepareLists();
            mainWindow.repaint();
            someInitialisation();
        }
    }

    public void setBins(int n) {
        s.numberOfBins = n;
        for (int i = 0; i < componentList.size(); i++) {
            if (componentList.get(i).getType().equals("histogram")) {
                Histogram hc = (Histogram) componentList.get(i);
                hc.setBins(s.numberOfBins);
            }
            if (componentList.get(i).getType().contains("accuracyHistogram")) {
                AccuracyHistogram ahc = (AccuracyHistogram) componentList.get(i);
                ahc.setBins(s.numberOfBins);
            }
        }
    }

    public void setBins() {
        try {
            s.numberOfBins = Integer.parseInt(JOptionPane.showInputDialog("Number:"));
            setBins(s.numberOfBins);
        } catch (Exception e) {
        }
    }

    public void help() {
        try {
            File pdfFile = new File("help.pdf");
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "See file help.pdf for detailed help.");
            }

        } catch (Exception ex) {
        }
    }

    public void pointSizeDec() {
        if (selectedComponent != null) {
            if (DataComponent.class.isAssignableFrom(selectedComponent.getClass())) {
                ((DataComponent) selectedComponent).dotSizeDec();
                ((DataComponent) selectedComponent).repaint();
            } else if (selectedComponent.getClass().equals(plot3D.class)) {
                ((plot3D) selectedComponent).pointSizeDec();

            }
        }
    }

    public void pointSizeInc() {
        if (selectedComponent != null) {
            if (DataComponent.class.isAssignableFrom(selectedComponent.getClass())) {
                ((DataComponent) selectedComponent).dotSizeInc();
                ((DataComponent) selectedComponent).repaint();
            } else if (selectedComponent.getClass().equals(plot3D.class)) {
                ((plot3D) selectedComponent).pointSizeInc();
            }
        }
    }

    public void zoomOut() {
        if (selectedComponent != null) {
            if (DataComponent.class.isAssignableFrom(selectedComponent.getClass())) {
                ((DataComponent) selectedComponent).scaleDecrease();
                ((DataComponent) selectedComponent).repaint();
            }
        }
    }

    public void zoomIn() {
        if (selectedComponent != null) {
            if (DataComponent.class.isAssignableFrom(selectedComponent.getClass())) {
                ((DataComponent) selectedComponent).scaleIncrease();
                ((DataComponent) selectedComponent).repaint();
            }
        }
    }


    public void setBarHistogram(boolean b) {
        Histogram.setShape(b);
        AccuracyHistogram.setShape(b);
    }

    public void removeFromComponentList(DataComponent c) {
        componentList.remove(c);
    }

    public void setSelectedComponent(Object c) {
        selectedComponent = c;
    }

    public void writeToConsole(String text) {
        if (s.consoleEnabled) {
            mainWindow.writeToConsole(text);
        }
    }

    public void setConsole(boolean b) {
        s.consoleEnabled = b;
    }

    public void setROCcurve(boolean b) {
        AccuracyHistogram.setROC(b);
    }

    public void setDAProbability(boolean b) {
        s.da.useP = b;
    }

    public void setDFMethod(int i) {
        s.da.method = i;
    }

    public void MultiClassHistogram(int[][] v, int[] classes, int[] features, String t, int index) {
        MultiClassHistogram mch = new MultiClassHistogram(s.trainingSet, classes, features, v);
        mch.setType("mchistogram");
        mch.setShape(s.multiClassShape);
        componentList.add(mch);
        mainWindow.createFrame("Multi Class Histogram (" + t + ") " + index, mch);
    }

    public void setMultiClassShape(boolean b) {
        s.multiClassShape = b;
        for (int i = 0; i < componentList.size(); i++) {
            if (componentList.get(i).getType().equals("mchistogram")) {
                MultiClassHistogram mhc = (MultiClassHistogram) componentList.get(i);
                mhc.setShape(b);
                mhc.repaint();
            }
        }
    }

    public void updateAccuracyHistograms() {
        for (int i = 0; i < componentList.size(); i++) {
            if (componentList.get(i).getType().equals("accuracyHistogram")) {
                AccuracyHistogram ac = (AccuracyHistogram) componentList.get(i);
                ac.updateSpecificity();
                ac.repaint();
            }
        }
    }

    public void findPC(int[] classesList, int[] featuresList) {
        s.pca = new PCA(s.trainingSet, classesList, featuresList);
    }

    public void draw2DPCAGraph() {
        if (s.pca == null) {
            JOptionPane.showMessageDialog(null, "Find Principal Components First");
            return;
        }
        ClassesCollection c = s.pca.graph(2);
        if (c != null) {
            DrawPlot(c, s.pca.getClassesList(), new int[]{0, 1}, false);
        }
    }

    public void draw3DPCAGraph() {
        if (s.pca == null) {
            JOptionPane.showMessageDialog(null, "Find Principal Components First");
            return;
        }
        ClassesCollection c = s.pca.graph(3);
        if (c != null) {
            DrawPlot(c, s.pca.getClassesList(), new int[]{0, 1, 2}, false);
        }
    }

    public void eigenvalueHistogram() {
        if (s.pca == null) {
            JOptionPane.showMessageDialog(null, "Find Principal Components First");
            return;
        }
        double[][] a = new double[1][];
        a[0] = s.pca.getEigenvalues();
        PieChart hc = new PieChart(a);
        componentList.add(hc);
        mainWindow.createFrame("Eigenvalues" + (++s.histogramCount), hc);
    }

    public void dataSummary(int[] classesList, int[] featuresList) {
        int sum = 0;
        for (int i = 0; i < classesList.length; i++) {
            writeToConsole("Class " + s.trainingSet.getClassesNames()[classesList[i]] + " size: "
                    + s.trainingSet.getTrainingClassSize(classesList[i]));
            sum += s.trainingSet.getTrainingClassSize(classesList[i]);
            writeToConsole("\n");
        }
        writeToConsole("Total Objects: " + sum);
        if (s.origFileName.length() != 0) {
            writeToConsole("\n");
            writeToConsole("Original Data File Name: " + s.origFileName);
            writeToConsole("\n");
        }
        writeToConsole("\n");
    }

    public void setShowDataName(boolean b) {
        s.showName = b;
    }

    public void lockBPNOptions() {
        mainWindow.lockBPNOptions();
    }

    public void transformData() {
        s.trainingSet.permanentTransform();
    }

    public void blockSVM(boolean b) {
        mainWindow.lockSVM();
    }

    public void chooseNegativeClass(String cn) {
        try {
            int a;
            if (cn != null) {
                a = s.trainingSet.containsClass(cn);
                s.negativeClass = cn;
            } else {
                String[] choices = s.trainingSet.getClassesNames();
                if (Arrays.asList(choices).contains("-1")) {
                    s.negativeClass = "-1";
                    a = s.trainingSet.containsClass(s.negativeClass);
                }
                s.negativeClass = (String) JOptionPane.showInputDialog(null, "Choose Negative Class:", "",
                        JOptionPane.QUESTION_MESSAGE, null, choices, choices[1]);
                a = s.trainingSet.containsClass(s.negativeClass);
            }
            s.trainingSet.replace(0, a);
            prepareLists();
        } catch (Exception e) {
            s.negativeClass = "";
            wrongInput();
        }
    }

    public  void someInitialisation() {
        s.ls.setNewTrainingSet(s.trainingSet);
        s.da.setTrainingSet(s.trainingSet);
        s.sv.setNewTrainingSet(s.trainingSet);
        s.bpn.setTrainingSet(s.trainingSet);
        s.nn.setNewTrainingSet(s.trainingSet);
        s.rfc.setTrainingSet(s.trainingSet);
        s.ab.setTrainingSet(s.trainingSet);
        mainWindow.transformGroup.clearSelection();
    }

    public void setTransformation(int i, int[] classes, int[] features) {
        s.trainingSet.setTransformation(i, classes, features);
    }

    public void changeClassName(int[] classesList) {
        String name = JOptionPane.showInputDialog("New Class Name:");
        s.trainingSet.changeClassName(classesList[0], name);
        prepareLists();
    }

    public void changeFeatureName(int[] featuresList) {
        String name = JOptionPane.showInputDialog("New Feature Name:");
        s.trainingSet.changeFeatureName(featuresList[0], name);
        prepareLists();
    }

    public void performTest(int[] classesList, int[] featuresList) {
        visan.statistics.Tests.performTest(s.trainingSet, classesList, featuresList);
    }

    public void addFeature() {
        try {
            String name = JOptionPane.showInputDialog("New feature name:");
            s.trainingSet.features.add(name);
            s.trainingSet.addFeature();
            prepareLists();
            someInitialisation();
        } catch (Exception ex) {
        }
    }

    public void addClass() {
        try {
            String name = JOptionPane.showInputDialog("Enter New Class Name:");
            s.trainingSet.addClass(name);
            prepareLists();
            someInitialisation();
        } catch (Exception ex) {
        }
    }

    public void removeClass(int index) {
        try {
            mainWindow.disposeAllIFrames();
            s.trainingSet.removeClass(index);
            prepareLists();
            someInitialisation();
        } catch (Exception ex) {
        }
    }

    public void removeFeature(int index) {
        try {
            mainWindow.disposeAllIFrames();
            s.trainingSet.features.remove(index);
            s.trainingSet.removeFeature(index);
            prepareLists();
            someInitialisation();
        } catch (Exception ex) {
        }
    }

    public void takeSnapshot() {
        if(DataComponent.class.isAssignableFrom(selectedComponent.getClass())) {
            io.takeSnapshot((JComponent) selectedComponent);
        }
    }
}
