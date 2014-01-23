import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 04.01.11
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public class Starter {

    private static MainForm mainForm = null;

    static public void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // Setting UI
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        //Create and set up the window.
        JFrame frame = new JFrame("Planner");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent e) {
                // Do nothing
            }

            public void windowClosing(WindowEvent e) {
                if (getMainForm().isDataChanged()) {
                    if (JOptionPane.showConfirmDialog(null, "План был изменен. Вы уверены, что хотите завершить работу без сохранения?",
                            "Завершение работы", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }

            public void windowClosed(WindowEvent e) {
                // Do nothing
            }

            public void windowIconified(WindowEvent e) {
                // Do nothing
            }

            public void windowDeiconified(WindowEvent e) {
                // Do nothing
            }

            public void windowActivated(WindowEvent e) {
                // Do nothing
            }

            public void windowDeactivated(WindowEvent e) {
                // Do nothing
            }
        });

        //Add the ubiquitous "Hello World" label.
        frame.getContentPane().add(getMainForm().getMainPanel());

        getMainForm().loadLastPlan();
        //Display the window.
        //frame.pack();
        frame.setBounds(10, 10, 800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static MainForm getMainForm() {
        if (mainForm == null) mainForm = new MainForm();
        return mainForm;
    }
}
