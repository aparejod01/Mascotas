package com.example.mascotas.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mascotas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class EncontradosController extends AppCompatActivity {

    private LinearLayout container;
    private RequestQueue requestQueue;
    private final String URL_GET = "http://10.0.2.2/Android/get_animales.php?tabla=animalesencontrados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas_encontradas);

        container = findViewById(R.id.containerMascotasEncontradas);
        FloatingActionButton fab = findViewById(R.id.fabAgregarEncontrada);
        ImageView ivBack = findViewById(R.id.ivBack);

        requestQueue = Volley.newRequestQueue(this);

        if (ivBack != null) ivBack.setOnClickListener(v -> finish());
        if (fab != null) fab.setOnClickListener(v -> startActivity(new Intent(this, AddEncontradoController.class)));
        
        obtenerMascotas();
    }

    private void obtenerMascotas() {
        if (container == null) return;
        requestQueue.cancelAll("get_encontrados");

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        container.removeAllViews();
                        String json = response.trim();
                        if (json.startsWith("[")) {
                            JSONArray array = new JSONArray(json);
                            if (array.length() == 0) {
                                Toast.makeText(this, "No hay mascotas encontradas", Toast.LENGTH_SHORT).show();
                            }
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                agregarItemALista(obj);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", "Error: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show());

        request.setTag("get_encontrados");
        requestQueue.add(request);
    }

    private void agregarItemALista(JSONObject obj) {
        try {
            View item = LayoutInflater.from(this).inflate(R.layout.item_mascota_encontrada, container, false);

            TextView tvRaza = item.findViewById(R.id.tvRazaPrincipal);
            TextView tvDesc = item.findViewById(R.id.tvDescEncontrada);
            TextView tvLoc = item.findViewById(R.id.tvLocEncontrada);
            TextView tvFecha = item.findViewById(R.id.tvFechaEncontrada);
            TextView tvTel = item.findViewById(R.id.tvTelEncontrada);
            TextView tvColor = item.findViewById(R.id.tvColorEncontrada);
            ImageView ivFoto = item.findViewById(R.id.ivFotoEncontrada);

            // Mapeo exacto de tus campos de animalesencontrados
            String nombre = obj.optString("nombre", "");
            String raza = obj.optString("raza", "Desconocida");
            
            tvRaza.setText(nombre.isEmpty() || nombre.equalsIgnoreCase("Desconocido") ? raza : nombre);
            tvDesc.setText(obj.optString("descripcion", "Sin descripción"));
            tvLoc.setText("📍 Mascota encontrada");
            tvFecha.setText("🕒 " + obj.optString("fechaEncontrado", "---"));
            tvTel.setText("📞 Ver detalle en ficha");
            tvColor.setText("🎨 " + obj.optString("color", "---"));

            // DECODIFICACIÓN DE IMAGEN
            String imgBase64 = obj.optString("imagen", "");
            if (!imgBase64.isEmpty() && !imgBase64.equals("null") && ivFoto != null) {
                try {
                    byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (bitmap != null) {
                        ivFoto.setImageBitmap(bitmap);
                        ivFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                } catch (Exception e) {
                    Log.e("IMG_ERROR", "Error foto");
                }
            }
            container.addView(item);
        } catch (Exception e) {
            Log.e("UI_ERROR", "Error item");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerMascotas();
    }
}