package c.controle.controlevacas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class ProdutoresLista extends AppCompatActivity {
    EditText edit_procurar;
    ListView listView;
    List<Produtor> lista;
    AdapterProdutores adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtores_lista);
        SugarContext.init( this );

        edit_procurar = findViewById(R.id.editText_produtores_procurar);
        listView = findViewById(R.id.listview_produtores_lista);
        lista = new ArrayList<>();
        adapter = new AdapterProdutores(this, R.id.txt_prod_nome, lista);
        listView.setAdapter(adapter);

        edit_procurar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    //pesquisa no banco e atualiza lista
                    lista.clear();
                    lista.addAll(Select.from(Produtor.class)
                            .where(
                                    Condition.prop("nome").like("%"+s+"%")
                            )
                            .orderBy("nome")
                            .list());
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produtor produtor = (Produtor) parent.getAdapter().getItem(position);
                alertaInfos(produtor);
            }
        });
    }

    public void alertaInfos(final Produtor produtor){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ID: "+produtor.getId()+"\nNOME: "+produtor.getNome()+"\nCIDADE: "+produtor.getTelefone()+"\nENDEREÇO: "+produtor.getEndereco()+"\nCIDADE: "+produtor.getCidade());
        // Add the buttons
        builder.setTitle("INFORMAÇÕES DO PRODUTOR");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent i = new Intent(ProdutoresLista.this,CadastrarProdutor.class);
                i.putExtra("id",produtor.getId());
                i.putExtra("Produtor", produtor);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void cadastrar(View v) {
        Intent i = new Intent(ProdutoresLista.this, CadastrarProdutor.class);
        startActivity(i);
    }

    public void voltar(View v) {
        finish();
    }

    public void onResume(){
        super.onResume();
        edit_procurar.setText("");
        lista.clear();
        adapter.notifyDataSetChanged();
        listView.invalidateViews();
        listView.refreshDrawableState();
    }
}
