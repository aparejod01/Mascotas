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
import com.google.android.material.card.MaterialCardView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEncontradoController extends AppCompatActivity {

    private EditText etNombre, etTipo, etRaza, etColor, etFecha, etDescripcion, etUbicacion;
    private ImageView ivPreview;
    private Bitmap bitmapFoto = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String URL_ADD = "http://10.0.2.2/Android/add_animal.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_encontrada);
        etTipo = findViewById(R.id.etTipoEncontrado);
        etRaza = findViewById(R.id.etRazaEncontrado);
        etColor = findViewById(R.id.etColorEncontrado);
        etFecha = findViewById(R.id.etFechaEncontrado);
        etDescripcion = findViewById(R.id.etDescripcionEncontrado);
        etUbicacion = findViewById(R.id.etUbicacion); // NUEVO
        ivPreview = findViewById(R.id.ivPreviewEncontrado);
        MaterialCardView cvSubirImagen = findViewById(R.id.cvSubirImagenEncontrado);
        MaterialButton btnPublicar = findViewById(R.id.btnPublicarEncontrado);

        etFecha.setFocusable(false);
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etFecha);
            }
        });

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        cvSubirImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona foto"), PICK_IMAGE_REQUEST);
        });

        btnPublicar.setOnClickListener(v -> agregarMascota());
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
        String tipo = etTipo.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim(); // NUEVO
        String fotoBase64 = convertirBitmapAString(bitmapFoto);

        SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String telefonoUsuario = pref.getString("telefono", "No disponible");

        if (fotoBase64.isEmpty()) {
            Toast.makeText(this, "Selecciona una foto de la galería", Toast.LENGTH_SHORT).show(); return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, URL_ADD,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(AddEncontradoController.this, "Mascota encontrada reportada", Toast.LENGTH_SHORT).show();
                        finish();
                    } else { Toast.makeText(AddEncontradoController.this, "Error: " + response, Toast.LENGTH_SHORT).show(); }
                },
                error -> Toast.makeText(AddEncontradoController.this, "Error de red", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tabla", "animalesencontrados");
                params.put("tipo", tipo);
                params.put("raza", raza);
                params.put("color", color);
                params.put("descripcion", desc);
                params.put("fechaEncontrado", fecha);
                params.put("imagen", fotoBase64);
                params.put("ubicacion", ubicacion);
                params.put("telefono", telefonoUsuario);
                return params;
            }
        };
        queue.add(request);
    }
}