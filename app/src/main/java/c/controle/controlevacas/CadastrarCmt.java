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
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CadastrarCmt extends AppCompatActivity {
    EditText edit_numero, edit_de, edit_dd, edit_te, edit_td, edit_obs;
    ListView listView, listView_prod;
    TextView text_produtor, text_data;
    String data = null;
    Produtor produtor;
    AdapterProdutores adapterProdutores;
    AdapterListaCmt adapter;
    List<Produtor> lista_prod;
    List<Cmt> lista;
    Cmt cmt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_cmt);
        SugarContext.init(this);

        edit_dd = findViewById(R.id.editText_cadcmt_dd);
        edit_de = findViewById(R.id.editText_cadcmt_de);
        edit_td = findViewById(R.id.editText_cadcmt_td);
        edit_te = findViewById(R.id.editText_cadcmt_te);
        edit_numero = findViewById(R.id.editText_cadcmt_numero);
        edit_obs = findViewById(R.id.editText_cadcmt_obs);
        text_produtor = findViewById(R.id.textView_cadcmt_produtor);
        text_data = findViewById(R.id.textView_cadcmt_data);

        listView = findViewById(R.id.listView_cadcmt);
        lista = new ArrayList<>();

        long id = getIntent().getLongExtra("id",0);
        String data = getIntent().getStringExtra("data");
        if (id>0){
            List<Cmt> listacmt = Select.from(Cmt.class)
                    .where(
                            Condition.prop("produtor").eq(id),
                            Condition.prop("data").eq(data)
                    )
                    //.groupBy("data")
                    .orderBy("animal")
                    .list();
            lista.addAll(listacmt);
            produtor = lista.get(0).getProdutor();
            text_produtor.setText(produtor.getNome());
            this.data=lista.get(0).getData();
            text_data.setText(data);
        }

        adapter = new AdapterListaCmt(this, R.id.txt_prod_nome, lista);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cmt = (Cmt) parent.getAdapter().getItem(position);
                edit_numero.setText(cmt.getanimal());
                edit_dd.setText(String.valueOf(cmt.getDd()));
                edit_de.setText(String.valueOf(cmt.getDe()));
                edit_td.setText(String.valueOf(cmt.getTd()));
                edit_te.setText(String.valueOf(cmt.getTe()));
                edit_obs.setText(cmt.getObs());
                System.out.println(cmt.getId() + " " + cmt.getanimal());
            }
        });
    }

    public boolean camposOk() {
        return edit_te.getText().length() > 0 &&
                edit_td.getText().length() > 0 &&
                edit_dd.getText().length() > 0 &&
                edit_de.getText().length() > 0 &&
                edit_numero.getText().length() > 0 &&
                data != null;
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

        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                data = new Formatador().getDateFromMillis(calendar.getTimeInMillis());
            }
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

    public void limpaCampos() {
        edit_dd.setText("");
        edit_de.setText("");
        edit_obs.setText("");
        edit_td.setText("");
        edit_te.setText("");
        edit_numero.setText("");
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

    public void voltar(View v) {
        /*if (!lista.isEmpty()) {
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
        }else */
        finish();
    }

    public void finalizar(View v) {

        if (!lista.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setTitle("Finalizar cadastro de CMT?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    for (Cmt item : lista) {
                        Cmt.save(item);
                    }
                    Toast.makeText(CadastrarCmt.this, "Salvo com sucesso!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Adicione CMT a lista antes de finalizar!", Toast.LENGTH_LONG).show();
    }

    public void adicionar(View v) {
        if (camposOk()) {
            if (cmt == null) {
                Cmt cmt = new Cmt();
                cmt.setData(data);
                cmt.setDd(Integer.parseInt(edit_dd.getText().toString()));
                cmt.setDe(Integer.parseInt(edit_de.getText().toString()));
                cmt.setTd(Integer.parseInt(edit_td.getText().toString()));
                cmt.setTe(Integer.parseInt(edit_te.getText().toString()));
                cmt.setanimal(edit_numero.getText().toString());
                if (edit_obs.getText().length() > 0)
                    cmt.setObs(edit_obs.getText().toString());
                else cmt.setObs("");
                cmt.setSituacao(calculoSituacao());
                cmt.setProdutor(produtor);
                if (Cmt.save(cmt) > 0) {
                    lista.add(cmt);
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    limpaCampos();
                    Toast.makeText(this, "Adicionado!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "Erro ao adicionar!", Toast.LENGTH_SHORT).show();
            } else {
                Cmt aux = Cmt.findById(Cmt.class, cmt.getId());
                aux.setDd(Integer.parseInt(edit_dd.getText().toString()));
                aux.setDe(Integer.parseInt(edit_de.getText().toString()));
                aux.setTd(Integer.parseInt(edit_td.getText().toString()));
                aux.setTe(Integer.parseInt(edit_te.getText().toString()));
                aux.setanimal(edit_numero.getText().toString());
                if (edit_obs.getText().length() > 0)
                    aux.setObs(edit_obs.getText().toString());
                else aux.setObs("");
                aux.setSituacao(calculoSituacao());
                if (Cmt.save(aux) > 0) {
                    lista.remove(cmt);
                    lista.add(aux);
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    Toast.makeText(this, "Alterado!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Erro ao alterar!", Toast.LENGTH_SHORT).show();
                limpaCampos();
                cmt = null;
            }
        } else Toast.makeText(this, "Digite todos os campos!", Toast.LENGTH_SHORT).show();

    }

    public String calculoSituacao() {
        int dd = Integer.parseInt(edit_dd.getText().toString());
        int de = Integer.parseInt(edit_de.getText().toString());
        int td = Integer.parseInt(edit_td.getText().toString());
        int te = Integer.parseInt(edit_te.getText().toString());
        if (dd + de + td + te <= 400)
            return "Sadia";
        else if (dd + de + td + te > 400 && dd + de + td + te <= 1000)
            return "Subclínica";
        else
            return "Subclínica Crônica";
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed Called");
    }

}
