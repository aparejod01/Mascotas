package com.example.mascotas.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddGuarderiaController.class)));
        obtenerAnimales();
    }

    private void obtenerAnimales() {
        container.removeAllViews();
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            View item = LayoutInflater.from(this).inflate(R.layout.item_mascota_guarderia, container, false);
                            
                            ((TextView) item.findViewById(R.id.tvNombreGuarderia)).setText(obj.getString("nombre"));
                            ((TextView) item.findViewById(R.id.tvRazaGuarderia)).setText(obj.getString("razo")); // Campo 'razo'
                            ((TextView) item.findViewById(R.id.tvDescGuarderia)).setText(obj.getString("descripcion"));
                            ((TextView) item.findViewById(R.id.tvLocGuarderia)).setText("📍 En Guardería");
                            ((TextView) item.findViewById(R.id.tvFechaGuarderia)).setText("🕒 " + obj.getString("fechaEntrada") + " - " + obj.getString("fechaSalida"));
                            ((TextView) item.findViewById(R.id.tvColorGuarderia)).setText("🎨 " + obj.getString("color"));

                            String imgBase64 = obj.optString("imagen", "");
                            if (!imgBase64.isEmpty()) {
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                ((ImageView) item.findViewById(R.id.ivFotoGuarderia)).setImageBitmap(decodedByte);
                            }

                            container.addView(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerAnimales();
    }
}