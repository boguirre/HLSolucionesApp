package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.TimeZoneFormat;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.hlsoluciones.hlsolucionesapp.Fragments.SeguimientoFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.SignUpFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.VehiculoFragment;
import app.hlsoluciones.hlsolucionesapp.Modelos.AreaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.MarcaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.ModeloSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.ServicioSpinner;

public class AddSeguimientoActivity extends AppCompatActivity {

    private Button btnAddSeguimiento;
    private FragmentManager fragmentManager;
    private ImageView imgSeg1, imgSeg2, imgSeg3, imgSeg4;
    private EditText edtOT;
    private TextView txtplaca;
    private Spinner spinnerSucursal;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;;
    private ArrayList<ServicioSpinner> listaServicios;
    Bitmap bitmap;
    Bitmap bitmap2;
    private int vehiculoId = 0;
    String subarea[]={"SQ1","SQ2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seguimiento);
        
        init();
    }
    
    private void init(){
        fragmentManager = getSupportFragmentManager();
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddSeguimiento = findViewById(R.id.btnAddSeguimiento);
        imgSeg1 = findViewById(R.id.imgSeg1);
        imgSeg2 = findViewById(R.id.imgSeg2);
        edtOT = findViewById(R.id.edtOrdenTrabajo);
        txtplaca = findViewById(R.id.txtPlacaSeleccionada);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        listaServicios = new ArrayList<>();
        vehiculoId = getIntent().getIntExtra("vehiculo_id", 0);
        txtplaca.setText("N° Placa Seleccionada: " + getIntent().getStringExtra("num_placa"));


        btnAddSeguimiento.setOnClickListener(v->{
            if (!edtOT.getText().toString().isEmpty()){

                AlertDialog.Builder builder = new AlertDialog.Builder(AddSeguimientoActivity.this);
                builder.setIcon(R.drawable.ic_baseline_info_24);
                builder.setTitle("Registrar un nuevo Ingreso al Servicio");
                builder.setMessage("Despues de esto ya no se podra actualizar\n¿Desea registrar el ingreso" +
                        " para el N° placa "+getIntent().getStringExtra("num_placa")+"?");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        guardarSeguimiento();
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

    }


    private void guardarSeguimiento() {
        progressDialog.setMessage("Guardando");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.SEGUIMIENTO,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Toast.makeText(this, String.format("Registro Correcto"), Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo guardar: Por favor ingresar todos los campos" +
                        "y/o verificar el OT", Toast.LENGTH_LONG).show();
            }
            limpiar();
            progressDialog.dismiss();;

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
                map.put("vehiculo_id", vehiculoId+"");
                map.put("servicio_id", "1");
                map.put("hora_ingreso", getDateTime());
                map.put("img_inicial_1", bitmapToString(bitmap));
                map.put("img_inicial_2", bitmapToString(bitmap2));
                return map;

            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddSeguimientoActivity.this);
        queue.add(request);
    }

    private void limpiar() {
        edtOT.setText("");
        imgSeg1.setImageBitmap(null);
        imgSeg2.setImageBitmap(null);
        imgSeg1.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        imgSeg2.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
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

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    public void cancelVehiculo(View view) {
        super.onBackPressed();
    }

    public void tomatFoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    public void tomatFoto2(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
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

    }
}