import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 04.01.11
 * Time: 23:11
 * To change this template use File | Settings | File Templates.
 */
public class MainForm  implements ChangeListener {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JTable workersTable;
    private JButton saveWorkers;
    private JButton loadWorkers;
    private JTabbedPane tabbedPane2;
    private JTextField month1Name;
    private JTextField month2Name;
    private JTextField month3Name;
    private JButton savePlanBtn;
    private JButton loadPlanBtn;
    private JButton планButton;
    private JButton отчетButton;
    private JButton отчетButton1;
    private JButton отчетButton2;
    private JButton отчетButton3;
    private JButton добавитьButton;
    private JButton удалитьButton;
    private JTabbedPane quarterPlan_divisions;
    private JTable table1;
    private JTable table2;
    private JButton добавитьButton1;
    private JButton удалитьButton1;
    private JButton добавитьButton2;
    private JButton удалитьButton2;

    //
    private ArrayList<Worker> workers = new ArrayList<Worker>();
    private ArrayList<PlanPart> plan = new ArrayList<PlanPart>();
    //

    public MainForm() {
        //
        workers.add(new Worker("Работник1", 0.0));
        workers.add(new Worker("Работник2", 0.0));
        workers.add(new Worker("Работник3", 0.0));
        workers.add(new Worker("Работник4", -1.0));
        //
        plan.add(new PlanPart("Собств.работы - новые"));
        plan.add(new PlanPart("Собств.работы - продолжение"));
        plan.add(new PlanPart("СМК"));
        plan.add(new PlanPart("Корр.мероприятия"));
        //
        workersTable.setModel(new AbstractTableModel() {
            public int getRowCount() {
                return workers.size();
            }

            public int getColumnCount() {
                return 2;
            }

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

            public Class getColumnClass(int col) {
                if (col <= 2) {
                    return getValueAt(0, col).getClass();
                } else {
                    return null;
                }
            }

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

            public boolean isCellEditable(int row, int col) {
                return true;
            }

            public void setValueAt(Object value, int row, int col) {
                switch (col) {
                    case 0:
                        workers.get(row).setName((String) value); break;
                    case 1:
                        workers.get(row).setLaborContentTotal((Double) value); break;
                }
                fireTableCellUpdated(row, col);
            }


        });
        //
        quarterPlan_divisions.removeAll();
        for (PlanPart part : plan) {
            quarterPlan_divisions.addTab(part.getName(), null);
        }
        //
        quarterPlan_divisions.getModel().addChangeListener(this);
        stateChanged(new ChangeEvent(this));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void stateChanged(ChangeEvent e) {
        updatePlanPart();
    }

    private void updatePlanPart() {
        //To change body of created methods use File | Settings | File Templates.
    }
}
