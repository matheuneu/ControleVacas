package c.controle.controlevacas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orm.SugarContext;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CCSLista extends AppCompatActivity {
    EditText edit_pesquisa;
    ListView listView;
    List<CCS> lista;
    AdapterCcs adapter;
    CCS ccs;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ccslista);
        SugarContext.init(this);

        listView = findViewById(R.id.listViewCcs);
        lista = new ArrayList<>();
        adapter = new AdapterCcs(this, R.id.txt_prod_nome, lista);
        listView.setAdapter(adapter);

        edit_pesquisa = findViewById(R.id.editText_ccs_pesquisar);
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
                            List<CCS> ccs = Select.from(CCS.class)
                                    .where(
                                            Condition.prop("produtor").eq(produtor.getId())
                                    )
                                    .groupBy("data")
                                    .orderBy("data")
                                    .list();
                            //for (ControleLeiteiro l:controle)
                            //System.out.println(l.toString());
                            lista.addAll(ccs);
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
            ccs = (CCS) parent.getAdapter().getItem(position);
            alerta();
        });
    }

    /*public void abrirpasta(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri mydir = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CCS");
        intent.setDataAndType(mydir, "application/folder");    // or use
        startActivity(intent);
    }*/

    public void alerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setTitle("O QUE VOCÊ DESEJA?");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent i = new Intent(CCSLista.this, CadastrarCcs.class);
                i.putExtra("id", ccs.getProdutor().getId());
                i.putExtra("data", ccs.getData());
                startActivity(i);
            }
        });
        builder.setNegativeButton("Gerar Relatório", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                progressDialog = new ProgressDialog(CCSLista.this);
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
        String DEST = Environment.getExternalStorageDirectory().getAbsolutePath() +//"/aa.pdf";
                "/CCS/" + ccs.getProdutor().getNome() + "/" + new Formatador().dataParaPath(ccs.getData()) + " " + ccs.getProdutor().getNome() + ".pdf";
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
        table.addCell(String.valueOf(ccs.getProdutor().getId()));
        table.addCell(ccs.getProdutor().getNome());
        table.addCell(ccs.getProdutor().getEndereco());
        table.addCell(ccs.getProdutor().getCidade());
        table.addCell(String.valueOf(ccs.getProdutor().getTelefone()));
        table.addCell(ccs.getData());
        document.add(table);

        document.add(new Paragraph("\n"));

        List<CCS> listaccs = Select.from(CCS.class)
                .where(
                        Condition.prop("produtor").eq(ccs.getProdutor().getId()),
                        Condition.prop("data").eq(ccs.getData())
                )
                //.groupBy("data")
                .orderBy("animal")
                .list();
        float sadia = 0, leve = 0, cronica = 0;
        table = criaRowInfosCCS();
        table.setHeaderRows(1);
        table.setWidthPercentage(100);
        cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(BaseColor.ORANGE);
        }
        for (CCS item : listaccs) {
            table.addCell(item.getAnimal());
            table.addCell(String.valueOf(item.getCcs()));
            table.addCell(situacao(item.getCcs()));

            if (item.getCcs() <= 400)
                sadia++;
            else if (item.getCcs() > 400 && item.getCcs() <= 1000)
                leve++;
            else cronica++;

        }
        document.add(table);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("NÚMERO DE ANIMAIS: " + listaccs.size()));
        document.add(new Paragraph("SADIAS: " + sadia + " (" + sadia / listaccs.size() * 100 + "%)"));
        document.add(new Paragraph("SUBCLÍNICA: " + leve + " (" + leve / listaccs.size() * 100 + "%)"));
        document.add(new Paragraph("SUBCLÍNICA CRÔNICA: " + cronica + " (" + cronica / listaccs.size() * 100 + "%)"));

        /*String[] labels = {"SADIAS","SUBCLÍNICA","SUBCLÍNICA CRÔNICA"};
        float[] values = {sadia,leve,cronica};

        Bitmap pie = new Formatador().criaGrafico(this,labels,values);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        pie.compress(Bitmap.CompressFormat.PNG, 100, stream);
        try {
            Image image = Image.getInstance(stream.toByteArray());
            document.add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        document.close();

        return file;
    }

    public String situacao(int ccs){
        if (ccs <= 400)
            return "Sadia";
        else if (ccs <= 1000)
            return "Subclínica";
        else
            return "Subclínica Crônica";
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

    public PdfPTable criaRowInfosCCS() {
        PdfPTable table = new PdfPTable(new float[]{2, 1, 2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("ANIMAL");
        table.addCell("CCS");
        table.addCell("SITUAÇÃO");
        return table;
    }

    public void cadastrar(View v) {
        Intent i = new Intent(CCSLista.this, CadastrarCcs.class);
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
