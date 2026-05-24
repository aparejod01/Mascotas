package com.example.mascotas.controller;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;
import android.content.SharedPreferences;
import android.content.Context;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase sirve para que el usuario pueda iniciar sesión en la aplicación.
 * Conecta con la base de datos para comprobar si el DNI y la contraseña existen.
 *
 * @author Alex y Hector
 */
public class LoginController extends AppCompatActivity {

    private EditText etDni, etPassword;
    private MaterialButton btnAccionLogin, btnIrARegistro;
    private final String URL_LOGIN = "http://10.0.2.2/Android/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etDni = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnAccionLogin = findViewById(R.id.btnAccionLogin);
        btnIrARegistro = findViewById(R.id.btnIrARegistro);

        btnAccionLogin.setOnClickListener(v -> loginUsuario());
        btnIrARegistro.setOnClickListener(v -> startActivity(new Intent(this, RegisterController.class)));
    }

    private void loginUsuario() {
        String dni = etDni.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (dni.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, introduzca DNI y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    if (!response.equals("error")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String nombre = jsonObject.getString("nombre");
                            String dniUsuario = jsonObject.getString("dni");
                            String telefonoUsuario = jsonObject.getString("telefono");

                            SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("nombre", nombre);
                            editor.putString("dni", dniUsuario);
                            editor.putString("telefono", telefonoUsuario);
                            editor.apply();

                            Intent intent = new Intent(LoginController.this, MainMenuController.class);
                            intent.putExtra("usuario", nombre);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginController.this, "Error procesando datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginController.this, "DNI o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(LoginController.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("dni", dni);
                params.put("contrasena", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}