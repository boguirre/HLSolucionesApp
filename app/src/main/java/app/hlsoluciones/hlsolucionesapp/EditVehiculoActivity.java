package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.hlsoluciones.hlsolucionesapp.Fragments.VehiculoFragment;
import app.hlsoluciones.hlsolucionesapp.Modelos.AreaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.MarcaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.Modelo;
import app.hlsoluciones.hlsolucionesapp.Modelos.ModeloSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.SubAreaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.Vehiculo;

public class EditVehiculoActivity extends AppCompatActivity {

    private int position = 0, id = 0, marca_id = 0, modelo_id = 0, area_id=0,
            sub_area_id =0;
    private EditText edtNumplaca, edtCifravin;
    private TouchImageView imgPlaca;
    private Spinner spinnerMarca;
    private Spinner spinnerModelo;
    private Spinner spinnerArea;
    private Spinner spinnerSubArea;
    private Button btnEdit;
    private ProgressDialog dialog;
    private TextView tvtSubarea;
    private SharedPreferences preferences;
    private ArrayList<MarcaSpinner> listaMarcas;
    private ArrayList<ModeloSpinner> listaModelos;
    private ArrayList<AreaSpinner> listaAreas;
    private ArrayList<SubAreaSpinner> listaSubareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehiculo);

        init();
    }

    private void init(){
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        edtNumplaca = findViewById(R.id.edtEditNumPlaca);
        edtCifravin = findViewById(R.id.edtEditCifraVin);
        spinnerMarca = findViewById(R.id.spinnerEditMarca);
        spinnerModelo = findViewById(R.id.spinnerEditModelo);
        spinnerArea = findViewById(R.id.spinnerAreaEdit);
        spinnerSubArea = findViewById(R.id.spinnerSubareaEdit);
        tvtSubarea = findViewById(R.id.tvtsubemenuedit);
        btnEdit = findViewById(R.id.btnEditVehiculo);
        imgPlaca = findViewById(R.id.imgEditPlaca);
        listaMarcas = new ArrayList<>();
        listaModelos = new ArrayList<>();
        listaAreas = new ArrayList<>();
        listaSubareas = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("vehiculo_id", 0);
        marca_id = getIntent().getIntExtra("marca_id", 0);
        modelo_id = getIntent().getIntExtra("modelo_id", 0);
        area_id = getIntent().getIntExtra("area_id", 0);
        sub_area_id = getIntent().getIntExtra("sub_area_id", 0);
        edtNumplaca.setText(getIntent().getStringExtra("num_placa"));

        if (getIntent().getStringExtra("cifra_vin").equals("null")){
            edtCifravin.setHint("Ingresar Cifra Vin");
        }
        else {
            edtCifravin.setText(getIntent().getStringExtra("cifra_vin"));
        }

        Picasso.get().load(Constraint.URL+"/storage/placas/"+getIntent().getStringExtra("foto_placa")).into(imgPlaca);

        btnEdit.setOnClickListener(v->{
            if (!edtNumplaca.getText().toString().isEmpty()){
                editVehiculo();
            }
        });

        CargarMarcas();
        CargarAreas();

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AreaSpinner  area = (AreaSpinner) adapterView.getSelectedItem();
                if (area.getId() == 3){
                    tvtSubarea.setVisibility(View.VISIBLE);
                    spinnerSubArea.setVisibility(View.VISIBLE);
                    if (sub_area_id > 2){
                        CargarSubareas();
                    }
                    else {
                        CargarSubareasEdit();
                    }
                }
                else {
                    tvtSubarea.setVisibility(View.GONE);
                    spinnerSubArea.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MarcaSpinner  marca = (MarcaSpinner) adapterView.getSelectedItem();
                listaModelos.clear();
                CargarModelos(marca.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void CargarMarcas(){
        StringRequest request = new StringRequest(Request.Method.GET, Constraint.MARCAS, response -> {

            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    MarcaSpinner m = new MarcaSpinner();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("nombre").toString().trim());
                    listaMarcas.add(m);
                }

                spinnerMarca.setAdapter(new ArrayAdapter<MarcaSpinner>(EditVehiculoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaMarcas));
                spinnerMarca.setSelection(marca_id-1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("msg","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditVehiculoActivity.this);
        queue.add(request);


    }

    private void CargarAreas() {
        StringRequest request = new StringRequest(Request.Method.GET, Constraint.AREAS, response -> {

            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    AreaSpinner m = new AreaSpinner();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("nombre").toString().trim());
                    listaAreas.add(m);
                }

                spinnerArea.setAdapter(new ArrayAdapter<AreaSpinner>(EditVehiculoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaAreas));

                spinnerArea.setSelection(area_id-1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("msg","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditVehiculoActivity.this);
        queue.add(request);
    }

    private void CargarSubareas() {
        StringRequest request = new StringRequest(Request.Method.GET, Constraint.SUBAREA, response -> {

            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    SubAreaSpinner m = new SubAreaSpinner();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("nombre").toString().trim());
                    listaSubareas.add(m);
                }

                spinnerSubArea.setAdapter(new ArrayAdapter<SubAreaSpinner>(EditVehiculoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaSubareas));
                spinnerSubArea.setSelection(sub_area_id-3);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("msg","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditVehiculoActivity.this);
        queue.add(request);
    }

    private void CargarSubareasEdit() {
        StringRequest request = new StringRequest(Request.Method.GET, Constraint.SUBAREA, response -> {

            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    SubAreaSpinner m = new SubAreaSpinner();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("nombre").toString().trim());
                    listaSubareas.add(m);
                }

                spinnerSubArea.setAdapter(new ArrayAdapter<SubAreaSpinner>(EditVehiculoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaSubareas));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("msg","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditVehiculoActivity.this);
        queue.add(request);
    }

//    private void CargarModelos(){
//        StringRequest request = new StringRequest(Request.Method.GET, Constraint.MODELOS, response -> {
//
//            try {
//                JSONArray array = new JSONArray(response);
//
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject jsonObject = array.getJSONObject(i);
//                    ModeloSpinner m = new ModeloSpinner();
//                    m.setId(jsonObject.getInt("id"));
//                    m.setName(jsonObject.getString("nombre").toString().trim());
//                    listaModelos.add(m);
//                }
//
//                spinnerModelo.setAdapter(new ArrayAdapter<ModeloSpinner>(EditVehiculoActivity.this,
//                        android.R.layout.simple_spinner_dropdown_item, listaModelos));
//                spinnerModelo.setSelection(modelo_id-1);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }, error -> {
//            error.printStackTrace();
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = preferences.getString("msg","");
//                HashMap<String,String> map = new HashMap<>();
//                map.put("Authorization","Bearer "+token);
//                return map;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(EditVehiculoActivity.this);
//        queue.add(request);
//
//
//    }

    private void CargarModelos(int item){
        StringRequest request = new StringRequest(Request.Method.GET, Constraint.MODELOSAREAS+"?id="+item, response -> {

            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    ModeloSpinner m = new ModeloSpinner();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("nombre").toString().trim());
                    listaModelos.add(m);
                }

                spinnerModelo.setAdapter(new ArrayAdapter<ModeloSpinner>(EditVehiculoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaModelos));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("msg","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditVehiculoActivity.this);
        queue.add(request);


    }

    private void editVehiculo() {
        int item;
        AreaSpinner  area = (AreaSpinner) spinnerArea.getSelectedItem();
        MarcaSpinner  marca = (MarcaSpinner) spinnerMarca.getSelectedItem();
        SubAreaSpinner  subarea = (SubAreaSpinner) spinnerSubArea.getSelectedItem();
        ModeloSpinner  modelo = (ModeloSpinner) spinnerModelo.getSelectedItem();;
        if (area.getId() == 1){
            item = 1;
        }
        else if (area.getId() == 2){
            item = 2;
        }
        else {
            item = subarea.getId();
        }
        dialog.setMessage("Actualizando");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.PUT,Constraint.UPDATE_VEHICULO+id,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Vehiculo vehiculo = VehiculoFragment.arrayList.get(position);
                    vehiculo.setNumplaca(edtNumplaca.getText().toString());
                    vehiculo.setCifravin(edtCifravin.getText().toString());
                    VehiculoFragment.arrayList.set(position,vehiculo);
                    VehiculoFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    VehiculoFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();
                    Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();

        },error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("msg","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("marca_id", marca.getId()+"");
                map.put("modelo_id", modelo.getId()+"");
                map.put("area_id", area.getId()+"");
                map.put("sub_area_id", item+"");
                map.put("numero_placa", edtNumplaca.getText().toString().trim());
                map.put("cifra_vin", edtCifravin.getText().toString().trim());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditVehiculoActivity.this);
        queue.add(request);

    }

    public void tomatFoto(View view) {
    }

    public void cancelVehiculo(View view) {
        super.onBackPressed();
    }
}