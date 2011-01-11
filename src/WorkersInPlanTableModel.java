import javax.swing.table.AbstractTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Сергей
 * Date: 11.01.11
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class WorkersInPlanTableModel extends AbstractTableModel {

    private WorkInPlan workInPlan = new WorkInPlan("Tmp", "Tmp");

    public int getRowCount() {
        return workInPlan.getWorkersInPlan().size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if ((rowIndex >= 0) && (rowIndex < workInPlan.getWorkersInPlan().size())) {
            switch (columnIndex) {
                case 0:
                    return workInPlan.getWorkersInPlan().get(rowIndex).getWorker();
                case 1:
                    return workInPlan.getWorkersInPlan().get(rowIndex).getLaborContent();
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
                return "Трудоемкость";
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
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ((rowIndex >= 0) && (rowIndex < workInPlan.getWorkersInPlan().size())) {
            switch (columnIndex) {
                case 0: {
                    // Вначале надо проверить - а вдруг уже такой работник-то есть в списке?
                    // Причем он должен быть не на rowIndex месте
                    Worker worker = (Worker) aValue;
                    boolean duplicate = false;
                    for (int i = 0; i < workInPlan.getWorkersInPlan().size(); i++) {
                        if (i != rowIndex)
                            if (workInPlan.getWorkersInPlan().get(i).getWorker() == worker) {
                                duplicate = true;
                                break;
                            }
                    }
                    if (!duplicate) {
                        workInPlan.getWorkersInPlan().get(rowIndex).setWorker(worker);
                        Starter.getMainForm().updateTotalWorkerLabor(worker);
                    }
                    break;
                }
                case 1: {
                    workInPlan.getWorkersInPlan().get(rowIndex).setLaborContent((Double) aValue);
                    workInPlan.updateLaborTotal();
                    Starter.getMainForm().updateTotalWorkerLabor(workInPlan.getWorkersInPlan().get(rowIndex).getWorker());
                }
            }
        }
    }
}
