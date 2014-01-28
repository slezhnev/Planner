import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Основной класс
 */
public class MainForm {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JTable workersTable;
    private JButton saveWorkersBtn;
    private JButton addWorkerBtn;
    private JButton delWorkerBtn;
    private JTabbedPane quarterPlan_divisions;
    private JTabbedPane kindWorksTabbedPane;
    private JButton savePlanBtn;
    private JButton loadPlanBtn;
    private JButton printPlanBtn;
    private JButton quarterReportButton;
    private JTable planPartTable;
    private JTable workersInPlanTable;
    private JButton addWorkBtn;
    private JButton delWorkBtn;
    private JButton addWorkerToWorkBtn;
    private JButton delWorkerFromWorkBtn;
    private JButton moveUpPlanPartBtn;
    private JButton moveDownPlanPartBtn;
    private JTabbedPane monthTabbedPane;
    private JComboBox quarterComboBox;
    private JButton monthReportBtn;
    private JTable monthWorksTable;
    private JTable monthWorkersPerWorkTable;
    private JTabbedPane quarterPlan_month;
    private JFormattedTextField yearEdit;
    private JButton editWorkBtn;
    private JButton createNewPlanBtn;
    private JButton perWorkerPlanBtn;
    private JButton analyzeBtn;
    private JComboBox workerSelectCB;

    public int getSelectedQuarter() {
        return quarterComboBox.getSelectedIndex() + 1;
    }

    public void setSelectedQuarter(int quarter) {
        if ((quarter >= 1) && (quarter <= 4)) {
            quarterComboBox.setSelectedIndex(quarter - 1);
        }
    }

    public String getYear() {
        return yearEdit.getText();
    }

