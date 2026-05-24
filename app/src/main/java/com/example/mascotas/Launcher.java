package com.example.mascotas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mascotas.controller.LoginController;
import com.example.mascotas.controller.RegisterController;

public class Launcher extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);

        btnIniciarSesion.setOnClickListener(v -> startActivity(new Intent(this, LoginController.class)));
        btnRegistrar.setOnClickListener(v -> startActivity(new Intent(this, RegisterController.class)));
    }
}