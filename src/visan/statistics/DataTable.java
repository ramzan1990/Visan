package visan.statistics;

import javax.swing.table.AbstractTableModel;

public class DataTable extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private Object[][] data;
    private String[] columnNames;    

    public DataTable(Object[][] data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndes, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = value;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    

    public DataTable(String[] columnNames, Object[][] data) {
        this.columnNames = columnNames;
        this.data = data;
    }

}

