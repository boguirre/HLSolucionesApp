package app.hlsoluciones.hlsolucionesapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import app.hlsoluciones.hlsolucionesapp.AddEspecializadoActivity;
import app.hlsoluciones.hlsolucionesapp.AddIncidenciaActivity;
import app.hlsoluciones.hlsolucionesapp.AddSeguimientoActivity;
import app.hlsoluciones.hlsolucionesapp.EditVehiculoActivity;
import app.hlsoluciones.hlsolucionesapp.HomeActivity;
import app.hlsoluciones.hlsolucionesapp.HomeLavadoActivity;
import app.hlsoluciones.hlsolucionesapp.Modelos.Vehiculo;
import app.hlsoluciones.hlsolucionesapp.R;

public class VehiculoAdapetr extends RecyclerView.Adapter<VehiculoAdapetr.VehiculoHolder>{

    private Context context;
    private ArrayList<Vehiculo> list;
    private ArrayList<Vehiculo> listAll;
    private SharedPreferences sharedPreferences;

    public VehiculoAdapetr(Context context, ArrayList<Vehiculo> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
//        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public VehiculoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vehiculo, parent, false);
        return new VehiculoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoHolder holder, int position) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        Vehiculo vehiculo = list.get(position);
        holder.txtNumPlaca.setText("N° Placa: "+vehiculo.getNumplaca());
        if (vehiculo.getCifravin().equals("null")){
            holder.txtCifravin.setText("Vehiculo No Seminuevo");
        }
        else {
            holder.txtCifravin.setText("Cifra Vin: "+vehiculo.getCifravin());
        }
        holder.txtMarca.setText("Marca: "+vehiculo.getMarca().getName());
        holder.txtModelo.setText("Modelo: "+vehiculo.getModelo().getName());
        holder.tctArea.setText("Area: "+vehiculo.getArea().getName());
        if (sharedPreferences.getInt("id",0) != vehiculo.getUser().getId()){
            holder.txtUser.setText("Registrado por: "+vehiculo.getUser().getName());
        }
        else {
            holder.txtUser.setText("Has registrado este vehiculo");
        }
        if (vehiculo.getStatus().equals("1")){
            holder.txtEstado.setText("Aun no Asignado un Servicio");
        }
        if (vehiculo.getStatus().equals("2")){
            holder.txtEstado.setText("Asignado a un Servicio Lavado");
            holder.imgcheck.setImageResource(R.drawable.check_amarillo);
        }

        if (vehiculo.getStatus().equals("3")){
            holder.txtEstado.setText("Asignado a un Servicio Especializado");
            holder.imgcheck.setImageResource(R.drawable.check_amarillo);
        }

        if (vehiculo.getStatus().equals("4")){
            holder.txtEstado.setText("Vehiculo con Servicio Finalizado");
            holder.imgcheck.setImageResource(R.drawable.check_verde);
        }

        if (vehiculo.getStatus().equals("5")){
            holder.txtEstado.setText("Vehiculo Aprobado");
            holder.imgcheck.setImageResource(R.drawable.check_verde);
        }


        holder.btnOptions.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnOptions);
            popupMenu.inflate(R.menu.menu_vehiculo_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.item_edit:{
                            if (sharedPreferences.getInt("id",0) != vehiculo.getUser().getId()){
                                Toast.makeText(context, "Accion no disponible", Toast.LENGTH_LONG).show();
                                return true;
                            }
                            else {
                                Intent i = new Intent(((HomeLavadoActivity)context), EditVehiculoActivity.class);
                                i.putExtra("vehiculo_id", vehiculo.getId());
                                i.putExtra("position", position);
                                i.putExtra("num_placa", vehiculo.getNumplaca());
                                i.putExtra("cifra_vin", vehiculo.getCifravin());
                                i.putExtra("marca_id", vehiculo.getMarca().getId());
                                i.putExtra("modelo_id", vehiculo.getModelo().getId());
                                i.putExtra("area_id", vehiculo.getArea().getId());
                                i.putExtra("sub_area_id", vehiculo.getSubArea().getId());
                                i.putExtra("foto_placa", vehiculo.getFotoplaca());
                                context.startActivity(i);
                                return true;
                            }

                        }

                        case R.id.item_seguimiento:{
                            if (vehiculo.getStatus().equals("2") || vehiculo.getStatus().equals("4") || vehiculo.getStatus().equals("3")){
                                Toast.makeText(context, "Accion no disponible: Vehiculo con Servicio Finalizado", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            else if (sharedPreferences.getInt("id",0) != vehiculo.getUser().getId()){
                                Toast.makeText(context, "No has registrado este vehiculo", Toast.LENGTH_LONG).show();
                                return true;
                            }
                            else {
                                Intent i = new Intent(((HomeLavadoActivity)context), AddSeguimientoActivity.class);
                                i.putExtra("vehiculo_id", vehiculo.getId());
                                i.putExtra("num_placa", vehiculo.getNumplaca());
                                context.startActivity(i);
                                return true;

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

            ArrayList<Vehiculo> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filteredList.addAll(listAll);
            }else {
                for (Vehiculo vehiculo : listAll){
                    if (vehiculo.getNumplaca().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || vehiculo.getArea().getName().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || vehiculo.getUser().getName().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || vehiculo.getMarca().getName().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || vehiculo.getModelo().getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(vehiculo);
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
            list.addAll((Collection<? extends Vehiculo>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public Filter getFilter() {
        return filter;
    }

    class VehiculoHolder extends RecyclerView.ViewHolder{

        private TextView txtNumPlaca, txtCifravin, txtMarca, txtModelo, txtEstado, tctArea, txtUser;
        private ImageButton btnOptions;
        private ImageView imgcheck;
        private ImageView imgcheckUser;

        public VehiculoHolder(@NonNull View itemView) {
            super(itemView);

            txtNumPlaca = itemView.findViewById(R.id.txtNumPlaca);
            txtCifravin = itemView.findViewById(R.id.txtCifraVin);
            txtMarca = itemView.findViewById(R.id.txtMarca);
            txtModelo = itemView.findViewById(R.id.txtModelo);
            txtEstado = itemView.findViewById(R.id.txtStatus);
            tctArea = itemView.findViewById(R.id.txtArea);
            txtUser = itemView.findViewById(R.id.txtUUsuario);
            imgcheck = itemView.findViewById(R.id.imgCheck);
            imgcheckUser = itemView.findViewById(R.id.imgCheckUser);
            btnOptions = itemView.findViewById(R.id.btnOpciones);
        }
    }
}
