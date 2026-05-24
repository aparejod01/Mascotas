package com.example.mascotas.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla de la Guardería. 
 * Muestra la lista de mascotas que están en adopción temporal o guardería, enseñando también el nombre del dueño.
 * 
 * @author Alex y Hector
 */
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

    private void eliminarMascota(String id, String tabla) {
        String url = "http://10.0.2.2/Android/delete_animal.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        android.widget.Toast.makeText(this, "Mascota eliminada", android.widget.Toast.LENGTH_SHORT).show();
                        obtenerAnimales(); 
                    } else {
                        android.widget.Toast.makeText(this, "Error al eliminar", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }, error -> android.widget.Toast.makeText(this, "Error de red", android.widget.Toast.LENGTH_SHORT).show()) {
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

    private void obtenerAnimales() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        container.removeAllViews();
                        JSONArray array = new JSONArray(response.trim());
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                View item = LayoutInflater.from(GuarderiaController.this).inflate(R.layout.item_mascota_guarderia, container, false);

                                ((TextView) item.findViewById(R.id.tvNombreGuarderia)).setText(obj.optString("nombre", "Desconocido"));
                                ((TextView) item.findViewById(R.id.tvRazaGuarderia)).setText(obj.optString("raza", "---"));
                                ((TextView) item.findViewById(R.id.tvDescGuarderia)).setText(obj.optString("descripcion", ""));
                                ((TextView) item.findViewById(R.id.tvFechaGuarderia)).setText("🕒 Ent: " + obj.optString("fechaEntrada", "") + " - Sal: " + obj.optString("fechaSalida", ""));
                                ((TextView) item.findViewById(R.id.tvColorGuarderia)).setText("🎨 " + obj.optString("color", ""));
                                ((TextView) item.findViewById(R.id.tvLocGuarderia)).setText("📍 C/ Guarderia 4");

                                TextView tvTelGuarderia = item.findViewById(R.id.tvTelGuarderia);
                                if(tvTelGuarderia != null) {
                                    tvTelGuarderia.setText("📞 " + obj.optString("telefono", "No disponible"));
                                }

                                TextView tvDuenoGuarderia = item.findViewById(R.id.tvDuenoGuarderia);
                                if(tvDuenoGuarderia != null) {
                                    tvDuenoGuarderia.setText("👱 " + obj.optString("nombre_dueno", "Dueño desconocido"));
                                }


                                String imgBase64 = obj.optString("imagen", "");
                                if (!imgBase64.isEmpty() && !imgBase64.equals("null")) {
                                    try {
                                        byte[] decodedString = android.util.Base64.decode(imgBase64, android.util.Base64.DEFAULT);
                                        android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        if (bitmap != null) {
                                            ImageView ivFoto = item.findViewById(R.id.ivFotoGuarderia);
                                            ivFoto.setImageBitmap(bitmap);
                                            ivFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        }
                                    } catch (Exception e) {
                                        android.util.Log.e("IMG_ERROR", "Error al decodificar imagen");
                                    }
                                }


                                SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                String dniUsuarioLogueado = pref.getString("dni", "");
                                String dniMascota = obj.optString("dni_usuario", "");

                                android.widget.LinearLayout llAcciones = item.findViewById(R.id.llAcciones);
                                if (llAcciones != null) {
                                    if (dniUsuarioLogueado.equals(dniMascota) && !dniMascota.isEmpty()) {
                                        llAcciones.setVisibility(View.VISIBLE);

                                        String idMascota = obj.optString("IdAnimalGuarderia", "");

                                        TextView tvDelete = item.findViewById(R.id.tvDelete);
                                        if (tvDelete != null) {
                                            tvDelete.setOnClickListener(v -> {
                                                new AlertDialog.Builder(GuarderiaController.this)
                                                    .setTitle("Eliminar mascota")
                                                    .setMessage("¿Estás seguro de que quieres eliminar esta mascota?")
                                                    .setPositiveButton("Sí", (dialog, which) -> eliminarMascota(idMascota, "animalesguarderia"))
                                                    .setNegativeButton("No", null)
                                                    .show();
                                            });
                                        }

                                        TextView tvEdit = item.findViewById(R.id.tvEdit);
                                        if (tvEdit != null) {
                                            tvEdit.setOnClickListener(v -> {
                                                Intent intent = new Intent(GuarderiaController.this, AddGuarderiaController.class);
                                                intent.putExtra("mascota_json", obj.toString());
                                                startActivity(intent);
                                            });
                                        }
                                    } else {
                                        llAcciones.setVisibility(View.GONE);
                                    }
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