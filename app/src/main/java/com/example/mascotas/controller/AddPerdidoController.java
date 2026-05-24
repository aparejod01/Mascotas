package com.example.mascotas.controller;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mascotas.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddPerdidoController extends AppCompatActivity {

    private EditText etNombre, etTipo, etRaza, etColor, etFecha, etDescripcion;
    private ImageView ivPreview;
    private String encodedImage = "";
    private final String URL_ADD = "http://10.0.2.2/Android/add_animal.php";

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        ivPreview.setImageBitmap(bitmap);
                        encodedImage = encodeImage(bitmap);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_mascotas);

        etNombre = findViewById(R.id.etNombreMascota);
        etTipo = findViewById(R.id.etTipo);
        etRaza = findViewById(R.id.etRaza);
        etColor = findViewById(R.id.etColor);
        etFecha = findViewById(R.id.etFecha);
        etDescripcion = findViewById(R.id.etDescripcion);
        ivPreview = findViewById(R.id.ivPreview);
        MaterialCardView cvSubirImagen = findViewById(R.id.cvSubirImagen);
        MaterialButton btnPublicar = findViewById(R.id.btnPublicar);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        cvSubirImagen.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnPublicar.setOnClickListener(v -> agregarMascota());
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 500;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void agregarMascota() {
        String nombre = etNombre.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty() || tipo.isEmpty() || raza.isEmpty() || color.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Rellena los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_ADD,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(this, "Mascota reportada con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else { Toast.makeText(this, "Error: " + response, Toast.LENGTH_SHORT).show(); }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tabla", "animalesperdidos");
                params.put("nombre", nombre);
                params.put("tipo", tipo);
                params.put("raza", raza);
                params.put("color", color);
                params.put("descripcion", desc);
                params.put("fechaPerdido", fecha);
                params.put("imagen", encodedImage);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}