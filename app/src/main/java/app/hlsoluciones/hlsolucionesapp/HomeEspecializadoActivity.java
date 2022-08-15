package app.hlsoluciones.hlsolucionesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import app.hlsoluciones.hlsolucionesapp.Fragments.EspecializadoFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.SeguimientoFragment;
import app.hlsoluciones.hlsolucionesapp.Fragments.VehiculoFragment;

public class HomeEspecializadoActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_especializado);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainerEspecial, new VehiculoFragment(), VehiculoFragment.class.getSimpleName()).commit();
        init();
    }

    private void init(){
        navigationView = findViewById(R.id.bottomNavEspecial);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.vehiclesEspcial:{
                        replaceFragment(new VehiculoFragment());
                        break;
                    }
                    case R.id.Servespecializado:{
                        replaceFragment(new EspecializadoFragment());
                        break;
                    }
                }

                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameHomeContainerEspecial, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}