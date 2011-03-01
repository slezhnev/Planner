import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 12.01.11
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
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
        finishResultsEdit.setText(workToEdit.getFinishDoc());
        reserveEdit.setText(workToEdit.getReserve());
        getContentPane().add(panel1);
        setBounds(0, 0, 900, 800);
        //setLocationRelativeTo(locationComp);
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Сохранить")) {
            res = new WorkInPlan(nameWorkEdit.getText(), resultsEdit.getText(), finishDateEdit.getText(),
                    reserveEdit.getText(), finishResultsEdit.getText());
        }
        dialog.setVisible(false);
    }
}
