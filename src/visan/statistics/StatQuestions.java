package visan.statistics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;

public class StatQuestions extends JDialog {

    private static StatQuestions dialog;
    private static Integer userChoice[];
    private static JRadioButton pairedYes, pairedNo, gaussianNo, gaussianEqualSD, gaussianDifferentSD;

    public static Integer[] showDialog() {
        dialog = new StatQuestions();
        dialog.setVisible(true);
        return userChoice;
    }

    private StatQuestions() {
        super(new JFrame(), "Test Selection", true);
        pairedYes = new JRadioButton("Yes. Perform paired test.");
        pairedNo = new JRadioButton("No. Perform unpaired test.");
        gaussianNo = new JRadioButton("No. Perform nonparametric test.");
        gaussianEqualSD = new JRadioButton("Yes. Also populations have equal variance.");
        gaussianDifferentSD = new JRadioButton("Yes, but populations may have different variance.");
        pairedYes.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (pairedYes.isSelected()) {
                    gaussianDifferentSD.setVisible(false);
                }
            }
        });
        pairedNo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (pairedNo.isSelected()) {
                    gaussianDifferentSD.setVisible(true);
                }
            }
        });
        ButtonGroup pairedGroup = new ButtonGroup();
        pairedGroup.add(pairedYes);
        pairedGroup.add(pairedNo);
        pairedNo.setSelected(true);
        ButtonGroup gaussianGroup = new ButtonGroup();
        gaussianGroup.add(gaussianNo);
        gaussianGroup.add(gaussianEqualSD);
        gaussianGroup.add(gaussianDifferentSD);
        gaussianNo.setSelected(true);

        final JButton OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                userChoice = new Integer[2];
                if (pairedYes.isSelected()) {
                    userChoice[0] = 1;
                } else {
                    userChoice[0] = 0;
                }
                if (gaussianNo.isSelected()) {
                    userChoice[1] = 0;
                } else if (gaussianEqualSD.isSelected()) {
                    userChoice[1] = 1;
                } else {
                    userChoice[1] = 2;
                }
                StatQuestions.dialog.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(OKButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                StatQuestions.dialog.setVisible(false);
            }
        });
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.PAGE_AXIS));

        JPanel paired = new JPanel();
        paired.setBorder(new TitledBorder("Are the values paired?"));
        paired.setLayout(new BoxLayout(paired, BoxLayout.PAGE_AXIS));
        paired.add(pairedNo);
        paired.add(Box.createRigidArea(new Dimension(10, 10)));
        paired.add(pairedYes);

        JPanel gaussian = new JPanel();
        gaussian.setBorder(new TitledBorder("Are samples normally distributed?"));
        gaussian.setLayout(new BoxLayout(gaussian, BoxLayout.PAGE_AXIS));
        gaussian.add(gaussianNo);
        gaussian.add(Box.createRigidArea(new Dimension(10, 10)));
        gaussian.add(gaussianEqualSD);
        gaussian.add(Box.createRigidArea(new Dimension(10, 10)));
        gaussian.add(gaussianDifferentSD);

        questionsPanel.add(paired);
        questionsPanel.add(Box.createRigidArea(new Dimension(20, 20)));
        questionsPanel.add(gaussian);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(OKButton);
        Container contentPane = getContentPane();
        contentPane.add(questionsPanel, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
        pack();
        setLocationRelativeTo(null);
    }
}
