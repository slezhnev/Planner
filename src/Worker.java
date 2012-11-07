/**
 * Представление работника
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

    @Override
    public int hashCode() {
        String str = this.toString();
        if (str == null) {
            return 0;
        } else {
            return str.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        String str = this.toString();
        if (str != null) {
            return str.equals("" + obj);
        } else {
            return false;
        }
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
