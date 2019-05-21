package c.controle.controlevacas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CadastrarControleLeiteiro extends AppCompatActivity {
    EditText edit_manha, edit_tarde, edit_numero;
    TextView text_produtor, text_data;
    ListView listView, listView_prod;
    List<Produtor> lista_prod;
    List<ControleLeiteiro> lista_controle;
    AdapterProdutores adapterProdutores;
    AdapterListaControleLeiteiro adapterLista;
    String data = null;
    Produtor produtor;
    ControleLeiteiro controleLeiteiro = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_controle_leiteiro);
        SugarContext.init(this);

        edit_manha = findViewById(R.id.editText_cadleit_manha);
        edit_tarde = findViewById(R.id.editText_cadleit_tarde);
        edit_numero = findViewById(R.id.editText_cadleit_numero);
        text_produtor = findViewById(R.id.textView_cadleit_produtor);
        text_data = findViewById(R.id.textView_cadleit_data);
        listView = findViewById(R.id.listView_cadleit);
        lista_controle = new ArrayList<>();

        long id = getIntent().getLongExtra("id",0);
        String data = getIntent().getStringExtra("data");
        if (id>0){
            List<ControleLeiteiro> controlelista = Select.from(ControleLeiteiro.class)
                    .where(
                            Condition.prop("produtor").eq(id),
                            Condition.prop("data").eq(data)
                    )
                    //.groupBy("data")
                    .orderBy("vaca")
                    .list();
            lista_controle.addAll(controlelista);
            produtor = lista_controle.get(0).getProdutor();
            text_produtor.setText(produtor.getNome());
            this.data=lista_controle.get(0).getData();
            text_data.setText(data);
        }

        adapterLista = new AdapterListaControleLeiteiro(this, R.id.txt_prod_nome, lista_controle);
        listView.setAdapter(adapterLista);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                controleLeiteiro = (ControleLeiteiro) parent.getAdapter().getItem(position);
                edit_manha.setText(String.valueOf(controleLeiteiro.getvolumemanha()));
                edit_tarde.setText(String.valueOf(controleLeiteiro.getvolumetarde()));
                edit_numero.setText(controleLeiteiro.getVaca());
            }
        });
    }

    CalendarView calendario;

    public void alertaCalendario() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_alerta_calendario);

        calendario = dialog.findViewById(R.id.calendarView_alerta);
        Calendar calendar1 = Calendar.getInstance();
        calendario.setDate(calendar1.getTimeInMillis());
        data = new Formatador().getDateFromMillis(calendar1.getTimeInMillis());

        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            data = new Formatador().getDateFromMillis(calendar.getTimeInMillis());
        });


        Button dialogButton = dialog.findViewById(R.id.bt_alertacalendario_salvar);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_data.setText(data);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public String longToData(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("pt", "BR"));
        return dateFormat.format(date);
    }

    public void seleciona_produtor(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_alerta_prod);
        listView_prod = dialog.findViewById(R.id.listView_alerta_prod);
        lista_prod = new ArrayList<>();
        adapterProdutores = new AdapterProdutores(this, R.id.txt_prod_nome, lista_prod);
        listView_prod.setAdapter(adapterProdutores);

        EditText edit_nome = dialog.findViewById(R.id.editText_nome_alerta_prod);
        edit_nome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    lista_prod.clear();
                    lista_prod.addAll(Select.from(Produtor.class)
                            .where(
                                    Condition.prop("nome").like("%" + s + "%")
                            )
                            .orderBy("nome")
                            .list());
                    adapterProdutores.notifyDataSetChanged();
                    listView_prod.invalidateViews();
                    listView_prod.refreshDrawableState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView_prod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                produtor = (Produtor) parent.getAdapter().getItem(position);
                text_produtor.setText(produtor.getNome());
                //System.out.println(produtor.toString());
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void seleciona_data(View v) {
        alertaCalendario();
    }

    public void adiciona(View v) {
        if (camposOk()) {
            if (controleLeiteiro == null) {
                ControleLeiteiro controleLeiteiro = new ControleLeiteiro(
                        data,
                        Double.parseDouble(edit_manha.getText().toString()),
                        Double.parseDouble(edit_tarde.getText().toString()),
                        edit_numero.getText().toString(),
                        produtor
                );
                if (ControleLeiteiro.save(controleLeiteiro) > 0) {
                    lista_controle.add(controleLeiteiro);
                    adapterLista.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    limpaCampos();
                    Toast.makeText(this, "Adicionado!", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(this, "Erro ao adicionar!", Toast.LENGTH_SHORT).show();
            } else {
                ControleLeiteiro aux = ControleLeiteiro.findById(ControleLeiteiro.class, controleLeiteiro.getId());
                aux.setVaca(edit_numero.getText().toString());
                aux.setvolumemanha(Double.parseDouble(edit_manha.getText().toString()));
                aux.setvolumetarde(Double.parseDouble(edit_tarde.getText().toString()));
                if (ControleLeiteiro.save(aux) > 0) {
                    lista_controle.remove(controleLeiteiro);
                    lista_controle.add(aux);
                    adapterLista.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    Toast.makeText(this, "Alterado!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "Erro ao alterar!", Toast.LENGTH_SHORT).show();
                limpaCampos();
                controleLeiteiro = null;
            }
        } else Toast.makeText(this, "Digite todos os campos!", Toast.LENGTH_SHORT).show();
    }

    public void finalizar(View v) {
        if (!lista_controle.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setTitle("Finalizar cadastro de controle leiteiro?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    for (ControleLeiteiro item : lista_controle) {
                        ControleLeiteiro.save(item);
                    }
                    Toast.makeText(CadastrarControleLeiteiro.this, "Salvo com sucesso!", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                    finish();
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else
            Toast.makeText(this, "Adicione controle leiteiro a lista antes de finalizar!", Toast.LENGTH_LONG).show();
    }

    public void limpaCampos() {
        edit_manha.setText("");
        edit_numero.setText("");
        edit_tarde.setText("");
    }

    public boolean camposOk() {
        return data != null && produtor != null && edit_manha.getText().length() > 0 &&
                edit_numero.getText().length() > 0 &&
                edit_tarde.getText().length() > 0;
    }

    public void voltar(View v) {
        /*if (!lista_controle.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setTitle("Deseja sair sem salvar?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    finish();
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else */
        finish();
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed Called");
    }

}
