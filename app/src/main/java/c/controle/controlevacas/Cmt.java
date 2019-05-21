package c.controle.controlevacas;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class Cmt extends SugarRecord {
    String data;
    String situacao, animal, obs;
    int de, dd, te, td;
    Produtor produtor;

    public Cmt() {
    }

    public Cmt(String data, String situacao, String animal, String obs, int de, int dd, int te, int td, Produtor produtor) {
        this.data = data;
        this.situacao = situacao;
        this.animal = animal;
        this.obs = obs;
        this.de = de;
        this.dd = dd;
        this.te = te;
        this.td = td;
        this.produtor = produtor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getanimal() {
        return animal;
    }

    public void setanimal(String animal) {
        this.animal = animal;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public int getDe() {
        return de;
    }

    public void setDe(int de) {
        this.de = de;
    }

    public int getDd() {
        return dd;
    }

    public void setDd(int dd) {
        this.dd = dd;
    }

    public int getTe() {
        return te;
    }

    public void setTe(int te) {
        this.te = te;
    }

    public int getTd() {
        return td;
    }

    public void setTd(int td) {
        this.td = td;
    }

    public Produtor getProdutor() {
        return produtor;
    }

    public void setProdutor(Produtor produtor) {
        this.produtor = produtor;
    }

    @Override
    public String toString() {
        return "Cmt{" +
                "data=" + data +
                ", situacao='" + situacao + '\'' +
                ", animal='" + animal + '\'' +
                ", obs='" + obs + '\'' +
                ", de=" + de +
                ", dd=" + dd +
                ", te=" + te +
                ", td=" + td +
                ", produtor=" + produtor +
                '}';
    }
}
