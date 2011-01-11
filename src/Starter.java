import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 04.01.11
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public class Starter {

    private static MainForm mainForm = null;

    static public void main (String[] args) {
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
        } catch (Exception e) {}
        //Create and set up the window.
        JFrame frame = new JFrame("Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        frame.getContentPane().add(getMainForm().getMainPanel());

        //Display the window.
        //frame.pack();
        frame.setBounds(10,10,800,600);
        frame.setVisible(true);

    }

    public static MainForm getMainForm() {
        if (mainForm == null) mainForm = new MainForm();
        return mainForm;
    }
}
