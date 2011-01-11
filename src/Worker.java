/**
 * Created by IntelliJ IDEA.
 * User: Alla
 * Date: 04.01.11
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class Worker {

    private String name;
    private Double laborContentTotal;

    public Worker(String name, Double laborContentTotal) {
        this.name = name;
        this.laborContentTotal = laborContentTotal;
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
}
