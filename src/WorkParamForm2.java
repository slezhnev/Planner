import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Редактирование параметров одной работы (версия 2)
 */
@SuppressWarnings("serial")
public class WorkParamForm2 extends JDialog implements ActionListener,
        ItemListener, ListSelectionListener {
    private JPanel contentPane;
    private JButton saveBtn;
    private JButton cancelBtn;
    private JTextArea nameWorkEdit;
    private JCheckBox makedCheckBox;
    private JFormattedTextField makerPercentEdit;
    private JComboBox<PlanUtils.WorkTypes> workTypeCB;
    private JTable stagesTable;
    private JButton addStageBtn;
    private JButton editStageBtn;
    private JButton delStageBtn;
    private JButton addWorkInStageBtn;
    private JButton editWorkInStageBtn;
    private JButton delWorkInStageBtn;
    private JTable worksInStageTable;
    private JButton moveUpBtn;
    private JButton moveDownBtn;
    private JButton moveUpWorkInStageBtn;
    private JButton moveDownWorkInStageBtn;

    /**
     * Результат редактирования
     */
    private WorkInPlan res = null;

    /**
     * Отображает диалог
     * 
     * @param frameComp
     *            Главный frame приложения
     * @param workToEdit
     *            Работа для редактирования
     * @return Измененную работу, или null - если сохранять ничего не надо
     */
    public static WorkInPlan showDialog(Component frameComp,
            WorkInPlan workToEdit) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        WorkParamForm2 dialog = new WorkParamForm2(frame, workToEdit);
        dialog.setVisible(true);
        return dialog.res;
    }

    private WorkParamForm2(Frame frame, WorkInPlan workToEdit) {
        super(frame, "Параметры работы", true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(saveBtn);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1024, 600);
        setLocationRelativeTo(null);
        //
        getRootPane().registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBtn.doClick();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        //
        DefaultComboBoxModel<PlanUtils.WorkTypes> cbModel =
                new DefaultComboBoxModel<>();
        for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
            cbModel.addElement(type);
        }
        workTypeCB.setModel(cbModel);
        //
        res = workToEdit.clone();
        //
        makedCheckBox.addItemListener(this);
        saveBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        addStageBtn.addActionListener(this);
        editStageBtn.addActionListener(this);
        delStageBtn.addActionListener(this);
        moveUpBtn.addActionListener(this);
        moveDownBtn.addActionListener(this);
        //
        WorkInStageActionListener workAL = new WorkInStageActionListener();
        addWorkInStageBtn.addActionListener(workAL);
        editWorkInStageBtn.addActionListener(workAL);
        delWorkInStageBtn.addActionListener(workAL);
        moveUpWorkInStageBtn.addActionListener(workAL);
        moveDownWorkInStageBtn.addActionListener(workAL);
        //
        stagesTable.setDefaultRenderer(String.class, new MultilineTableCell());
        stagesTable.getSelectionModel().addListSelectionListener(this);
        stagesTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        stagesTable.setModel(new StageTableModel(
                new ArrayList<WorkInPlan.WorkStage>()));
        //
        worksInStageTable.setDefaultRenderer(String.class,
                new MultilineTableCell());
        worksInStageTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        worksInStageTable.setModel(new WorkInStageTableModel(
                new ArrayList<WorkInPlan.WorkInStage>()));
        //
        // if (workToEdit != null) {
        nameWorkEdit.setText(workToEdit.getName());
        makedCheckBox.setSelected(workToEdit.isMaked());
        makerPercentEdit.setValue(workToEdit.getMakedPercent());
        workTypeCB.setSelectedItem(workToEdit.getWorkType());
        stagesTable.setModel(new StageTableModel(res.getStages()));
        /*
         * } else { nameWorkEdit.setText(""); makedCheckBox.setSelected(false);
         * makerPercentEdit.setValue(0.0);
         * workTypeCB.setSelectedItem(PlanUtils.WorkTypes.INNER);
         * stagesTable.setModel(new StageTableModel( new
         * ArrayList<WorkInPlan.WorkStage>())); }
         */
        if (workToEdit.isMaked()) {
            itemStateChanged(new ItemEvent(makedCheckBox,
                    ItemEvent.ITEM_STATE_CHANGED, makedCheckBox,
                    ItemEvent.SELECTED));
        } else {
            itemStateChanged(new ItemEvent(makedCheckBox,
                    ItemEvent.ITEM_STATE_CHANGED, makedCheckBox,
                    ItemEvent.DESELECTED));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Сохранить": {
                // Сохраняем дополнительные параметры
                res.setName(nameWorkEdit.getText());
                res.setMaked(makedCheckBox.isSelected());
                res.setMakedPercent((Double) makerPercentEdit.getValue());
                res.setWorkType((PlanUtils.WorkTypes) workTypeCB
                        .getSelectedItem());
                setVisible(false);
                break;
            }
            case "Отмена": {
                res = null;
                setVisible(false);
                break;
            }
            case "Добавить": {
                res.getStages().add(new WorkInPlan.WorkStage(""));
                ((StageTableModel) stagesTable.getModel())
                        .fireTableDataChanged();
                stagesTable.getSelectionModel().setSelectionInterval(
                        res.getStages().size() - 1, res.getStages().size() - 1);
                editStageBtn.doClick();
                break;
            }
            case "Изменить": {
                int row = stagesTable.getSelectedRow();
                if ((row > -1) && (row < res.getStages().size())) {
                    String stageName =
                            WorkStageParamDialog.showDialog(stagesTable, res
                                    .getStages().get(row).getName());
                    if (stageName != null) {
                        res.getStages().get(row).setName(stageName);
                        ((StageTableModel) stagesTable.getModel())
                                .fireTableDataChanged();
                    }
                }
                break;
            }
            case "Удалить": {
                int row = stagesTable.getSelectedRow();
                if ((row > -1) && (row < res.getStages().size())) {
                    if (JOptionPane
                            .showConfirmDialog(
                                    worksInStageTable,
                                    "Вы уверены, "
                                            + "что хотите удалить выбранный этап? /n При этом будут удалены все работы выбранного "
                                            + "этапа", "Удаление этапа",
                                    JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                        res.getStages().remove(row);
                        // Сбросим модель у работ в этапе
                        worksInStageTable.setModel(new WorkInStageTableModel(
                                new ArrayList<WorkInPlan.WorkInStage>()));
                        // Бабахем изменением
                        ((StageTableModel) stagesTable.getModel())
                                .fireTableDataChanged();
                    }
                }
                break;
            }
            case "^": {
                int row = stagesTable.getSelectedRow();
                if ((row > 0) && (res.getStages().size() > 1)) {
                    // Меняем местами row и row-1 строки
                    WorkInPlan.WorkStage stage = res.getStages().get(row - 1);
                    res.getStages().set(row - 1, res.getStages().get(row));
                    res.getStages().set(row, stage);
                    ((StageTableModel) stagesTable.getModel())
                            .fireTableDataChanged();
                    stagesTable.getSelectionModel().setSelectionInterval(
                            row - 1, row - 1);
                }
                break;
            }
            case "\\/": {
                int row = stagesTable.getSelectedRow();
                if ((row > -1) && (row < (res.getStages().size() - 1)) &&
                        (res.getStages().size() > 1)) {
                    // Меняем местами row и row+1 строки
                    WorkInPlan.WorkStage stage = res.getStages().get(row + 1);
                    res.getStages().set(row + 1, res.getStages().get(row));
                    res.getStages().set(row, stage);
                    ((StageTableModel) stagesTable.getModel())
                            .fireTableDataChanged();
                    stagesTable.getSelectionModel().setSelectionInterval(
                            row + 1, row + 1);
                }
                break;
            }
            default:
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            makerPercentEdit.setEnabled(true);
        } else if (e.getStateChange() == ItemEvent.SELECTED) {
            makerPercentEdit.setEnabled(false);
            makerPercentEdit.setValue(100.0);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // Все делаем только после того, как выбор пользователя завершен
            ListSelectionModel selM = stagesTable.getSelectionModel();
            int selectedRow = res.getStages().size();
            if (selM.isSelectedIndex(e.getFirstIndex())) {
                selectedRow = e.getFirstIndex();
            } else if (selM.isSelectedIndex(e.getLastIndex())) {
                selectedRow = e.getLastIndex();
            }
            if (selectedRow < res.getStages().size()) {
                worksInStageTable.setModel(new WorkInStageTableModel(res
                        .getStages().get(selectedRow).getWorksInStage()));
                ((WorkInStageTableModel) worksInStageTable.getModel())
                        .fireTableDataChanged();
            }
        }
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT
     * edit this method OR call it in your code!
     * 
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10,
                10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0),
                -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null,
                null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0),
                -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
                0, false));
        saveBtn = new JButton();
        saveBtn.setText("Сохранить");
        panel2.add(saveBtn, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelBtn = new JButton();
        cancelBtn.setText("Отмена");
        panel2.add(cancelBtn, new GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
                0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Наименование работы:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.25;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Выполнено:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label2, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel4, gbc);
        makedCheckBox = new JCheckBox();
        makedCheckBox.setText("");
        panel4.add(makedCheckBox, BorderLayout.WEST);
        makerPercentEdit = new JFormattedTextField();
        makerPercentEdit.setEnabled(false);
        panel4.add(makerPercentEdit, BorderLayout.CENTER);
        final JLabel label3 = new JLabel();
        label3.setText(" % ");
        panel4.add(label3, BorderLayout.EAST);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel5, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spacer1, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(panel6, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel7, gbc);
        addStageBtn = new JButton();
        addStageBtn.setText("Добавить");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(addStageBtn, gbc);
        editStageBtn = new JButton();
        editStageBtn.setText("Изменить");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(editStageBtn, gbc);
        delStageBtn = new JButton();
        delStageBtn.setText("Удалить");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(delStageBtn, gbc);
        moveUpBtn = new JButton();
        moveUpBtn.setText("^");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(moveUpBtn, gbc);
        moveDownBtn = new JButton();
        moveDownBtn.setText("\\/");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(moveDownBtn, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(spacer2, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(scrollPane1, gbc);
        stagesTable = new JTable();
        stagesTable.setAutoResizeMode(2);
        stagesTable.setPreferredScrollableViewportSize(new Dimension(150, 400));
        scrollPane1.setViewportView(stagesTable);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(panel8, gbc);
        panel8.setBorder(BorderFactory.createTitledBorder(BorderFactory
                .createLineBorder(new Color(-16777216)), "Работы этапа"));
        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(scrollPane2, gbc);
        worksInStageTable = new JTable();
        scrollPane2.setViewportView(worksInStageTable);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(panel9, gbc);
        addWorkInStageBtn = new JButton();
        addWorkInStageBtn.setText("Добавить");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(addWorkInStageBtn, gbc);
        editWorkInStageBtn = new JButton();
        editWorkInStageBtn.setText("Изменить");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(editWorkInStageBtn, gbc);
        delWorkInStageBtn = new JButton();
        delWorkInStageBtn.setText("Удалить");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(delWorkInStageBtn, gbc);
        moveUpWorkInStageBtn = new JButton();
        moveUpWorkInStageBtn.setText("^");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(moveUpWorkInStageBtn, gbc);
        moveDownWorkInStageBtn = new JButton();
        moveDownWorkInStageBtn.setText("\\/");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(moveDownWorkInStageBtn, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(spacer3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Вид работы:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label4, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer4, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0),
                -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel10, gbc);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel10.add(scrollPane3, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK |
                        GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null,
                0, false));
        nameWorkEdit = new JTextArea();
        scrollPane3.setViewportView(nameWorkEdit);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0),
                -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel11, gbc);
        workTypeCB = new JComboBox<>();
        panel11.add(workTypeCB, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    /**
     * Модель для отображения списка этапов
     */
    private class StageTableModel extends AbstractTableModel {

        private List<WorkInPlan.WorkStage> stages = new ArrayList<>();

        public StageTableModel(List<WorkInPlan.WorkStage> stages) {
            if (stages != null) {
                this.stages = stages;
            }
        }

        @Override
        public int getRowCount() {
            return stages.size();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < stages.size()) {
                return stages.get(rowIndex).getName();
            } else {
                return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            return "Этапы";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    /**
     * Модель для отображения работ в этапе
     */
    private class WorkInStageTableModel extends AbstractTableModel {

        private List<WorkInPlan.WorkInStage> works = new ArrayList<>();

        public List<WorkInPlan.WorkInStage> getWorks() {
            return works;
        }

        private WorkInStageTableModel(List<WorkInPlan.WorkInStage> works) {
            if (works != null) {
                this.works = works;
            } else {
                works = new ArrayList<>();
            }
        }

        @Override
        public String getColumnName(int column) {
            return "Перечень работ";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public int getRowCount() {
            if (works == null) {
                return 0;
            } else {
                return works.size();
            }
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if ((works != null) && (rowIndex < works.size())) {
                return works.get(rowIndex).getWorks();
            } else {
                return null;
            }
        }
    }

    /**
     * Обработчик кнопок при редактировании worksInStageTable
     */
    private class WorkInStageActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Добавить": {
                    List<WorkInPlan.WorkInStage> works =
                            ((WorkInStageTableModel) worksInStageTable
                                    .getModel()).getWorks();
                    works.add(new WorkInPlan.WorkInStage("", "", "", ""));
                    ((WorkInStageTableModel) worksInStageTable.getModel())
                            .fireTableDataChanged();
                    worksInStageTable.getSelectionModel().setSelectionInterval(
                            works.size() - 1, works.size() - 1);
                    // Сразу будем и редактировать
                    editWorkInStageBtn.doClick();
                    break;
                }
                case "Изменить": {
                    List<WorkInPlan.WorkInStage> works =
                            ((WorkInStageTableModel) worksInStageTable
                                    .getModel()).getWorks();
                    int row = worksInStageTable.getSelectedRow();
                    if ((row > -1) && (row < works.size())) {
                        WorkInPlan.WorkInStage work =
                                WorkInStageParamForm.showDialog(
                                        worksInStageTable, works.get(row));
                        if (work != null) {
                            works.set(row, work);
                            ((WorkInStageTableModel) worksInStageTable
                                    .getModel()).fireTableDataChanged();
                        }
                    }
                    break;
                }
                case "Удалить": {
                    List<WorkInPlan.WorkInStage> works =
                            ((WorkInStageTableModel) worksInStageTable
                                    .getModel()).getWorks();
                    int row = worksInStageTable.getSelectedRow();
                    if ((row > -1) && (row < works.size())) {
                        if (JOptionPane
                                .showConfirmDialog(
                                        worksInStageTable,
                                        "Вы уверены, "
                                                + "что хотите удалить выбранную работу в этапе?",
                                        "Удаление работы",
                                        JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                            works.remove(row);
                            ((WorkInStageTableModel) worksInStageTable
                                    .getModel()).fireTableDataChanged();
                        }
                    }
                    break;
                }
                case "^": {
                    List<WorkInPlan.WorkInStage> works =
                            ((WorkInStageTableModel) worksInStageTable
                                    .getModel()).getWorks();
                    int row = worksInStageTable.getSelectedRow();
                    if ((row > 0) && (works.size() > 1)) {
                        // Меняем местами row и row-1
                        WorkInPlan.WorkInStage work = works.get(row - 1);
                        works.set(row - 1, works.get(row));
                        works.set(row, work);
                        ((WorkInStageTableModel) worksInStageTable.getModel())
                                .fireTableDataChanged();
                        worksInStageTable.getSelectionModel()
                                .setSelectionInterval(row - 1, row - 1);
                    }
                    break;
                }
                case "\\/": {
                    List<WorkInPlan.WorkInStage> works =
                            ((WorkInStageTableModel) worksInStageTable
                                    .getModel()).getWorks();
                    int row = worksInStageTable.getSelectedRow();
                    if ((row > -1) && (row < (works.size() - 1)) &&
                            (works.size() > 1)) {
                        // Меняем местами row и row+1
                        WorkInPlan.WorkInStage work = works.get(row + 1);
                        works.set(row + 1, works.get(row));
                        works.set(row, work);
                        ((WorkInStageTableModel) worksInStageTable.getModel())
                                .fireTableDataChanged();
                        worksInStageTable.getSelectionModel()
                                .setSelectionInterval(row + 1, row + 1);
                    }
                    break;
                }
                default:
            }
        }
    }

    /**
     * Multiline table cell rendered Сперто с
     * http://stackoverflow.com/questions/
     * 965023/how-to-wrap-lines-in-a-jtable-cell
     */
    public class MultilineTableCell implements TableCellRenderer {
        class CellArea extends DefaultTableCellRenderer {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            private String text;
            protected int rowIndex;
            protected int columnIndex;
            protected JTable table;
            protected Font font;
            private int paragraphStart, paragraphEnd;
            private LineBreakMeasurer lineMeasurer;

            public CellArea(String s, JTable tab, int row, int column,
                    boolean isSelected) {
                text = s;
                rowIndex = row;
                columnIndex = column;
                table = tab;
                font = table.getFont();
                if (isSelected) {
                    setForeground(table.getSelectionForeground());
                    setBackground(table.getSelectionBackground());
                }
            }

            public void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                if (text != null && !text.isEmpty()) {
                    Graphics2D g = (Graphics2D) gr;
                    if (lineMeasurer == null) {
                        AttributedCharacterIterator paragraph =
                                new AttributedString(text).getIterator();
                        paragraphStart = paragraph.getBeginIndex();
                        paragraphEnd = paragraph.getEndIndex();
                        FontRenderContext frc = g.getFontRenderContext();
                        lineMeasurer =
                                new LineBreakMeasurer(paragraph, BreakIterator
                                        .getWordInstance(), frc);
                    }
                    float breakWidth =
                            (float) table.getColumnModel().getColumn(
                                    columnIndex).getWidth();
                    float drawPosY = 0;
                    // Set position to the index of the first character in the
                    // paragraph.
                    lineMeasurer.setPosition(paragraphStart);
                    // Get lines until the entire paragraph has been displayed.
                    while (lineMeasurer.getPosition() < paragraphEnd) {
                        // Retrieve next layout. A cleverer program would also
                        // cache
                        // these layouts until the component is re-sized.
                        TextLayout layout = lineMeasurer.nextLayout(breakWidth);
                        // Compute pen x position. If the paragraph is
                        // right-to-left we
                        // will align the TextLayouts to the right edge of the
                        // panel.
                        // Note: this won't occur for the English text in this
                        // sample.
                        // Note: drawPosX is always where the LEFT of the text
                        // is placed.
                        float drawPosX =
                                layout.isLeftToRight() ? 0 : breakWidth -
                                        layout.getAdvance();
                        // Move y-coordinate by the ascent of the layout.
                        drawPosY += layout.getAscent();
                        // Draw the TextLayout at (drawPosX, drawPosY).
                        layout.draw(g, drawPosX, drawPosY);
                        // Move y-coordinate in preparation for next layout.
                        drawPosY += layout.getDescent() + layout.getLeading();
                    }

                    final int height = (int) drawPosY;
                    // table.setRowHeight(rowIndex, height);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            table.setRowHeight(rowIndex, height);
                        }
                    });
                }
            }
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            CellArea area =
                    new CellArea(value.toString(), table, row, column,
                            isSelected);
            return area;
        }

    }
}
