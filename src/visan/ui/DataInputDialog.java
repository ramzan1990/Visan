package visan.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class DataInputDialog extends JDialog  {

    private static DataInputDialog dialog;
    private static Integer value;
    private static String StringField;
    private JList list;
    private JTextField delimeterField;
    private static Object[] result;

    public static Object[] showDialog(String labelText, String title,  String[] possibleValues) {       
        dialog = new DataInputDialog(labelText, title, possibleValues);
        dialog.setVisible(true);
        return result;
    }

    private DataInputDialog(String labelText, String title,  Object[] data) {
        super(new JFrame(), title, true);
        final JButton OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DataInputDialog.value = list.getSelectedIndex();
                DataInputDialog.StringField = delimeterField.getText();
                DataInputDialog.dialog.setVisible(false);
                result = new Object[]{value, StringField};
            }
        });
        getRootPane().setDefaultButton(OKButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DataInputDialog.dialog.setVisible(false);
            }
        });
        list = new JList(data);
        list.setSelectedIndex(0);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(2);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 40));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        JLabel label2 = new JLabel("Class Position in a data row:");
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        delimeterField = new JTextField();
        listPane.add(delimeterField);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));
        listPane.add(label2);
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
