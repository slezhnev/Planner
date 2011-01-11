/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 05.01.11
 * Time: 1:00
 * To change this template use File | Settings | File Templates.
 */
public class WorkerInPlan {

    private Worker worker;
    private Double laborContent;

    public WorkerInPlan(Worker worker, Double laborContent) {
        this.worker = worker;
        this.laborContent = laborContent;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Double getLaborContent() {
        return laborContent;
    }

    public void setLaborContent(Double laborContent) {
        this.laborContent = laborContent;
    }
}
