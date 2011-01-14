import javax.swing.table.AbstractTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Сергей
 * Date: 11.01.11
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
class PlanPartModel extends AbstractTableModel {

    public void setPlanPart(PlanPart planPart) {
        this.planPart = planPart;
        fireTableDataChanged();
    }

    private PlanPart planPart;

    public PlanPartModel() {
        planPart = new PlanPart("Tmp", "Tmp");
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
        return planPart.getWorks().size();
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

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (planPart.getWorks().size() > rowIndex) {
            switch (columnIndex) {
                case 0:
                    return planPart.getWorks().get(rowIndex).getName();
//                case 1:
//                    return planPart.getWorks().get(rowIndex).getDesc();
                case 1:
                    return planPart.getWorks().get(rowIndex).getLaborTotal();
                default:
                    return "";
            }
        } else {
            return null;
        }
    }

    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Наим.";
//            case 1:
//                return "Описание";
            case 1:
                return "Общая труд.";
            default:
                return "WTF?!";
        }
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0 : return String.class;
//            case 1 : return String.class;
            case 1 : return Double.class;
            default : return String.class;
        }
        /*if (columnIndex <= 3) {
            Object val = getValueAt(0, columnIndex);
            if (val != null) return val.getClass();
            else return null;
        } else {
            return null;
        }*/
    }

    /**
     * Включаем редактирование первой колонки
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0);
    }

    /**
     * Сохраняет введенные значения
     *
     * @param aValue      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (planPart.getWorks().size() > rowIndex) {
            switch (columnIndex) {
                case 0: {
                    planPart.getWorks().get(rowIndex).setName((String) aValue);
                    break;
                }
//                case 1: {
//                    planPart.getWorks().get(rowIndex).setDesc((String) aValue);
//                    break;
//                }
            }
        }
    }
}
