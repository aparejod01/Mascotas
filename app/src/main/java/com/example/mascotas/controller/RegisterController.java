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

/**
 * Controlador para la pantalla de registro.
 * Aquí guardamos a los nuevos usuarios en la base de datos haciendo varias comprobaciones,
 * como por ejemplo que el DNI sea válido (8 números y 1 letra) y que la contraseña sea segura.
 * Usamos Volley para mandar la información al servidor de forma sencilla.
 * 
 * @author Alex y Hector
 */
public class RegisterController extends AppCompatActivity {

    private EditText etDni, etNombre, etEmail, etPassword, etTelefono;
    private MaterialButton btnRegistrar, btnVolver;
    private final String URL_REGISTRO = "http://10.0.2.2/Android/registro.php";

    /**
     * Lo primero que se ejecuta al entrar a la pantalla de Registro.
     * Enlazamos los huecos de texto (EditText) y los botones con el código de Java.
     */
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

    /**
     * Este método lee lo que el usuario ha escrito, comprueba que todos los datos
     * cumplen con los requisitos (longitud, números de teléfono, DNI, etc.) y si
     * está todo bien, lo envía al servidor con una petición POST.
     */
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

        if (!dni.matches("^\\d{8}[A-Za-z]$")) {
            Toast.makeText(this, "Por favor, introduzca un DNI válido (8 números y 1 letra)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombre.length() < 3 || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            Toast.makeText(this, "El nombre debe tener al menos 3 letras y no contener números", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, introduzca un email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!telefono.matches("^\\d{9}$")) {
            Toast.makeText(this, "Por favor, introduzca un teléfono válido (9 dígitos)", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(RegisterController.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (response.equals("duplicate")) {
                        Toast.makeText(RegisterController.this, "Ese DNI ya está registrado en la aplicación", Toast.LENGTH_SHORT).show();
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
