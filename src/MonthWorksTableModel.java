import javax.swing.table.AbstractTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Сергей
 * Date: 11.01.11
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class MonthWorksTableModel extends AbstractTableModel {

    private int month = 0;

    public void setPlanPart(PlanPart planPart) {
        this.planPart = planPart;
        fireTableDataChanged();
    }

    private PlanPart planPart;

    public MonthWorksTableModel() {
        planPart = new PlanPart("Tmp");
    }


    public int getRowCount() {
        return planPart.getWorks().size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if ((rowIndex > -1) && (rowIndex < planPart.getWorks().size())) {
            switch (columnIndex) {
                case 0:
                    return planPart.getWorks().get(rowIndex).getName();
                case 1:
                    return planPart.getWorks().get(rowIndex).getLaborTotal();
                case 2:
                    return planPart.getWorks().get(rowIndex).calcRestLabor(month);
            }
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Наименование";
            case 1:
                return "Общ.труд.";
            case 2:
                return "Осталось";
            default:
                return "WTF?!";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Double.class;
            case 2:
                return Double.class;
            default:
                return String.class;
        }
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
