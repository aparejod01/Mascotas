package com.example.mascotas.controller;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mascotas.R;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

public class RegisterController extends AppCompatActivity {

    private EditText etDni, etNombre, etEmail, etPassword, etTelefono;
    private MaterialButton btnRegistrar, btnVolver;
    private final String URL_REGISTRO = "http://10.0.2.2/Android/registro.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etDni = findViewById(R.id.etDni);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etTelefono = findViewById(R.id.etTelefono);
        btnRegistrar = findViewById(R.id.btnAccionRegistro);
        btnVolver = findViewById(R.id.btnVolverLogin);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String dni = etDni.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contrasena = etPassword.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (dni.isEmpty() || nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(RegisterController.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterController.this, "Error al registrar: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(RegisterController.this, "Error de conexión", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("dni", dni);
                params.put("nombre", nombre);
                params.put("email", email);
                params.put("contrasena", contrasena);
                params.put("telefono", telefono);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
