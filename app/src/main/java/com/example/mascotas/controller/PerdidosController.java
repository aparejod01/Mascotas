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

public class PerdidosController extends AppCompatActivity {

    private LinearLayout container;
    private RequestQueue requestQueue;
    private final String URL_GET = "http://10.0.2.2/Android/get_animales.php?tabla=animalesperdidos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascota_perdida);

        container = findViewById(R.id.containerMascotasPerdidas);
        FloatingActionButton fab = findViewById(R.id.fabAgregarMascota);
        ImageView ivBack = findViewById(R.id.ivBack);

        requestQueue = Volley.newRequestQueue(this);

        if (ivBack != null) ivBack.setOnClickListener(v -> finish());
        if (fab != null) {
            fab.setOnClickListener(v -> startActivity(new Intent(this, AddPerdidoController.class)));
        }

        // Llamamos a obtenerMascotas SOLO aquí
        obtenerMascotas();
    }

    private void obtenerMascotas() {
        if (container == null) return;
        
        requestQueue.cancelAll("list_req"); // Evita peticiones duplicadas

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        container.removeAllViews();
                        String json = response.trim();
                        if (json.startsWith("[")) {
                            JSONArray array = new JSONArray(json);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                agregarCardMascota(obj);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Respuesta no JSON: " + response);
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());

        request.setTag("list_req");
        requestQueue.add(request);
    }

    private void agregarCardMascota(JSONObject obj) {
        try {
            View item = LayoutInflater.from(this).inflate(R.layout.item_mascota_perdidas, container, false);

            TextView tvNombre = item.findViewById(R.id.tvNombre);
            TextView tvRaza = item.findViewById(R.id.tvRaza);
            TextView tvDesc = item.findViewById(R.id.tvDescripcion);
            TextView tvFecha = item.findViewById(R.id.tvFecha);
            TextView tvColor = item.findViewById(R.id.tvColor);
            ImageView ivFoto = item.findViewById(R.id.ivFotoMascota);

            tvNombre.setText(obj.optString("nombre", "Sin nombre"));
            tvRaza.setText(obj.optString("raza", "---"));
            tvDesc.setText(obj.optString("descripcion", ""));
            tvFecha.setText("🕒 " + obj.optString("fechaPerdido", "---"));
            tvColor.setText("🎨 " + obj.optString("color", "---"));

            // Decodificar imagen Base64 con seguridad de memoria
            String imgBase64 = obj.optString("imagen", "");
            if (!imgBase64.isEmpty() && !imgBase64.equals("null") && ivFoto != null) {
                try {
                    byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // Reduce el tamaño para no saturar la RAM
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                    if (bitmap != null) ivFoto.setImageBitmap(bitmap);
                } catch (Throwable t) { Log.e("IMG_ERROR", "No se pudo cargar imagen"); }
            }

            container.addView(item);
        } catch (Exception e) { Log.e("UI_ERROR", "Error inflar item"); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Solo refrescamos si ya se inició la actividad antes
        if (container != null && container.getChildCount() > 0) {
            obtenerMascotas();
        }
    }
}