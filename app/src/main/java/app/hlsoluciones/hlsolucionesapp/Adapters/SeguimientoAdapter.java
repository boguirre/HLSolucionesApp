package app.hlsoluciones.hlsolucionesapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import app.hlsoluciones.hlsolucionesapp.EditVehiculoActivity;
import app.hlsoluciones.hlsolucionesapp.HomeActivity;
import app.hlsoluciones.hlsolucionesapp.HomeLavadoActivity;
import app.hlsoluciones.hlsolucionesapp.Modelos.Seguimiento;
import app.hlsoluciones.hlsolucionesapp.Modelos.Vehiculo;
import app.hlsoluciones.hlsolucionesapp.R;

public class SeguimientoAdapter extends RecyclerView.Adapter<SeguimientoAdapter.SeguimientoHolder> {

    private Context context;
    private ArrayList<Seguimiento> list;
    private ArrayList<Seguimiento> listAll;
    private SharedPreferences sharedPreferences;

    public SeguimientoAdapter(Context context, ArrayList<Seguimiento> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
//        this.sharedPreferences = this.context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public SeguimientoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_seguimiento, parent, false);
        return new SeguimientoAdapter.SeguimientoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoHolder holder, int position) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        Seguimiento seguimiento = list.get(position);
        holder.txtOt.setText("N° OT: "+seguimiento.getOt());
        holder.txtVehiculo.setText("N° Placa Vehiculo: "+seguimiento.getVehiculo().getNumplaca());
        holder.txtServicio.setText("Servicio: "+seguimiento.getServicio().getName());
        holder.txtUser.setText("Usuario encargado: "+seguimiento.getUser().getName());
        holder.txtfechaing.setText("Fecha Ingreso: "+seguimiento.getHoraIngreso());
        if (seguimiento.getHoraSalida().equals("null")){
            holder.txtfechasali.setText("Aun no ha salido");
        }
        else {
            holder.txtfechasali.setText("Fecha Salida: "+seguimiento.getHoraSalida());
        }
//        holder.txtfechasali.setText("fecha salida");
        if (seguimiento.getStatus().equals("1")){
            holder.txtEstado.setText("Registrado");
        }
        if (seguimiento.getStatus().equals("2")){
            holder.txtEstado.setText("Finalizado");
            holder.imgcheck.setImageResource(R.drawable.check_amarillo);
        }

        if (seguimiento.getStatus().equals("3")){
            holder.txtEstado.setText("Aceptado por el administrador");
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
                            if (seguimiento.getStatus().equals("2")){
                                Toast.makeText(context, "Orden en Supervision", Toast.LENGTH_SHORT).show();
                            }
                            else if (sharedPreferences.getInt("id",0) != seguimiento.getUser().getId()){
                                Toast.makeText(context, "No estas permitido hacer este control de calidad", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Intent i = new Intent(((HomeLavadoActivity)context), ControlCalidadActivity.class);
                                i.putExtra("seguimiento_id", seguimiento.getId());
                                i.putExtra("ot", seguimiento.getOt());
                                i.putExtra("vehiculo_id", seguimiento.getVehiculo().getId());
                                i.putExtra("numero_placa", seguimiento.getVehiculo().getNumplaca());
                                i.putExtra("servicio_id", seguimiento.getServicio().getId());
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

            ArrayList<Seguimiento> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filteredList.addAll(listAll);
            }else {
                for (Seguimiento seguimiento : listAll){
                    if (seguimiento.getOt().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            seguimiento.getVehiculo().getNumplaca().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || seguimiento.getUser().getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(seguimiento);
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
            list.addAll((Collection<? extends Seguimiento>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public Filter getFilter() {
        return filter;
    }

    class SeguimientoHolder extends RecyclerView.ViewHolder{

        private TextView txtOt, txtVehiculo, txtServicio, txtUser, txtEstado, txtfechaing, txtfechasali;
        private ImageButton btnOptions;
        private ImageView imgcheck;

        public SeguimientoHolder(@NonNull View itemView) {
            super(itemView);

            txtOt = itemView.findViewById(R.id.txtOT);
            txtVehiculo = itemView.findViewById(R.id.txtPlacaVehiculo);
            txtServicio = itemView.findViewById(R.id.txtServicio);
            txtUser = itemView.findViewById(R.id.txtArea);
            txtfechaing = itemView.findViewById(R.id.txtFechaIngreso);
            txtfechasali = itemView.findViewById(R.id.txtFechaSalida);
            txtEstado = itemView.findViewById(R.id.txtStatusSeguimiento);
            imgcheck = itemView.findViewById(R.id.imgCheckSeguimiento);
            btnOptions = itemView.findViewById(R.id.btnOpcionesSeguimiento);

        }
    }
}
