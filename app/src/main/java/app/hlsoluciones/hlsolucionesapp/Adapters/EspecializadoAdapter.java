package app.hlsoluciones.hlsolucionesapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

import app.hlsoluciones.hlsolucionesapp.ControlCalidadActivity;
import app.hlsoluciones.hlsolucionesapp.EspControlCalidadActivity;
import app.hlsoluciones.hlsolucionesapp.HomeActivity;
import app.hlsoluciones.hlsolucionesapp.HomeEspecializadoActivity;
import app.hlsoluciones.hlsolucionesapp.Modelos.Especializado;
import app.hlsoluciones.hlsolucionesapp.Modelos.Seguimiento;
import app.hlsoluciones.hlsolucionesapp.R;

public class EspecializadoAdapter extends RecyclerView.Adapter<EspecializadoAdapter.EspecializadoHolder>{

    private Context context;
    private ArrayList<Especializado> list;
    private ArrayList<Especializado> listAll;
    private SharedPreferences sharedPreferences;

    public EspecializadoAdapter(Context context, ArrayList<Especializado> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
//        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public EspecializadoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_especializado, parent, false);
        return new EspecializadoAdapter.EspecializadoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EspecializadoHolder holder, int position) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        Especializado especializado = list.get(position);
        holder.txtOt.setText("N° OT: "+especializado.getOt());
        holder.txtOc.setText("N° OC:"+especializado.getOc());
        holder.txtVehiculo.setText("N° Placa Vehiculo: "+especializado.getVehiculo().getNumplaca());
        holder.txtServicio.setText("Servicio: "+especializado.getServicio().getName());
        holder.txtUser.setText("Usuario Encargado: "+especializado.getUser().getName());
        holder.txtEspecialista.setText("Especialista Encargado: "+especializado.getEspecialista().getName());
        holder.txtfechaing.setText("Fecha Ingreso: "+especializado.getHoraIngreso());
        if (especializado.getHoraSalida().equals("null")){
            holder.txtfechasali.setText("Aun no ha salido");
        }
        else {
            holder.txtfechasali.setText("Fecha Salida: "+especializado.getHoraSalida());
        }
        if (especializado.getStatus().equals("1")){
            holder.txtEstado.setText("Registrado");
        }
        if (especializado.getStatus().equals("2")){
            holder.txtEstado.setText("Finalizado");
            holder.imgcheck.setImageResource(R.drawable.check_amarillo);
        }
        if (especializado.getStatus().equals("3")){
            holder.txtEstado.setText("Aceptado");
            holder.imgcheck.setImageResource(R.drawable.check_verde);
        }

        holder.btnOptions.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnOptions);
            popupMenu.inflate(R.menu.menu_seguimiento_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.item_calidad:{
                            if (especializado.getStatus().equals("2")){
                                Toast.makeText(context, "Orden en Supervision", Toast.LENGTH_SHORT).show();
                            }
                            else if (sharedPreferences.getInt("id",0) != especializado.getUser().getId()){
                                Toast.makeText(context, "No estas permitido hacer este control de calidad", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Intent i = new Intent(((HomeEspecializadoActivity)context), EspControlCalidadActivity.class);
                                i.putExtra("especializado_id", especializado.getId());
                                i.putExtra("ot", especializado.getOt());
                                i.putExtra("vehiculo_id", especializado.getVehiculo().getId());
                                i.putExtra("servicio_id", especializado.getServicio().getId());
                                i.putExtra("especialista_id", especializado.getEspecialista().getId());
                                context.startActivity(i);
                                return  true;
                            }

                        }
                    }
                    return false;
                }
            });
            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<Especializado> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filteredList.addAll(listAll);
            }else {
                for (Especializado especializado : listAll){
                    if (especializado.getOt().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            especializado.getVehiculo().getNumplaca().toLowerCase().contains(charSequence.toString().toLowerCase())
                    || especializado.getOc().toLowerCase().contains(charSequence.toString().toLowerCase())
                    || especializado.getServicio().getName().contains(charSequence.toString().toLowerCase())
                    || especializado.getUser().getName().contains(charSequence.toString().toLowerCase())
                    || especializado.getEspecialista().getName().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(especializado);
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
            list.addAll((Collection<? extends Especializado>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public Filter getFilter() {
        return filter;
    }

    class EspecializadoHolder extends RecyclerView.ViewHolder{

        private TextView txtOt, txtOc, txtVehiculo, txtServicio, txtUser, txtEspecialista, txtEstado, txtfechaing, txtfechasali;
        private ImageButton btnOptions;
        private ImageView imgcheck;

        public EspecializadoHolder(@NonNull View itemView) {
            super(itemView);

            txtOt = itemView.findViewById(R.id.txtOTesp);
            txtOc = itemView.findViewById(R.id.txtOcEsp);
            txtVehiculo = itemView.findViewById(R.id.txtPlacaVehiculoEsp);
            txtServicio = itemView.findViewById(R.id.txtServicioEsp);
            txtUser = itemView.findViewById(R.id.txtAreaEsp);
            txtUser = itemView.findViewById(R.id.txtAreaEsp);
            txtEspecialista = itemView.findViewById(R.id.txtEspecialista);
            txtfechaing = itemView.findViewById(R.id.txtFechaIngresoEsp);
            txtfechasali = itemView.findViewById(R.id.txtFechaSalidaEsp);
            txtEstado = itemView.findViewById(R.id.txtStatusEspecializado);
            imgcheck = itemView.findViewById(R.id.imgCheckEspec);
            btnOptions = itemView.findViewById(R.id.btnOpcionesEspecializado);

        }
    }
}
