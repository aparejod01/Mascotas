package com.example.mascotas.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mascotas.R;
import com.google.android.material.card.MaterialCardView;

public class MainMenuController extends AppCompatActivity {

    private TextView tvGreeting;
    private MaterialCardView cvMenuPerdido, cvMenuEncontrado, cvMenuGuarderia;

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
    }
}