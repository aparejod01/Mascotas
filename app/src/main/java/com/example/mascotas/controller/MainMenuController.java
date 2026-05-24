package com.example.mascotas.controller;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mascotas.R;
import com.google.android.material.card.MaterialCardView;

/**
 * Menú principal de la aplicación.
 * Desde aquí podemos navegar a las secciones de Animales Perdidos, Encontrados y Guardería.
 * También permite cerrar la sesión del usuario borrando sus datos temporales de SharedPreferences.
 * 
 * @author Alex y Hector
 */
public class MainMenuController extends AppCompatActivity {

    private TextView tvGreeting;
    private MaterialCardView cvMenuPerdido, cvMenuEncontrado, cvMenuGuarderia;

    /**
     * Se llama al arrancar el menú principal.
     * Lee el nombre del usuario de la sesión para darle la bienvenida y configura los botones
     * para ir a las distintas listas de animales o para cerrar sesión.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        tvGreeting = findViewById(R.id.tvGreeting);
        cvMenuPerdido = findViewById(R.id.cvMenuPerdido);
        cvMenuEncontrado = findViewById(R.id.cvMenuEncontrado);
        cvMenuGuarderia = findViewById(R.id.cvMenuGuarderia);

        String usuario = getIntent().getStringExtra("usuario");
        if (usuario != null) {
            tvGreeting.setText("Hola, " + usuario);
        }

        cvMenuPerdido.setOnClickListener(v -> startActivity(new Intent(this, PerdidosController.class)));
        cvMenuEncontrado.setOnClickListener(v -> startActivity(new Intent(this, EncontradosController.class)));
        cvMenuGuarderia.setOnClickListener(v -> startActivity(new Intent(this, GuarderiaController.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MainMenuController.this, LoginController.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}