package c.controle.controlevacas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarRecord;

import java.util.Date;

public class CadastrarProdutor extends AppCompatActivity {
    EditText edit_cidade, edit_nome, edit_fone, edit_endereco;
    Produtor produtor = null;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produtor);
        SugarContext.init(this);

        edit_cidade = findViewById(R.id.editText_cadprod_cidade);
        edit_endereco = findViewById(R.id.editText_cadprod_endereco);
        edit_nome = findViewById(R.id.editText_cadprod_nome);
        edit_fone = findViewById(R.id.editText_cadprod_fone);

        Intent i = getIntent();
        id = i.getLongExtra("id", 1);
        produtor = (Produtor) i.getSerializableExtra("Produtor");
        if (produtor != null) {
            edit_cidade.setText(produtor.getCidade());
            edit_nome.setText(produtor.getNome());
            edit_endereco.setText(produtor.getEndereco());
            edit_fone.setText(String.valueOf(produtor.getTelefone()));
        }
    }

    public void limpaCampos() {
        edit_cidade.setText("");
        edit_endereco.setText("");
        edit_nome.setText("");
        edit_fone.setText("");
    }

    public boolean camposOk() {
        return edit_fone.getText().length() > 0
                && edit_nome.getText().length() > 0
                && edit_cidade.getText().length() > 0
                && edit_endereco.getText().length() > 0;
    }

    public void salvar(View v) {
        if (camposOk()) {
            if (this.produtor == null) {
                Produtor produtor = new Produtor();
                produtor.setNome(edit_nome.getText().toString());
                produtor.setCidade(edit_cidade.getText().toString());
                produtor.setEndereco(edit_endereco.getText().toString());
                produtor.setTelefone(Long.parseLong(edit_fone.getText().toString()));
                produtor.setData(System.currentTimeMillis());
                SugarRecord.save(produtor);
                Toast.makeText(this, "Adicionado!", Toast.LENGTH_SHORT).show();
                limpaCampos();
            } else {
                Produtor produtor = Produtor.findById(Produtor.class, this.id);
                produtor.setNome(edit_nome.getText().toString());
                produtor.setCidade(edit_cidade.getText().toString());
                produtor.setEndereco(edit_endereco.getText().toString());
                produtor.setTelefone(Long.parseLong(edit_fone.getText().toString()));
                produtor.save();
                Toast.makeText(this, "Alterado!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
    }

    public void voltar(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed Called");
    }

}
