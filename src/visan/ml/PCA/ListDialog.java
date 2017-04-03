package visan.ml.PCA;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ListDialog extends JDialog {

    private static ListDialog dialog;
    private static int[] values;
    private JList list;

    public static int[] showDialog(String labelText, String title, String[] possibleValues) {
        dialog = new ListDialog(labelText, title, possibleValues);
        dialog.setVisible(true);
        return values;
    }

    private ListDialog(String labelText, String title, Object[] data) {
        super(new JFrame(), title, true);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ListDialog.dialog.setVisible(false);
            }
        });
        final JButton OKButton = new JButton("OK");
        OKButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ListDialog.values = list.getSelectedIndices();
                ListDialog.dialog.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(OKButton);


        list = new JList(data) {

            public int getScrollableUnitIncrement(Rectangle visibleRect,
                    int orientation,
                    int direction) {
                int row;
                if (orientation == SwingConstants.VERTICAL
                        && direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                    Rectangle r = getCellBounds(row, row);
                    if ((r.y == visibleRect.y) && (row != 0)) {
                        Point loc = r.getLocation();
                        loc.y--;
                        int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);

                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                }
                return super.getScrollableUnitIncrement(
                        visibleRect, orientation, direction);
            }
        };

        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        label.setLabelFor(list);
        listPane.add(label);
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