    public void setYear(String year) {
        yearEdit.setText(year);
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    /**
     * Работники
     */
    private ArrayList<Worker> workers = new ArrayList<Worker>();

    public ArrayList<PlanPart> getPlan() {
        return plan;
    }

    /**
     * План
     */
    private ArrayList<PlanPart> plan = new ArrayList<PlanPart>();

    public void setPlanVersion(int planVersion) {
        this.planVersion = planVersion;
    }

    public int getPlanVersion() {
        return planVersion;
    }

    /**
     * Версия плана. От этого будет зависеть - какой диалог редактирования будет открываться.
     * Задается ЖЕСТКО при загрузке
     * По умолчанию сейчас работаем с планом второй версии
     */
    private int planVersion = 2;


    private boolean dataChanged = false;

    public MainForm() {
        //
        makeNewPlanTabs();
        //
        yearEdit.setText(new SimpleDateFormat("yyyy").format(new Date()));
        quarterPlan_divisions.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (quarterPlan_divisions.getSelectedIndex() != -1) {
                    PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                    ((PlanPartModel) planPartTable.getModel()).setPlanPart(currPart);
                    // Сбрасываем еще список работников
                    ((WorkersInPlanTableModel) workersInPlanTable.getModel()).clearWorkInPlan();
                }
            }
        });
        //
        workersTable.setModel(new WorkersTableModel(workers));
        workersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //
        PlanPartModel planPartModel = new PlanPartModel();
        planPartTable.setModel(planPartModel);
        planPartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planPartModel.setPlanPart(plan.get(0));
        planPartTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                if ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < currPart.getWorks().size())) {
                    ((WorkersInPlanTableModel) workersInPlanTable.getModel()).setWorkInPlan(currPart.getWorks().get(planPartTable.getSelectedRow()));
                }
            }
        });
        //
        workersInPlanTable.setModel(new WorkersInPlanTableModel());
        workersInPlanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workerSelectCB = new JComboBox();
        workerSelectCB.setModel(new DefaultComboBoxModel(workers.toArray()));
        workersInPlanTable.setDefaultEditor(Worker.class, new DefaultCellEditor(workerSelectCB));
        //
        addWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                currPart.getWorks().add(new WorkInPlan("", ""));
                ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
                ((WorkersInPlanTableModel) workersInPlanTable.getModel()).clearWorkInPlan();
                dataChanged = true;
            }
        });
        delWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (planPartTable.getSelectedRow() != -1) {
                    if (JOptionPane.showConfirmDialog(null, "Вы уверены что хотите удалить выбранную работу?", "Удаление", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                        currPart.getWorks().remove(planPartTable.getSelectedRow());
                        ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
                        ((WorkersInPlanTableModel) workersInPlanTable.getModel()).clearWorkInPlan();
                        dataChanged = true;
                    }
                }
            }
        });
        addWorkerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                workers.add(new Worker("", 0.0));
                ((WorkersTableModel) workersTable.getModel()).fireTableDataChanged();
                dataChanged = true;
            }
        });
        delWorkerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((workersTable.getSelectedRow() != -1) && (workersTable.getSelectedRow() < workers.size())) {
                    // Тут вначале проверим - а может у нас этот работник фигурирует в какой работе?!
                    Worker worker = workers.get(workersTable.getSelectedRow());
                    boolean presentInWork = false;
                    for (PlanPart planPart : plan) {
                        for (WorkInPlan work : planPart.getWorks()) {
                            for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                                if (workerInPlan.getWorker() == worker) {
                                    presentInWork = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (presentInWork) {
                        JOptionPane.showMessageDialog(null, "Работник занят в какой-то работе. Вначале удалите его из всех работ",
                                "Удаление работника", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить выбранного работника?",
                                "Удаление работника", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            workers.remove(worker);
                            ((WorkersTableModel) workersTable.getModel()).fireTableDataChanged();
                            dataChanged = true;
                        }
                    }
                }
            }
        });
        //
        addWorkerToWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                if ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < currPart.getWorks().size())) {
                    WorkInPlan work = currPart.getWorks().get(planPartTable.getSelectedRow());
                    // Теперь надо из списка работников добавить ПЕРВОГО, кого тут нет
                    for (Worker worker : workers) {
                        boolean found = false;
                        for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                            if (workerInPlan.getWorker() == worker) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            work.getWorkersInPlan().add(new WorkerInPlan(worker, 0.0));
                            ((WorkersInPlanTableModel) workersInPlanTable.getModel()).fireTableDataChanged();
                            dataChanged = true;
                            break;
                        }
                    }
                }
            }
        });
        delWorkerFromWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                if ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < currPart.getWorks().size())) {
                    WorkInPlan work = currPart.getWorks().get(planPartTable.getSelectedRow());
                    if ((workersInPlanTable.getSelectedRow() > -1) && (workersInPlanTable.getSelectedRow() < work.getWorkersInPlan().size())) {
                        if (JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить выбранного работника из работы?",
                                "Удаление работника из работы", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            WorkerInPlan worker = work.getWorkersInPlan().get(workersInPlanTable.getSelectedRow());
                            work.getWorkersInPlan().remove(worker);
                            ((WorkersInPlanTableModel) workersInPlanTable.getModel()).fireTableDataChanged();
                            dataChanged = true;
                        }
                    }
                }
            }
        });
        moveUpPlanPartBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                movePlanPart(-1);
                dataChanged = true;
            }
        });
        moveDownPlanPartBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                movePlanPart(1);
                dataChanged = true;
            }
        });
        //
        quarterComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "I квартал",
                "II квартал",
                "III квартал",
                "IV квартал"}));
        quarterComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (quarterComboBox.getSelectedIndex()) {
                    case 0: {
                        monthTabbedPane.setTitleAt(0, "Январь");
                        monthTabbedPane.setTitleAt(1, "Февраль");
                        monthTabbedPane.setTitleAt(2, "Март");
                        break;
                    }
                    case 1: {
                        monthTabbedPane.setTitleAt(0, "Апрель");
                        monthTabbedPane.setTitleAt(1, "Май");
                        monthTabbedPane.setTitleAt(2, "Июнь");
                        break;
                    }
                    case 2: {
                        monthTabbedPane.setTitleAt(0, "Июль");
                        monthTabbedPane.setTitleAt(1, "Август");
                        monthTabbedPane.setTitleAt(2, "Сентябрь");
                        break;
                    }
                    case 3: {
                        monthTabbedPane.setTitleAt(0, "Октябрь");
                        monthTabbedPane.setTitleAt(1, "Ноябрь");
                        monthTabbedPane.setTitleAt(2, "Декабрь");
                        break;
                    }
                }
            }
        });
        quarterComboBox.setSelectedIndex(0);
        //
        monthTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if ((monthTabbedPane.getSelectedIndex() > -1) && (monthTabbedPane.getSelectedIndex() < 4)) {
                    ((MonthWorksTableModel) monthWorksTable.getModel()).setMonth(monthTabbedPane.getSelectedIndex());
                    int i = monthWorksTable.getSelectedRow();
                    ((MonthWorksTableModel) monthWorksTable.getModel()).fireTableDataChanged();
                    if (i > -1) monthWorksTable.getSelectionModel().setSelectionInterval(i, i);
                    ((MonthWorkersTableModel) monthWorkersPerWorkTable.getModel()).setMonth(monthTabbedPane.getSelectedIndex());
                }
            }
        });
        monthTabbedPane.setSelectedIndex(0);
        //
        quarterPlan_month.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (quarterPlan_month.getSelectedIndex() != -1) {
                    PlanPart currPart = plan.get(quarterPlan_month.getSelectedIndex());
                    ((MonthWorksTableModel) monthWorksTable.getModel()).setPlanPart(currPart);
                    // Сбрасываем еще список работников
                    ((MonthWorkersTableModel) monthWorkersPerWorkTable.getModel()).clearWorkInPlan();
                }
            }
        });
        //
        monthWorkersPerWorkTable.setModel(new MonthWorkersTableModel());
        monthWorkersPerWorkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //
        MonthWorksTableModel monthWorksTableModel = new MonthWorksTableModel();
        monthWorksTable.setModel(monthWorksTableModel);
        monthWorksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        monthWorksTableModel.setPlanPart(plan.get(0));
        monthWorksTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_month.getSelectedIndex());
                if ((monthWorksTable.getSelectedRow() > -1) && (monthWorksTable.getSelectedRow() < currPart.getWorks().size())) {
                    ((MonthWorkersTableModel) monthWorkersPerWorkTable.getModel()).setWorkInPlan(currPart.getWorks().get(monthWorksTable.getSelectedRow()));
                }
            }
        });
        kindWorksTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // Запустим обновление всего чего можно тама...
                ((MonthWorksTableModel) monthWorksTable.getModel()).fireTableDataChanged();
                ((MonthWorkersTableModel) monthWorkersPerWorkTable.getModel()).clearWorkInPlan();
            }
        });
        //
        editWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
                if ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < currPart.getWorks().size())) {
                    WorkInPlan currWork = currPart.getWorks().get(planPartTable.getSelectedRow());
                    WorkInPlan resWork = null;
                    if (planVersion == 1) {
                        resWork = WorkParamForm.showDialog(planPartTable, planPartTable, currWork);
                    } else if (planVersion == 2) {
                        resWork = WorkParamForm2.showDialog(planPartTable, currWork);
                    }
                    if (resWork != null) {
                        currWork.assign(resWork);
                        // Fire change
                        ((PlanPartModel) planPartTable.getModel()).fireTableCellUpdated(planPartTable.getSelectedRow(), 0);
                        dataChanged = true;
                    }
                }
            }
        });
        //
        savePlanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Вначале - выбираем куда сохранять
                PlanUtils.savePlanToFile();
                dataChanged = false;
            }
        });
        loadPlanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (PlanUtils.loadPlanFromFile()) {
                    refreshData();
                    dataChanged = false;
                }
            }
        });
        //
        createNewPlanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите создать новый план? Все работы текущего плана будут удалены!",
                        "Создание нового плана", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    for (PlanPart part : plan) {
                        part.getWorks().clear();
                    }
                    plan.clear();
                    //
                    makeNewPlanTabs();
                    //
                    ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
                    ((WorkersInPlanTableModel) workersInPlanTable.getModel()).fireTableDataChanged();
                    for (Worker worker : workers) {
                        worker.setLaborContentTotal(0.0);
                    }
                    ((WorkersTableModel) workersTable.getModel()).fireTableDataChanged();
                    dataChanged = true;
                }
            }
        });
        printPlanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PlanUtils.makeQuarterPlan(0);
                    ReportViewer.showPreview(kindWorksTabbedPane, kindWorksTabbedPane, "quarterPlan", "quarterPlan.toReport");
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка ввода-вывода");
                } catch (TransformerException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка сохранения XML документа");
                } catch (ParserConfigurationException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка работы с XML документом");
                }
            }
        });
        //
        monthReportBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PlanUtils.makeMonthPlan(monthTabbedPane.getSelectedIndex(), monthTabbedPane.getTitleAt(monthTabbedPane.getSelectedIndex()));
                    ReportViewer.showPreview(monthTabbedPane, monthTabbedPane, "monthReport", "monthReport.toReport");
                } catch (ParserConfigurationException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка работы с XML документом");
                } catch (TransformerException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка сохранения XML документа");
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка ввода-вывода");
                }
            }
        });
        perWorkerPlanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PlanUtils.makePerWorkerPlan();
                    //ReportViewer.showPreview(kindWorksTabbedPane, kindWorksTabbedPane, "perWorkerPlan", "perWorkerPlan.toReport");
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка ввода-вывода");
                } catch (TransformerException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка сохранения XML документа");
                } catch (ParserConfigurationException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка работы с XML документом");
                }
            }
        });
        quarterReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PlanUtils.makeQuarterPlan(1);
                    ReportViewer.showPreview(kindWorksTabbedPane, kindWorksTabbedPane, "quarterPlan", "quarterPlan.toReport");
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка ввода-вывода");
                } catch (TransformerException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка сохранения XML документа");
                } catch (ParserConfigurationException e1) {
                    JOptionPane.showMessageDialog(null, "Ошибка работы с XML документом");
                }
            }
        });
        analyzeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите перейти к анализу? Все изменения текущего плана не будут сохранены!",
                        "Анализ", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    AnalyzeForm form = new AnalyzeForm();
                    form.showForm();
                }
            }
        });
    }

    private void makeNewPlanTabs() {
        plan.add(new PlanPart("Собств.работы - новые", "1. Собственные работы (по основному процессу подразделения)"));
        plan.add(new PlanPart("Собств.работы - продолжение", "1а). Собственные работы (завершение работ по предыдущим квартальным планам)"));
        plan.add(new PlanPart("Подд.произв.", "2. Работы по поддержке производства"));
        plan.add(new PlanPart("Внутр.кооп.", "3. Работы по внутренней кооперации"));
        plan.add(new PlanPart("Внешн.заказчик", "4. Работы по внешней кооперации (с другими организациями)"));
        plan.add(new PlanPart("СМК", "5. Разработка документации по СМК"));
        plan.add(new PlanPart("Корр.мероприятия", "6. Корректирующие и предупреждающие действия"));
        //
        makePlanPartTabs();
    }

    /**
     * Обновление всех моделей
     * Применяется после загрузки данных
     */
    public void refreshData() {
        // Пересчитаем общую трудоемкость по всем
        for (Worker worker : workers) {
            updateTotalWorkerLabor(worker);
        }
        // Теперь еще перезапустим все модели
        makePlanPartTabs();
        ((AbstractTableModel) workersTable.getModel()).fireTableDataChanged();
        ((AbstractTableModel) planPartTable.getModel()).fireTableDataChanged();
        ((AbstractTableModel) workersInPlanTable.getModel()).fireTableDataChanged();
        ((AbstractTableModel) monthWorksTable.getModel()).fireTableDataChanged();
        ((AbstractTableModel) monthWorkersPerWorkTable.getModel()).fireTableDataChanged();
        workerSelectCB.setModel(new DefaultComboBoxModel(workers.toArray()));
    }

    private void fillTempInfo() {
        Worker worker1 = new Worker("Рогов П.А.", 0.0);
        workers.add(worker1);
        Worker worker2 = new Worker("Золотов А.В.", 0.0);
        workers.add(worker2);
        workers.add(new Worker("Полулях А.В.", 0.0));
        //workers.add(new Worker("Горелов А.Г.", 0.0));
        workers.add(new Worker("Воронин Р.М.", 0.0));
        workers.add(new Worker("Гуськов С.С.", 0.0));
        workers.add(new Worker("Конев Д.С.", 0.0));
        workers.add(new Worker("Шумский Ю.Н.", 0.0));
        //workers.add(new Worker("Мироненко Н.Л.", 0.0));
        workers.add(new Worker("Мармер В.В.", -1.0));
        workers.add(new Worker("Зореев В.П.", -1.0));
        workers.add(new Worker("Шварцман А.М.", 0.0));
        workers.add(new Worker("Никитина Н.Е.", 0.0));
        workers.add(new Worker("Хехнева А.В.", 0.0));
        workers.add(new Worker("Конашина О.А.", 0.0));
        //workers.add(new Worker("Косарев В.В.", 0.0));
        //
        plan.get(0).getWorks().add(new WorkInPlan("Работа 1", "Описание 1\nСтрока2\n]]", "10.10.2010", "", "Предоставлено \n куча \n всего \n ]]", PlanUtils.WorkTypes.INNER));
        plan.get(0).getWorks().get(0).getWorkersInPlan().add(new WorkerInPlan(worker1, 3.5));
        plan.get(0).getWorks().get(0).getWorkersInPlan().add(new WorkerInPlan(worker2, 5.5));
        plan.get(0).getWorks().add(new WorkInPlan("Работа 2", "Описание 2"));
        plan.get(0).getWorks().add(new WorkInPlan("Работа 3", "Описание 3"));
        dataChanged = true;
    }

    /**
     * Загружает план, который сохраняли последним
     */
    public void loadLastPlan() {
        Properties props = new Properties();
        try {
            props.loadFromXML(new FileInputStream("work.file"));
            if (props.get("workFileName") != null) {
                File f = new File((String) props.get("workFileName"));
                if (f.isFile() && f.canRead()) {
                    boolean loadResult = false;
                    if ("plan".equals(PlanUtils.getExt(f))) {
                        loadResult = PlanUtils.loadPlanFromXML(f);
                    } else if ("plan2".equals(PlanUtils.getExt(f))) {
                        loadResult = PlanUtils.loadPlanFromJSON(f);
                    }
                    if (loadResult) {
                        refreshData();
                        JOptionPane.showMessageDialog(null, "Загружен план из файла " + f.getPath(), "Загрузка последнего плана", JOptionPane.INFORMATION_MESSAGE);
                        dataChanged = false;
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Создает табы по видам работ
     */
    private void makePlanPartTabs() {
        quarterPlan_divisions.removeAll();
        quarterPlan_month.removeAll();
        for (PlanPart part : plan) {
            quarterPlan_divisions.addTab(part.getName(), null);
            quarterPlan_month.addTab(part.getName(), null);
        }
    }


    /**
     * Двигает выбранный элемент в planPartTable вверх или вниз
     *
     * @param moveDiff -1 - сдвинуть вверх, 1 - сдвинуть вниз.
     *                 При других значениях или при невозможности двигать - ничего не делает
     */
    private void movePlanPart(int moveDiff) {
        PlanPart currPart = plan.get(quarterPlan_divisions.getSelectedIndex());
        if (((moveDiff == -1) && ((planPartTable.getSelectedRow() > 0) && (planPartTable.getSelectedRow() < currPart.getWorks().size()))) ||
                ((moveDiff == 1) && ((planPartTable.getSelectedRow() > -1) && (planPartTable.getSelectedRow() < (currPart.getWorks().size() - 1))))) {
            // Значит тут можно попробовать подвигать...
            int selPos = planPartTable.getSelectedRow();
            WorkInPlan work = currPart.getWorks().get(selPos);
            currPart.getWorks().remove(work);
            currPart.getWorks().add(selPos + moveDiff, work);
            ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
            planPartTable.getSelectionModel().setSelectionInterval(selPos + moveDiff, selPos + moveDiff);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void planPartTotalLaborChanged() {
        if (planPartTable.getSelectedRow() != -1) {
            ((PlanPartModel) planPartTable.getModel()).fireTableCellUpdated(planPartTable.getSelectedRow(), 1);
        } else {
            ((PlanPartModel) planPartTable.getModel()).fireTableDataChanged();
        }
    }

    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    public void updateTotalWorkerLabor(Worker worker) {
        double total = 0;
        for (PlanPart planPart : plan) {
            for (WorkInPlan work : planPart.getWorks()) {
                for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                    if (workerInPlan.getWorker() == worker) {
                        total = total + workerInPlan.getLaborContent();
                    }
                }
            }
        }

        if (worker != null) {
            worker.setLaborContentTotal(total);
            ((WorkersTableModel) workersTable.getModel()).fireTableCellUpdated(workers.indexOf(worker), 1);
        }
    }

    /**
     * Обновляет информацию о оставшейся трудоемкости в monthWorksTable
     */
    public void updateRestLabor() {
        if (monthWorksTable.getSelectedRow() != -1) {
            ((MonthWorksTableModel) monthWorksTable.getModel()).fireTableCellUpdated(monthWorksTable.getSelectedRow(), 2);
        } else {
            ((MonthWorksTableModel) monthWorksTable.getModel()).fireTableDataChanged();
            ((MonthWorkersTableModel) monthWorkersPerWorkTable.getModel()).clearWorkInPlan();
        }
    }

    public boolean isDataChanged() {
        return dataChanged;
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1 = new JTabbedPane();
        mainPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("План", panel1);
        kindWorksTabbedPane = new JTabbedPane();
        panel1.add(kindWorksTabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        kindWorksTabbedPane.addTab("Квартальный", panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(174, 200), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addWorkBtn = new JButton();
        addWorkBtn.setText("Добавить");
        panel5.add(addWorkBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel5.add(spacer1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        delWorkBtn = new JButton();
        delWorkBtn.setText("Удалить");
        panel5.add(delWorkBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        moveUpPlanPartBtn = new JButton();
        moveUpPlanPartBtn.setText("^");
        panel5.add(moveUpPlanPartBtn, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        moveDownPlanPartBtn = new JButton();
        moveDownPlanPartBtn.setText("\\/");
        panel5.add(moveDownPlanPartBtn, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editWorkBtn = new JButton();
        editWorkBtn.setText("Изменить");
        editWorkBtn.setToolTipText("Изменить параметры работы");
        panel5.add(editWorkBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        planPartTable = new JTable();
        scrollPane1.setViewportView(planPartTable);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addWorkerToWorkBtn = new JButton();
        addWorkerToWorkBtn.setText("Добавить");
        panel7.add(addWorkerToWorkBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel7.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        delWorkerFromWorkBtn = new JButton();
        delWorkerFromWorkBtn.setText("Удалить");
        panel7.add(delWorkerFromWorkBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel6.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        workersInPlanTable = new JTable();
        scrollPane2.setViewportView(workersInPlanTable);
        quarterPlan_divisions = new JTabbedPane();
        quarterPlan_divisions.setTabLayoutPolicy(1);
        panel2.add(quarterPlan_divisions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 20), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        quarterPlan_divisions.addTab("Собств.работы - новые", panel8);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 5, new Insets(0, 10, 0, 0), -1, -1));
        panel2.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Квартал:");
        panel9.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        quarterComboBox = new JComboBox();
        panel9.add(quarterComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Год:");
        panel9.add(label2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yearEdit = new JFormattedTextField();
        yearEdit.setText("0");
        panel9.add(yearEdit, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        kindWorksTabbedPane.addTab("Помесячное", panel10);
        monthTabbedPane = new JTabbedPane();
        monthTabbedPane.setTabLayoutPolicy(1);
        panel10.add(monthTabbedPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(200, 20), null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        monthTabbedPane.addTab("Первый месяц", panel11);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        monthTabbedPane.addTab("Второй месяц", panel12);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        monthTabbedPane.addTab("Третий месяц", panel13);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel14, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel14.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        monthWorksTable = new JTable();
        scrollPane3.setViewportView(monthWorksTable);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel14.add(scrollPane4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        monthWorkersPerWorkTable = new JTable();
        scrollPane4.setViewportView(monthWorkersPerWorkTable);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel15, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        monthReportBtn = new JButton();
        monthReportBtn.setText("Отчет");
        panel15.add(monthReportBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel15.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        quarterPlan_month = new JTabbedPane();
        quarterPlan_month.setTabLayoutPolicy(1);
        panel10.add(quarterPlan_month, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 20), null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        quarterPlan_month.addTab("Собств.работы - новые", panel16);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Работники", panel17);
        final JScrollPane scrollPane5 = new JScrollPane();
        panel17.add(scrollPane5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        workersTable = new JTable();
        workersTable.setAutoCreateRowSorter(false);
        workersTable.setPreferredScrollableViewportSize(new Dimension(800, 600));
        scrollPane5.setViewportView(workersTable);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(panel18, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel18.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        addWorkerBtn = new JButton();
        addWorkerBtn.setText("Добавить");
        panel18.add(addWorkerBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delWorkerBtn = new JButton();
        delWorkerBtn.setText("Удалить");
        panel18.add(delWorkerBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(2, 8, new Insets(3, 3, 10, 3), -1, -1));
        mainPanel.add(panel19, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        savePlanBtn = new JButton();
        savePlanBtn.setText("Сохранить план");
        panel19.add(savePlanBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel19.add(spacer5, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        loadPlanBtn = new JButton();
        loadPlanBtn.setText("Загрузить план");
        panel19.add(loadPlanBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        printPlanBtn = new JButton();
        printPlanBtn.setText("План");
        panel19.add(printPlanBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        quarterReportButton = new JButton();
        quarterReportButton.setText("Отчет");
        panel19.add(quarterReportButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createNewPlanBtn = new JButton();
        createNewPlanBtn.setText("Начать создание нового плана");
        createNewPlanBtn.setToolTipText("Создать новый план");
        panel19.add(createNewPlanBtn, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel19.add(spacer6, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        perWorkerPlanBtn = new JButton();
        perWorkerPlanBtn.setText("План по людям");
        perWorkerPlanBtn.setToolTipText("Сформировать квартальный план по людям");
        panel19.add(perWorkerPlanBtn, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        analyzeBtn = new JButton();
        analyzeBtn.setText("Годовой анализ");
        panel19.add(analyzeBtn, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
