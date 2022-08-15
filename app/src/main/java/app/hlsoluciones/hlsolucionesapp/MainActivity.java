package app.hlsoluciones.hlsolucionesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txtLinkRegistroUser;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                boolean isLoggedInEncargado = userPref.getBoolean("isLoggedInEncargado", false);
                boolean isLoggedInEspecial = userPref.getBoolean("isLoggedInEspecial", false);
                boolean isLoggedInColaborador = userPref.getBoolean("isLoggedInColaborador", false);

                if (isLoggedInEncargado){
                    startActivity(new Intent(MainActivity.this, HomeLavadoActivity.class));
                    finish();

                }
                else if (isLoggedInEspecial){
                    startActivity(new Intent(MainActivity.this, HomeEspecializadoActivity.class));
                    finish();

                }
                else if (isLoggedInColaborador){
                    startActivity(new Intent(MainActivity.this, HomeColaboradorActivity.class));
                    finish();

                }
                else {
                    InAuth();
                }

            }
        }, 1500);

        int nightModeFlags = MainActivity.this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                /* si esta activo el modo oscuro lo desactiva */
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }


    private void InAuth() {
        startActivity(new Intent(MainActivity.this, AuthActivity.class));
        finish();
    }
}