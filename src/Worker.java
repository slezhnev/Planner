/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 04.01.11
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class Worker {

    private String name;
    private boolean overhead;
    private boolean notInMonthReport;
    private Double laborContentTotal;

    public Worker(String name, Double laborContentTotal) {
        this(name, laborContentTotal, false, false);
    }

    public Worker(String name, Double laborContentTotal, boolean overhead, boolean notInMonthReport) {
        this.name = name;
        this.laborContentTotal = laborContentTotal;
        this.overhead = overhead;
        this.notInMonthReport = notInMonthReport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLaborContentTotal() {
        return laborContentTotal;
    }

    public void setLaborContentTotal(Double laborContentTotal) {
        this.laborContentTotal = laborContentTotal;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return name;
    }

    public boolean isOverhead() {
        return overhead;
    }

    public void setOverhead(boolean overhead) {
        this.overhead = overhead;
    }

    public boolean isNotInMonthReport() {
        return notInMonthReport;
    }

    public void setNotInMonthReport(boolean notInMonthReport) {
        this.notInMonthReport = notInMonthReport;
    }
}
