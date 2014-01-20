import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Класс - модель списка работников
 */
class WorkersTableModel extends AbstractTableModel {
    private List<Worker> workers;

    public WorkersTableModel(List<Worker> workers) {
        this.workers = workers;
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return workers.size();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Имя";
            case 1:
                return "Накл.расходы";
            case 2:
                return "Не вкл. в мес.отчет";
            case 3:
                return "Общая трудоемкость";
            default:
                return "Этого тут быть не должно!";
        }
    }

    @Override
    public Class getColumnClass(int col) {
        if (col <= 3) {
            return getValueAt(0, col).getClass();
        } else {
            return null;
        }
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return workers.get(rowIndex).getName();
            case 1:
                return workers.get(rowIndex).isOverhead();
            case 2:
                return workers.get(rowIndex).isNotInMonthReport();
            case 3:
                return workers.get(rowIndex).getLaborContentTotal();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return (col < 3);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        switch (col) {
            case 0:
                workers.get(row).setName((String) value);
                Starter.getMainForm().setDataChanged(true);
                break;
            case 1:
                workers.get(row).setOverhead((Boolean) value);
                Starter.getMainForm().setDataChanged(true);
                break;
            case 2:
                workers.get(row).setNotInMonthReport((Boolean) value);
                Starter.getMainForm().setDataChanged(true);
                break;
        }
        fireTableCellUpdated(row, col);
    }


}
