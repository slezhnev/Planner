import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class AnalyzeForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList list1;
    private JButton loadPlanBtn;
    private JButton deletePlan;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JTable table2;
    private JButton analyzeBtn;
    private JTree tree1;
    private JCheckBox withoutLaborCB;
    private JButton saveBtn;
    private JTable table3;
    private JTable table4;

    /**
     * Default constructor
     */
    public AnalyzeForm() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        list1.setModel(new DefaultListModel());


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Закрываем
                setVisible(false);
            }
        });
        loadPlanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("./plans"));
                fc.setDialogTitle("Загрузка плана");
                fc.setFileFilter(new FileNameExtensionFilter("Файлы планов", "plan"));
                fc.setMultiSelectionEnabled(true);
                File[] files;
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    files = fc.getSelectedFiles();
                } else {
                    return;
                }
                if (files != null) {
                    // Проверяем наличие в списке таких файлов
                    for (File file : files) {
                        boolean mustAdd = true;
                        for (Enumeration en = ((DefaultListModel) list1.getModel()).elements(); en.hasMoreElements(); ) {
                            File enFile = (File) en.nextElement();
                            if (file.equals(enFile)) {
                                // Если такой файл уже есть - тупо сваливаем
                                mustAdd = false;
                                break;
                            }
                        }
                        // Если такого не найдено - добавляем с список
                        if (mustAdd) {
                            ((DefaultListModel) list1.getModel()).addElement(file);
                        }
                    }
                }
            }
        });
        deletePlan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (list1.getSelectedIndex() != -1) {
                    if (JOptionPane.showConfirmDialog(AnalyzeForm.this, "Убрать выбранный план из анализа?", "Анализ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                        ((DefaultListModel) list1.getModel()).remove(list1.getSelectedIndex());
                    }
                }
            }
        });
        analyzeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doAnalyze();

            }
        });
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    doSaveToCSV();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(AnalyzeForm.this, "При сохранении возникла ошибка - " + e1.getClass().getName() + " - " + e1.getLocalizedMessage(),
                            "Ошибка при сохранении", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalStateException e1) {
                    JOptionPane.showMessageDialog(AnalyzeForm.this, e1.getLocalizedMessage(),
                            "Ошибка при сохранении", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Сохраняет результаты анализа в CSV
     *
     * @throws java.io.IOException В случае пробелем при сохранении
     * @throws IllegalStateException
     *                             В случае если анализ не проводился
     */
    private void doSaveToCSV() throws IOException, IllegalStateException {
        // Вначале проверим - а анализ-то проводился?
        if ((!(table1.getModel() instanceof TotalLaboriousnessModel)) || (!(table2.getModel() instanceof PerWorkerLaboriousnessModel))) {
            throw new IllegalStateException("Анализ не проводился");
        }
        //
        PrintWriter fOut;
        //
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setFileFilter(new FileNameExtensionFilter("CSV файлы", "csv"));
        fc.setDialogTitle("Выбор файла для сохранения");
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            fOut = new PrintWriter(new FileWriter(fc.getSelectedFile()));
        } else {
            return;
        }
        // Поехали сохранять
        fOut.println("Общая информация");
        doSaveTableModel(fOut, table1.getModel());
        fOut.println("По людям");
        doSaveTableModel(fOut, table2.getModel());
        fOut.println("По проектам");
        doSaveTableModel(fOut, table3.getModel());
        // Сохраняем дерево...
        // У нас тут ВСЕГДА фиксированно два или три уровня
        fOut.println("По людям и проектам");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree1.getModel().getRoot();
        for (int worker = 0; worker < root.getChildCount(); worker++) {
            boolean firstLineOnWorker = true;
            // Поехали по видам работ...
            for (int workType = 0; workType < root.getChildAt(worker).getChildCount(); workType++) {
                // Теперь - по работам
                boolean firstLineInWorkType = true;
                if (root.getChildAt(worker).getChildAt(workType).getChildCount() == 0) {
                    // Работ - нет
                    printLineInTree(fOut, firstLineOnWorker, firstLineInWorkType,
                            root.getChildAt(worker).toString(),
                            root.getChildAt(worker).getChildAt(workType).toString(), "-");
                    firstLineOnWorker = false;
                } else {
                    // Поехали по работам
                    for (int work = 0; work < root.getChildAt(worker).getChildAt(workType).getChildCount(); work++) {
                        printLineInTree(fOut, firstLineOnWorker, firstLineInWorkType,
                                root.getChildAt(worker).toString(),
                                root.getChildAt(worker).getChildAt(workType).toString(),
                                root.getChildAt(worker).getChildAt(workType).getChildAt(work).toString().replaceAll("\n", " "));
                        firstLineOnWorker = false;
                        firstLineInWorkType = false;
                    }
                }
            }
        }
        //
        fOut.println("Проценты выполнения");
        doSaveTableModel(fOut, table4.getModel());
        //
        fOut.close();
        JOptionPane.showMessageDialog(this, "Файл сохранени", "Сохранение", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Печатает в файл строку из дерева
     *
     * @param out                 Файл
     * @param firstLineOnWorker   Печатать аль нет работника
     * @param firstLineInWorkType Печатать аль нет работу
     * @param workerName          Работник
     * @param workTypeName        Работа
     * @param text                Текст вершины
     */
    private void printLineInTree(PrintWriter out, boolean firstLineOnWorker, boolean firstLineInWorkType,
                                 String workerName, String workTypeName, String text) {
        if (firstLineOnWorker) {
            out.print(workerName);
        }
        out.print(";");
        if (firstLineInWorkType) {
            out.print(workTypeName);
        }
        out.println(";" + text);
    }

    /**
     * Сохраняет модель в файл
     *
     * @param out   Файл
     * @param model Модель
     */
    private void doSaveTableModel(PrintWriter out, TableModel model) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            out.print(model.getColumnName(i) + ";");
        }
        out.println();
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                out.print(model.getValueAt(row, col) + ";");
            }
            out.println();
        }
    }


    /**
     * Выполняет анализ и заполнение таблиц
     */
    private void doAnalyze() {
        // Формируем список файлов - и пускаем анализ
        ArrayList<File> files = new ArrayList<File>();
        for (Enumeration<File> en = (Enumeration<File>) ((DefaultListModel) list1.getModel()).elements(); en.hasMoreElements(); ) {
            files.add(en.nextElement());
        }
        AnalyzeResult result = new AnalyzeResult(files);
        table1.setModel(new TotalLaboriousnessModel(result.totalLaboriousness, !withoutLaborCB.isSelected()));
        table2.setModel(new PerWorkerLaboriousnessModel(result.workers, result.totalLabByWorker, !withoutLaborCB.isSelected()));
        table3.setModel(new ByWorkTableModel(result.byWorks));
        // Поехали строить дереффо!
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        //
        DecimalFormat df = new DecimalFormat("#.##");
        for (Worker worker : result.workers.keySet()) {
            // По работникам
            DefaultMutableTreeNode workerNode = new DefaultMutableTreeNode(worker);
            rootNode.add(workerNode);
            double byWorker = 0;
            for (PlanUtils.WorkTypes type : result.totalLabByWorker.get(worker).keySet()) {
                byWorker = byWorker + result.totalLabByWorker.get(worker).get(type);
            }
            for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
                // По типам работ
                DefaultMutableTreeNode typeNode = new DefaultMutableTreeNode(type);
                workerNode.add(typeNode);
                for (WorkInPlan work : result.workers.get(worker).get(type).keySet()) {
                    if (result.workers.get(worker).get(type).get(work) != 0) {
                        StringBuffer res = new StringBuffer(work.toString()).append(" - ");
                        if (!withoutLaborCB.isSelected()) {
                            res.append(df.format(result.workers.get(worker).get(type).get(work)))
                                    .append(" ч/м (");
                        }
                        res.append("от всего по типу - ")
                                .append(df.format(result.workers.get(worker).get(type).get(work) / result.totalLaboriousness.get(type) * 100))
                                .append("%, от общей по работнику - ")
                                .append(df.format(result.workers.get(worker).get(type).get(work) / byWorker * 100))
                                .append("%");
                        if (!withoutLaborCB.isSelected()) {
                            res.append(")");
                        }
                        typeNode.add(new DefaultMutableTreeNode(res.toString()));
                    }
                }
            }
        }
        //
        tree1.setModel(treeModel);
        tree1.setRootVisible(false);
        // Завершаем
        JOptionPane.showMessageDialog(this, "Анализ завершен", "Анализ", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Отображает форму
     */
    public void showForm() {
        setBounds(10, 10, 800, 600);
        setLocationRelativeTo(null);
        setTitle("Годовой анализ");
        setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(3, 3, 3, 3), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        withoutLaborCB = new JCheckBox();
        withoutLaborCB.setText("формировать без трудоемкости");
        panel2.add(withoutLaborCB, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        analyzeBtn = new JButton();
        analyzeBtn.setText("Анализ выбранных планов");
        panel1.add(analyzeBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("Закрыть");
        panel1.add(buttonOK, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveBtn = new JButton();
        saveBtn.setText("Сохранить результаты в CSV");
        panel1.add(saveBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        list1 = new JList();
        panel4.add(list1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        loadPlanBtn = new JButton();
        loadPlanBtn.setText("Добавить план");
        panel4.add(loadPlanBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deletePlan = new JButton();
        deletePlan.setText("Удалить план");
        panel4.add(deletePlan, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        panel3.add(tabbedPane1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Общее", panel5);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel5.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("По людям", panel6);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel6.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table2 = new JTable();
        scrollPane2.setViewportView(table2);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("По проектам", panel7);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel7.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table3 = new JTable();
        scrollPane3.setViewportView(table3);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("По людям и проектам", panel8);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel8.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tree1 = new JTree();
        scrollPane4.setViewportView(tree1);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Проценты выполнения", panel9);
        final JScrollPane scrollPane5 = new JScrollPane();
        panel9.add(scrollPane5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table4 = new JTable();
        scrollPane5.setViewportView(table4);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    /*
* Вспомогательное - модели и результаты анализов
*/

    /**
     * Результат анализа
     */
    private static class AnalyzeResult {

        /**
         * Работы по типам
         */
        private HashMap<PlanUtils.WorkTypes, ArrayList<WorkInPlan>> works = new HashMap<PlanUtils.WorkTypes, ArrayList<WorkInPlan>>();
        /**
         * Работы по типам по людям
         */
        private HashMap<Worker, HashMap<PlanUtils.WorkTypes, HashMap<WorkInPlan, Double>>> workers = new HashMap<Worker, HashMap<PlanUtils.WorkTypes, HashMap<WorkInPlan, Double>>>();
        /**
         * Общая трудоемкость по типу работу для рабоникам
         */
        private HashMap<Worker, HashMap<PlanUtils.WorkTypes, Double>> totalLabByWorker = new HashMap<Worker, HashMap<PlanUtils.WorkTypes, Double>>();
        /**
         * Общая трудоемкость по работам
         */
        private HashMap<WorkInPlan, Double> byWorks = new HashMap<WorkInPlan, Double>();
        /**
         * Общая трудоемкость по типам работ
         */
        private HashMap<PlanUtils.WorkTypes, Double> totalLaboriousness = new HashMap<PlanUtils.WorkTypes, Double>();

        /**
         * Default constructor
         *
         * @param files список файлов для анализа
         */
        public AnalyzeResult(ArrayList<File> files) {
            // Создаем структуру
            for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
                works.put(type, new ArrayList<WorkInPlan>());
                totalLaboriousness.put(type, new Double(0));
            }
            analyze(files);
        }

        /**
         * По очереди загружает файлы и выполняет построение анализа
         *
         * @param files Список файлов планов для анализа
         */
        private void analyze(ArrayList<File> files) {
            for (File file : files) {
                // Грузим
                if (PlanUtils.loadPlanFromXML(file)) {
                    // Если план удачно загрузился - поедем его обрабатывать
                    for (PlanPart planPart : Starter.getMainForm().getPlan()) {
                        for (WorkInPlan work : planPart.getWorks()) {
                            // Обрабатываем
                            double byWorkLab = 0;
                            if (byWorks.containsKey(work)) {
                                byWorkLab = byWorks.get(work);
                            }
                            byWorks.put(work, work.getLaborTotal() - work.calcRestLabor(2) + byWorkLab);
                            // Добавляем в общий список
                            works.get(work.getWorkType()).add(work);
                            // Обрабатываем еще людей
                            for (WorkerInPlan worker : work.getWorkersInPlan()) {
                                // Получаем мап типы работы - работы
                                HashMap<PlanUtils.WorkTypes, HashMap<WorkInPlan, Double>> wwtWorks;
                                if (workers.containsKey((worker.getWorker()))) {
                                    wwtWorks = workers.get(worker.getWorker());
                                } else {
                                    // Создаем все - и сохраняем в мап
                                    wwtWorks = new HashMap<PlanUtils.WorkTypes, HashMap<WorkInPlan, Double>>();
                                    for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
                                        wwtWorks.put(type, new HashMap<WorkInPlan, Double>());
                                    }
                                    workers.put(worker.getWorker(), wwtWorks);
                                }
                                // получаем мап работа - трудоемкость
                                HashMap<WorkInPlan, Double> worksPerWorker = wwtWorks.get(work.getWorkType());
                                double laborByWork = worker.getPerMonth()[0] + worker.getPerMonth()[1] + worker.getPerMonth()[2];
                                if (worksPerWorker.containsKey(work)) {
                                    laborByWork = laborByWork + worksPerWorker.get(work);
                                }
                                worksPerWorker.put(work, laborByWork);
                            }
                        }
                    }
                }
            }
            // Теперь в works - общий список работ по ВСЕМ планам анализа
            // Первый анализ - сколько там всего трудоемкости было потрачено на КАЖДЫЙ тип работы
            for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
                for (WorkInPlan work : works.get(type)) {
                    totalLaboriousness.put(type, totalLaboriousness.get(type) + work.getLaborTotal() - work.calcRestLabor(2));
                }
            }
            // Считаем по работнику
            for (Worker worker : workers.keySet()) {
                HashMap<PlanUtils.WorkTypes, Double> map = new HashMap<PlanUtils.WorkTypes, Double>();
                for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
                    // Оно там гарантированно есть - оно так формируется. 146%!
                    HashMap<WorkInPlan, Double> works = workers.get(worker).get(type);
                    double totalLab = 0;
                    for (WorkInPlan work : works.keySet()) {
                        totalLab = totalLab + works.get(work);
                    }
                    map.put(type, totalLab);
                }
                totalLabByWorker.put(worker, map);
            }
        }

    }

/**
 * Модели таблиц
 */

    /**
     * Модель таблицы общих результатов
     */
    private static class TotalLaboriousnessModel extends AbstractTableModel {

        /**
         * Общие трудоемкости по направлениями
         */
        private HashMap<PlanUtils.WorkTypes, Double> totalLaboriousness;
        /**
         * Вообще общая трудоемкость по направлениям
         */
        private double totalAllLab = 0;
        /**
         * Formatter
         */
        DecimalFormat df = new DecimalFormat("#.##");
        /**
         * Отображать или нет трудоемкость в ч/м
         */
        private boolean showLabor = true;

        /**
         * Default constructor
         *
         * @param totalLaboriousness Общие трудоемкости по направлениям
         * @param showLabor          Отображать или нет трудоемкость в ч/м
         */
        public TotalLaboriousnessModel(HashMap<PlanUtils.WorkTypes, Double> totalLaboriousness, boolean showLabor) {
            this.totalLaboriousness = totalLaboriousness;
            this.showLabor = showLabor;
            // Считаем ОБЩУЮ трудоемкость по всему
            for (PlanUtils.WorkTypes type : PlanUtils.WorkTypes.values()) {
                totalAllLab = totalAllLab + this.totalLaboriousness.get(type);
            }
        }

        /**
         * @return см. описание
         * @see javax.swing.table.AbstractTableModel#getRowCount()
         */
        public int getRowCount() {
            return PlanUtils.WorkTypes.values().length + 1;
        }

        /**
         * @return см. описание
         * @see javax.swing.table.AbstractTableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /**
         * @param rowIndex    см. описание
         * @param columnIndex см. описание
         * @return см. описание
         * @see javax.swing.table.AbstractTableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    if (rowIndex < PlanUtils.WorkTypes.values().length) {
                        return PlanUtils.WorkTypes.values()[rowIndex].toString();
                    } else {
                        return "Всего";
                    }
                }
                case 1: {
                    if (rowIndex < PlanUtils.WorkTypes.values().length) {
                        double lab = totalLaboriousness.get(PlanUtils.WorkTypes.values()[rowIndex]);
                        StringBuffer res = new StringBuffer();
                        if (showLabor) {
                            res.append(df.format(lab)).append(" ч/м (");
                        }
                        if (totalAllLab == 0) {
                            res.append("-");
                        } else {
                            res.append(df.format(((lab / totalAllLab) * 100))).append("%");
                        }
                        if (showLabor) {
                            res.append(")");
                        }
                        return res.toString();
                    } else {
                        if (showLabor) {
                            return df.format(totalAllLab) + " ч/м";
                        } else {
                            return "-";
                        }
                    }
                }
                default: {
                    return "WTF?!";
                }
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0: {
                    return "Тип работы";
                }
                case 1: {
                    return "Трудоемкость";
                }
                default: {
                    return "WTF?!";
                }
            }
        }
    }

    /**
     * Модель таблицы "по людям"
     */
    private static class PerWorkerLaboriousnessModel extends AbstractTableModel {

        /**
         * Работы по типам по людям
         */
        private HashMap<Worker, HashMap<PlanUtils.WorkTypes, HashMap<WorkInPlan, Double>>> workers = new HashMap<Worker, HashMap<PlanUtils.WorkTypes, HashMap<WorkInPlan, Double>>>();
        /**
         * Общая трудоемкость по типу работу для рабоникам
         */
        private HashMap<Worker, HashMap<PlanUtils.WorkTypes, Double>> totalLabByWorker = new HashMap<Worker, HashMap<PlanUtils.WorkTypes, Double>>();
        /**
         * Formatter
         */
        private DecimalFormat df = new DecimalFormat("#.##");
        /**
         * Отображать или нет трудоемкость в ч/м
         */
        private boolean showLabor = true;

        /**
         * Default constructor
         *
         * @param workers          Работы по типам по людям
         * @param totalLabByWorker Общие суммы по типам по людям
         * @param showLabor        Отображать или нет трудоемкость в ч/м
         */
        public PerWorkerLaboriousnessModel(HashMap<Worker, HashMap<PlanUtils.WorkTypes, HashMap<WorkInPlan, Double>>> workers,
                                           HashMap<Worker, HashMap<PlanUtils.WorkTypes, Double>> totalLabByWorker, boolean showLabor) {
            this.workers = workers;
            this.totalLabByWorker = totalLabByWorker;
            this.showLabor = showLabor;
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
            return PlanUtils.WorkTypes.values().length + 2;
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
            if (columnIndex == 0) {
                return workers.keySet().toArray()[rowIndex];
            } else {
                // Получаем по работнику
                HashMap<PlanUtils.WorkTypes, Double> map = totalLabByWorker.get(workers.keySet().toArray()[rowIndex]);
                // Считаем все
                double res = 0;
                for (Double d : map.values()) {
                    res = res + d;
                }
                StringBuffer str = new StringBuffer();
                if (columnIndex <= PlanUtils.WorkTypes.values().length) {
                    // по конкретному типу
                    if (showLabor) {
                        str.append(df.format(map.get(PlanUtils.WorkTypes.values()[columnIndex - 1]))).append(" ч/м (");
                        if (res == 0) {
                            str.append("-");
                        } else {
                            str.append(df.format(map.get(PlanUtils.WorkTypes.values()[columnIndex - 1]) / res * 100));
                        }
                        str.append("%)");
                    } else {
                        str.append(df.format(map.get(PlanUtils.WorkTypes.values()[columnIndex - 1]) / res * 100)).append("%");
                    }
                } else {
                    if (showLabor) {
                        str.append(df.format(res)).append(" ч/м");
                    } else {
                        str.append("-");
                    }
                }
                return str.toString();
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
            if (column == 0) {
                return "Работник";
            } else if (column <= PlanUtils.WorkTypes.values().length) {
                return PlanUtils.WorkTypes.values()[column - 1].toString();
            } else {
                return "Итого";
            }
        }
    }

    /**
     * Модель отображения трудоемоксти по работам
     */
    private static class ByWorkTableModel extends AbstractTableModel {

        /**
         * Список работ
         */
        private HashMap<WorkInPlan, Double> works;
        /**
         * Formatter
         */
        private DecimalFormat df = new DecimalFormat("#.##");

        /**
         * Default constructor
         *
         * @param works Работы
         */
        public ByWorkTableModel(HashMap<WorkInPlan, Double> works) {
            this.works = works;
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
            return works.keySet().size();
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
            switch (columnIndex) {
                case 0:
                    return works.keySet().toArray()[rowIndex].toString();
                case 1:
                    return df.format(works.get(works.keySet().toArray()[rowIndex])) + " ч/м";
                default:
                    return "WTF?!";
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
                    return "Название работы";
                case 1:
                    return "Трудоемкость";
                default:
                    return "WTF?!";
            }
        }
    }
}
