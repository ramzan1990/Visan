package visan.main;

import visan.Visan;
import visan.common.ClassesCollection;
import visan.ui.DataInputDialog;
import visan.ui.VisFilter;
import visan.visualisation.DataComponent;
import visan.visualisation.plot3D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Umarov on 4/3/2017.
 */
public class IOManager {
    private boolean isSaved;
    private boolean classInBeginning;
    private VisState s;
    private File fcDir;
    private GUIManager gm;

    public IOManager(VisState s, GUIManager gm) {
        this.s = s;
        this.gm = gm;
        fcDir = new File(System.getProperty("user.dir"));
    }

    private boolean dataInputOptions() {
        Object[] v;
        String[] choices = new String[2];
        choices[0] = "Class in the end";
        choices[1] = "Class in the beginning";
        v = DataInputDialog.showDialog("Choose Delimeter:", "Data Input Options", choices);
        if (v == null) {
            return false;
        }
        if ((Integer) v[0] == 1) {
            classInBeginning = true;
        } else {
            classInBeginning = false;
        }
        s.delimeter = (String) v[1];

        if (s.delimeter.trim().length() != 0) {
            s.delimeter = s.delimeter.trim();
            s.delimeter = "\\s*(" + s.delimeter + "\\s*)|(\\s*\r*\n)|(\r)";

        } else {
            s.delimeter = "\\s+";
        }
        return true;
    }

    public String getFileName() {
        if (s.showName) {
            return s.origFileName;
        } else {
            return "";
        }
    }

    private void processInput(File input, String cn, boolean defaultType, boolean reset) throws Exception {
        Scanner featuresScan, scan;
        // In case we are reading data file created by VISAN, we dont need show
        // this dialog
        if (!defaultType) {
            if (!dataInputOptions()) {
                return;
            }
        } else {
            classInBeginning = false;
        }
        s.trainingSet = new ClassesCollection();
        s.testingSet = false;
        s.trainingSet.features = new ArrayList<String>();
        scan = new Scanner(input);
        featuresScan = new Scanner(input);
        String firstLine = featuresScan.nextLine();
        featuresScan.close();
        featuresScan = new Scanner(firstLine);
        featuresScan.useDelimiter(s.delimeter);
        try {
            if (featuresScan.next().equals("/features")) {
                while (featuresScan.hasNext()) {
                    String t = featuresScan.next();
                    s.trainingSet.features.add(t);
                }
                scan.nextLine();
            } else {
                // we need to count number of features
                for (int i = 1; featuresScan.hasNext(); i++) {
                    s.trainingSet.features.add("fn" + i);
                    featuresScan.next();
                }
            }
            while (scan.hasNext()) {
                scan.useDelimiter(s.delimeter);
                String className = "";
                if (classInBeginning) {
                    className = scan.next();
                }
                ArrayList<Double> dataObject = new ArrayList<>();
                for (int i = 0; i < s.trainingSet.features.size(); i++) {
                    dataObject.add(scan.nextDouble());
                }
                if (!classInBeginning) {
                    className = scan.next();
                }
                int c = s.trainingSet.containsClass(className);
                if (c == -1) {
                    s.trainingSet.addClass(className);
                    s.trainingSet.addTrainingExample(s.trainingSet.getSize() - 1, dataObject);
                } else {
                    s.trainingSet.addTrainingExample(c, dataObject);
                }
            }
            int a = s.trainingSet.containsClass("-1");
            if (a != -1) {
                s.trainingSet.replace(a, s.trainingSet.getSize() - 1);
            }
            gm.chooseNegativeClass(cn);
        } catch (Exception x) {
            JOptionPane.showMessageDialog(null, "Cannot read the file!", "Error", JOptionPane.ERROR_MESSAGE);
            x.printStackTrace();
        } finally {
            featuresScan.close();
            scan.close();
        }
        gm.prepareLists();
        if (reset) {
            gm.someInitialisation();
        }
    }

