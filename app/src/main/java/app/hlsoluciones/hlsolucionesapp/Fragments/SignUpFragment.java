package app.hlsoluciones.hlsolucionesapp.Fragments;

import android.app.ProgressDialog;
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

import app.hlsoluciones.hlsolucionesapp.Constraint;
import app.hlsoluciones.hlsolucionesapp.R;


public class SignUpFragment extends Fragment {


    private View view;
    private TextInputEditText edtEmail,edtPassword, edtName, edtConfirm;
    private TextInputLayout iplEmail,iplPassword, iplName, iplConfirmpas;
    private Button btnRegister;
    private TextView txtLogin;
    private ProgressDialog dialog;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        iniciar();
        return  view;
    }

    private void iniciar(){
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtName = view.findViewById(R.id.edtNomrbres);
        edtConfirm = view.findViewById(R.id.edtConfirmPassword);
        iplEmail = view.findViewById(R.id.iplEmailSignUp);
        iplPassword = view.findViewById(R.id.iplPasswordSignUp);
        iplConfirmpas = view.findViewById(R.id.iplConfirmPasSignUp);
        btnRegister = view.findViewById(R.id.btnSignUp);
        txtLogin = view.findViewById(R.id.txtLinkLogin);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtLogin.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();
        });

        btnRegister.setOnClickListener(v->{
            if (validate()){
                register();
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

        edtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtConfirm.getText().toString().equals(edtPassword.getText().toString())){
                    iplConfirmpas.setErrorEnabled(false);
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
            iplPassword.setError("La contraseña mo debe ser menor de 8 caracteres");
            return  false;
        }

        if (!edtConfirm.getText().toString().equals(edtPassword.getText().toString())){
            iplConfirmpas.setErrorEnabled(true);
            iplConfirmpas.setError("La contraseña no es igual");
            return  false;
        }

        return true;
    }

    private void register() {
        dialog.setMessage("Registrando");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constraint.REGISTER, response -> {

            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("res")){
//                    JSONObject user = object.getJSONObject("user");
//                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
//                    SharedPreferences.Editor editor = userPref.edit();
//                    editor.putString("msg",object.getString("msg"));
//                    editor.putString("name",user.getString("name"));
//                    editor.apply();
//
//                    Toast.makeText(getContext(), "Login Correcto", Toast.LENGTH_SHORT).show();
//                }
                Toast.makeText(getContext(), "Registro  Correcto", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }

            dialog.dismiss();

        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("name", edtName.getText().toString().trim());
                map.put("email", edtEmail.getText().toString().trim());
                map.put("password",edtPassword.getText().toString());
                return map;
            }
        };


        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }
}