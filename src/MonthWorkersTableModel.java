import javax.swing.table.AbstractTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Сергей
 * Date: 11.01.11
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class MonthWorkersTableModel extends AbstractTableModel {

    public void setMonth(int month) {
        this.month = month;
        fireTableDataChanged();
    }

    private int month = 0;

    private WorkInPlan workInPlan = new WorkInPlan("Tmp", "Tmp");

    public int getRowCount() {
        return workInPlan.getWorkersInPlan().size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if ((rowIndex >= 0) && (rowIndex < workInPlan.getWorkersInPlan().size())) {
            switch (columnIndex) {
                case 0:
                    return workInPlan.getWorkersInPlan().get(rowIndex).getWorker();
                case 1:
                    return workInPlan.getWorkersInPlan().get(rowIndex).getPerMonth()[month];
                case 2:
                    return workInPlan.getWorkersInPlan().get(rowIndex).calcRestLabor(month);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public void setWorkInPlan(WorkInPlan workInPlan) {
        this.workInPlan = workInPlan;
        fireTableDataChanged();
    }

    public void clearWorkInPlan() {
        workInPlan = new WorkInPlan("Tmp", "Tmp");
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Работник";
            case 1:
                return "Месячная труд.";
            case 2:
                return "Остаток труд.";
            default:
                return "WTF?!";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Worker.class;
            case 1:
                return Double.class;
            case 2:
                return Double.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 1);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ((rowIndex >= 0) && (rowIndex < workInPlan.getWorkersInPlan().size())) {
            switch (columnIndex) {
                case 1: {
                    workInPlan.getWorkersInPlan().get(rowIndex).getPerMonth()[month] = (Double)aValue;
                    // Обновим остаток трудоемкости
                    fireTableCellUpdated(rowIndex, columnIndex+1);
                    //
                    Starter.getMainForm().updateRestLabor();
                }
            }
        }
    }
}
