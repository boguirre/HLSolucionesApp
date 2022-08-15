package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
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

public class EspControlCalidadActivity extends AppCompatActivity {

    private int  id = 0, area_id = 0, servicio_id = 0, especialista_id=0;
    private Button btnAddControlCalidad;
    private ImageView imgSeg1, imgSeg2, imgSeg3, imgSeg4;
    private Spinner spinnerServicio;
    private TextView txtot;
    private ProgressDialog progressDialog;
    private EditText edtcomentario;
    private Spinner spinnerEspecialista;
    private SharedPreferences preferences;
    private ArrayList<AreaSpinner> listaAreas;
    private ArrayList<ServicioSpinner> listaServicios;
    private ArrayList<EspecialistaSpinner> listaEspecialista;
    Bitmap bitmap;
    Bitmap bitmap2;
    Bitmap bitmap3;
    Bitmap bitmap4;
    String subarea[]={"SQ1","SQ2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esp_control_calidad);
        init();
    }

    private void init(){
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddControlCalidad = findViewById(R.id.btnAddControlcalidadEsp);
        imgSeg1 = findViewById(R.id.imgEsp1Calidad);
        imgSeg2 = findViewById(R.id.imgEsp2Calidad);
        imgSeg3 = findViewById(R.id.imgEsp3Calidad);
        imgSeg4 = findViewById(R.id.imgEsp4Calidad);
        txtot = findViewById(R.id.txtOTSeleccionadaEsp);
        edtcomentario = findViewById(R.id.edtComentarioEsp);
        spinnerEspecialista = findViewById(R.id.spinnerEspecialistaEdit);
        spinnerServicio = findViewById(R.id.spinnerServicioCalidadEsp);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        listaAreas = new ArrayList<>();
        listaServicios = new ArrayList<>();
        listaEspecialista = new ArrayList<>();
        id = getIntent().getIntExtra("especializado_id", 0);
        area_id = getIntent().getIntExtra("area_id", 0);
        servicio_id = getIntent().getIntExtra("servicio_id", 0);
        especialista_id = getIntent().getIntExtra("especialista_id", 0);
        txtot.setText("N° OT Seleccionada: " + getIntent().getStringExtra("ot"));


        btnAddControlCalidad.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(EspControlCalidadActivity.this);
            builder.setIcon(R.drawable.ic_baseline_info_24);
            builder.setTitle("Registrar un Control de Calidad ");
            builder.setMessage("Despues de esto ya no se podra actualizar\n¿Desea registrar el control " +
                    "de calidad con el OT: "
                    +getIntent().getStringExtra("ot")+" ?");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    guardarControlCalidad();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        });
        CargarServicios();
        CargarEspecialistas();
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

                spinnerServicio.setAdapter(new ArrayAdapter<ServicioSpinner>(EspControlCalidadActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaServicios));
                spinnerServicio.setSelection(servicio_id-2);

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

        RequestQueue queue = Volley.newRequestQueue(EspControlCalidadActivity.this);
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

                spinnerEspecialista.setAdapter(new ArrayAdapter<EspecialistaSpinner>(EspControlCalidadActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaEspecialista));
                spinnerEspecialista.setSelection(especialista_id-1);

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

        RequestQueue queue = Volley.newRequestQueue(EspControlCalidadActivity.this);
        queue.add(request);
    }


    private void guardarControlCalidad() {
        ServicioSpinner  servicio = (ServicioSpinner) spinnerServicio.getSelectedItem();
        EspecialistaSpinner especialista = (EspecialistaSpinner) spinnerEspecialista.getSelectedItem();
        progressDialog.setMessage("Guardando");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Constraint.UPDATE_ESPECIALIZADO+id,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Toast.makeText(this, String.format("Registro Correcto"), Toast.LENGTH_LONG).show();
                    limpiar();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_LONG).show();
            }
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
                map.put("hora_salida", getDateTime());
                map.put("comentario", edtcomentario.getText().toString());
                map.put("vehiculo_id", getIntent().getIntExtra("vehiculo_id",0)+"");
                map.put("servicio_id", servicio.getId()+"");
                map.put("especialista_id", especialista.getId()+"");
                map.put("img_salida_1", bitmapToString(bitmap));
                map.put("img_salida_2", bitmapToString(bitmap2));
                map.put("img_salida_3", bitmapToString(bitmap3));
                map.put("img_salida_4", bitmapToString(bitmap4));
                map.put("status", "2");
                return map;

            }
        };

        RequestQueue queue = Volley.newRequestQueue(EspControlCalidadActivity.this);
        queue.add(request);
    }


    public void cancelControl(View view) {
        super.onBackPressed();
    }

    private void limpiar() {
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