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

import app.hlsoluciones.hlsolucionesapp.Adapters.IncidenciaAdapter;
import app.hlsoluciones.hlsolucionesapp.AddIncidenciaActivity;
import app.hlsoluciones.hlsolucionesapp.AddVehiculoActivity;
import app.hlsoluciones.hlsolucionesapp.AuthActivity;
import app.hlsoluciones.hlsolucionesapp.HomeColaboradorActivity;
import app.hlsoluciones.hlsolucionesapp.Constraint;
import app.hlsoluciones.hlsolucionesapp.Modelos.Incidencia;
import app.hlsoluciones.hlsolucionesapp.Modelos.Marca;
import app.hlsoluciones.hlsolucionesapp.Modelos.Modelo;
import app.hlsoluciones.hlsolucionesapp.Modelos.Servicio;
import app.hlsoluciones.hlsolucionesapp.Modelos.User;
import app.hlsoluciones.hlsolucionesapp.Modelos.Vehiculo;
import app.hlsoluciones.hlsolucionesapp.R;


public class IncidenciaFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Incidencia> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private IncidenciaAdapter incidenciaAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;

    public IncidenciaFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_incidencia, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerIncidencia);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swiperIncidencia);
        toolbar = view.findViewById(R.id.toolbarIncidencia);
        int perfil = sharedPreferences.getInt("tipo_user_id", 0);
        if (perfil == 3){
            ((HomeColaboradorActivity)getContext()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getIncidencias();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getIncidencias();
    }

    private void getIncidencias() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constraint.INCIDENCIA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject incidenciaObject = array.getJSONObject(i);
                    JSONObject marcaObject = incidenciaObject.getJSONObject("marca");
                    JSONObject modeloObject = incidenciaObject.getJSONObject("modelo");
                    JSONObject userObject = incidenciaObject.getJSONObject("user");

                    Marca marca = new Marca();
                    marca.setId(marcaObject.getInt("marca_id"));
                    marca.setName(marcaObject.getString("nombre_marca"));


                    Modelo modelo = new Modelo();
                    modelo.setId(modeloObject.getInt("modelo_id"));
                    modelo.setName(modeloObject.getString("nombre_modelo"));

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setName(userObject.getString("name"));
                    user.setEmail(userObject.getString("email"));

                    Incidencia incidencia = new Incidencia();
                    incidencia.setId(incidenciaObject.getInt("id"));
                    incidencia.setDescripcion(incidenciaObject.getString("descripcion"));
                    incidencia.setFecha(incidenciaObject.getString("fecha"));
                    incidencia.setMarca(marca);
                    incidencia.setModelo(modelo);
                    incidencia.setUser(user);
                    incidencia.setImgIncidente1(incidenciaObject.getString("img_incidente_1"));
                    incidencia.setImgIncidente2(incidenciaObject.getString("img_incidente_2"));
                    incidencia.setImgIncidente3(incidenciaObject.getString("img_incidente_3"));

                    arrayList.add(incidencia);
                }

                incidenciaAdapter = new IncidenciaAdapter(getContext(),arrayList);
                recyclerView.setAdapter(incidenciaAdapter);

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
                startActivity(new Intent(getContext(), AddIncidenciaActivity.class));
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
                incidenciaAdapter.getFilter().filter(newText);
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
                    if (perfil == 3){
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(((HomeColaboradorActivity)getContext()), AuthActivity.class));
                        ((HomeColaboradorActivity)getContext()).finish();
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