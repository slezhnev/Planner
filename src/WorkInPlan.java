import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 05.01.11
 * Time: 1:01
 * To change this template use File | Settings | File Templates.
 */
public class WorkInPlan {

    private String name;
    private String desc;
    private ArrayList<WorkerInPlan> workersInPlan = new ArrayList<WorkerInPlan>();

    public WorkInPlan(String name, String desc) {
        this.name = name;
        this.desc = desc;
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
}
