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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mascotas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class PerdidosController extends AppCompatActivity {

    private LinearLayout container;
    private final String URL_GET = "http://10.0.2.2/Android/get_animales.php?tabla=animalesperdidos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascota_perdida);

        container = findViewById(R.id.containerMascotasPerdidas);
        FloatingActionButton fab = findViewById(R.id.fabAgregarMascota);
        ImageView ivBack = findViewById(R.id.ivBack);

        if (ivBack != null) ivBack.setOnClickListener(v -> finish());
        if (fab != null) fab.setOnClickListener(v -> startActivity(new Intent(this, AddPerdidoController.class)));

        obtenerMascotas();
    }

    private void obtenerMascotas() {
        if (container == null) return;
        container.removeAllViews();

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        String json = response.trim();
                        if (json.startsWith("[")) {
                            JSONArray array = new JSONArray(json);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                agregarItemLista(obj);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", "Error: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void agregarItemLista(JSONObject obj) {
        try {
            View item = LayoutInflater.from(this).inflate(R.layout.item_mascota_perdidas, container, false);

            TextView tvNombre = item.findViewById(R.id.tvNombre);
            TextView tvRaza = item.findViewById(R.id.tvRaza);
            TextView tvDesc = item.findViewById(R.id.tvDescripcion);
            TextView tvFecha = item.findViewById(R.id.tvFecha);
            TextView tvColor = item.findViewById(R.id.tvColor);
            ImageView ivFoto = item.findViewById(R.id.ivFotoMascota);

            // USANDO TUS CAMPOS EXACTOS
            tvNombre.setText(obj.optString("nombre", "Mascota"));
            tvRaza.setText(obj.optString("raza", "---"));
            tvDesc.setText(obj.optString("descripcion", ""));
            tvFecha.setText("🕒 " + obj.optString("fechaPerdido", "---"));
            tvColor.setText("🎨 " + obj.optString("color", "---"));

            // DECODIFICAR IMAGEN BASE64
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
                    Log.e("IMG_ERROR", "Error al decodificar imagen");
                }
            }

            container.addView(item);
        } catch (Exception e) {
            Log.e("UI_ERROR", "Error al añadir item");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerMascotas();
    }
}