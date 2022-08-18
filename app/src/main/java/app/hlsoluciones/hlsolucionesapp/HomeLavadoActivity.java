package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import app.hlsoluciones.hlsolucionesapp.Fragments.EspecializadoFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.IncidenciaFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.SeguimientoFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.VehiculoFragment;

public class HomeLavadoActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        int nightModeFlags = HomeLavadoActivity.this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                /* si esta activo el modo oscuro lo desactiva */
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainerLavado, new VehiculoFragment(), VehiculoFragment.class.getSimpleName()).commit();
        init();
    }

    private void init(){
        navigationView = findViewById(R.id.bottomNavLavado);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.vehiclesLavado:{
                        replaceFragment(new VehiculoFragment());
                        break;
                    }
                    case R.id.Encargadolavado:{
                        replaceFragment(new SeguimientoFragment());
                        break;
                    }
                }

                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameHomeContainerLavado, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}