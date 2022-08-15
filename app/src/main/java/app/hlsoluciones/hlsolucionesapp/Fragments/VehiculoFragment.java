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

import app.hlsoluciones.hlsolucionesapp.Adapters.VehiculoAdapetr;
import app.hlsoluciones.hlsolucionesapp.Adapters.VehiculoAdapterEsp;
import app.hlsoluciones.hlsolucionesapp.AddVehiculoActivity;
import app.hlsoluciones.hlsolucionesapp.AuthActivity;
import app.hlsoluciones.hlsolucionesapp.Constraint;
import app.hlsoluciones.hlsolucionesapp.HomeEspecializadoActivity;
import app.hlsoluciones.hlsolucionesapp.HomeLavadoActivity;
import app.hlsoluciones.hlsolucionesapp.Modelos.Area;
import app.hlsoluciones.hlsolucionesapp.Modelos.Marca;
import app.hlsoluciones.hlsolucionesapp.Modelos.Modelo;
import app.hlsoluciones.hlsolucionesapp.Modelos.SubArea;
import app.hlsoluciones.hlsolucionesapp.Modelos.User;
import app.hlsoluciones.hlsolucionesapp.Modelos.Vehiculo;
import app.hlsoluciones.hlsolucionesapp.R;

public class VehiculoFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Vehiculo> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private VehiculoAdapetr vehiculoAdapetr;
    private VehiculoAdapterEsp vehiculoAdapterEsp;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;

    public VehiculoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_vehiculo, container, false);
        init();
        return view;
    }

    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerVehiculo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swiperVehiculo);
        toolbar = view.findViewById(R.id.toolbarVehiculo);
        int perfil = sharedPreferences.getInt("tipo_user_id", 0);
        if (perfil == 1){
            ((HomeLavadoActivity)getContext()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
        else if (perfil == 2){
            ((HomeEspecializadoActivity)getContext()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getVehiculos();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getVehiculos();
    }

    private void getVehiculos(){
        int perfil = sharedPreferences.getInt("tipo_user_id", 0);
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constraint.VEHICULOS,response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject vehiculoObject = array.getJSONObject(i);
                    JSONObject marcaObject = vehiculoObject.getJSONObject("marca");
                    JSONObject modeloObject = vehiculoObject.getJSONObject("modelo");
                    JSONObject areaObject = vehiculoObject.getJSONObject("area");
                    JSONObject subAreaObject = vehiculoObject.getJSONObject("subArea");
                    JSONObject userObject = vehiculoObject.getJSONObject("user");

                    Marca marca = new Marca();
                    marca.setId(marcaObject.getInt("marca_id"));
                    marca.setName(marcaObject.getString("nombre_marca"));

                    Modelo modelo = new Modelo();
                    modelo.setId(modeloObject.getInt("modelo_id"));
                    modelo.setName(modeloObject.getString("nombre_modelo"));

                    Area area = new Area();
                    area.setId(areaObject.getInt("area_id"));
                    area.setName(areaObject.getString("nombre_area"));

                    SubArea subArea = new SubArea();
                    subArea.setId(subAreaObject.getInt("id"));
                    subArea.setName(subAreaObject.getString("nombre"));

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setName(userObject.getString("name"));
                    user.setEmail(userObject.getString("email"));

                    Vehiculo vehiculo = new Vehiculo();
                    vehiculo.setId(vehiculoObject.getInt("vehiculo_id"));
                    vehiculo.setNumplaca(vehiculoObject.getString("num_placa"));
                    vehiculo.setCifravin(vehiculoObject.getString("cifra_vin"));
                    vehiculo.setMarca(marca);
                    vehiculo.setModelo(modelo);
                    vehiculo.setArea(area);
                    vehiculo.setSubArea(subArea);
                    vehiculo.setUser(user);
                    vehiculo.setFotoplaca(vehiculoObject.getString("foto_placa"));
                    vehiculo.setStatus(vehiculoObject.getString("status"));

                    arrayList.add(vehiculo);
                }

                if (perfil == 1){
                    vehiculoAdapetr = new VehiculoAdapetr(getContext(),arrayList);
                    recyclerView.setAdapter(vehiculoAdapetr);
                }
                else if (perfil == 2){
                    vehiculoAdapterEsp = new VehiculoAdapterEsp(getContext(),arrayList);
                    recyclerView.setAdapter(vehiculoAdapterEsp);
                }

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
                int perfil = sharedPreferences.getInt("tipo_user_id", 0);
                if (perfil == 1){
                    vehiculoAdapetr.getFilter().filter(newText);
                }
                else if (perfil == 2){
                    vehiculoAdapterEsp.getFilter().filter(newText);
                }
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
                builder.setMessage("¿Quiere Cerrar Sesion?");
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
                    else if (perfil == 2){
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(((HomeEspecializadoActivity)getContext()), AuthActivity.class));
                        ((HomeEspecializadoActivity)getContext()).finish();
                    }
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