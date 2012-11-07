import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Редактирование параметров работы в плане
 */
public class WorkParamForm extends JDialog implements ActionListener {
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
        makedCheckBox.setSelected(workToEdit.isMaked());
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
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Сохранить")) {
            res = new WorkInPlan(nameWorkEdit.getText(), resultsEdit.getText(), finishDateEdit.getText(),
                    reserveEdit.getText(), finishResultsEdit.getText(), (PlanUtils.WorkTypes) workTypeCB.getSelectedItem());
            res.setMaked(makedCheckBox.isSelected());
        }
        dialog.setVisible(false);
    }
}
