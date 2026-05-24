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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddGuarderiaController extends AppCompatActivity {

    private EditText etNombre, etTipo, etColor, etRaza, etFechaEntrada, etFechaSalida, etDescripcion, etNombreDueno;
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
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }
    );

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
        etNombreDueno = findViewById(R.id.etNombreDueno);
        ivPreview = findViewById(R.id.ivPreviewAlta);
        MaterialCardView cvSubirImagen = findViewById(R.id.cvSubirImagenAlta);
        MaterialButton btnDarDeAlta = findViewById(R.id.btnDarDeAlta);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        
        // El layout activity_dar_alta_guarderia no tiene id para el CardView de imagen por defecto, 
        // lo buscamos por su contenedor o el ImageView
        ivPreview.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnDarDeAlta.setOnClickListener(v -> agregarMascota());
    }

    private String encodeImage(Bitmap bitmap) {
        int width = 500;
        int height = (int) (bitmap.getHeight() * (500.0 / bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void agregarMascota() {
        String nombre = etNombre.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String fEntrada = etFechaEntrada.getText().toString().trim();
        String fSalida = etFechaSalida.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty() || tipo.isEmpty() || color.isEmpty() || raza.isEmpty() || fEntrada.isEmpty() || fSalida.isEmpty()) {
            Toast.makeText(this, "Rellena los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_ADD,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(this, "Mascota en guardería dada de alta", Toast.LENGTH_SHORT).show();
                        finish();
                    } else { Toast.makeText(this, "Error: " + response, Toast.LENGTH_SHORT).show(); }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tabla", "animalesguarderia");
                params.put("nombre", nombre);
                params.put("tipo", tipo);
                params.put("raza", raza); // En PHP se mapea a 'razo'
                params.put("color", color);
                params.put("descripcion", desc);
                params.put("fechaEntrada", fEntrada);
                params.put("fechaSalida", fSalida);
                params.put("imagen", encodedImage);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}