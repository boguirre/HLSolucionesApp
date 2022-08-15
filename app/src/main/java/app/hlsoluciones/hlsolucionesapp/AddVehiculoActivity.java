package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ortiz.touchview.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.hlsoluciones.hlsolucionesapp.Modelos.AreaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.MarcaSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.ModeloSpinner;
import app.hlsoluciones.hlsolucionesapp.Modelos.SubAreaSpinner;

public class AddVehiculoActivity extends AppCompatActivity {

    private Button btnAddVehiculo;
    private TouchImageView imgFotoPlaca;
    private EditText edtNumplaca, edtCifravin;
    private Spinner spinnerMarca;
    private Spinner spinnerModelo;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private ArrayList<MarcaSpinner> listaMarcas;
    private ArrayList<ModeloSpinner> listaModelos;
    private ArrayList<AreaSpinner> listaAreas;
    private ArrayList<SubAreaSpinner> listaSubareas;
    private Spinner spinnerArea;
    private Spinner spinnerSubarea;
    private TextView tvtSubarea;
    private TextView tvtaddmarcaModelo;
    private static final  int GALLERY_CHANGE_POST = 3;
    String subarea[]={"NO","Test Drive","Exhibicion","Gerencia","Seminuevos"};
    private Bitmap bitmap = null;
    String imageurl;

    private static final int PICTURE_RESULT = 122 ;
    private ContentValues values;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehiculo);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            //Verifica permisos para Android 6.0+
