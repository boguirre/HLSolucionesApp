package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.hlsoluciones.hlsolucionesapp.Modelos.AreaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.MarcaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.ModeloSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.ServicioSpinner;

public class AddIncidenciaActivity extends AppCompatActivity {

    private Button btnAddIncidencia;
    private ImageView imgSeg1, imgSeg2, imgSeg3;
    private EditText edtDescripcion;
    private Spinner spinnerMarca, spinnerModelo;
    private TextView txtplaca;
//    private Spinner spinnerArea;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
//    private ArrayList<AreaSpinner> listaAreas;
    private ArrayList<ServicioSpinner> listaServicios;
    private ArrayList<MarcaSpinner> listaMarcas;
    private ArrayList<ModeloSpinner> listaModelos;
    Bitmap bitmap;
    Bitmap bitmap2;
    Bitmap bitmap3;
    Bitmap bitmap4;
    private int vehiculoId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_incidencia);

        int nightModeFlags = AddIncidenciaActivity.this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                /* si esta activo el modo oscuro lo desactiva */
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }

        init();
    }

    private void init(){
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddIncidencia = findViewById(R.id.btnAddIncidencia);
        imgSeg1 = findViewById(R.id.imgInci1);
        imgSeg2 = findViewById(R.id.imgInci2);
        imgSeg3 = findViewById(R.id.imgInci3);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        spinnerModelo = findViewById(R.id.spinnerModeloInci);
        spinnerMarca = findViewById(R.id.spinnerMarcaInci);
//        spinnerArea = findViewById(R.id.spinnerArea);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        listaServicios = new ArrayList<>();
        listaMarcas = new ArrayList<>();
        listaModelos = new ArrayList<>();
        CargarMarcas();

        btnAddIncidencia.setOnClickListener(v->{
            if (!edtDescripcion.getText().toString().isEmpty() || imgSeg1.getDrawable() == null || imgSeg2.getDrawable() == null
                    || imgSeg3.getDrawable() == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddIncidenciaActivity.this);
                builder.setIcon(R.drawable.ic_baseline_info_24);
                builder.setTitle("Registrar un nuevo Incidencia");
                builder.setMessage("Despues de esto ya no se podra Actualizar. ¿Desea registrar una incidencia");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        guardarIncidencia();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
            else {
                Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show();
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

                spinnerMarca.setAdapter(new ArrayAdapter<MarcaSpinner>(AddIncidenciaActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaMarcas));
//                ArrayAdapter<Marca> mc = new ArrayAdapter<Marca>(AddVehiculoActivity.this,
//                        android.R.layout.simple_spinner_dropdown_item);
//                spinnerMarca.setAdapter(mc);

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

        RequestQueue queue = Volley.newRequestQueue(AddIncidenciaActivity.this);
        queue.add(request);


    }

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

                spinnerModelo.setAdapter(new ArrayAdapter<ModeloSpinner>(AddIncidenciaActivity.this,
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

        RequestQueue queue = Volley.newRequestQueue(AddIncidenciaActivity.this);
        queue.add(request);


    }


    private void guardarIncidencia() {
        progressDialog.setMessage("Guardando");
        progressDialog.show();

        MarcaSpinner marca = (MarcaSpinner) spinnerMarca.getSelectedItem();
        ModeloSpinner modelo = (ModeloSpinner) spinnerModelo.getSelectedItem();

        StringRequest request = new StringRequest(Request.Method.POST, Constraint.INCIDENCIA,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Toast.makeText(this, String.format("Registro Correcto"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo guardar: Por favor ingresar todos los campos" +
                        "y/o imagenes" , Toast.LENGTH_LONG).show();
            }
            limpiar();
            progressDialog.dismiss();

        },error -> {

            error.printStackTrace();
            progressDialog.dismiss();

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
                map.put("modelo_id", modelo.getId()+"");
                map.put("marca_id", marca.getId()+"");
                map.put("fecha_registrada", getDateTime());
                map.put("descripcion", edtDescripcion.getText().toString());
                map.put("foto_incidencia_1", bitmapToString(bitmap));
                map.put("foto_incidencia_2", bitmapToString(bitmap2));
                map.put("foto_incidencia_3", bitmapToString(bitmap3));
                return map;

            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddIncidenciaActivity.this);
        queue.add(request);
    }

    private void limpiar() {
        edtDescripcion.setText("");
        imgSeg1.setImageBitmap(null);
        imgSeg2.setImageBitmap(null);
        imgSeg3.setImageBitmap(null);
        imgSeg1.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        imgSeg2.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        imgSeg3.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void cancelIncidencia(View view) {
        super.onBackPressed();
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }

        return "";
    }

    public void tomatFoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    public void tomatFoto2(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    public void tomatFoto3(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Cómo obtener el mapa de bits de la Galería
            bitmap = (Bitmap) extras.get("data");
            //Configuración del mapa de bits en ImageView
            imgSeg1.setImageBitmap(bitmap);
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Cómo obtener el mapa de bits de la Galería
            bitmap2 = (Bitmap) extras.get("data");
            //Configuración del mapa de bits en ImageView
            imgSeg2.setImageBitmap(bitmap2);
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Cómo obtener el mapa de bits de la Galería
            bitmap3 = (Bitmap) extras.get("data");
            //Configuración del mapa de bits en ImageView
            imgSeg3.setImageBitmap(bitmap3);
        }

    }
}