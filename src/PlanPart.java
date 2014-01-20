import java.util.ArrayList;

/**
 * Одна работа плана
 *
 * @author s.lezhnev
 */
public class PlanPart {

    private String name;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    private String longName;
    private ArrayList<WorkInPlan> works = new ArrayList<WorkInPlan>();

    public PlanPart(String name, String longName) {
        this.name = name;
        this.longName = longName;
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
