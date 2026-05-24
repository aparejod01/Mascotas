package com.example.mascotas.controller;

import android.content.Intent;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mascotas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GuarderiaController extends AppCompatActivity {

    private LinearLayout container;
    private final String URL_GET = "http://10.0.2.2/Android/get_animales.php?tabla=animalesguarderia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas_guarderia);

        container = findViewById(R.id.containerMascotasGuarderia);
        FloatingActionButton fab = findViewById(R.id.fabAgregarGuarderia);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(GuarderiaController.this, AddGuarderiaController.class);
            startActivity(intent);
        });
    }

    private void obtenerAnimales() {
        container.removeAllViews();

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response.trim());
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                View item = LayoutInflater.from(GuarderiaController.this).inflate(R.layout.item_mascota_guarderia, container, false);

                                ((TextView) item.findViewById(R.id.tvNombreGuarderia)).setText(obj.getString("nombre"));
                                ((TextView) item.findViewById(R.id.tvRazaGuarderia)).setText(obj.getString("razo"));
                                ((TextView) item.findViewById(R.id.tvDescGuarderia)).setText(obj.getString("descripcion"));
                                ((TextView) item.findViewById(R.id.tvFechaGuarderia)).setText("🕒 " + obj.getString("fechaEntrada") + " - " + obj.getString("fechaSalida"));
                                ((TextView) item.findViewById(R.id.tvColorGuarderia)).setText("🎨 " + obj.getString("color"));
                                ((TextView) item.findViewById(R.id.tvLocGuarderia)).setText("📍 C/ Guarderia 4");

                                String rutaImagen = obj.getString("imagen");
                                if (!rutaImagen.isEmpty() && !rutaImagen.equals("null")) {
                                    String urlCompleta = "http://10.0.2.2/Android/" + rutaImagen;

                                    Glide.with(GuarderiaController.this)
                                            .load(urlCompleta)
                                            .placeholder(R.mipmap.ic_launcher)
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .into((ImageView) item.findViewById(R.id.ivFotoGuarderia));
                                }

                                container.addView(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerAnimales();
    }
}