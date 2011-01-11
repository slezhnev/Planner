import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 04.01.11
 * Time: 23:11
 * To change this template use File | Settings | File Templates.
 */
public class MainForm {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JTable workersTable;
    private JButton saveWorkersBtn;
    private JButton loadWorkersBtn;
    private JButton addWorkerBtn;
    private JButton delWorkerBtn;
    private JTabbedPane quarterPlan_divisions;
    private JTabbedPane tabbedPane2;
    private JButton savePlanBtn;
    private JButton loadPlanBtn;
    private JButton планButton;
    private JButton отчетButton;
    private JTable planPartTable;
    private JTable workersInPlanTable;
    private JButton addWorkBtn;
    private JButton delWorkBtn;
    private JButton addWorkerToWorkBtn;
    private JButton delWorkerFromWorkBtn;
    private JTextField month1Name;
    private JButton отчетButton1;
    private JTextField month2Name;
    private JButton отчетButton2;
    private JTextField month3Name;
    private JButton отчетButton3;
    private JButton moveUpPlanPartBtn;
    private JButton moveDownPlanPartBtn;

    public Vector<Worker> getWorkers() {
        return workers;
    }

    //
    private Vector<Worker> workers = new Vector<Worker>();
    private ArrayList<PlanPart> plan = new ArrayList<PlanPart>();
    //

