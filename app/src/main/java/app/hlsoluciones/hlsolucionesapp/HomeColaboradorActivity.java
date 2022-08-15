package app.hlsoluciones.hlsolucionesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import app.hlsoluciones.hlsolucionesapp.Fragments.IncidenciaFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.VehiculoFragment;

public class HomeColaboradorActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colaborador);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainerColaborador, new IncidenciaFragment(), IncidenciaFragment.class.getSimpleName()).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}