package com.example.mascotas.controller;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mascotas.R;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla que permite añadir a un perro a la guardería.
 * En esta pantalla se piden las fechas de entrada y salida, y manda automáticamente tu nombre como dueño.
 * 
 * @author Alex y Hector
 */
public class AddGuarderiaController extends AppCompatActivity {

    private EditText etNombre, etTipo, etColor, etRaza, etFechaEntrada, etFechaSalida, etDescripcion;
    private ImageView ivPreview;
    private Bitmap bitmapFoto = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String URL_ADD = "http://10.0.2.2/Android/add_animal.php";
    private final String URL_EDIT = "http://10.0.2.2/Android/update_animal.php";
    private boolean isEditMode = false;
    private String idMascotaEdit = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dar_alta_guarderia);

        etNombre = findViewById(R.id.etNombreAlta);
        etTipo = findViewById(R.id.etTipoAlta);
        etColor = findViewById(R.id.etColorAlta);
        etRaza = findViewById(R.id.etRazaAlta);
        etFechaEntrada = findViewById(R.id.etFechaEntrada);
        etFechaSalida = findViewById(R.id.etFechaSalida);
        etDescripcion = findViewById(R.id.etDescripcionAlta);
        ivPreview = findViewById(R.id.ivPreviewAlta);
        MaterialButton btnDarDeAlta = findViewById(R.id.btnDarDeAlta);

        etFechaEntrada.setFocusable(false);
        etFechaEntrada.setOnClickListener(v -> showDatePicker(etFechaEntrada));

        etFechaSalida.setFocusable(false);
        etFechaSalida.setOnClickListener(v -> showDatePicker(etFechaSalida));

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        if (getIntent().hasExtra("mascota_json")) {
            isEditMode = true;
            btnDarDeAlta.setText("Actualizar");
            try {
                org.json.JSONObject obj = new org.json.JSONObject(getIntent().getStringExtra("mascota_json"));
                idMascotaEdit = obj.optString("IdAnimalGuarderia", "");
                etNombre.setText(obj.optString("nombre", ""));
                etTipo.setText(obj.optString("tipo", ""));
                etRaza.setText(obj.optString("raza", ""));
                etColor.setText(obj.optString("color", ""));
                etFechaEntrada.setText(obj.optString("fechaEntrada", ""));
                etFechaSalida.setText(obj.optString("fechaSalida", ""));
                etDescripcion.setText(obj.optString("descripcion", ""));

                String imgBase64 = obj.optString("imagen", "");
                if (!imgBase64.isEmpty() && !imgBase64.equals("null")) {
                    try {
                        byte[] decodedString = android.util.Base64.decode(imgBase64, android.util.Base64.DEFAULT);
                        android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivPreview.setImageBitmap(decodedByte);
                        ivPreview.setVisibility(android.view.View.VISIBLE);
                        bitmapFoto = decodedByte;
                    } catch (Exception e) { e.printStackTrace(); }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ivPreview.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona foto"), PICK_IMAGE_REQUEST);
        });

        btnDarDeAlta.setOnClickListener(v -> agregarMascota());
    }

    private void showDatePicker(EditText campoDestino) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, day1) -> {
                    String fecha = year1 + "-" + (month1 + 1) + "-" + day1;
                    campoDestino.setText(fecha);
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmapFoto = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmapFoto);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private String convertirBitmapAString(Bitmap bitmap) {
        if (bitmap == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    private void agregarMascota() {
        String nombre = etNombre.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String fEntrada = etFechaEntrada.getText().toString().trim();
        String fSalida = etFechaSalida.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();
        String fotoBase64 = convertirBitmapAString(bitmapFoto);

        SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String telefonoUsuario = pref.getString("telefono", "No disponible");
        String dniUsuario = pref.getString("dni", "");
        String nombreUsuario = pref.getString("nombre", "Dueño desconocido");

        if (nombre.isEmpty() || tipo.isEmpty() || color.isEmpty() || raza.isEmpty() || fEntrada.isEmpty() || fSalida.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show(); return;
        }
        if (fotoBase64.isEmpty() && !isEditMode) {
            Toast.makeText(this, "Selecciona foto de la galería", Toast.LENGTH_SHORT).show(); return;
        }

        String urlTarget = isEditMode ? URL_EDIT : URL_ADD;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, urlTarget,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(AddGuarderiaController.this, "Dada de alta", Toast.LENGTH_SHORT).show();
                        finish();
                    } else { Toast.makeText(AddGuarderiaController.this, "Error: " + response, Toast.LENGTH_SHORT).show(); }
                },
                error -> Toast.makeText(AddGuarderiaController.this, "Error de red", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tabla", "animalesguarderia");
                params.put("nombre", nombre);
                params.put("tipo", tipo);
                params.put("raza", raza);
                params.put("color", color);
                params.put("descripcion", desc);
                params.put("fechaEntrada", fEntrada);
                params.put("fechaSalida", fSalida);
                params.put("imagen", fotoBase64);
                params.put("telefono", telefonoUsuario); 
                params.put("usuario_dni", dniUsuario);
                params.put("nombre_dueno", nombreUsuario); 

                if (isEditMode) {
                    params.put("id", idMascotaEdit);
                }

                return params;
            }
        };
        queue.add(request);
    }
}