package com.example.primeraplicacion;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Puntajes extends AppCompatActivity {

    private TableLayout tableLayout;

    String nombre;
    String cedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntajes);

        tableLayout = findViewById(R.id.tableLayout);
        Bundle datosUsuario = getIntent().getExtras();
        nombre = datosUsuario.getString("nombreUsuario");
        cedula = datosUsuario.getString("cedulaUsuario");
        obtenerPuntajes("http://192.168.1.2/preguntas/ObtenerPuntajes.php", String.valueOf(cedula));
    }

    public void abrirModal(View view, int id) {
        Intent intent = new Intent(Puntajes.this, DetallePreguntas.class);
        intent.putExtra("id_puntaje", id);
        startActivity(intent);
    }

    public void comenzar(View vista) {
        Intent intencion = new Intent(getApplicationContext(), PreguntasApi.class);
        intencion.putExtra("nombreUsuario", nombre);
        intencion.putExtra("cedulaUsuario", cedula);
        startActivity(intencion);
        finish();
    }
    public void salir(View vista) {
        Intent intencion = new Intent(getApplicationContext(), Login.class);
        startActivity(intencion);
        finish();
    }

    public void obtenerPuntajes(String url, final String cedula) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext()) ;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Procesar la respuesta aquí
                        procesarRespuesta(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores aquí
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Agregar los parámetros de la solicitud POST
                Map<String, String> params = new HashMap<>();
                params.put("cedula", cedula);
                return params;
            }
        };

        queue.add(stringRequest);
    }







    private void procesarRespuesta(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // Obtiene el array de puntajes del objeto JSON
            JSONArray puntajesArray = jsonObject.getJSONArray("puntajes");
            for (int i = 0; i < puntajesArray.length(); i++) {
                JSONObject puntaje = puntajesArray.getJSONObject(i);

                String nombre = puntaje.getString("nombre");
                String puntajeStr = puntaje.getString("puntaje");
                String fecha = puntaje.getString("fecha");
                int idPuntaje = puntaje.getInt("id_puntaje");

                TableRow row = new TableRow(Puntajes.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);

                TextView tvNombre = new TextView(Puntajes.this);
                tvNombre.setText(nombre);
                tvNombre.setGravity(View.TEXT_ALIGNMENT_CENTER);
                row.addView(tvNombre);

                TextView tvPuntaje = new TextView(Puntajes.this);
                tvPuntaje.setText(puntajeStr);
                tvPuntaje.setGravity(View.TEXT_ALIGNMENT_CENTER);
                row.addView(tvPuntaje);

                TextView tvFecha = new TextView(Puntajes.this);
                tvFecha.setText(fecha);
                tvFecha.setGravity(View.TEXT_ALIGNMENT_CENTER);
                row.addView(tvFecha);

                Button btnVerPreguntas = new Button(Puntajes.this);
                btnVerPreguntas.setText("Ver Preguntas");
                btnVerPreguntas.setTag(idPuntaje);
                btnVerPreguntas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirModal(v, idPuntaje);
                    }
                });
                row.addView(btnVerPreguntas);

                tableLayout.addView(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
