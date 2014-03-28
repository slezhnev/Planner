import java.util.ArrayList;
import java.util.List;

/**
 * Работа в плане
 */
public class WorkInPlan {

    /**
     * Конструктор используется ТОЛЬКО в clone!
     */
    private WorkInPlan() {

    }

    /**
     * Этап работы
     */
    public static class WorkStage {
        /**
         * Наименование этапа
         */
        private String name;
        /**
         * Список работ в этапе
         */
        private List<WorkInStage> worksInStage = new ArrayList<>();

        public WorkStage(String name, List<WorkInStage> worksInStage) {
            this.name = name;
            this.worksInStage = worksInStage;
        }

        public WorkStage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<WorkInStage> getWorksInStage() {
            return worksInStage;
        }

        public WorkStage clone() {
            ArrayList<WorkInStage> works = new ArrayList<>();
            for (WorkInStage work : worksInStage) {
                works.add(work.clone());
            }
            return new WorkStage(name, works);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setWorksInStage(List<WorkInStage> worksInStage) {
            this.worksInStage = worksInStage;
        }
    }

    /**
     * Работа в этапе
     */
    public static class WorkInStage {
        /**
         * Перечень работ
         */
        private String works;
        /**
         * Представляемые результаты
         */
        private String results;
        /**
         * Дата завершения
         */
        private String endDate;

        /**
         * Представленные результаты
         */
        private String actualResults;

        public WorkInStage clone() {
            return new WorkInStage(works, results, endDate, actualResults);
        }

        /**
         * Default constructor
         *
         * @param works         Перечень работ
         * @param results       Представляемые результаты
         * @param endDate       Дата завершения
         * @param actualResults Представленные результаты
         */
        public WorkInStage(String works, String results, String endDate, String actualResults) {
            this.works = works;
            this.results = results;
            this.endDate = endDate;
            this.actualResults = actualResults;
        }

        public String getWorks() {
            return works;
        }

        public String getResults() {
            return results;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getActualResults() {
            return actualResults;
        }
    }

    /**
     * Наименование
     */
    private String name;
    /**
     * Описание
     */
    private String desc;
    /**
     * Дата завершения
     */
    private String endDate;
    /**
     * Задел
     */
    private String reserve;
    /**
     * Выполнено
     */
    private boolean maked;
    /**
     * Процент выполнения
     */
    private Double makedPercent;
    /**
     * Отчетные документы по выполнению
     */
    private String finishDoc;
    /**
     * Общая трудоемкость
     */
    private Double laborTotal;
    /**
     * Тип работы
     */
    private PlanUtils.WorkTypes workType;
    /**
     * Список работников в работе
     */
    private List<WorkerInPlan> workersInPlan = new ArrayList<>();
    /**
     * Список этапов работы
     */
    private List<WorkStage> stages = new ArrayList<>();


    public WorkInPlan(String name, String desc, String endDate, String reserve, String finishDoc, PlanUtils.WorkTypes workType) {
        this.name = name;
        this.desc = desc;
        this.endDate = endDate;
        this.reserve = reserve;
        this.finishDoc = finishDoc;
        this.workType = workType;
        laborTotal = 0.0;
        maked = false;
    }

    public WorkInPlan(String name, String desc) {
        this.name = name;
        this.desc = desc;
        this.workType = PlanUtils.WorkTypes.INNER;
        this.endDate = "";
        this.reserve = "";
        this.finishDoc = "";
        laborTotal = 0.0;
        maked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<WorkerInPlan> getWorkersInPlan() {
        return workersInPlan;
    }

    public Double getLaborTotal() {
        double total = 0;
        for (WorkerInPlan worker : workersInPlan) {
            total = total + worker.getLaborContent();
        }
        laborTotal = total;
        return laborTotal;
    }

    public void updateLaborTotal() {
        // Делаем не очень красиво - но что ж делать-то...
        Starter.getMainForm().planPartTotalLaborChanged();
    }

    public double calcRestLabor(int month) {
        // Считаем ОБЩИЙ остаток трудоемкости по этой работе за указанный месяц...
        double total = 0;
        for (WorkerInPlan worker : workersInPlan) {
            total = total + worker.calcRestLabor(month);
        }
        return total;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFinishDoc() {
        return finishDoc;
    }

    public void setFinishDoc(String finishDoc) {
        this.finishDoc = finishDoc;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public boolean isMaked() {
        return maked;
    }

    public void setMaked(boolean maked) {
        this.maked = maked;
    }

    public Double getMakedPercent() {
        return makedPercent;
    }

    public void setMakedPercent(Double makedPercent) {
        this.makedPercent = makedPercent;
    }

    public PlanUtils.WorkTypes getWorkType() {
        return workType;
    }

    public void setWorkType(PlanUtils.WorkTypes workType) {
        this.workType = workType;
    }

    public List<WorkStage> getStages() {
        return stages;
    }

    public void setStages(List<WorkStage> stages) {
        this.stages = stages;
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        } else {
            return name.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WorkInPlan) {
            WorkInPlan sec = (WorkInPlan) obj;
            if (name == null) {
                return false;
            } else {
                return name.equals(sec.getName());
            }
        } else {
            return false;
        }
    }

    @Override
    public WorkInPlan clone() {
        WorkInPlan res = new WorkInPlan();
        res.assign(this);
        // А теперь еще отдельно скопируем workersInPlan и stages
        res.workersInPlan.clear();
        for (WorkerInPlan workerInPlan : workersInPlan) {
            res.workersInPlan.add(workerInPlan.clone());
        }
        res.stages.clear();
        for (WorkStage stage : stages) {
            res.stages.add(stage.clone());
        }        
        //
        return res;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Переносит значения полей из другого объекта
     *
     * @param resWork Откуда переносить значения
     */
    public void assign(WorkInPlan resWork) {
        this.setName(resWork.getName());
        this.setDesc(resWork.getDesc());
        this.setEndDate(resWork.getEndDate());
        this.setReserve(resWork.getReserve());
        this.setFinishDoc(resWork.getFinishDoc());
        this.setWorkType(resWork.getWorkType());
        this.setMaked(resWork.isMaked());
        this.setMakedPercent(resWork.getMakedPercent());
        //
        this.workersInPlan.clear();
        this.workersInPlan.addAll(resWork.getWorkersInPlan());
        this.stages.clear();
        this.stages.addAll(resWork.getStages());
    }
}
