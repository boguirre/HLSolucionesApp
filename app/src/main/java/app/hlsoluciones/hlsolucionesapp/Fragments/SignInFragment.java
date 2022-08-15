package app.hlsoluciones.hlsolucionesapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.hlsoluciones.hlsolucionesapp.AuthActivity;
import app.hlsoluciones.hlsolucionesapp.HomeColaboradorActivity;
import app.hlsoluciones.hlsolucionesapp.Constraint;
import app.hlsoluciones.hlsolucionesapp.HomeEspecializadoActivity;
import app.hlsoluciones.hlsolucionesapp.HomeLavadoActivity;
import app.hlsoluciones.hlsolucionesapp.R;


public class SignInFragment extends Fragment {

    private View view;
    private TextInputEditText edtEmail,edtPassword;
    private TextInputLayout iplEmail,iplPassword;
    private Button btnLogin;
    private TextView txtRegistrarse;
    private ProgressDialog dialog;
    private String encargado;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        iniciar();
        return  view;
    }

    private void iniciar(){
        edtEmail = view.findViewById(R.id.edtEmailLogin);
        edtPassword = view.findViewById(R.id.edtPasswordLogin);
        iplEmail = view.findViewById(R.id.iplEmail);
        iplPassword = view.findViewById(R.id.iplPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        txtRegistrarse = view.findViewById(R.id.txtLinkRegistroUser);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtRegistrarse.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignUpFragment()).commit();
        });

        btnLogin.setOnClickListener(v->{
            if (validate()){
                login();
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtEmail.getText().toString().isEmpty()){
                    iplEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtPassword.getText().toString().length()>7){
                    iplPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private boolean validate(){
        if (edtEmail.getText().toString().isEmpty()){
            iplEmail.setErrorEnabled(true);
            iplEmail.setError("Email requerido");
            return  false;
        }

        if (edtPassword.getText().toString().length()<8){
            iplPassword.setErrorEnabled(true);
            iplPassword.setError("La contraseña no debe ser menor de 8 caracteres");
            return  false;
        }

        return true;
    }

    private void login(){
        dialog.setMessage("Iniciando");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.LOGIN,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("res")){
                    JSONObject user = object.getJSONObject("user");
                    int tipo = user.getInt("tipo_user_id");
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("msg",object.getString("msg"));
                    editor.putString("name",user.getString("name"));
                    editor.putInt("id",user.getInt("id"));
                    editor.putInt("tipo_user_id",tipo);

                    if (tipo == 1){
                        editor.putBoolean("isLoggedInEncargado", true);
                        editor.apply();
                        startActivity(new Intent(((AuthActivity)getContext()), HomeLavadoActivity.class));
                        ((AuthActivity) getContext()).finish();
                        Toast.makeText(getContext(), "Login Correcto", Toast.LENGTH_SHORT).show();
                    }
                    else if (tipo == 2){
                        editor.putBoolean("isLoggedInEspecial", true);
                        editor.apply();
                        startActivity(new Intent(((AuthActivity)getContext()), HomeEspecializadoActivity.class));
                        ((AuthActivity) getContext()).finish();
                        Toast.makeText(getContext(), "Login Correcto", Toast.LENGTH_SHORT).show();
                    }
                    else if (tipo == 3){
                        editor.putBoolean("isLoggedInColaborador", true);
                        editor.apply();
                        startActivity(new Intent(((AuthActivity)getContext()), HomeColaboradorActivity.class));
                        ((AuthActivity) getContext()).finish();
                        Toast.makeText(getContext(), "Login Correcto", Toast.LENGTH_SHORT).show();
                    }


                }
            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Usuario no encontrado ", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();

        },error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Usuario no encontrado "+error.getMessage(), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("email", edtEmail.getText().toString().trim());
                map.put("password",edtPassword.getText().toString());
                return map;
            }
        };


        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}