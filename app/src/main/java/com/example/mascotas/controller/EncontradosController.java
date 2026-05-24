package com.example.mascotas.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla que muestra la lista de todos los animales que se han encontrado.
 * Nos descargamos los datos del servidor para poder ver la lista y editar o borrar los nuestros.
 * 
 * @author Alex y Hector
 */
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
        Volley.newRequestQueue(this).add(request);
    }

    private void eliminarMascota(String id, String tabla) {
        String url = "http://10.0.2.2/Android/delete_animal.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(this, "Mascota eliminada", Toast.LENGTH_SHORT).show();
                        obtenerMascotas(); 
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tabla", tabla);
                params.put("id", id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
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


            String nombre = obj.optString("nombre", "");
            String raza = obj.optString("raza", "Desconocida");

            tvRaza.setText(nombre.isEmpty() || nombre.equalsIgnoreCase("Desconocido") ? raza : nombre);
            tvDesc.setText(obj.optString("descripcion", "Sin descripción"));

            String ubicacion = obj.optString("ubicacion", "Desconocida");
            String telefono = obj.optString("telefono", "No disponible");

            tvLoc.setText("📍 " + ubicacion);
            tvFecha.setText("🕒 " + obj.optString("fechaEncontrado", "---"));
            tvTel.setText("📞 " + telefono);
            tvColor.setText("🎨 " + obj.optString("color", "---"));


            String imgBase64 = obj.optString("imagen", "");
            if (!imgBase64.isEmpty() && !imgBase64.equals("null") && ivFoto != null) {
                try {
                    byte[] decodedString = android.util.Base64.decode(imgBase64, android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (bitmap != null) {
                        ivFoto.setImageBitmap(bitmap);
                        ivFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                } catch (Exception e) {
                    Log.e("IMG_ERROR", "Error al decodificar imagen");
                }
            }

            SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String dniUsuarioLogueado = pref.getString("dni", "");
            String dniMascota = obj.optString("dni_usuario", "");

            LinearLayout llAcciones = item.findViewById(R.id.llAcciones);
            if (llAcciones != null) {
                if (dniUsuarioLogueado.equals(dniMascota) && !dniMascota.isEmpty()) {
                    llAcciones.setVisibility(View.VISIBLE);

                    String idMascota = obj.optString("idAnimalEncontrado", "");

                    TextView tvDelete = item.findViewById(R.id.tvDelete);
                    if (tvDelete != null) {
                        tvDelete.setOnClickListener(v -> {
                            new AlertDialog.Builder(this)
                                .setTitle("Eliminar mascota")
                                .setMessage("¿Estás seguro de que quieres eliminar esta mascota?")
                                .setPositiveButton("Sí", (dialog, which) -> eliminarMascota(idMascota, "animalesencontrados"))
                                .setNegativeButton("No", null)
                                .show();
                        });
                    }

                    TextView tvEdit = item.findViewById(R.id.tvEdit);
                    if (tvEdit != null) {
                        tvEdit.setOnClickListener(v -> {
                            Intent intent = new Intent(this, AddEncontradoController.class);
                            intent.putExtra("mascota_json", obj.toString());
                            startActivity(intent);
                        });
                    }
                } else {
                    llAcciones.setVisibility(View.GONE);
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