    public MainForm() {
        //
        Worker worker1 = new Worker("Рогов П.?.", 0.0);
        workers.add(worker1);
        Worker worker2 = new Worker("Золотов А.?.", 0.0);
        workers.add(worker2);
        workers.add(new Worker("Полулях А.?.", 0.0));
        workers.add(new Worker("Горелов А.?.", 0.0));
        workers.add(new Worker("Воронин Р.?.", 0.0));
        workers.add(new Worker("Гуськов С.?.", 0.0));
        workers.add(new Worker("Мармер В.?.", -1.0));
        workers.add(new Worker("Зореев В.?.", -1.0));
        workers.add(new Worker("Шварцман А.?.", 0.0));
        //
        plan.add(new PlanPart("Собств.работы - новые"));
        plan.add(new PlanPart("Собств.работы - продолжение"));
        plan.add(new PlanPart("СМК"));
        plan.add(new PlanPart("Корр.мероприятия"));
        //
        plan.get(0).getWorks().add(new WorkInPlan("Работа 1", "Описание 1"));
        plan.get(0).getWorks().get(0).getWorkersInPlan().add(new WorkerInPlan(worker1, 3.5));
        plan.get(0).getWorks().get(0).getWorkersInPlan().add(new WorkerInPlan(worker2, 5.5));
        plan.get(0).getWorks().add(new WorkInPlan("Работа 2", "Описание 2"));
        plan.get(0).getWorks().add(new WorkInPlan("Работа 3", "Описание 3"));
        //
        quarterPlan_divisions.removeAll();
        for (PlanPart part : plan) {
            quarterPlan_divisions.addTab(part.getName(), null);
        }
        quarterPlan_divisions.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                ((PlanPartModel) planPartTable.getModel()).setPlanPart(currPart);
                // Сбрасываем еще список работников
                ((WorkersInPlanTableModel) workersInPlanTable.getModel()).clearWorkInPlan();
            }
        });
        //
        workersTable.setModel(new WorkersTableModel());
        workersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //
        PlanPartModel planPartModel = new PlanPartModel();
        planPartTable.setModel(planPartModel);
        planPartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planPartModel.setPlanPart(plan.get(0));
        planPartTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                if ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < currPart.getWorks().size())) {
                    ((WorkersInPlanTableModel) workersInPlanTable.getModel()).setWorkInPlan(currPart.getWorks().get(planPartTable.getSelectedRow()));
                }
            }
        });
        //
        workersInPlanTable.setModel(new WorkersInPlanTableModel());
        workersInPlanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn workerColumn = workersInPlanTable.getColumnModel().getColumn(0);
        JComboBox workerSelectCB = new JComboBox();
        workerSelectCB.setModel(new DefaultComboBoxModel(workers));
        workerColumn.setCellEditor(new DefaultCellEditor(workerSelectCB));
        //
        addWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                currPart.getWorks().add(new WorkInPlan("", ""));
                ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
                ((WorkersInPlanTableModel) workersInPlanTable.getModel()).clearWorkInPlan();
            }
        });
        delWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (planPartTable.getSelectedRow() != -1) {
                    if (JOptionPane.showConfirmDialog(null, "Вы уверены что хотите удалить выбранную работу?", "Удаление", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                        currPart.getWorks().remove(planPartTable.getSelectedRow());
                        ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
                        ((WorkersInPlanTableModel) workersInPlanTable.getModel()).clearWorkInPlan();
                    }
                }
            }
        });
        addWorkerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                workers.add(new Worker("", 0.0));
                ((WorkersTableModel) workersTable.getModel()).fireTableDataChanged();
            }
        });
        delWorkerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((workersTable.getSelectedRow() != -1) && (workersTable.getSelectedRow() < workers.size())) {
                    // Тут вначале проверим - а может у нас этот работник фигурирует в какой работе?!
                    Worker worker = workers.get(workersTable.getSelectedRow());
                    boolean presentInWork = false;
                    for (PlanPart planPart : plan) {
                        for (WorkInPlan work : planPart.getWorks()) {
                            for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                                if (workerInPlan.getWorker() == worker) {
                                    presentInWork = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (presentInWork) {
                        JOptionPane.showMessageDialog(null, "Работник занят в какой-то работе. Вначале удалите его из всех работ",
                                "Удаление работника", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить выбранного работника?",
                                "Удаление работника", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            workers.remove(worker);
                            ((WorkersTableModel) workersTable.getModel()).fireTableDataChanged();
                        }
                    }
                }
            }
        });
        //
        addWorkerToWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                if ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < currPart.getWorks().size())) {
                    WorkInPlan work = currPart.getWorks().get(planPartTable.getSelectedRow());
                    // Теперь надо из списка работников добавить ПЕРВОГО, кого тут нет
                    for (Worker worker : workers) {
                        boolean found = false;
                        for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                            if (workerInPlan.getWorker() == worker) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            work.getWorkersInPlan().add(new WorkerInPlan(worker, 0.0));
                            ((WorkersInPlanTableModel) workersInPlanTable.getModel()).fireTableDataChanged();
                            break;
                        }
                    }
                }
            }
        });
        delWorkerFromWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                if ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < currPart.getWorks().size())) {
                    WorkInPlan work = currPart.getWorks().get(planPartTable.getSelectedRow());
                    if ((workersInPlanTable.getSelectedRow() > -1) && (workersInPlanTable.getSelectedRow() < work.getWorkersInPlan().size())) {
                        if (JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить выбранного работника из работы?",
                                "Удаление работника из работы", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            WorkerInPlan worker = work.getWorkersInPlan().get(workersInPlanTable.getSelectedRow());
                            work.getWorkersInPlan().remove(worker);
                            ((WorkersInPlanTableModel) workersInPlanTable.getModel()).fireTableDataChanged();
                        }
                    }
                }
            }
        });
        moveUpPlanPartBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                movePlanPart(-1);
            }
        });
        moveDownPlanPartBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                movePlanPart(1);
            }
        });
    }

    /**
     * Двигает выбранный элемент в planPartTable вверх или вниз
     *
     * @param moveDiff -1 - сдвинуть вверх, 1 - сдвинуть вниз.
     *                 При других значениях или при невозможности двигать - ничего не делает
     */
    private void movePlanPart(int moveDiff) {
        PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
        if (((moveDiff == -1) && ((planPartTable.getSelectedRow() > 0) && (planPartTable.getSelectedRow() < currPart.getWorks().size()))) ||
                ((moveDiff == 1) && ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < (currPart.getWorks().size() - 1))))) {
            // Значит тут можно попробовать подвигать...
            int selPos = planPartTable.getSelectedRow();
            WorkInPlan work = currPart.getWorks().get(selPos);
            currPart.getWorks().remove(work);
            currPart.getWorks().add(selPos + moveDiff, work);
            ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
            planPartTable.getSelectionModel().setSelectionInterval(selPos + moveDiff, selPos + moveDiff);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void planPartTotalLaborChanged() {
        if (planPartTable.getSelectedRow() != -1) {
            ((PlanPartModel) planPartTable.getModel()).fireTableCellUpdated(planPartTable.getSelectedRow(), 2);
        } else {
            ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
        }
    }

    public void updateTotalWorkerLabor(Worker worker) {
        double total = 0;
        for (PlanPart planPart : plan) {
            for (WorkInPlan work : planPart.getWorks()) {
                for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                    if (workerInPlan.getWorker() == worker) {
                        total = total + workerInPlan.getLaborContent();
                    }
                }
            }
        }
        worker.setLaborContentTotal(total);
        ((WorkersTableModel) workersTable.getModel()).fireTableCellUpdated(workers.indexOf(worker), 1);
    }

    /**
     * Класс - модель списка работников
     */
    private class WorkersTableModel extends AbstractTableModel {
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
            return 2;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Имя";
                case 1:
                    return "Общая трудоемкость";
                default:
                    return "Этого тут быть не должно!";
            }
        }

        @Override
        public Class getColumnClass(int col) {
            if (col <= 2) {
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
                    return workers.get(rowIndex).getLaborContentTotal();
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            switch (col) {
                case 0:
                    workers.get(row).setName((String) value);
                    break;
                case 1:
                    workers.get(row).setLaborContentTotal((Double) value);
                    break;
            }
            fireTableCellUpdated(row, col);
        }


    }
}
