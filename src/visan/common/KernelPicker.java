package visan.common;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class KernelPicker extends JDialog {

    private static KernelPicker dialog;
    private static Integer kernel;
    private static Double paramValue;
    private JList list;
    private JTextField kernelParameter;
    private static Object[] result;

    public static Object[] showDialog(String[] possibleValues) {
        dialog = new KernelPicker(possibleValues);
        dialog.setVisible(true);
        return result;
    }

    private KernelPicker(Object[] data) {
        super(new JFrame(), "Choose Kernel", true);
        final JButton OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                KernelPicker.kernel = list.getSelectedIndex();
                try {
                    KernelPicker.paramValue = Double.parseDouble(kernelParameter.getText());
                } catch (Exception ex) {
                    KernelPicker.paramValue=new Double(1);
                }
                result = new Object[]{kernel, paramValue};
                KernelPicker.dialog.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(OKButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                KernelPicker.dialog.setVisible(false);
            }
        });
        list = new JList(data);
        list.setSelectedIndex(0);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(4);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 40));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Kernel parameter:");
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        kernelParameter = new JTextField();
        listPane.add(kernelParameter);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(OKButton);
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
        pack();
        setLocationRelativeTo(null);
    }
}
