package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
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
import app.hlsoluciones.hlsolucionesapp.Modelos.EspecialistaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.ServicioSpinner;

public class AddEspecializadoActivity extends AppCompatActivity {

    private Button btnAddEspecializado;
    private ImageView imgSeg1, imgSeg2, imgSeg3, imgSeg4;
    private EditText edtOT;
    private EditText edtOC;
    private TextView txtplaca;
    private Spinner spinnerServicio;
    private Spinner spinnerEspecialista;
    private TextView tvtAddService;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private ArrayList<AreaSpinner> listaAreas;
    private ArrayList<ServicioSpinner> listaServicios;
    private ArrayList<EspecialistaSpinner> listaEspecialista;
    private SwipeRefreshLayout refreshLayout;
    Bitmap bitmap;
    Bitmap bitmap2;
    Bitmap bitmap3;
    Bitmap bitmap4;
    private int vehiculoId = 0;
    private String num_placa = "";
    String subarea[]={"SQ1","SQ2"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_especializado);

        int nightModeFlags = AddEspecializadoActivity.this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
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
        btnAddEspecializado = findViewById(R.id.btnAddEspecializado);
        imgSeg1 = findViewById(R.id.imgEsp1);
        imgSeg2 = findViewById(R.id.imgEsp2);
        imgSeg3 = findViewById(R.id.imgEsp3);
        imgSeg4 = findViewById(R.id.imgEsp4);
        tvtAddService = findViewById(R.id.txtAddService);
        edtOT = findViewById(R.id.edtOrdenTrabajoEsp);
        edtOC= findViewById(R.id.edtOC);
        txtplaca = findViewById(R.id.txtPlacaSeleccionadoEsp);
        spinnerServicio = findViewById(R.id.spinnerServicioEsp);
        spinnerEspecialista = findViewById(R.id.spinnerEspecialista);
        progressDialog = new ProgressDialog(this);
        refreshLayout = findViewById(R.id.swiperAddEspecializado);
        progressDialog.setCancelable(false);
        listaAreas = new ArrayList<>();
        listaServicios = new ArrayList<>();
        listaEspecialista = new ArrayList<>();
        vehiculoId = getIntent().getIntExtra("vehiculo_id", 0);
        num_placa = getIntent().getStringExtra("num_placa");
        txtplaca.setText("N° Placa Seleccionada: " + getIntent().getStringExtra("num_placa"));

        CargarServicios();
        CargarEspecialistas();

        btnAddEspecializado.setOnClickListener(v->{
            if (!edtOT.getText().toString().isEmpty() || !edtOC.getText().toString().isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEspecializadoActivity.this);
                builder.setIcon(R.drawable.ic_baseline_info_24);
                builder.setTitle("Registrar un nuevo ingreso al Servicio");
                builder.setMessage("Despues de esto ya no se podra actualizar\n¿Desea registrar el ingreso" +
                        " para el N° placa "+getIntent().getStringExtra("num_placa")+"?");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        guardarSeguiEspecializado();
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
        tvtAddService.setOnClickListener(v->{
            addService();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void addService(){
        final Dialog dialog = new Dialog(AddEspecializadoActivity.this);
        dialog.setCancelable(true);
        dialog.setTitle("Añadir Nuevo Servicio");
        dialog.setContentView(R.layout.layout_form_servicio);

        final EditText edtname = dialog.findViewById(R.id.edtNameService);
        Button btnsave = dialog.findViewById(R.id.btnAddServicioesp);

        btnsave.setOnClickListener(v->{
            String nombre = edtname.getText().toString();
            if (nombre.isEmpty()){
                Toast.makeText(this, "Completar el nombre", Toast.LENGTH_SHORT).show();
            }
            else {
                guardarServicioEsp(nombre);
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    private void guardarServicioEsp(String nombre) {
        progressDialog.setMessage("Registrando Servicio");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.SERVIESP,response -> {


            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Toast.makeText(this, String.format("Registro Correcto"), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddEspecializadoActivity.this, AddEspecializadoActivity.class);
                    intent.putExtra("vehiculo_id", vehiculoId);
                    intent.putExtra("num_placa", num_placa);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();

        },error -> {
            error.printStackTrace();

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
                map.put("nombre", nombre);
                map.put("tipo_servicio_id", "2");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddEspecializadoActivity.this);
        queue.add(request);
    }

    private void CargarServicios() {
        StringRequest request = new StringRequest(Request.Method.GET, Constraint.SERVIESP, response -> {

            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    ServicioSpinner m = new ServicioSpinner();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("nombre").toString().trim());
                    listaServicios.add(m);
                }

                spinnerServicio.setAdapter(new ArrayAdapter<ServicioSpinner>(AddEspecializadoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaServicios));

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

        RequestQueue queue = Volley.newRequestQueue(AddEspecializadoActivity.this);
        queue.add(request);
    }

    private void CargarEspecialistas() {
        StringRequest request = new StringRequest(Request.Method.GET, Constraint.ESPECIALISTA, response -> {

            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    EspecialistaSpinner m = new EspecialistaSpinner();
                    m.setId(jsonObject.getInt("id"));
                    m.setName(jsonObject.getString("nombres").toString().trim());
                    listaEspecialista.add(m);
                }

                spinnerEspecialista.setAdapter(new ArrayAdapter<EspecialistaSpinner>(AddEspecializadoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaEspecialista));

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

        RequestQueue queue = Volley.newRequestQueue(AddEspecializadoActivity.this);
        queue.add(request);
    }

    private void guardarSeguiEspecializado() {
        ServicioSpinner  servicio = (ServicioSpinner) spinnerServicio.getSelectedItem();
        EspecialistaSpinner especialista = (EspecialistaSpinner) spinnerEspecialista.getSelectedItem();
        progressDialog.setMessage("Guardando");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constraint.ESPECIALIZADO,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Toast.makeText(this, String.format("Registro Correcto"), Toast.LENGTH_LONG).show();
                    limpiar();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo guardar: Por favor ingresar todos los campos" +
                        "y/o verificar el OT/OC", Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();


        },error -> {
            error.printStackTrace();
            Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_LONG).show();
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
                map.put("ot", edtOT.getText().toString());
                map.put("oc", edtOC.getText().toString());
                map.put("vehiculo_id", vehiculoId+"");
                map.put("servicio_id", servicio.getId()+"");
                map.put("especialista_id", especialista.getId()+"");
                map.put("hora_ingreso", getDateTime());
                map.put("img_inicial_1", bitmapToString(bitmap));
                map.put("img_inicial_2", bitmapToString(bitmap2));
                map.put("img_inicial_3", bitmapToString(bitmap3));
                map.put("img_inicial_4", bitmapToString(bitmap4));
                return map;

            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddEspecializadoActivity.this);
        queue.add(request);
    }

    private void limpiar() {
        edtOT.setText("");
        edtOC.setText("");
        imgSeg1.setImageBitmap(null);
        imgSeg2.setImageBitmap(null);
        imgSeg3.setImageBitmap(null);
        imgSeg4.setImageBitmap(null);
        imgSeg1.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        imgSeg2.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        imgSeg3.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        imgSeg4.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
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

    public void cancelVehiculo(View view) {
        super.onBackPressed();
    }

    public void tomatFoto2(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    public void tomatFoto3(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 3);
    }

    public void tomatFoto4(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 4);
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
        if (requestCode == 4 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Cómo obtener el mapa de bits de la Galería
            bitmap4 = (Bitmap) extras.get("data");
            //Configuración del mapa de bits en ImageView
            imgSeg4.setImageBitmap(bitmap4);
        }

    }
}