/**
 * Работник
 */
public class WorkerInPlan {

    /**
     * Работник
     */
    private Worker worker;
    /**
     * Трудоемкость
     */
    private Double laborContent;
    /**
     * Трудоемкость - по месяцам
     */
    private Double[] perMonth;

    public WorkerInPlan(Worker worker, Double laborContent) {
        this.worker = worker;
        this.laborContent = laborContent;
        perMonth = new Double[3];
        for (int i = 0; i < 3; i++)
            perMonth[i] = 0.0;
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

    public Double[] getPerMonth() {
        return perMonth;
    }

    public double calcRestLabor(int month) {
        double total = laborContent;
        if (month >= 0) total = total - perMonth[0];
        if (month >= 1) total = total - perMonth[1];
        if (month >= 2) total = total - perMonth[2];
        return total;
    }

    public WorkerInPlan clone() {
        WorkerInPlan res = new WorkerInPlan(worker, laborContent);
        res.perMonth = new Double[perMonth.length];
        int i = 0;
        for (Double tmp : perMonth) {
            res.perMonth[i++] = tmp;
        }
        return res;
    }
}
