package c.controle.controlevacas;


import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;

@Table
public class Produtor extends SugarRecord implements Serializable {
    long telefone, data;
    String nome, endereco, cidade;

    public Produtor() {
    }

    public Produtor(long telefone, long data, String nome, String endereco, String cidade) {
        this.telefone = telefone;
        this.data = data;
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
    }


    public long getTelefone() {
        return telefone;
    }

    public void setTelefone(long telefone) {
        this.telefone = telefone;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    @Override
    public String toString() {
        return "Produtor{" +
                "telefone=" + telefone +
                ", data=" + data +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", cidade='" + cidade + '\'' +
                '}';
    }
}