//            if(!checkExternalStoragePermission()){
//                finish();
//            }
//        }
        init();


    }

    private void init(){
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddVehiculo = findViewById(R.id.btnAddVehiculo);
        imgFotoPlaca = findViewById(R.id.imgPlaca);
        edtNumplaca = findViewById(R.id.edtNumPlaca);
        edtCifravin = findViewById(R.id.edtCifraVin);
        spinnerMarca = findViewById(R.id.spinnerMarca);
        spinnerArea = findViewById(R.id.spinnerArea);
        spinnerSubarea = findViewById(R.id.spinnerSubarea);
        tvtSubarea = findViewById(R.id.tvtsubemenu);
        tvtaddmarcaModelo = findViewById(R.id.txtAddMarcaModelo);
        spinnerModelo = findViewById(R.id.spinnerModelo);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        listaMarcas = new ArrayList<>();
        listaModelos = new ArrayList<>();
        listaAreas = new ArrayList<>();
        listaSubareas = new ArrayList<>();

//        imgFotoPlaca.setImageURI(getIntent().getData());
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        ArrayAdapter<String> adapteSub= new ArrayAdapter(this, android.R.layout.simple_list_item_1,subarea);
//        spinnerSubarea.setAdapter(adapteSub);

        btnAddVehiculo.setOnClickListener(v->{
            if (!edtNumplaca.getText().toString().isEmpty()){
                guardarVehiculo();
            }
            else {
                Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        CargarMarcas();
        CargarAreas();
        CargarSubareas();

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AreaSpinner  area = (AreaSpinner) adapterView.getSelectedItem();
                if (area.getId() == 3){
                    tvtSubarea.setVisibility(View.VISIBLE);
                    spinnerSubarea.setVisibility(View.VISIBLE);
                }
                else {
                    tvtSubarea.setVisibility(View.GONE);
                    spinnerSubarea.setVisibility(View.GONE);
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

        spinnerSubarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item =  adapterView.getSelectedItem().toString();
                if (item.equals("Seminuevos")){
                    tvtaddmarcaModelo.setVisibility(View.VISIBLE);
                    edtCifravin.setVisibility(View.VISIBLE);
                }
                else if (item.equals("Nuevo")){
                    tvtaddmarcaModelo.setVisibility(View.VISIBLE);
                    edtCifravin.setVisibility(View.VISIBLE);
                }
                else {
                    tvtaddmarcaModelo.setVisibility(View.GONE);
                    edtCifravin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvtaddmarcaModelo.setOnClickListener(v->{
            addMarcaModelo();
        });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean checkExternalStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!");
            return true;
        }

        return false;
    }

    public void addMarcaModelo(){
        final Dialog dialog = new Dialog(AddVehiculoActivity.this);
        dialog.setCancelable(true);
        dialog.setTitle("Añadir Nueva Marca");
        dialog.setContentView(R.layout.layout_form_marca);

        final EditText edtname = dialog.findViewById(R.id.edtNameMarca);
        Button btnsave = dialog.findViewById(R.id.btnAddMarca);

        btnsave.setOnClickListener(v->{
            String nombre = edtname.getText().toString();
            if (nombre.isEmpty()){
                Toast.makeText(this, "Completar el campo nombre", Toast.LENGTH_SHORT).show();
            }
            else {
                guardarMarca(nombre);
            }

            dialog.dismiss();
        });
        dialog.show();
    }

//    public void addService(){
//        final Dialog dialog = new Dialog(AddVehiculoActivity.this);
//        dialog.setCancelable(true);
//        dialog.setTitle("Añadir Nuevo Servicio");
//        dialog.setContentView(R.layout.layout_form_servicio);
//
//        final EditText edtname = dialog.findViewById(R.id.edtNameService);
//        Button btnsave = dialog.findViewById(R.id.btnAddServicioesp);
//
//        btnsave.setOnClickListener(v->{
//            String nombre = edtname.getText().toString();
//            guardarMarca(nombre);
//            dialog.dismiss();
//        });
//        dialog.show();
//    }

    private void guardarMarca(String nombre) {
        progressDialog.setMessage("Guardando");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.MARCAS,response -> {


            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    JSONObject marca = object.getJSONObject("data");
                    int id = marca.getInt("id");
                    Toast.makeText(this, String.format("Registro Guardado"), Toast.LENGTH_LONG).show();
                    final Dialog dialog = new Dialog(AddVehiculoActivity.this);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.layout_form_modelo);

                    final EditText edtname = dialog.findViewById(R.id.edtNameModelo);
                    Button btnsave = dialog.findViewById(R.id.btnAddModelo);

                    btnsave.setOnClickListener(v->{
                        String  modelo = edtname.getText().toString();
                        if (nombre.isEmpty()){
                            Toast.makeText(this, "Completar el campo nombre", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            guardarModelo(modelo, id);
                        }
                        dialog.dismiss();
                    });
                    dialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddVehiculoActivity.this);
        queue.add(request);
    }

    private void guardarModelo(String modelo, int id) {
        progressDialog.setMessage("Guardando");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.MODELOS,response -> {


            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Toast.makeText(this, String.format("Registro Guardado"), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(AddVehiculoActivity.this, AddVehiculoActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                map.put("nombre", modelo);
                map.put("marca_id", id+"");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddVehiculoActivity.this);
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

                spinnerArea.setAdapter(new ArrayAdapter<AreaSpinner>(AddVehiculoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listaAreas));

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

        RequestQueue queue = Volley.newRequestQueue(AddVehiculoActivity.this);
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

                spinnerSubarea.setAdapter(new ArrayAdapter<SubAreaSpinner>(AddVehiculoActivity.this,
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

        RequestQueue queue = Volley.newRequestQueue(AddVehiculoActivity.this);
        queue.add(request);
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

                spinnerMarca.setAdapter(new ArrayAdapter<MarcaSpinner>(AddVehiculoActivity.this,
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

        RequestQueue queue = Volley.newRequestQueue(AddVehiculoActivity.this);
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

                spinnerModelo.setAdapter(new ArrayAdapter<ModeloSpinner>(AddVehiculoActivity.this,
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

        RequestQueue queue = Volley.newRequestQueue(AddVehiculoActivity.this);
        queue.add(request);


    }

    private void guardarVehiculo() {
        int item;
        AreaSpinner  area = (AreaSpinner) spinnerArea.getSelectedItem();
        MarcaSpinner  marca = (MarcaSpinner) spinnerMarca.getSelectedItem();
        SubAreaSpinner  subarea = (SubAreaSpinner) spinnerSubarea.getSelectedItem();
        ModeloSpinner  modelo = (ModeloSpinner) spinnerModelo.getSelectedItem();
        if (area.getId() == 1){
            item = 1;
        }
        else if (area.getId() == 2){
            item = 2;
        }
        else {
            item = subarea.getId();
        }
        progressDialog.setMessage("Guardando");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.VEHICULOS,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    Toast.makeText(this, "Registro Correcto", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo guardar: Por favor ingresar todos los campos" +
                        "y/o verificar la foto", Toast.LENGTH_LONG).show();
            }
            limpiar();
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
                map.put("marca_id", marca.getId()+"");
                map.put("modelo_id", modelo.getId()+"");
                map.put("area_id", area.getId()+"");
                map.put("sub_area_id", item+"");
                map.put("numero_placa", edtNumplaca.getText().toString().trim());
                map.put("cifra_vin", edtCifravin.getText().toString().trim());
                map.put("foto_placa", bitmapToString(bitmap));
                return map;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddVehiculoActivity.this);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
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


    public void cancelVehiculo(View view) {
        super.onBackPressed();
    }
    public void tomatFoto(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddVehiculoActivity.this);
        CharSequence[] dialogItem = {"Tomar Foto", "Elegir Imagen"};
        builder.setTitle("Elegir una opcion");
        builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i){

                    case 0:
                        tomatFotoCamara();
                        break;
                    case 1:
                        changePhoto();
                        break;

                }

            }
        });

        builder.create().show();
    }

    public void tomatFotoCamara() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, 1);
        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "MyPicture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICTURE_RESULT);
    }

    public void changePhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_CHANGE_POST);
    }

    private void limpiar(){
        edtNumplaca.setText("");
        edtCifravin.setText("");
        imgFotoPlaca.setImageBitmap(null);
        imgFotoPlaca.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            //Cómo obtener el mapa de bits de la Galería
//            bitmap = (Bitmap) extras.get("data");
//            //Configuración del mapa de bits en ImageView
//            imgFotoPlaca.setImageBitmap(bitmap);
//        }

        if(requestCode==GALLERY_CHANGE_POST && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imgFotoPlaca.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (requestCode) {
            case PICTURE_RESULT:
                if (requestCode == PICTURE_RESULT)
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imgFotoPlaca.setImageBitmap(bitmap);
                            //Obtiene la ruta donde se encuentra guardada la imagen.
                            imageurl = getRealPathFromURI(imageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }
    }
}