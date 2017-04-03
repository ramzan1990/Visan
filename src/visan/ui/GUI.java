package visan.ui;

import visan.main.GUIManager;
import visan.Visan;
import visan.ml.Ensemble.AdaBoost;
import visan.ml.Ensemble.RandomForest;
import visan.common.CustomTitledBorder;
import visan.visualisation.DataComponent;
import visan.visualisation.plot3D;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class GUI extends VFrame {

    private static final int panelMargin = 6;
    private JMenu File, HelpM, Options, NumberOfBins, ColorTheme, Data, Window, Draw, DAMenu, NeuralNet, RFC, BPNOptions, SVMOptions,
            DAOptionsMenu, MultiClassVisualization, NearestNeighbour, LeastSquares, Classification, NNOptions, PCA, Miscellaneous,
            SVM, HistogramOptions, Statistics, LSOptions, RFCOptions, AB, ABOptions;
    private JMenuItem ExportData, DAExport, ABBranchingFactor, ABNumTrees, ABMaxDepth, ABClassify, ABHistogram, ABTestHistogram, ABTrain, RFCMaxDepth, ClearConsole, RFCNumThreads, RFCNumTrees, RFCBranchingFactor, RFCSampleSize, RFCNumFeatures, RFCClassify, WhiteTheme, BlackTheme, PerformTest, EigenvalueHistogram, FindPC, PC2DGraph, PC3DGraph, SetBins, ChooseNegative, Exit, LoadLearningSet,
            Save, Help, About, AddClass, AddFeature, RemoveFeature, RemoveClass, LoadData, LoadTestingSet, NewProject, SaveAs, OpenProject, Tile, LSSetA,
            Cascade, DrawPlot, DrawHistogram, DrawTrend, DescriptiveStatistics, DataSummary, DFClassify, DAAnalyse, SignificanceAnalysis,
            DrawDFHistogram, DrawDFTestHistogram, startSelection, finishSelection, BPNLearn, BPNSetMomentum, BPNSetDecRate, BPNSetMinCoef, BPNSetLerningRate,
            BPNSetFunc, BPNSetTest, BPNSetAllowedTime, BPNSetAllowedEpochs, BPNSetNumberOfLearns, BPNSetHiddenLayers, NNChooseN, LSTestHistogram,
            BPNDrawAccuracyHistogram, BPNDrawTestAccuracyHistogram, BPNExport, BPNImport, BPNStop, classifyBPN, NNTestAccuracyHistogram, LSFindParameters, LSClassify,
            LSHistogram, classifyNN, SVMLearn, SVMClassify, SVMDrawAccuracyHistogram, SVMDrawTestAccuracyHistogram, changeClassName,
            changeFeatureName, BPNSetNumberOfImprovementTries, LSKernel, NNKernel, SVMSetC, SVMSetThreshold, SVMSetTolerance, SVMSetToleranceFeas, RFGrowTrees, RFTrainingSet, RFTestingSet;
    private JCheckBoxMenuItem ROCCurve, BarHistogramType, HideDataPanel, HideTools, HideConsole, LSDualForm, NNDualForm;
    public JCheckBoxMenuItem UsePriorProbability;
    private JRadioButtonMenuItem Bin10, Bin50, Bin100, Bin500, Bin1000, PieChart, Histogram;
    public JRadioButton normalise, logarithmScale, sqrt, reciprocal;
    public ButtonGroup transformGroup;
    public ButtonGroup binNumberGroup, DFMethodGroup, MultiShape, BPNStopping;
    private JPanel dataSelectPanel, consolePanel, dataTransformPanel;
    private JPanel dataPanel;
    private JToolBar toolsPanel;
    private JDesktopPane workSpacePanel;
    private JMenuBar menu;
    public JTextArea consoleArea;
    private JScrollPane consoleScroll;
    public JList featureList, classList;
    private JButton selectAllFeatures, selectAllClasses, selectAll, clearSelection, ApplyTransformation;
    private JLabel zoomIn, zoomOut, pointSizeInc, pointSizeDec, snapShotLabel, newProject, openProject, saveProject, loadDataLabel;
    private JScrollPane classPane, featuresPane;
    public JRadioButtonMenuItem BPNTestingSetStop, BPNNoImprovementStop, BPNManualStop, LinearMethod, QuadraticMethod;
    public JCheckBoxMenuItem ShowDataName;
    private GUIManager manager;

    public GUI(GUIManager manager) {
        this.manager = manager;
        // <editor-fold defaultstate="collapsed" desc="menu">
        ABOptions = new JMenu("AdaBoost");
        ABMaxDepth = new JMenuItem("Tree max depth");
        ABNumTrees = new JMenuItem("Number of trees");
        ABBranchingFactor = new JMenuItem("Branching Factor");
        RFCBranchingFactor = new JMenuItem("Branching Factor");
        RFCMaxDepth = new JMenuItem("Tree max depth");
        ClearConsole = new JMenuItem("Clear console");
        RFCClassify = new JMenuItem("Classify");
        ColorTheme = new JMenu("Color Theme");
        BlackTheme = new JMenuItem("Black Theme");
        WhiteTheme = new JMenuItem("White Theme");
        RFGrowTrees = new JMenuItem("Grow Trees");
        RFTrainingSet = new JMenuItem("Training Set");
        RFTestingSet = new JMenuItem("Testing Set");
        NNDualForm = new JCheckBoxMenuItem("Dual Form");
        LSDualForm = new JCheckBoxMenuItem("Dual Form");
        LSSetA = new JMenuItem("Set parameter a");
        BPNSetNumberOfLearns = new JMenuItem("Set Number Of Learns");
        changeClassName = new JMenuItem("Change Class Name");
        changeFeatureName = new JMenuItem("Change Feature Name");
        ROCCurve = new JCheckBoxMenuItem("ROC Curve");
        HideDataPanel = new JCheckBoxMenuItem("Data Panel");
        BarHistogramType = new JCheckBoxMenuItem("Bar Histogram");
        HideTools = new JCheckBoxMenuItem("Tools Panel");
        HideConsole = new JCheckBoxMenuItem("Console");
        PCA = new JMenu("PCA");
        PieChart = new JRadioButtonMenuItem("Pie Chart");
        Histogram = new JRadioButtonMenuItem("Histogram");
        menu = new JMenuBar();
        File = new JMenu("File");
        NNOptions = new JMenu("Nearest Neighbour");
        NearestNeighbour = new JMenu("Nearest Neighbour");
        LeastSquares = new JMenu("Least Squares");
        Classification = new JMenu("Classification");
        AB = new JMenu("AdaBoost");
        ABClassify = new JMenuItem("Classify");
        ABHistogram = new JMenuItem("Accuracy Histogram");
        ABTestHistogram = new JMenuItem("Test Histogram");
        ABTrain = new JMenuItem("Train");
        SVMClassify = new JMenuItem("Classify");
        HelpM = new JMenu("Help");
        Options = new JMenu("Options");
        SVMLearn = new JMenuItem("Learn");
        SVMDrawAccuracyHistogram = new JMenuItem("Accuracy Histogram");
        SVMDrawTestAccuracyHistogram = new JMenuItem("Test Accuracy Histogram");
        SVM = new JMenu("SVM");
        DAOptionsMenu = new JMenu("DA Options");
        LSOptions = new JMenu("LS Options");
        MultiClassVisualization = new JMenu("Multi Class Visualization");
        NumberOfBins = new JMenu("Number Of Bins");
        HistogramOptions = new JMenu("Histogram Options");
        Statistics = new JMenu("Statistics");
        Data = new JMenu("Data");
        Window = new JMenu("Window");
        Draw = new JMenu("Draw");
        DAMenu = new JMenu("Discriminant Analysis");
        NeuralNet = new JMenu("Neural Net");
        RFC = new JMenu("Random Forest");
        BPNStop = new JMenuItem("Stop Learning");
        BPNOptions = new JMenu("Neural Net");
        SVMOptions = new JMenu("SVM");
        Miscellaneous = new JMenu("Miscellaneous");
        SetBins = new JMenuItem("Set Bins");
        ExportData = new JMenuItem("Export Data");
        DFClassify = new JMenuItem("Classify");
        EigenvalueHistogram = new JMenuItem("Eigenvalue Histogram");
        DAAnalyse = new JMenuItem("Analyse");
        PerformTest = new JMenuItem("Perform Test");
        SVMSetC = new JMenuItem("Set C");
        SVMSetThreshold = new JMenuItem("Set threshold");
        SVMSetToleranceFeas = new JMenuItem("Set tolerance feasibility");
        SVMSetTolerance = new JMenuItem("Set tolerance");
        SignificanceAnalysis = new JMenuItem("Significance Analysis");
        DAExport = new JMenuItem("Export");
        DrawDFHistogram = new JMenuItem("Histogram");
        BPNDrawAccuracyHistogram = new JMenuItem("Histogram");
        DrawDFTestHistogram = new JMenuItem("Test Histogram");
        BPNDrawTestAccuracyHistogram = new JMenuItem("Test Histogram");
        BPNExport = new JMenuItem("Export");
        BPNImport = new JMenuItem("Import");
        FindPC = new JMenuItem("Find Principal Components");
        PC2DGraph = new JMenuItem("Draw 2D Graph");
        PC3DGraph = new JMenuItem("Draw 3D Graph");
        ShowDataName = new JCheckBoxMenuItem("Show Data Name");
        Exit = new JMenuItem("Exit");
        LoadLearningSet = new JMenuItem("Load Learning Set");
        Save = new JMenuItem("Save");
        Help = new JMenuItem("Help");
        About = new JMenuItem("About");
        AddClass = new JMenuItem("Add Class");
        AddFeature = new JMenuItem("Add Feature");
        RemoveFeature = new JMenuItem("Remove Feature");
        RemoveClass = new JMenuItem("Remove Class");
        LoadData = new JMenuItem("Load Data");
        LoadTestingSet = new JMenuItem("Load Testing Set");
        NewProject = new JMenuItem("New Project");
        SaveAs = new JMenuItem("Save As");
        OpenProject = new JMenuItem("Open Project");
        Tile = new JMenuItem("Tile");
        Cascade = new JMenuItem("Cascade");
        DrawPlot = new JMenuItem("Draw Plot");
        DrawHistogram = new JMenuItem("Draw Histogram");
        LSKernel = new JMenuItem("Choose Kernel");
        NNKernel = new JMenuItem("Choose Kernel");
        DrawTrend = new JMenuItem("Draw Trend");
        DescriptiveStatistics = new JMenuItem("Descriptive Statistics");
        DataSummary = new JMenuItem("Data Summary");
        startSelection = new JMenuItem("Start Selection");
        finishSelection = new JMenuItem("Finish");
        LinearMethod = new JRadioButtonMenuItem("Linear");
        QuadraticMethod = new JRadioButtonMenuItem("Quadratic");
        BPNLearn = new JMenuItem("Learn");
        NNChooseN = new JMenuItem("Choose N");
        BPNSetMomentum = new JMenuItem("Set Momentum");
        BPNSetDecRate = new JMenuItem("Set Decrease Rate");
        BPNSetMinCoef = new JMenuItem("Set Min Lerning Rate Coefficient");
        BPNSetLerningRate = new JMenuItem("Set Lerning Rate");
        BPNSetFunc = new JMenuItem("Set Activation Function");
        BPNSetTest = new JMenuItem("Test Every N epochs");
        BPNSetAllowedTime = new JMenuItem("Set Allowed Time");
        BPNSetAllowedEpochs = new JMenuItem("Set Allowed Epochs");
        BPNSetNumberOfImprovementTries = new JMenuItem("Set Number Of Improvement Tries");
        BPNNoImprovementStop = new JRadioButtonMenuItem("No Improvements Stop");
        BPNManualStop = new JRadioButtonMenuItem("Manual Stop");
        BPNSetHiddenLayers = new JMenuItem("Set Number Of Hidden Layers");
        BPNTestingSetStop = new JRadioButtonMenuItem("Testing Set Stop");
        ChooseNegative = new JMenuItem("Choose Negative Class");
        UsePriorProbability = new JCheckBoxMenuItem("Use Prior Probability");
        LSFindParameters = new JMenuItem("Find Parameters");
        LSClassify = new JMenuItem("Classify");
        LSHistogram = new JMenuItem("Histogram");
        LSTestHistogram = new JMenuItem("Test Histogram");
        classifyBPN = new JMenuItem("Classify");
        classifyNN = new JMenuItem("classify");
        NNTestAccuracyHistogram = new JMenuItem("test histogram");
        RFCNumThreads = new JMenuItem("Number of threads");
        RFCNumTrees = new JMenuItem("Number of trees");
        RFCSampleSize = new JMenuItem("Sample Size");
        RFCNumFeatures = new JMenuItem("Number of features");
        RFCOptions = new JMenu("RFC Options");
        LeastSquares.add(LSFindParameters);
        LSFindParameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.ls.startFindingParameters(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        LeastSquares.add(LSClassify);
        LSClassify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.classify(3, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        LeastSquares.add(LSHistogram);
        LSHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.ls.DrawAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        LeastSquares.add(LSTestHistogram);
        LSTestHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.ls.DrawTestAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        ChooseNegative.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.chooseNegativeClass(null);
            }
        });
        MultiShape = new ButtonGroup();
        MultiShape.add(PieChart);
        Histogram.setSelected(true);
        MultiShape.add(Histogram);
        DFMethodGroup = new ButtonGroup();
        DFMethodGroup.add(LinearMethod);
        LinearMethod.setSelected(true);
        DFMethodGroup.add(QuadraticMethod);
        NewProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.newProject();
            }
        });
        Bin10 = new JRadioButtonMenuItem("10");
        Bin50 = new JRadioButtonMenuItem("50");
        Bin100 = new JRadioButtonMenuItem("100");
        Bin500 = new JRadioButtonMenuItem("500");
        Bin1000 = new JRadioButtonMenuItem("1000");
        File.add(NewProject);
        File.add(OpenProject);
        File.addSeparator();
        File.add(Save);
        File.add(SaveAs);
        File.addSeparator();
        File.add(Exit);
        OpenProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.openProject();
            }
        });
        SaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.saveProjectAs();
            }
        });
        Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });
        LoadLearningSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.readFile();
            }
        });
        Save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.saveProject();
            }
        });
        ROCCurve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (ROCCurve.isSelected()) {
                    manager.setROCcurve(true);
                } else {
                    manager.setROCcurve(false);
                }
                workSpacePanel.repaint();

            }
        });
        Options.add(HistogramOptions);
        BarHistogramType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (BarHistogramType.isSelected()) {
                    manager.setBarHistogram(true);
                } else {
                    manager.setBarHistogram(false);
                }
                workSpacePanel.repaint();
            }
        });

        HistogramOptions.add(NumberOfBins);
        HistogramOptions.add(ColorTheme);
        HistogramOptions.add(MultiClassVisualization);
        HistogramOptions.add(BarHistogramType);
        HistogramOptions.add(ROCCurve);
        ColorTheme.add(BlackTheme);
        BlackTheme.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DataComponent.setBlackTheme();
                manager.s.blackTheme = true;
                workSpacePanel.repaint();
            }
        });
        ColorTheme.add(WhiteTheme);
        WhiteTheme.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DataComponent.setWhiteTheme();
                manager.s.blackTheme = false;
                workSpacePanel.repaint();
            }
        });
        NumberOfBins.add(Bin10);
        NumberOfBins.add(Bin50);
        NumberOfBins.add(Bin100);
        NumberOfBins.add(Bin500);
        NumberOfBins.add(Bin1000);
        NumberOfBins.addSeparator();
        NumberOfBins.add(SetBins);
        SetBins.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBins();
            }
        });
        binNumberGroup = new ButtonGroup();
        binNumberGroup.add(Bin10);
        binNumberGroup.add(Bin50);
        binNumberGroup.add(Bin100);
        binNumberGroup.add(Bin500);
        binNumberGroup.add(Bin1000);
        Bin50.setSelected(true);

        Bin10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBins(10);
            }
        });
        Bin50.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBins(50);
            }
        });
        Bin100.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBins(100);
            }
        });
        Bin500.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBins(500);
            }
        });
        Bin1000.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBins(1000);
            }
        });
        changeFeatureName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.changeFeatureName(featureList.getSelectedIndices());

            }
        });
        changeClassName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.changeClassName(classList.getSelectedIndices());
            }
        });
        Data.add(LoadLearningSet);
        Data.add(LoadTestingSet);
        Data.addSeparator();
        Data.add(AddClass);
        Data.add(AddFeature);
        Data.add(LoadData);
        Data.addSeparator();
        Data.add(changeFeatureName);
        Data.add(changeClassName);
        Data.addSeparator();
        Data.add(RemoveClass);
        Data.add(RemoveFeature);
        Data.addSeparator();
        Data.add(startSelection);
        Data.add(finishSelection);
        Data.addSeparator();
        Data.add(DataSummary);
        Data.add(ChooseNegative);
        Data.add(ExportData);
        ExportData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.exportData(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        AddClass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.addClass();
            }
        });
        AddFeature.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.addFeature();
            }
        });
        LoadData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.loadData(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        RemoveFeature.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.removeFeature(featureList.getSelectedIndex());
            }
        });
        RemoveClass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.removeClass(classList.getSelectedIndex());
            }
        });
        LoadTestingSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.chooseTestingSet(true);
            }
        });
        DAMenu.add(DAAnalyse);
        DAMenu.add(SignificanceAnalysis);
        DAMenu.add(DAExport);
        DAMenu.addSeparator();
        DAMenu.add(DFClassify);
        DAMenu.add(DrawDFHistogram);
        DAMenu.add(DrawDFTestHistogram);

        DAExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.DAExport(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });

        DAAnalyse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.da.beginAnalysis(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        SignificanceAnalysis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.da.beginSignificanceAnalysis(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });

        DrawDFTestHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                try {
                    manager.s.da.DrawTestAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "DA cannot be done. Singular matrix obtained. ");
                }
            }
        });
        DrawDFHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    manager.s.da.DrawAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "DA cannot be done. Singular matrix obtained. ");
                }
            }
        });
        DFClassify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.classify(0, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });

        Draw.add(DrawPlot);
        Draw.add(DrawHistogram);
        Draw.add(DrawTrend);
        DrawPlot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.plot(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        DrawHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.histogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        DrawTrend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.trend(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        DescriptiveStatistics.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.dataStatistics(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        DataSummary.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.dataSummary(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });

        HideConsole.setSelected(true);
        HideConsole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (HideConsole.isSelected()) {
                    consolePanel.setVisible(true);
                } else {
                    consolePanel.setVisible(false);
                }
                repaint();
            }
        });
        HideTools.setSelected(true);
        HideTools.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (HideTools.isSelected()) {
                    toolsPanel.setVisible(true);
                } else {
                    toolsPanel.setVisible(false);
                }
                repaint();
            }
        });
        HideDataPanel.setSelected(true);
        HideDataPanel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (HideDataPanel.isSelected()) {
                    dataPanel.setVisible(true);
                } else {
                    dataPanel.setVisible(false);
                }
                repaint();
            }
        });
        Window.add(HideDataPanel);
        Window.add(HideTools);
        Window.add(HideConsole);
        Window.addSeparator();
        Window.add(Tile);
        Window.add(Cascade);
        Window.addSeparator();
        Window.add(ClearConsole);
        ClearConsole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                consoleArea.setText("");
            }
        });
        Tile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tile(workSpacePanel);
            }
        });
        Cascade.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cascade(workSpacePanel);
            }
        });

        HelpM.add(Help);
        HelpM.addSeparator();
        HelpM.add(About);
        Help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.help();

            }
        });
        About.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final JEditorPane editorPane = new JEditorPane();

                // Enable use of custom set fonts
                editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                editorPane.setFont(new Font("Verdana", Font.PLAIN, 14));

                editorPane.setPreferredSize(new Dimension(470, 100));
                editorPane.setEditable(false);
                editorPane.setContentType("text/html");
                editorPane.setText(
                        "<html>"
                                + "<body bgcolor = \"#535353\" link=\"#009aff\" vlink=\"#009aff\" alink=\"#009aff\">"
                                + "Visan - Visual data analysis package,  (c) Softberry Inc. 2017"
                                + "<br>Authors: Ramzan Umarov & Victor Solovyev<br>"
                                + "<a href='http://www.softberry.com'><font color=\"009aff\">www.softberry.com</font>"
                                + "<a href=\"mailto:softberry@softberry.com?Subject=VISAN\" target=\"_top\"><br>"
                                + "<font color=\"009aff\">Send Mail</font></a>"
                                + "</body>"
                                + "</html>");

                editorPane.setBorder(BorderFactory.createEmptyBorder());
                editorPane.setBackground(new Color(40, 41, 43));
                // TIP: Add Hyperlink listener to process hyperlinks
                editorPane.addHyperlinkListener(new HyperlinkListener() {
                    public void hyperlinkUpdate(final HyperlinkEvent e) {
                        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    // TIP: Show hand cursor
                                    SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                    // TIP: Show URL as the tooltip
                                    editorPane.setToolTipText(e.getURL().toExternalForm());
                                }
                            });
                        } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    // Show default cursor
                                    SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getDefaultCursor());

                                    // Reset tooltip
                                    editorPane.setToolTipText(null);
                                }
                            });
                        } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            // TIP: Starting with JDK6 you can show the URL in desktop browser
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().browse(e.getURL().toURI());
                                } catch (Exception ex) {
                                }
                            }
                            //System.out.println("Go to URL: " + e.getURL());
                        }
                    }
                });

                JOptionPane.showMessageDialog(null,
                        editorPane,
                        "About",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        });
        RFC.add(RFGrowTrees);
        RFGrowTrees.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.rfc.growForestNewThread(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        RFC.add(RFTrainingSet);
        RFTrainingSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.rfc.DrawAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        RFTestingSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //manager.s.rfc.outputTestingStats(classList.getSelectedIndices(), featureList.getSelectedIndices());
                manager.s.rfc.DrawTestAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        RFCClassify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.classify(5, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        RFC.add(RFTestingSet);
        RFC.add(RFCClassify);
        NeuralNet.add(BPNLearn);
        NeuralNet.add(BPNStop);
        BPNStop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        BPNStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.stopLearning();

            }
        });
        NeuralNet.add(classifyBPN);
        NeuralNet.addSeparator();
        NeuralNet.add(BPNDrawAccuracyHistogram);
        NeuralNet.add(BPNDrawTestAccuracyHistogram);
        NeuralNet.addSeparator();
        NeuralNet.add(BPNExport);
        NeuralNet.add(BPNImport);
        BPNImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.BPNImport(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        BPNExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.io.BPNExport(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        BPNDrawTestAccuracyHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.DrawTestHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        BPNDrawAccuracyHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.DrawHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });

        PCA.add(FindPC);
        PCA.add(EigenvalueHistogram);
        PCA.add(PC2DGraph);
        PCA.add(PC3DGraph);
        EigenvalueHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.eigenvalueHistogram();

            }
        });
        FindPC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.findPC(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        PC2DGraph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.draw2DPCAGraph();

            }
        });
        PC3DGraph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.draw3DPCAGraph();

            }
        });
        BPNLearn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
        BPNLearn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.startLearning(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        classifyBPN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.classify(1, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });

        BPNNoImprovementStop.setSelected(true);
        BPNStopping = new ButtonGroup();
        BPNStopping.add(BPNNoImprovementStop);
        BPNStopping.add(BPNTestingSetStop);
        BPNStopping.add(BPNManualStop);

        BPNManualStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (BPNManualStop.isSelected()) {
                    manager.s.bpn.setManualStop(true);
                } else {
                    manager.s.bpn.setManualStop(false);
                }
            }
        });

        BPNOptions.add(BPNSetLerningRate);
        BPNOptions.add(BPNSetMomentum);
        BPNOptions.add(BPNSetFunc);
        BPNOptions.add(BPNSetTest);
        BPNOptions.add(BPNSetMinCoef);
        BPNOptions.add(BPNSetDecRate);
        BPNOptions.add(BPNSetAllowedTime);
        BPNOptions.add(BPNSetAllowedEpochs);
        BPNOptions.add(BPNSetNumberOfImprovementTries);
        BPNOptions.add(BPNSetHiddenLayers);
        BPNOptions.add(BPNSetNumberOfLearns);
        BPNOptions.addSeparator();
        BPNOptions.add(BPNTestingSetStop);
        BPNOptions.add(BPNNoImprovementStop);
        BPNOptions.add(BPNManualStop);

        LSOptions.add(LSSetA);
        LSSetA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.ls.setA();
            }
        });
        LSOptions.add(LSDualForm);
        LSOptions.add(LSKernel);
        LSKernel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.ls.chooseKernel();
            }
        });
        LSDualForm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (!manager.s.ls.setDual(LSDualForm.isSelected())) {
                    LSDualForm.setSelected(!LSDualForm.isSelected());
                }
            }
        });
        Options.add(DAOptionsMenu);
        Options.add(LSOptions);
        Options.add(BPNOptions);
        //Options.add(SVMOptions);
        Options.add(RFCOptions);
        RFCOptions.add(RFCNumThreads);
        RFCOptions.add(RFCNumTrees);
        RFCOptions.add(RFCSampleSize);
        RFCOptions.add(RFCNumFeatures);
        RFCOptions.add(RFCMaxDepth);
        RFCOptions.add(RFCBranchingFactor);

        //Options.add(ABOptions);
        ABOptions.add(ABMaxDepth);
        ABMaxDepth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AdaBoost.setMaxDepth();
            }
        });
        ABOptions.add(ABNumTrees);
        ABNumTrees.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AdaBoost.setNumTrees();
            }
        });
        ABOptions.add(ABBranchingFactor);
        ABBranchingFactor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AdaBoost.setBranchingFactor();
            }
        });
        RFCBranchingFactor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RandomForest.setBranchingFactor();
            }
        });
        RFCMaxDepth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RandomForest.setMaxDepth();
            }
        });
        RFCNumThreads.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    int n = Integer.parseInt(JOptionPane.showInputDialog("Choose number of threads to use: "));
                    if (n > 0) {
                        RandomForest.setNumThreads(n);
                    }
                } catch (Exception ex) {

                }
            }
        });
        RFCNumTrees.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    int n = Integer.parseInt(JOptionPane.showInputDialog("Choose number of trees: "));
                    if (n > 0) {
                        RandomForest.setNumTrees(n);
                    }
                } catch (Exception ex) {

                }
            }
        });
        RFCSampleSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    double n = Double.parseDouble(JOptionPane.showInputDialog("Choose sample size (-1 for sqrt, 0<k<=1 for n*k): "));
                    if (n != 0) {
                        RandomForest.setSampleFraction(n);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Wrong format. Example: 0.75");
                }
            }
        });
        RFCNumFeatures.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    int n = Integer.parseInt(JOptionPane.showInputDialog("Number of random features to build each tree (-1 for all, 0 for sqrt, k>0 for k): "));
                    if (n > 0) {
                        RandomForest.setMaxFeatures(n);
                    }
                } catch (Exception ex) {
                }
            }
        });
        SVMOptions.add(SVMSetC);
        SVMSetC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.sv.setC();
            }
        });
        SVMOptions.add(SVMSetThreshold);
        SVMSetThreshold.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.sv.setThreshold();
            }
        });
        SVMOptions.add(SVMSetTolerance);
        SVMSetTolerance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.sv.setTolerance();
            }
        });
        SVMOptions.add(SVMSetToleranceFeas);
        SVMSetToleranceFeas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.sv.setToleranceFeas();
            }
        });
        NNOptions.add(NNChooseN);
        NNChooseN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.nn.chooseN();
            }
        });
        NNOptions.add(NNDualForm);
        NNDualForm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (!manager.s.nn.setDual(NNDualForm.isSelected())) {
                    NNDualForm.setSelected(!NNDualForm.isSelected());
                }
            }
        });
        NNOptions.add(NNKernel);
        NNKernel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.nn.chooseKernel();
            }
        });
        Options.add(NNOptions);

        Options.add(Miscellaneous);
        Miscellaneous.add(ShowDataName);
        ShowDataName.setSelected(true);
        ShowDataName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (ShowDataName.isSelected()) {
                    manager.setShowDataName(true);
                } else {
                    manager.setShowDataName(false);
                }
            }
        });
        MultiClassVisualization.add(PieChart);
        MultiClassVisualization.add(Histogram);
        PieChart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setMultiClassShape(true);
            }
        });
        Histogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setMultiClassShape(false);
            }
        });
        DAOptionsMenu.add(UsePriorProbability);
        UsePriorProbability.setSelected(true);
        DAOptionsMenu.addSeparator();
        DAOptionsMenu.add(LinearMethod);
        DAOptionsMenu.add(QuadraticMethod);
        LinearMethod.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setDFMethod(0);
            }
        });
        QuadraticMethod.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setDFMethod(1);
            }
        });
        UsePriorProbability.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (UsePriorProbability.isSelected()) {
                    manager.setDAProbability(true);
                } else {
                    manager.setDAProbability(false);
                }
            }
        });
        BPNTestingSetStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (BPNTestingSetStop.isSelected()) {
                    manager.s.bpn.useTestingSet();
                }
            }
        });
        BPNSetLerningRate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setLerningRate();
            }
        });
        BPNSetMomentum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setMomentum();

            }
        });
        BPNSetDecRate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setDecRate();

            }
        });
        BPNSetMinCoef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setMinCoef();

            }
        });
        BPNSetFunc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setFunc();
            }
        });
        BPNSetTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setTest();
            }
        });
        BPNSetNumberOfLearns.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setNumberOfLearns();
            }
        });
        BPNSetHiddenLayers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setHiddenLayers();
            }
        });
        BPNSetAllowedTime.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setAllowedTime();
            }
        });
        BPNSetAllowedEpochs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setAllowedEpochs();
            }
        });
        BPNSetNumberOfImprovementTries.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.bpn.setNumberOfImprovementTries();
            }
        });
        BPNNoImprovementStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (BPNNoImprovementStop.isSelected()) {
                    manager.s.bpn.setImprovementStop();
                }
            }
        });
        NearestNeighbour.add(classifyNN);
        classifyNN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.classify(2, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        NearestNeighbour.add(NNTestAccuracyHistogram);
        NNTestAccuracyHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.nn.DrawTestAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        Classification.add(DAMenu);
        Classification.add(NeuralNet);
        Classification.add(NearestNeighbour);
        Classification.add(LeastSquares);
        //Classification.add(SVM);
        Classification.add(RFC);
        //Classification.add(AB);
        AB.add(ABTrain);
        AB.add(ABHistogram);
        AB.add(ABTestHistogram);
        AB.add(ABClassify);


        ABTrain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.ab.growForestNewThread(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        ABHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.ab.DrawAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());

            }
        });
        ABTestHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //manager.s.rfc.outputTestingStats(classList.getSelectedIndices(), featureList.getSelectedIndices());
                manager.s.ab.DrawTestAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        ABClassify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.classify(6, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });


        SVM.add(SVMLearn);
        SVM.add(SVMClassify);
        SVM.add(SVMDrawAccuracyHistogram);
        SVM.add(SVMDrawTestAccuracyHistogram);
        SVMClassify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.classify(4, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        SVMLearn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.sv.learn(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        SVMDrawAccuracyHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.sv.DrawAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        SVMDrawTestAccuracyHistogram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.s.sv.DrawTestAccuracyHistogram(classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        Statistics.add(DescriptiveStatistics);
        // Statistics.add(PerformTest);
        PerformTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (classList.getSelectedIndices().length == 2 && featureList.getSelectedIndices().length == 1) {
                    manager.performTest(classList.getSelectedIndices(), featureList.getSelectedIndices());
                } else {
                    JOptionPane.showMessageDialog(null, "Select two classes and one feature for this test.");
                }

            }
        });
        menu.add(File);
        menu.add(Data);
        menu.add(Statistics);
        menu.add(Classification);
        menu.add(PCA);
        menu.add(Draw);
        menu.add(Options);
        menu.add(Window);
        menu.add(HelpM);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="panels">
        classList = new JList();
        featureList = new JList();
        classPane = new JScrollPane();
        featuresPane = new JScrollPane();
        classPane.setViewportView(classList);
        featuresPane.setViewportView(featureList);
        featuresPane.setPreferredSize(new Dimension(130, 200));
        featuresPane.setBorder(BorderFactory.createEmptyBorder(0, panelMargin, 0, panelMargin));
        classPane.setPreferredSize(new Dimension(130, 200));
        classPane.setBorder(BorderFactory.createEmptyBorder(0, panelMargin, 0, panelMargin));
        //featuresPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Select Features:")));
        //classPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Select classes:")));
        reciprocal = new JRadioButton("Reciprocal");
        reciprocal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setTransformation(4, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        normalise = new JRadioButton("Normalise");
        normalise.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setTransformation(1, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        logarithmScale = new JRadioButton("Logarithm Scale");
        logarithmScale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setTransformation(2, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        selectAllFeatures = new JButton("Select All Features");
        selectAllFeatures.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (featureList.getSelectedIndices().length != featureList.getModel().getSize()) {
                    featureList.setSelectionInterval(0, featureList.getModel().getSize() - 1);
                } else {
                    featureList.clearSelection();
                }
            }
        });
        selectAllClasses = new JButton("Select All Classes");
        selectAllClasses.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (classList.getSelectedIndices().length != classList.getModel().getSize()) {
                    classList.setSelectionInterval(0, classList.getModel().getSize() - 1);
                } else {
                    classList.clearSelection();
                }
            }
        });
        selectAll = new JButton("Select All");
        selectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (classList.getSelectedIndices().length != classList.getModel().getSize()) {
                    selectAll();
                } else {
                    classList.clearSelection();
                    featureList.clearSelection();
                }
            }
        });
        sqrt = new JRadioButton("Square Root");
        sqrt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setTransformation(3, classList.getSelectedIndices(), featureList.getSelectedIndices());
            }
        });
        dataSelectPanel = new JPanel();
        dataSelectPanel.setBorder(BorderFactory.createEmptyBorder(0, panelMargin, 0, panelMargin));
        GridLayout blayout = new GridLayout(3, 2);
        blayout.setVgap(10);
        blayout.setHgap(10);
        dataSelectPanel.setLayout(blayout);
        dataSelectPanel.add(selectAll);
        dataSelectPanel.add(selectAllClasses);
        dataSelectPanel.add(selectAllFeatures);
        ApplyTransformation = new JButton("Permanent Transformation");
        ApplyTransformation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.transformData();
                transformGroup.clearSelection();
            }
        });
        clearSelection = new JButton("Clear Selection");
        clearSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.setTransformation(0, classList.getSelectedIndices(), featureList.getSelectedIndices());
                transformGroup.clearSelection();
            }
        });
        transformGroup = new ButtonGroup();
        transformGroup.add(normalise);
        transformGroup.add(sqrt);
        transformGroup.add(logarithmScale);
        transformGroup.add(reciprocal);
        dataTransformPanel = new JPanel();

        GridLayout dtpl = new GridLayout(6, 1);
        dtpl.setVgap(10);
        dtpl.setHgap(10);
        dataTransformPanel.setLayout(dtpl);
        dataTransformPanel.setBorder(new CustomTitledBorder("Data Transformation"));
        dataTransformPanel.add(normalise);
        dataTransformPanel.add(sqrt);
        dataTransformPanel.add(logarithmScale);
        dataTransformPanel.add(reciprocal);
        dataTransformPanel.add(clearSelection);
        dataTransformPanel.add(ApplyTransformation);
        // <editor-fold defaultstate="collapsed" desc="labels">
        zoomIn = new JLabel();
        zoomIn.setIcon(new ImageIcon(Visan.class.getResource("ui/images/zoomIn.png")));
        zoomIn.setToolTipText("Zoom In");
        zoomIn.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.zoomIn();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                zoomIn.setIcon(new ImageIcon(Visan.class.getResource("ui/images/zoomInRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                zoomIn.setIcon(new ImageIcon(Visan.class.getResource("ui/images/zoomIn.png")));
            }
        });
        zoomOut = new JLabel();
        zoomOut.setIcon(new ImageIcon(Visan.class.getResource("ui/images/ZoomOut.png")));
        zoomOut.setToolTipText("Zoom Out");
        zoomOut.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.zoomOut();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                zoomOut.setIcon(new ImageIcon(Visan.class.getResource("ui/images/ZoomOutRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                zoomOut.setIcon(new ImageIcon(Visan.class.getResource("ui/images/ZoomOut.png")));
            }
        });

        pointSizeInc = new JLabel();
        pointSizeInc.setIcon(new ImageIcon(Visan.class.getResource("ui/images/sizeInc.png")));
        pointSizeInc.setToolTipText("Increase Point Size");
        pointSizeInc.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.pointSizeInc();

            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                pointSizeInc.setIcon(new ImageIcon(Visan.class.getResource("ui/images/sizeIncRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                pointSizeInc.setIcon(new ImageIcon(Visan.class.getResource("ui/images/sizeInc.png")));
            }
        });

        pointSizeDec = new JLabel();
        pointSizeDec.setIcon(new ImageIcon(Visan.class.getResource("ui/images/sizeDec.png")));
        pointSizeDec.setToolTipText("Decrease Point Size");
        pointSizeDec.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.pointSizeDec();

            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                pointSizeDec.setIcon(new ImageIcon(Visan.class.getResource("ui/images/sizeDecRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                pointSizeDec.setIcon(new ImageIcon(Visan.class.getResource("ui/images/sizeDec.png")));
            }
        });

        snapShotLabel = new JLabel();
        snapShotLabel.setIcon(new ImageIcon(Visan.class.getResource("ui/images/camera.png")));
        snapShotLabel.setToolTipText("Take Snapshot");
        snapShotLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.takeSnapshot();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                snapShotLabel.setIcon(new ImageIcon(Visan.class.getResource("ui/images/cameraRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                snapShotLabel.setIcon(new ImageIcon(Visan.class.getResource("ui/images/camera.png")));
            }
        });

        newProject = new JLabel();
        newProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/new.png")));
        newProject.setToolTipText("New Project");
        newProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.io.newProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                newProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/newRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                newProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/new.png")));
            }
        });

        openProject = new JLabel();
        openProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/open.png")));
        openProject.setToolTipText("Open Project");
        openProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.io.openProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                openProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/openRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                openProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/open.png")));
            }
        });

        saveProject = new JLabel();
        saveProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/save.png")));
        saveProject.setToolTipText("Save Project");
        saveProject.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.io.saveProject();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/saveRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                saveProject.setIcon(new ImageIcon(Visan.class.getResource("ui/images/save.png")));
            }
        });

        loadDataLabel = new JLabel();
        loadDataLabel.setIcon(new ImageIcon(Visan.class.getResource("ui/images/data.png")));
        loadDataLabel.setToolTipText("Load Learning Set");
        loadDataLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                manager.io.readFile();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                loadDataLabel.setIcon(new ImageIcon(Visan.class.getResource("ui/images/dataRollover.png")));
            }

            public void mouseExited(MouseEvent e) {
                loadDataLabel.setIcon(new ImageIcon(Visan.class.getResource("ui/images/data.png")));
            }
        });

        toolsPanel = new JToolBar();

        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
        toolsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(newProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(openProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(saveProject);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(loadDataLabel);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.addSeparator();
        toolsPanel.add(zoomIn);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(zoomOut);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.addSeparator();
        toolsPanel.add(pointSizeInc);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.add(pointSizeDec);
        toolsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolsPanel.addSeparator();
        toolsPanel.add(snapShotLabel);
        //</editor-fold>

        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
        dataPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel labelPan1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPan1.add(new JLabel("Classes"));

        dataPanel.add(labelPan1);
        dataPanel.add(classPane);
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel labelPan2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPan2.add(new JLabel("Features"));

        dataPanel.add(labelPan2);
        dataPanel.add(featuresPane);
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dataPanel.add(dataSelectPanel);
        dataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dataPanel.add(dataTransformPanel);

        startSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.startSelection();
            }
        });
        finishSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manager.finishSelection();
            }
        });
        workSpacePanel = new JDesktopPane();
        workSpacePanel.setBackground(Color.GRAY);
        workSpacePanel.setBorder(BorderFactory.createEmptyBorder());
        // </editor-fold>
        // <editor-fold desc="console">
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setLineWrap(true);
        consoleArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        consoleScroll = new JScrollPane();
        consoleScroll.setPreferredSize(new Dimension(250, 110));
        consoleScroll.setViewportView(consoleArea);
        consoleScroll.setBorder(BorderFactory.createEmptyBorder());

        consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());
        consolePanel.add(consoleScroll, BorderLayout.CENTER);
        consolePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));// </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="prepairing frame">
        setTitle("VISAN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setJMenuBar(menu);
        add(toolsPanel, BorderLayout.NORTH);
        add(dataPanel, BorderLayout.EAST);
        add(consolePanel, BorderLayout.SOUTH);
        add(workSpacePanel, BorderLayout.CENTER);
        // </editor-fold>
    }

    public void disposeAllIFrames() {
        for (JInternalFrame iframe : workSpacePanel.getAllFrames()) {
            iframe.dispose();
        }
    }

    public static void tile(JDesktopPane desktopPane) {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) {
            return;
        }
        tile(frames, desktopPane.getBounds());
    }

    private static void tile(JInternalFrame[] frames, Rectangle dBounds) {
        int cols = (int) Math.sqrt(frames.length);
        int rows = (int) (Math.ceil(((double) frames.length) / cols));
        int lastRow = frames.length - cols * (rows - 1);
        int width, height;

        if (lastRow == 0) {
            rows--;
            height = dBounds.height / rows;
        } else {
            height = dBounds.height / rows;
            if (lastRow < cols) {
                rows--;
                width = dBounds.width / lastRow;
                for (int i = 0; i < lastRow; i++) {
                    frames[cols * rows + i].setBounds(i * width, rows * height,
                            width, height);
                }
            }
        }

        width = dBounds.width / cols;
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                frames[i + j * cols].setBounds(i * width, j * height,
                        width, height);
            }
        }
    }

    public static void cascade(JDesktopPane desktopPane) {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) {
            return;
        }

        cascade(frames, desktopPane.getBounds(), 24);
    }

    private static void cascade(JInternalFrame[] frames, Rectangle dBounds, int separation) {
        int margin = frames.length * separation + separation;
        int width = dBounds.width - margin;
        int height = dBounds.height - margin;
        for (int i = 0; i < frames.length; i++) {
            frames[i].setBounds(separation + dBounds.x + i * separation,
                    separation + dBounds.y + i * separation,
                    width, height);
        }
    }

    public void setLists(String[] classesModel, Object[] featuresModel) {
        classList.setModel(new DefaultComboBoxModel(classesModel));
        featureList.setModel(new DefaultComboBoxModel(featuresModel));
        selectAll();
    }

    void selectAll() {
        classList.setSelectionInterval(0, classList.getModel().getSize() - 1);
        featureList.setSelectionInterval(0, featureList.getModel().getSize() - 1);
    }

    public void writeToConsole(String text) {
        consoleArea.append(text);
    }

    public void createFrame(String title, final DataComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                c.dispatchEvent(e);

            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        MyInternalFrame frame = new MyInternalFrame(title, sp);
        frame.setVisible(true);
        frame.addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameOpened(InternalFrameEvent e) {
            }

            public void internalFrameClosing(InternalFrameEvent e) {
            }

            public void internalFrameClosed(InternalFrameEvent e) {
                manager.removeFromComponentList(c);
            }

            public void internalFrameIconified(InternalFrameEvent e) {
            }

            public void internalFrameDeiconified(InternalFrameEvent e) {
            }

            public void internalFrameActivated(InternalFrameEvent e) {
                manager.setSelectedComponent(c);
            }

            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        });
        workSpacePanel.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    public void createFrame(String title, final JTabbedPane c) {
        MyInternalFrame frame = new MyInternalFrame(title, c);
        frame.setVisible(true);
        workSpacePanel.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    public void createFrame(String title, final plot3D graph3D) {
        MyInternalFrame frame = new MyInternalFrame(title, (java.awt.Component) graph3D.chart.getCanvas());
        frame.setVisible(true);
        frame.addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameOpened(InternalFrameEvent e) {
            }

            public void internalFrameClosing(InternalFrameEvent e) {
            }

            public void internalFrameClosed(InternalFrameEvent e) {
            }

            public void internalFrameIconified(InternalFrameEvent e) {
            }

            public void internalFrameDeiconified(InternalFrameEvent e) {
            }

            public void internalFrameActivated(InternalFrameEvent e) {
                manager.setSelectedComponent(graph3D);
            }

            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        });
        workSpacePanel.add(frame);
        workSpacePanel.setSelectedFrame(frame);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException ex) {
        }
    }

    public void lockBPNOptions() {
        BPNOptions.setEnabled(!BPNOptions.isEnabled());
    }

    public void lockSVM() {
        SVMOptions.setEnabled(!SVMOptions.isEnabled());
        SVM.setEnabled(!SVM.isEnabled());
    }
}
