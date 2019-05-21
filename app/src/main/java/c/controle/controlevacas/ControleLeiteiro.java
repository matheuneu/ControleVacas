package c.controle.controlevacas;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

@Table
public class ControleLeiteiro extends SugarRecord {
    String data;
    double volumemanha,volumetarde;
    String vaca;

    Produtor produtor;


    public ControleLeiteiro() {
    }

    public ControleLeiteiro(String data, double volumemanha, double volumetarde, String vaca, Produtor produtor) {
        this.data = data;
        this.volumemanha = volumemanha;
        this.volumetarde = volumetarde;
        this.vaca = vaca;
        this.produtor = produtor;
    }
    public Produtor getProdutor() {
        return produtor;
    }

    public String getVaca() {
        return vaca;
    }

    public void setVaca(String vaca) {
        this.vaca = vaca;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getvolumemanha() {
        return volumemanha;
    }

    public void setvolumemanha(double volumemanha) {
        this.volumemanha = volumemanha;
    }

    public double getvolumetarde() {
        return volumetarde;
    }

    public void setvolumetarde(double volumetarde) {
        this.volumetarde = volumetarde;
    }

    @Override
    public String toString() {
        return "ControleLeiteiro{" +
                "data=" + data +
                ", volumemanha=" + volumemanha +
                ", volumetarde=" + volumetarde +
                ", vaca='" + vaca + '\'' +
                ", produtor=" + produtor +
                '}';
    }
}
