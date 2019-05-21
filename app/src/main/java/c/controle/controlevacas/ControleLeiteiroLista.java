package c.controle.controlevacas;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ControleLeiteiroLista extends AppCompatActivity {
    ListView listView;
    EditText edit_pesquisa;
    ControleLeiteiro controle;
    AdapterControleLeiteiro adapter;
    List<ControleLeiteiro> lista;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle_leiteiro_lista);
        SugarContext.init(this);

        listView = findViewById(R.id.listView_controleleiteiro);
        lista = new ArrayList<>();
        adapter = new AdapterControleLeiteiro(this, R.id.txt_prod_nome, lista);
        listView.setAdapter(adapter);

        edit_pesquisa = findViewById(R.id.editText_pesquisa_contleit);
        edit_pesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    //pesquisa no banco e atualiza lista
                    lista.clear();
                    List<Produtor> produtors = Select.from(Produtor.class)
                            .where(
                                    Condition.prop("nome").like("%" + s + "%")
                            )
                            .orderBy("nome")
                            .list();
                    if (!produtors.isEmpty()) {
                        for (Produtor produtor : produtors) {
                            //System.out.println(produtor.toString());
                            List<ControleLeiteiro> controle = Select.from(ControleLeiteiro.class)
                                    .where(
                                            Condition.prop("produtor").eq(produtor.getId())
                                    )
                                    .groupBy("data")
                                    .orderBy("data")
                                    .list();
                            //for (ControleLeiteiro l:controle)
                            //System.out.println(l.toString());
                            lista.addAll(controle);
                        }
                    }
                    //System.out.println("lista tamanho: " + lista.size());
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            controle = (ControleLeiteiro) parent.getAdapter().getItem(position);
            //imprimeLista();
            alerta();
        });
    }

    public void alerta(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        // Add the buttons
        builder.setTitle("O QUE VOCÊ DESEJA?");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent i = new Intent(ControleLeiteiroLista.this,CadastrarControleLeiteiro.class);
                i.putExtra("id",controle.getProdutor().getId());
                i.putExtra("data",controle.getData());
                startActivity(i);
            }
        });
        builder.setNegativeButton("Gerar Relatório", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                progressDialog = new ProgressDialog(ControleLeiteiroLista.this);
                progressDialog.setTitle("Aguarde...");
                progressDialog.show();
                new LongOperation().execute();
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void imprimeLista() {
        List<ControleLeiteiro> controlelista = Select.from(ControleLeiteiro.class)
                .where(
                        Condition.prop("produtor").eq(controle.getProdutor().getId()),
                        Condition.prop("data").eq(controle.getData())
                )
                //.groupBy("data")
                .orderBy("vaca")
                .list();
        for (ControleLeiteiro item : controlelista) {
            System.out.println(item.toString());
        }
    }

    public String longToData(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("pt", "BR"));
        return dateFormat.format(date);
    }

    public File createPdf() throws FileNotFoundException, DocumentException {
        Document document = new Document();
        String DEST = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/CONTROLE LEITEIRO/" + controle.getProdutor().getNome() + "/" + new Formatador().dataParaPath(controle.getData()) + " " + controle.getProdutor().getNome() + ".pdf";
        File file = new File(DEST);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        } else {
            if (file.delete())
                file.getParentFile().mkdirs();
        }

        PdfWriter.getInstance(document, new FileOutputStream(DEST));
        document.open();
        PdfPTable table = criaRowInfosProd();
        table.setHeaderRows(1);
        table.setWidthPercentage(100);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(BaseColor.ORANGE);
        }
        table.addCell(String.valueOf(controle.getProdutor().getId()));
        table.addCell(controle.getProdutor().getNome());
        table.addCell(controle.getProdutor().getEndereco());
        table.addCell(controle.getProdutor().getCidade());
        table.addCell(String.valueOf(controle.getProdutor().getTelefone()));
        table.addCell(controle.getData());
        document.add(table);

        document.add(new Paragraph("\n"));

        List<ControleLeiteiro> controlelista = Select.from(ControleLeiteiro.class)
                .where(
                        Condition.prop("produtor").eq(controle.getProdutor().getId()),
                        Condition.prop("data").eq(controle.getData())
                )
                //.groupBy("data")
                .orderBy("vaca")
                .list();

        float volumemanha = 0, volumetarde = 0, volumetotal = 0;
        table = criaRowInfosControle();
        table.setHeaderRows(1);
        table.setWidthPercentage(100);
        cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(BaseColor.ORANGE);
        }
        for (ControleLeiteiro item : controlelista) {
            table.addCell(item.getVaca());
            table.addCell(String.valueOf(item.getvolumemanha()));
            table.addCell(String.valueOf(item.getvolumetarde()));

            volumemanha += item.getvolumemanha();
            volumetarde += item.getvolumetarde();
            volumetotal = (float) (volumetotal + item.getvolumemanha() + item.getvolumetarde());

        }
        document.add(table);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("NÚMERO DE ANIMAIS: " + controlelista.size()));
        document.add(new Paragraph("VOLUME TOTAL MANHA: " + volumemanha + " l"));
        document.add(new Paragraph("VOLUME TOTAL TARDE: " + volumetarde + " l"));
        document.add(new Paragraph("VOLUME TOTAL: " + volumetotal + " l"));

        document.close();

        return file;
    }

    private void viewPdf(File myFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public PdfPTable criaRowInfosProd() {
        PdfPTable table = new PdfPTable(new float[]{1, 2, 2, 2, 2, 2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("ID");
        table.addCell("NOME");
        table.addCell("ENDEREÇO");
        table.addCell("CIDADE");
        table.addCell("TELEFONE");
        table.addCell("DATA");
        return table;
    }

    public PdfPTable criaRowInfosControle() {
        PdfPTable table = new PdfPTable(new float[]{2, 1, 2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("ANIMAL");
        table.addCell("VOLUME MANHÃ");
        table.addCell("VOLUME TARDE");
        return table;
    }

    public void cadastrar(View v) {
        Intent i = new Intent(ControleLeiteiroLista.this, CadastrarControleLeiteiro.class);
        startActivity(i);
    }

    public void voltar(View v) {
        finish();
    }

    public void onResume() {
        super.onResume();
    }

    public void alertaViewPdf(File file) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ControleLeiteiroLista.this);
        builderSingle.setTitle("Deseja visualizar o relatório:");
        builderSingle.setNegativeButton("CANCELAR", (dialog, which) -> dialog.dismiss());
        builderSingle.setPositiveButton("VISUALIZAR", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
        builderSingle.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class LongOperation extends AsyncTask<Void, Void, File> {

        @Override
        protected File doInBackground(Void... params) {
            try {
                return createPdf();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            progressDialog.cancel();
            viewPdf(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //progressDialog.setTitle("Aguarde..."+values[0]);
        }
    }
}
