package c.controle.controlevacas;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;

@Table
public class CCS extends SugarRecord {
    int ccs;
    String data;
    String animal, situacao;

    Produtor produtor;

    public CCS() {
    }

    public CCS(int ccs, String data, String animal, String situacao, Produtor produtor) {
        this.ccs = ccs;
        this.data = data;
        this.animal = animal;
        this.situacao = situacao;
        this.produtor = produtor;
    }

    public int getCcs() {
        return ccs;
    }

    public String getData() {
        return data;
    }

    public String getAnimal() {
        return animal;
    }

    public String getSituacao() {
        return situacao;
    }

    public Produtor getProdutor() {
        return produtor;
    }

    public void setCcs(int ccs) {
        this.ccs = ccs;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }
}
