import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Редактирование параметров работы в плане
 */
public class WorkParamForm extends JDialog implements ActionListener, ItemListener {
    private JPanel panel1;
    private JTextArea nameWorkEdit;
    private JFormattedTextField finishDateEdit;
    private JButton saveBtn;
    private JButton cancelBtn;
    private JTextArea resultsEdit;
    private JTextArea finishResultsEdit;
    private JTextArea reserveEdit;
    private JCheckBox makedCheckBox;
    private JComboBox workTypeCB;
    private JFormattedTextField makerPercentEdit;
    //
    private static WorkInPlan res = null;
    private static WorkParamForm dialog;

    public static WorkInPlan showDialog(Component frameComp,
                                        Component locationComp,
                                        WorkInPlan workToEdit) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new WorkParamForm(frame, locationComp, workToEdit);
        dialog.setVisible(true);
        return res;
    }

    private WorkParamForm(Frame frame,
                          Component locationComp,
                          WorkInPlan workToEdit) {
        super(frame, "Параметры работы", true);
        //
        res = null;
        // Кнопки
        cancelBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        //
        nameWorkEdit.setText(workToEdit.getName());
        resultsEdit.setText(workToEdit.getDesc());
        finishDateEdit.setText(workToEdit.getEndDate());
        // Trying to fire itemStateChange listener
        makedCheckBox.setSelected(workToEdit.isMaked());
        if (workToEdit.isMaked()) {
            itemStateChanged(new ItemEvent(makedCheckBox, ItemEvent.ITEM_STATE_CHANGED, makedCheckBox, ItemEvent.SELECTED));
        } else {
            itemStateChanged(new ItemEvent(makedCheckBox, ItemEvent.ITEM_STATE_CHANGED, makedCheckBox, ItemEvent.DESELECTED));
        }
        makerPercentEdit.setValue(workToEdit.getMakedPercent());
        //
        finishResultsEdit.setText(workToEdit.getFinishDoc());
        reserveEdit.setText(workToEdit.getReserve());
        //
        DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
        for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
            cbModel.addElement(type);
        }
        workTypeCB.setModel(cbModel);
        //
        workTypeCB.setSelectedItem(workToEdit.getWorkType());
        getContentPane().add(panel1);
        setBounds(0, 0, 900, 800);
        //setLocationRelativeTo(locationComp);
        setLocationRelativeTo(null);
        makedCheckBox.addItemListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Сохранить")) {
            res = new WorkInPlan(nameWorkEdit.getText(), resultsEdit.getText(), finishDateEdit.getText(),
                    reserveEdit.getText(), finishResultsEdit.getText(), (PlanUtils.WorkTypes) workTypeCB.getSelectedItem());
            res.setMaked(makedCheckBox.isSelected());
            res.setMakedPercent((Double) makerPercentEdit.getValue());
        }
        dialog.setVisible(false);
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            makerPercentEdit.setEnabled(true);
        } else if (e.getStateChange() == ItemEvent.SELECTED) {
            makerPercentEdit.setEnabled(false);
            makerPercentEdit.setValue(100.0);
        }
    }
}
