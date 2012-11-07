import java.util.ArrayList;

/**
 * Работа в плане
 */
public class WorkInPlan {

    private String name;
    private String desc;
    private String endDate;
    private String reserve;
    private boolean maked;
    private String finishDoc;
    private Double laborTotal;
    private PlanUtils.WorkTypes workType;
    private ArrayList<WorkerInPlan> workersInPlan = new ArrayList<WorkerInPlan>();

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

    public ArrayList<WorkerInPlan> getWorkersInPlan() {
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


    public PlanUtils.WorkTypes getWorkType() {
        return workType;
    }

    public void setWorkType(PlanUtils.WorkTypes workType) {
        this.workType = workType;
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
            WorkInPlan sec = (WorkInPlan)obj;
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
    public String toString() {
        return name;
    }
}
