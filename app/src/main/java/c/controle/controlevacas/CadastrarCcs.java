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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CadastrarCcs extends AppCompatActivity {
    EditText edit_ccs, edit_numero;
    TextView text_produtor, text_data;
    ListView listView, listView_prod;
    List<Produtor> lista_prod;
    List<CCS> lista_ccs;
    AdapterProdutores adapterProdutores;
    AdapterListaCcs adapterLista;
    String data = null;
    CCS ccs = null;
    Produtor produtor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_ccs);
        SugarContext.init(this);

        text_data = findViewById(R.id.textView_cadccs_data);
        text_produtor = findViewById(R.id.textView_cadccs_produtor);
        edit_ccs = findViewById(R.id.editText_cadccs_ccs);
        edit_numero = findViewById(R.id.editText_cadccs_numero);

        listView = findViewById(R.id.listView_cadccs);
        lista_ccs = new ArrayList<>();

        long id = getIntent().getLongExtra("id",0);
        String data = getIntent().getStringExtra("data");
        if (id>0){
            List<CCS> listaccs = Select.from(CCS.class)
                    .where(
                            Condition.prop("produtor").eq(id),
                            Condition.prop("data").eq(data)
                    )
                    .orderBy("animal")
                    .list();
            lista_ccs.addAll(listaccs);
            produtor = lista_ccs.get(0).getProdutor();
            text_produtor.setText(produtor.getNome());
            this.data=lista_ccs.get(0).getData();
            text_data.setText(data);
        }

        adapterLista = new AdapterListaCcs(this, R.id.txt_prod_nome, lista_ccs);
        listView.setAdapter(adapterLista);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ccs = (CCS) parent.getAdapter().getItem(position);
                edit_numero.setText(ccs.getAnimal());
                edit_ccs.setText(String.valueOf(ccs.getCcs()));
                System.out.println(ccs.getId() + " " + ccs.getAnimal());
            }
        });
    }

    public void adiciona(View v) {
        if (camposOk()) {
            if (ccs == null) {
                CCS ccs = new CCS(
                        Integer.parseInt(edit_ccs.getText().toString()),
                        data,
                        edit_numero.getText().toString(),
                        calculoSituacao(),
                        produtor
                );
                if (CCS.save(ccs) > 0) {
                    lista_ccs.add(ccs);
                    adapterLista.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    limpaCampos();
                    Toast.makeText(this, "Adicionado!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Erro ao adicionar!", Toast.LENGTH_SHORT).show();
            } else {
                CCS ccsaux = CCS.findById(CCS.class, ccs.getId());
                ccsaux.setAnimal(edit_numero.getText().toString());
                ccsaux.setCcs(Integer.parseInt(edit_ccs.getText().toString()));
                if (CCS.save(ccsaux) > 0) {
                    lista_ccs.remove(ccs);
                    lista_ccs.add(ccsaux);
                    adapterLista.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    Toast.makeText(this, "Alterado!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "Erro ao alterar!", Toast.LENGTH_SHORT).show();
                limpaCampos();
                ccs = null;
            }
        } else Toast.makeText(this, "Digite todos os campos!", Toast.LENGTH_SHORT).show();
    }

    public void voltar(View v) {
        /*if (!lista_ccs.isEmpty()) {
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
        } else */finish();
    }

    public void finalizar(View v) {
        if (!lista_ccs.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setTitle("Finalizar cadastro de CCS?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    for (CCS item : lista_ccs) {
                        CCS.save(item);
                    }
                    Toast.makeText(CadastrarCcs.this, "Salvo com sucesso!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Adicione CCS a lista antes de finalizar!", Toast.LENGTH_LONG).show();

    }

    CalendarView calendario;

    public void seleciona_data(View v) {
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
                System.out.println(data);
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

    public void limpaCampos() {
        edit_ccs.setText("");
        edit_numero.setText("");
    }

    public boolean camposOk() {
        return data != null && produtor != null && edit_ccs.getText().length() > 0 &&
                edit_numero.getText().length() > 0;
    }

    public String calculoSituacao() {
        int ccs = Integer.parseInt(edit_ccs.getText().toString());
        if (ccs <= 400)
            return "Sadia";
        else if (ccs <= 1000)
            return "Subclínica";
        else
            return "Subclínica Crônica";
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed Called");
    }


}
