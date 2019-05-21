package c.controle.controlevacas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdapterControleLeiteiro extends ArrayAdapter<ControleLeiteiro> {
    private Activity activity;
    private List<ControleLeiteiro> lProdutor;
    private static LayoutInflater inflater = null;

    public AdapterControleLeiteiro(Activity activity, int textViewResourceId, List<ControleLeiteiro> _lProdutores) {
        super(activity, textViewResourceId, _lProdutores);
        try {
            this.activity = activity;
            this.lProdutor = _lProdutores;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {

        }
    }

    public int getCount() {
        return lProdutor.size();
    }

    public Produtor getItem(Produtor position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
        //public TextView display_data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final AdapterProdutores.ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.layout_list_prod, null);
                holder = new AdapterProdutores.ViewHolder();

                holder.display_name = vi.findViewById(R.id.txt_prod_nome);
                //holder.display_data = (TextView) vi.findViewById(R.id.txt_cont);

                vi.setTag(holder);
            } else {
                holder = (AdapterProdutores.ViewHolder) vi.getTag();
            }

            holder.display_name.setText(lProdutor.get(position).produtor.getNome()+"\n"+lProdutor.get(position).data);

        } catch (Exception e) {

        }
        return vi;
    }
    public String longToData(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("pt", "BR"));
        return dateFormat.format(date);
    }

}
