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
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

/**
 * Pantalla que usamos para subir o editar la información de una mascota perdida.
 * Aquí el usuario rellena los datos y la foto, y se envía todo a nuestra base de datos.
 * 
 * @author Alex y Hector
 */
public class AddPerdidoController extends AppCompatActivity {

    private EditText etNombre, etTipo, etRaza, etColor, etFecha, etDescripcion, etUbicacion;
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
        setContentView(R.layout.activity_reportar_mascotas);

        etNombre = findViewById(R.id.etNombreMascota);
        etTipo = findViewById(R.id.etTipo);
        etRaza = findViewById(R.id.etRaza);
        etColor = findViewById(R.id.etColor);
        etFecha = findViewById(R.id.etFecha);
        etDescripcion = findViewById(R.id.etDescripcion);
        etUbicacion = findViewById(R.id.etUbicacion); 
        ivPreview = findViewById(R.id.ivPreview);
        MaterialCardView cvSubirImagen = findViewById(R.id.cvSubirImagen);
        MaterialButton btnPublicar = findViewById(R.id.btnPublicar);

        etFecha.setFocusable(false);
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etFecha);
            }
        });

        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().hasExtra("mascota_json")) {
            isEditMode = true;
            btnPublicar.setText("Actualizar");
            try {
                org.json.JSONObject obj = new org.json.JSONObject(getIntent().getStringExtra("mascota_json"));
                idMascotaEdit = obj.optString("idAnimalPerdido", "");
                etNombre.setText(obj.optString("nombre", ""));
                etTipo.setText(obj.optString("tipo", ""));
                etRaza.setText(obj.optString("raza", ""));
                etColor.setText(obj.optString("color", ""));
                etFecha.setText(obj.optString("fechaPerdido", ""));
                etDescripcion.setText(obj.optString("descripcion", ""));
                etUbicacion.setText(obj.optString("ubicacion", ""));

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

        cvSubirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), PICK_IMAGE_REQUEST);
            }
        });

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarMascota();
            }
        });
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
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        String raza = etRaza.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim(); 
        String fotoBase64 = convertirBitmapAString(bitmapFoto);


        SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String telefonoUsuario = pref.getString("telefono", "No disponible");
        String dniUsuario = pref.getString("dni", "");

        if (nombre.isEmpty() || tipo.isEmpty() || raza.isEmpty() || color.isEmpty() || fecha.isEmpty() || desc.isEmpty() || ubicacion.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fotoBase64.isEmpty() && !isEditMode) {
            Toast.makeText(this, "Selecciona una foto de la galería", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlTarget = isEditMode ? URL_EDIT : URL_ADD;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, urlTarget,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(AddPerdidoController.this, "Mascota reportada", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddPerdidoController.this, "Error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AddPerdidoController.this, "Error de red", Toast.LENGTH_SHORT).show()) {
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
                params.put("imagen", fotoBase64);
                params.put("ubicacion", ubicacion); 
                params.put("telefono", telefonoUsuario); 
                params.put("usuario_dni", dniUsuario);

                if (isEditMode) {
                    params.put("id", idMascotaEdit);
                }

                return params;
            }
        };
        queue.add(request);
    }
}