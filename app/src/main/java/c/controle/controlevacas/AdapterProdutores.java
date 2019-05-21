package c.controle.controlevacas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class AdapterProdutores extends ArrayAdapter<Produtor> {
    private Activity activity;
    private List<Produtor> lProdutor;
    private static LayoutInflater inflater = null;

    public AdapterProdutores(Activity activity, int textViewResourceId, List<Produtor> _lProdutores) {
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
        //public TextView display_number;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.layout_list_prod, null);
                holder = new ViewHolder();

                holder.display_name = vi.findViewById(R.id.txt_prod_nome);
                //holder.display_number = (TextView) vi.findViewById(R.id.display_number);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.display_name.setText(lProdutor.get(position).nome);
            //holder.display_number.setText(lProducts.get(position).number);

        } catch (Exception e) {

        }
        return vi;
    }
}