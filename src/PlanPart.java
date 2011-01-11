import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 05.01.11
 * Time: 1:06
 * To change this template use File | Settings | File Templates.
 */
public class PlanPart {

    private String name;
    private ArrayList<WorkInPlan> works = new ArrayList<WorkInPlan>();

    public PlanPart(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<WorkInPlan> getWorks() {
        return works;
    }
}
