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

import java.util.HashSet;
import java.util.Set;

public class EncontradosController extends AppCompatActivity {

    private LinearLayout container;
    private RequestQueue requestQueue;
    private final String URL_GET = "http://10.0.2.2/Android/get_animales.php?tabla=animalesencontrados";
    private final String TAG_VOLLEY = "get_encontrados_tag";
    // Set para evitar duplicados si el servidor responde dos veces o tiene datos repetidos
    private final Set<String> idsCargados = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas_encontradas);

        container = findViewById(R.id.containerMascotasEncontradas);
        FloatingActionButton fab = findViewById(R.id.fabAgregarEncontrada);
        ImageView ivBack = findViewById(R.id.ivBack);

        // Inicializamos Volley
        requestQueue = Volley.newRequestQueue(this);

        if (ivBack != null) ivBack.setOnClickListener(v -> finish());
        
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(EncontradosController.this, AddEncontradoController.class);
                startActivity(intent);
            });
        }
        
        // Dejamos que onResume gestione la carga para evitar duplicados al arrancar
    }

    private void obtenerMascotas() {
        if (container == null) return;
        
        // Cancelamos cualquier petición idéntica en curso
        requestQueue.cancelAll(TAG_VOLLEY);

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    if (isFinishing()) return;
                    try {
                        String json = response.trim();
                        if (json.startsWith("[")) {
                            // LIMPIEZA TOTAL: Vaciamos la lista y el set de IDs antes de procesar la respuesta
                            container.removeAllViews();
                            idsCargados.clear();
                            
                            JSONArray array = new JSONArray(json);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                
                                // Usamos el ID de la tabla (idAnimalesEncontrado) para garantizar unicidad
                                String id = obj.optString("idAnimalesEncontrado", String.valueOf(i));
                                
                                if (!idsCargados.contains(id)) {
                                    idsCargados.add(id);
                                    agregarItemALista(obj);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", "Fallo al procesar: " + e.getMessage());
                    }
                },
                error -> {
                    if (!isFinishing()) {
                        Toast.makeText(this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });

        request.setTag(TAG_VOLLEY);
        request.setShouldCache(false); // Desactivamos caché para evitar datos viejos duplicados
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

            // Mapeamos los datos de la tabla animalesencontrados
            String nombre = obj.optString("nombre", "");
            String raza = obj.optString("raza", "Desconocida");
            
            if (tvRaza != null) {
                tvRaza.setText(nombre.isEmpty() || nombre.equalsIgnoreCase("Desconocido") ? raza : nombre);
            }
            
            if (tvDesc != null) tvDesc.setText(obj.optString("descripcion", ""));
            if (tvLoc != null) tvLoc.setText("📍 Mascota encontrada");
            if (tvFecha != null) tvFecha.setText("🕒 " + obj.optString("fechaEncontrado", "---"));
            if (tvTel != null) tvTel.setText("📞 Disponible en ficha");
            if (tvColor != null) tvColor.setText("🎨 " + obj.optString("color", "---"));

            // Decodificación segura de imagen Base64
            String imgBase64 = obj.optString("imagen", "");
            if (!imgBase64.isEmpty() && !imgBase64.equals("null") && ivFoto != null) {
                try {
                    byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // Optimización de memoria
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                    if (bitmap != null) {
                        ivFoto.setImageBitmap(bitmap);
                        ivFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                } catch (Throwable t) {
                    Log.e("IMAGE_ERROR", "No se pudo cargar una imagen");
                }
            }

            container.addView(item);
        } catch (Exception e) {
            Log.e("UI_ERROR", "Error al crear elemento de la lista");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cargamos los datos siempre que la pantalla aparezca. 
        obtenerMascotas();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Cancelamos peticiones pendientes al salir para evitar fugas de memoria
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG_VOLLEY);
        }
    }
}