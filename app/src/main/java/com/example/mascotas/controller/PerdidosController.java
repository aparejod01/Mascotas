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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mascotas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla que muestra la lista de todos los animales que se han perdido.
 * Se conecta a la base de datos para descargar los animales y además permite borrar o editar los tuyos propios.
 *
 * @author Alex y Hector
 */
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


    }

    private void obtenerMascotas() {
        if (container == null) return;
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        container.removeAllViews();
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

    private void agregarItemLista(JSONObject obj) {
        try {
            View item = LayoutInflater.from(this).inflate(R.layout.item_mascota_perdidas, container, false);

            TextView tvNombre = item.findViewById(R.id.tvNombre);
            TextView tvRaza = item.findViewById(R.id.tvRaza);
            TextView tvDesc = item.findViewById(R.id.tvDescripcion);
            TextView tvFecha = item.findViewById(R.id.tvFecha);
            TextView tvColor = item.findViewById(R.id.tvColor);
            ImageView ivFoto = item.findViewById(R.id.ivFotoMascota);

            TextView tvUbicacion = item.findViewById(R.id.tvUbicacion);
            TextView tvTelefono = item.findViewById(R.id.tvTelefono);


            tvNombre.setText(obj.optString("nombre", "Mascota"));
            tvRaza.setText(obj.optString("raza", "---"));
            tvDesc.setText(obj.optString("descripcion", ""));
            tvFecha.setText("🕒 " + obj.optString("fechaPerdido", "---"));
            tvColor.setText("🎨 " + obj.optString("color", "---"));

            String ubicacion = obj.optString("ubicacion", "Desconocida");
            String telefono = obj.optString("telefono", "No disponible");
            if (tvUbicacion != null) tvUbicacion.setText("📍 " + ubicacion);
            if (tvTelefono != null) tvTelefono.setText("📞 " + telefono);


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

                    String idMascota = obj.optString("idAnimalPerdido", "");

                    TextView tvDelete = item.findViewById(R.id.tvDelete);
                    if (tvDelete != null) {
                        tvDelete.setOnClickListener(v -> {
                            new AlertDialog.Builder(this)
                                .setTitle("Eliminar mascota")
                                .setMessage("¿Estás seguro de que quieres eliminar esta mascota?")
                                .setPositiveButton("Sí", (dialog, which) -> eliminarMascota(idMascota, "animalesperdidos"))
                                .setNegativeButton("No", null)
                                .show();
                        });
                    }

                    TextView tvEdit = item.findViewById(R.id.tvEdit);
                    if (tvEdit != null) {
                        tvEdit.setOnClickListener(v -> {
                            Intent intent = new Intent(this, AddPerdidoController.class);
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
            Log.e("UI_ERROR", "Error al añadir item");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerMascotas();
    }
}