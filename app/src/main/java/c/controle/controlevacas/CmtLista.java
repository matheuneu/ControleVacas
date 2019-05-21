package c.controle.controlevacas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orm.SugarContext;
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

public class CmtLista extends AppCompatActivity {
    ListView listView;
    EditText edit_pesquisa;
    AdapterCmt adapter;
    Cmt cmt;
    List<Cmt> lista;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmt_lista);
        SugarContext.init(this);

        listView = findViewById(R.id.listViewCmt);
        lista = new ArrayList<>();
        adapter = new AdapterCmt(this, R.id.txt_prod_nome, lista);
        listView.setAdapter(adapter);

        edit_pesquisa = findViewById(R.id.editText_cmt_pesquisar);
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
                            List<Cmt> cmt = Select.from(Cmt.class)
                                    .where(
                                            Condition.prop("produtor").eq(produtor.getId())
                                    )
                                    .groupBy("data")
                                    .orderBy("data")
                                    .list();
                            //for (ControleLeiteiro l:controle)
                            //System.out.println(l.toString());
                            lista.addAll(cmt);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cmt = (Cmt) parent.getAdapter().getItem(position);
                alerta();
            }
        });
    }

    public void alerta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setTitle("O QUE VOCÊ DESEJA?");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent i = new Intent(CmtLista.this,CadastrarCmt.class);
                i.putExtra("id",cmt.getProdutor().getId());
                i.putExtra("data",cmt.getData());
                startActivity(i);
            }
        });
        builder.setNegativeButton("Gerar Relatório", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                progressDialog = new ProgressDialog(CmtLista.this);
                progressDialog.setTitle("Aguarde...");
                progressDialog.show();
                new LongOperation().execute();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String longToData(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("pt", "BR"));
        return dateFormat.format(date);
    }

    public File createPdf() throws FileNotFoundException, DocumentException {
        Document document = new Document();
        String DEST = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/CMT/" + cmt.getProdutor().getNome() + "/" + new Formatador().dataParaPath(cmt.getData()) + " " + cmt.getProdutor().getNome() + ".pdf";
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
        table.addCell(String.valueOf(cmt.getProdutor().getId()));
        table.addCell(cmt.getProdutor().getNome());
        table.addCell(cmt.getProdutor().getEndereco());
        table.addCell(cmt.getProdutor().getCidade());
        table.addCell(String.valueOf(cmt.getProdutor().getTelefone()));
        table.addCell(cmt.getData());
        document.add(table);

        document.add(new Paragraph("\n"));

        List<Cmt> listacmt = Select.from(Cmt.class)
                .where(
                        Condition.prop("produtor").eq(cmt.produtor.getId()),
                        Condition.prop("data").eq(cmt.getData())
                )
                //.groupBy("data")
                .orderBy("animal")
                .list();

        float negativo = 0, traco = 0, m = 0, mm = 0, mmm = 0;
        table = criaRowInfosCmt();
        table.setHeaderRows(1);
        table.setWidthPercentage(100);
        cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(BaseColor.ORANGE);
        }
        for (Cmt item : listacmt) {
            table.addCell(item.getanimal());
            table.addCell(String.valueOf(item.getTe()));
            table.addCell(String.valueOf(item.getTd()));
            table.addCell(String.valueOf(item.getDd()));
            table.addCell(String.valueOf(item.getDe()));
            table.addCell(item.getSituacao());
            table.addCell(String.valueOf(item.getObs()));

            if (item.getDd() + item.getDe() + item.getTd() + item.getTe() < 200)
                negativo++;
            else if (item.getDd() + item.getDe() + item.getTd() + item.getTe() >= 200 && item.getDd() + item.getDe() + item.getTd() + item.getTe() < 400)
                traco++;
            else if (item.getDd() + item.getDe() + item.getTd() + item.getTe() >= 400 && item.getDd() + item.getDe() + item.getTd() + item.getTe() < 1200)
                m++;
            else if (item.getDd() + item.getDe() + item.getTd() + item.getTe() >= 1200 && item.getDd() + item.getDe() + item.getTd() + item.getTe() < 5000)
                mm++;
            else mmm++;
        }
        document.add(table);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("NÚMERO DE ANIMAIS: " + listacmt.size()));
        document.add(new Paragraph("NORMAL: " + negativo + " (" + negativo / listacmt.size() * 100 + "%)"));
        document.add(new Paragraph("TRAÇO (MUITO POUCO): " + traco + " (" + traco / listacmt.size() * 100 + "%)"));
        document.add(new Paragraph("+ (POUCO): " + m + " (" + m / listacmt.size() * 100 + "%)"));
        document.add(new Paragraph("++ (FORTE): " + mm + " (" + mm / listacmt.size() * 100 + "%)"));
        document.add(new Paragraph("+++ (MUITO FORTE): " + mmm + " (" + mmm / listacmt.size() * 100 + "%)"));

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

    public PdfPTable criaRowInfosCmt() {
        PdfPTable table = new PdfPTable(new float[]{1, 1, 1, 1, 1, 2, 2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("ANIMAL");
        table.addCell("TE(x1000)");
        table.addCell("TD(x1000)");
        table.addCell("DE(x1000)");
        table.addCell("DD(x1000)");
        table.addCell("SITUAÇÃO");
        table.addCell("OBS");
        return table;
    }


    public void cadastrar(View v) {
        Intent i = new Intent(CmtLista.this, CadastrarCmt.class);
        startActivity(i);
    }

    public void voltar(View e) {
        finish();
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
