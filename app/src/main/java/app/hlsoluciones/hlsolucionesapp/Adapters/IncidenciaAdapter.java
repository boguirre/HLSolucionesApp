package app.hlsoluciones.hlsolucionesapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

import app.hlsoluciones.hlsolucionesapp.Modelos.Incidencia;
import app.hlsoluciones.hlsolucionesapp.Modelos.Seguimiento;
import app.hlsoluciones.hlsolucionesapp.R;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.IncidenciaHolder>{

    private Context context;
    private ArrayList<Incidencia> list;
    private ArrayList<Incidencia> listAll;
    private SharedPreferences sharedPreferences;

    public IncidenciaAdapter(Context context, ArrayList<Incidencia> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
//        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public IncidenciaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_incidencia, parent, false);
        return new IncidenciaAdapter.IncidenciaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidenciaHolder holder, int position) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        Incidencia incidencia = list.get(position);
        holder.txtId.setText(incidencia.getId()+"");
        holder.txtVehiculo.setText("Modelo: "+incidencia.getMarca().getName());
        holder.txtServicio.setText("Marca: "+incidencia.getModelo().getName());
        holder.txtDescripcion.setText(incidencia.getDescripcion());
        holder.txtFecha.setText("Fecha: "+incidencia.getFecha());
        if (sharedPreferences.getInt("id",0) != incidencia.getUser().getId()){
            holder.txtUser.setText("Registrado por: "+incidencia.getUser().getName());
        }
        else {
            holder.txtUser.setText("Has registrado esta incidencia");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<Incidencia> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filteredList.addAll(listAll);
            }else {
                for (Incidencia incidencia : listAll){
                    if (incidencia.getMarca().getName().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            incidencia.getModelo().getName().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || incidencia.getUser().getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(incidencia);
                    }
                }


            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends Incidencia>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public Filter getFilter() {
        return filter;
    }

    class IncidenciaHolder extends RecyclerView.ViewHolder{

        private TextView txtId, txtVehiculo, txtServicio, txtDescripcion, txtUser, txtFecha;
        private ImageButton btnOptions;

        public IncidenciaHolder(@NonNull View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.txtIdIncidencia);
            txtVehiculo = itemView.findViewById(R.id.txtPlacaVehiculoIncidencia);
            txtServicio = itemView.findViewById(R.id.txtServicioIncidencia);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionIncidencia);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtUser = itemView.findViewById(R.id.txtUserIncidencia);
            btnOptions = itemView.findViewById(R.id.btnOpcionesIncidencia);

        }
    }
}