    private void readInTestingSet(File f) {
        Scanner scan = null;
        try {
            scan = new Scanner(f);
            scan.useDelimiter(s.delimeter);
            s.trainingSet.cleanTestingSet();
            while (scan.hasNext()) {
                String className = "";
                if (classInBeginning) {
                    className = scan.next();
                }
                ArrayList<Double> tempD = new ArrayList<>();
                int inc = 0;
                for (int i = 0; i < s.trainingSet.features.size(); i++) {
                    tempD.add(scan.nextDouble());
                }
                if (!classInBeginning) {
                    className = scan.next();
                }
                int c = s.trainingSet.containsClass(className);
                s.trainingSet.addTestingExample(c, tempD);
            }
            s.testingSet = true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Cannot read the file!", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (scan != null) {
                scan.close();
            }
        }
    }


    public void readFile() {
        FileDialog fd = new FileDialog((Frame) null, "Open Data File", FileDialog.LOAD);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            try {
                File f = fd.getFiles()[0];
                processInput(f, null, false, true);
                s.origFileName = f.getName();
                fcDir = f;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Cannot read the file!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void openProject() {
        FileDialog fd = new FileDialog((Frame) null, "Open Project", FileDialog.LOAD);
        fd.setVisible(true);
        fd.setFilenameFilter(new VisFilter());
        if (fd.getFiles().length > 0) {
            ObjectInputStream ois = null;
            try {
                File f = fd.getFiles()[0];
                FileInputStream fin = new FileInputStream(f.getAbsolutePath());
                GZIPInputStream gis = new GZIPInputStream(fin);
                ois = new ObjectInputStream(gis);
                s = (VisState) ois.readObject();
                s.projectPath = f.getParentFile().toString() + "/";
                Visan.setState(s);
                isSaved = true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Cannot open the project!", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }



    public boolean saveProjectAs() {
        if (s.chechIfBusy()) {
            JOptionPane.showMessageDialog(null, "Wait till the current operation finishes.");
            return false;
        }
        FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
        fd.setVisible(true);
        fd.setFilenameFilter(new VisFilter());

        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            s.projectName = f.getName();
            s.projectPath = f.getAbsolutePath();
            gm.setTitle(s.projectName);
            isSaved = true;
            new File(s.projectPath).mkdir();
            s.projectPath += "/";
            new File(s.projectPath + s.projectName).mkdir();
            new File(s.projectPath + s.projectName + "/images").mkdir();
            new File(s.projectPath + s.projectName + "/analysis").mkdir();
            new File(s.projectPath + s.projectName + "/analysis/DA").mkdir();
            new File(s.projectPath + s.projectName + "/analysis/BPN").mkdir();
            isSaved = true;
            saveProject();
            return true;
        }
        return false;
    }

    public void saveProject() {
        if (s.chechIfBusy()) {
            JOptionPane.showMessageDialog(null, "Wait till the current operation finishes.");
            return;
        }
        if (!isSaved) {
            saveProjectAs();
            return;
        }
        try {
            FileOutputStream fout = new FileOutputStream(s.projectPath + s.projectName + ".vis");
            GZIPOutputStream gz = new GZIPOutputStream(fout);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            oos.writeObject(s);
            oos.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error While Saving!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void takeSnapshot(JComponent selectedComponent) {
        try {
            Object[] options = {"Project folder", "Save as", "Cancel"};
            int n = JOptionPane.showOptionDialog(null, "Save the snapshot in:", "Snapshot taken",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            String name;
            if (n == 0) {
                if (!isSaved) {
                    saveProject();
                }
                name = JOptionPane.showInputDialog("Enter the snapshot name:");
                File f = new File(s.projectPath + s.projectName + "/images/" + name + ".png");
                for (int i = 0; f.exists(); i++) {
                    f = new File(s.projectPath + s.projectName + "/images/" + name + i + ".png");
                }
                saveComponent(selectedComponent, "png", f);
                JOptionPane.showMessageDialog(null, "Snapshot saved in project's images folder.");
            } else if (n == 1) {
                FileDialog fd = new FileDialog((Frame) null, "Save Snapshot", FileDialog.SAVE);
                fd.setVisible(true);
                fd.setFilenameFilter(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.toLowerCase().endsWith(".png")) {

                            return true;

                        }

                        return false;
                    }
                });

                if (fd.getFiles().length > 0) {
                    File f = fd.getFiles()[0];
                    name = f.getAbsolutePath();
                    if (!name.toLowerCase().endsWith(".png")) {
                        name = name + ".png";
                    }
                    File o = new File(name);
                    saveComponent(selectedComponent, "png", o);
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean chooseTestingSet(boolean b) {
        if (!b && s.testingSet) {
            return true;
        }
        if (s.trainingSet == null) {
            JOptionPane.showMessageDialog(null, "Load Training Set First");
            return false;
        }
        FileDialog fd = new FileDialog((Frame) null, "Open Testing Set", FileDialog.LOAD);
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            if (!dataInputOptions()) {
                return false;
            }
            try {
                readInTestingSet(fd.getFiles()[0]);
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Cannot read the file!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            return false;
        }
    }

    public void newProject() {
        saveProjectAs();
        s.trainingSet = new ClassesCollection();
        s.trainingSet.features = new ArrayList<String>();
        gm.reset();
    }

    private void saveComponent(Object c, String format, File outputfile) throws IOException {
        BufferedImage myImage;
        if (DataComponent.class.isAssignableFrom(c.getClass())) {
            JComponent jc = (JComponent) c;
            jc.repaint();
            myImage = new BufferedImage(jc.getWidth(), jc.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = myImage.createGraphics();
            jc.paint(g);
        } else {
            plot3D p = (plot3D) c;
            myImage = p.chart.screenshot();
        }
        try {
            ImageIO.write(myImage, format, outputfile);
        } catch (Exception e) {
        }
    }

    public void BPNExport(int[] classes, int[] features) {
        if (s.bpn.threadIsBusy()) {
            return;
        }
        FileDialog fd = new FileDialog((Frame) null, "Export BPN", FileDialog.SAVE);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(fd.getFiles()[0].getAbsolutePath()));
                out.write(s.bpn.toString(classes, features).replaceAll("\n", System.getProperty("line.separator")));
                out.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cannot Export!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void DAExport(int[] classes, int[] features) {
        if (s.da.threadIsBusy()) {
            return;
        }
        FileDialog fd = new FileDialog((Frame) null, "Export DA", FileDialog.SAVE);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(fd.getFiles()[0].getAbsolutePath()));
                out.write(s.da.toString(classes, features).replaceAll("\n", System.getProperty("line.separator")));
                out.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cannot Export!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData(int[] classes, int[] features) {
        if (classes.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one class!");

        } else if (features.length == 0) {
            JOptionPane.showMessageDialog(null, "You need to select at least one feature!");
        } else {
            Scanner scan = null;
            try {
                JOptionPane.showMessageDialog(null,
                        "Select data to input to class " + s.trainingSet.getClassesNames()[classes[0]] + ", feature "
                                + s.trainingSet.features.get(features[0]));
                FileDialog fd = new FileDialog((Frame) null, "Load Data", FileDialog.LOAD);
                fd.setVisible(true);
                if (fd.getFiles().length > 0) {
                    scan = new Scanner(fd.getFiles()[0]);
                    scan.useDelimiter(gm.chooseDelimeter());
                    for (int i = 0; scan.hasNext(); i++) {
                        if (s.trainingSet.getTrainingClassSize(classes[0]) <= i) {
                            double[] temp = new double[features.length];
                            temp[features[0]] = Double.parseDouble(scan.next().trim());
                            s.trainingSet.addTrainingExample(classes[0], temp);
                        } else {
                            s.trainingSet.changeClassObject(classes[0], i, features[0],
                                    Double.parseDouble(scan.next().trim()));
                        }

                    }
                }
            } catch (Exception ex) {
            } finally {
                if (scan != null) {
                    scan.close();
                }
            }
        }
    }

    public void BPNImport(int[] classesList, int[] featuresList) {
        if (s.bpn.threadIsBusy()) {
            return;
        }
        FileDialog fd = new FileDialog((Frame) null, "Import BPN", FileDialog.LOAD);
        fd.setDirectory(s.projectPath + s.projectName + "/analysis/BPN/");
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            try {
                s.bpn.importBPN(classesList, featuresList, fd.getFiles()[0]);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Cannot read the file!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }


    public void exportData(int[] classes, int[] features) {
        classInBeginning = false;
        FileDialog fd = new FileDialog((Frame) null, "Export data", FileDialog.SAVE);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            try {
                BufferedWriter out;
                String cl[] = s.trainingSet.getClassesNames();
                if (s.testingSet) {
                    StringBuilder ts = new StringBuilder();
                    for (int i = 0; i < classes.length; i++) {
                        for (int j = 0; j < s.trainingSet.getTestingClassSize(classes[i]); j++) {
                            double[] to = s.trainingSet.testingObject(i, j, features);
                            if (classInBeginning) {
                                ts.append(cl[i]);
                                ts.append(", ");
                            }
                            for (int f = 0; f < features.length; f++) {
                                ts.append(to[f]);
                                ts.append(", ");
                            }
                            if (!classInBeginning) {
                                ts.append(cl[i]);
                                ts.append(", ");
                            }
                            ts.append("\n");
                        }
                    }
                    out = new BufferedWriter(new FileWriter(fd.getFiles()[0].getAbsolutePath() + "TS.txt"));
                    out.write(ts.toString());
                    out.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cannot Export!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }


}
