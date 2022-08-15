package app.hlsoluciones.hlsolucionesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegistroUsuarioActivity extends AppCompatActivity {

    TextView txtLinkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        txtLinkLogin = findViewById(R.id.txtLinkLogin);

        txtLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroUsuarioActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}