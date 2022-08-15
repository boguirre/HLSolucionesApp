package app.hlsoluciones.hlsolucionesapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.hlsoluciones.hlsolucionesapp.Adapters.SeguimientoAdapter;
import app.hlsoluciones.hlsolucionesapp.AddVehiculoActivity;
import app.hlsoluciones.hlsolucionesapp.AuthActivity;
import app.hlsoluciones.hlsolucionesapp.Constraint;
import app.hlsoluciones.hlsolucionesapp.HomeEspecializadoActivity;
import app.hlsoluciones.hlsolucionesapp.HomeLavadoActivity;
import app.hlsoluciones.hlsolucionesapp.Modelos.Area;
import app.hlsoluciones.hlsolucionesapp.Modelos.Seguimiento;
import app.hlsoluciones.hlsolucionesapp.Modelos.Servicio;
import app.hlsoluciones.hlsolucionesapp.Modelos.User;
import app.hlsoluciones.hlsolucionesapp.Modelos.Vehiculo;
import app.hlsoluciones.hlsolucionesapp.R;

public class SeguimientoFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Seguimiento> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private SeguimientoAdapter seguimientoAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;

    public SeguimientoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_seguimiento, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerSeguimiento);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swiperSeguimiento);
        toolbar = view.findViewById(R.id.toolbarSeguimiento);
        int perfil = sharedPreferences.getInt("tipo_user_id", 0);
        if (perfil == 1){
            ((HomeLavadoActivity)getContext()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSeguimientos();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        getSeguimientos();
    }

    private void getSeguimientos() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constraint.SEGUIMIENTO, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject seguimientoObject = array.getJSONObject(i);
                    JSONObject vehiculoObject = seguimientoObject.getJSONObject("vehiculo");
                    JSONObject userObject = seguimientoObject.getJSONObject("user");
                    JSONObject servicioObject = seguimientoObject.getJSONObject("servicio");

                    Vehiculo vehiculo = new Vehiculo();
                    vehiculo.setId(vehiculoObject.getInt("vehiculo_id"));
                    vehiculo.setNumplaca(vehiculoObject.getString("num_placa"));

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setName(userObject.getString("name"));
                    user.setEmail(userObject.getString("email"));

                    Servicio servicio = new Servicio();
                    servicio.setId(servicioObject.getInt("servicio_id"));
                    servicio.setName(servicioObject.getString("nombre_servicio"));

                    Seguimiento seguimiento = new Seguimiento();
                    seguimiento.setId(seguimientoObject.getInt("id"));
                    seguimiento.setOt(seguimientoObject.getString("ot"));
                    seguimiento.setHoraIngreso(seguimientoObject.getString("hora_ingreso"));
                    seguimiento.setHoraSalida(seguimientoObject.getString("hora_salida"));
                    seguimiento.setVehiculo(vehiculo);
                    seguimiento.setUser(user);
                    seguimiento.setServicio(servicio);
                    seguimiento.setImgIngreso1(seguimientoObject.getString("img_inicial_1"));
                    seguimiento.setImgIngreso2(seguimientoObject.getString("img_inicial_2"));
                    seguimiento.setImgSalida1(seguimientoObject.getString("img_salida_1"));
                    seguimiento.setImgSalida2(seguimientoObject.getString("img_salida_2"));
                    seguimiento.setImgSalida3(seguimientoObject.getString("img_salida_3"));
                    seguimiento.setImgSalida4(seguimientoObject.getString("img_salida_4"));
                    seguimiento.setStatus(seguimientoObject.getString("status"));

                    arrayList.add(seguimiento);
                }

                seguimientoAdapter = new SeguimientoAdapter(getContext(),arrayList);
                recyclerView.setAdapter(seguimientoAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        },error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);

        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("msg", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItem item1 = menu.findItem(R.id.add);
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getContext(), AddVehiculoActivity.class));
                return false;
            }
        });
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                seguimientoAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:{
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Cerrar Sesion");
                builder.setIcon(R.drawable.ic_baseline_info_24);
                builder.setMessage("¿Quieres Cerrar Sesion?");
                builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        int perfil = sharedPreferences.getInt("tipo_user_id", 0);
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.LOGOUT,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (perfil == 1){
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(((HomeLavadoActivity)getContext()), AuthActivity.class));
                        ((HomeLavadoActivity)getContext()).finish();
                    }
//                    else if (perfil == 2){
//                        editor.clear();
//                        editor.apply();
//                        startActivity(new Intent(((HomeEspecializadoActivity)getContext()), AuthActivity.class));
//                        ((HomeEspecializadoActivity)getContext()).finish();
//                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error -> {
            error.printStackTrace();

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("msg", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }
}