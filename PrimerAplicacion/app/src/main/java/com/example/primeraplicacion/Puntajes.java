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
        obtenerPuntajes("http://192.168.244.151/preguntas/ObtenerPreguntasPuntaje.php", String.valueOf(cedula));
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

    private void obtenerPuntajes(String url, final String idPuntaje) {


        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        procesarRespuesta(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        queue.add(jsonArrayRequest);
    }

    public void consumoPostJson(String url, final String idPuntaje) {
        System.out.println("Iniciando consumo");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest solicitud = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("DetallePreguntas", "El servidor POST responde OK");
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("DetallePreguntas", jsonObject.toString());

                    JSONArray registrosArray = jsonObject.getJSONArray("registros");

                    for (int i = 0; i < registrosArray.length(); i++) {
                        JSONObject registro = registrosArray.getJSONObject(i);

                        String descripcion = registro.getString("descripcion");
                        String respuesta = registro.getString("respuesta");
                        String solucion = obtenerSolucion(i);

                        TableRow row = new TableRow(DetallePreguntas.this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                        row.setLayoutParams(lp);

                        agregarTextView(row, descripcion, Gravity.CENTER);
                        agregarTextView(row, respuesta, Gravity.CENTER);
                        agregarTextView(row, solucion, Gravity.CENTER);

                        if (tableLayoutDetalle != null) {
                            tableLayoutDetalle.addView(row);
                        } else {
                            Log.e("DetallePreguntas", "tableLayout es nulo al intentar agregar una fila");
                        }
                    }

                } catch (JSONException e) {
                    Log.e("DetallePreguntas", "El servidor POST responde con un error:");
                    Log.e("DetallePreguntas", e.getMessage());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("El servidor POST responde con un error:");
                        System.out.println(error.getMessage());
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("id_puntaje", idPuntaje);
                    return jsonParams.toString().getBytes("utf-8");
                } catch (JSONException e) {
                    System.out.println("Error al construir el cuerpo de la solicitud JSON");
                    return null;
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        queue.add(solicitud);
    }

    private void procesarRespuesta(JSONArray puntajesArray) {
        try {
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
