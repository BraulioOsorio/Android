package com.example.app_pregunta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_pregunta.utils.Config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResumenUsuario extends AppCompatActivity {

    Config dataConfig;

    String id_usuario;
    String nombre_usuario;

    TextView etq_usuario;

    LinearLayout layoutCuestionarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_usuario);

        dataConfig = new Config(getApplicationContext());

        etq_usuario = findViewById(R.id.etq_usuario);
        layoutCuestionarios = findViewById(R.id.layoutCuestionarios);

        SharedPreferences archivo = getSharedPreferences("app_preguntas", Context.MODE_PRIVATE);

        id_usuario = archivo.getString("id_usuario",null);
        nombre_usuario = archivo.getString("nombres",null);

        etq_usuario.setText(nombre_usuario);

        FloatingActionButton btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrar_sesion();
            }
        });

        FloatingActionButton btn_create = findViewById(R.id.btn_create);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crear_cuestionario();
            }
        });


        obtenerCuestionarios();
    }

    public void cerrar_sesion(){
        Log.d("Logout", "Sesión cerrada. Redirigiendo a MainActivity.");
        SharedPreferences archivo = getSharedPreferences("app_preguntas", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = archivo.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void crear_cuestionario(){
        Intent intent = new Intent(getApplicationContext(), CrearCuestionario.class);
        startActivity(intent);
        finish();
    }

    public void obtenerCuestionarios(){
        System.out.println("Iniciando consumo");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = dataConfig.getEndPoint("/getCuestionarios.php");

        StringRequest solicitud =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    System.out.println(response);
                    JSONObject datos = new JSONObject(response);

                    if (datos.getBoolean("status")){

                        cargarCuestionarios(datos.getJSONArray("resumen"));

                    }else{
                        Toast.makeText(getApplicationContext(),"Cuestionarios no encontrados",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    System.out.println("El servidor POST responde con un error:");
                    System.out.println(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("El servidor POST responde con un error:");
                System.out.println(error.getMessage());
                Toast.makeText(getApplicationContext(),"Error en Datos del servidor: "+ error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_usuario", id_usuario);
                return params;
            }
        };

        queue.add(solicitud);
    }

    public void cargarCuestionarios(JSONArray resumen){

            try {
                System.out.println("iniciando carga");
                for (int i = 0; i < resumen.length(); i++) {

                    JSONObject cuestionario = resumen.getJSONObject(i);

                    TextView etiqueta = new TextView( getApplicationContext() );
                    etiqueta.setText("N° "+(i+1) + "\n");

                    etiqueta.append("Iniciado el: "+cuestionario.getString("fecha_inicio")+"\n");
                    etiqueta.append("Finalizado el: " + (cuestionario.getString("fecha_fin") != null ? cuestionario.getString("fecha_fin") : "No disponible") + "\n");
                    etiqueta.append("Preguntas: "+cuestionario.getString("cant_preguntas")+"\n");
                    etiqueta.append("Correctas: "+cuestionario.getString("cant_ok")+"\n");
                    etiqueta.append("Incorrectas: "+cuestionario.getString("cant_error")+"\n");
                    etiqueta.setTextColor(Color.BLACK);
                    layoutCuestionarios.addView(etiqueta);

                    String id_cuestionario = cuestionario.getString("id");

                    etiqueta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            detalle_cuestionario(id_cuestionario,etiqueta);
                        }
                    });

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


    }

    public void detalle_cuestionario(String idCuestionario, TextView etq) {
        System.out.println("Iniciando consumo");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = dataConfig.getEndPoint("/getRespuestas.php");

        StringRequest solicitud =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    System.out.println(response);
                    JSONObject datos = new JSONObject(response);

                    if (datos.getBoolean("status")){
                        changeActivity(response,etq);
                    }else{
                        Toast.makeText(getApplicationContext(),"Cuestionario no encontrado",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    System.out.println("El servidor POST responde con un error:");
                    System.out.println(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("El servidor POST responde con un error:");
                System.out.println(error.getMessage());
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_cuestionario", idCuestionario);
                return params;
            }
        };

        queue.add(solicitud);
    }

    public void changeActivity(String response,TextView etq) {
        String resumen_cuestionario = etq.getText().toString();
        Intent intencion = new Intent(getApplicationContext(), DetalleCuestionario.class);
        intencion.putExtra("datosJSON", response);
        intencion.putExtra("resumen_cuestionario", resumen_cuestionario);
        startActivity(intencion);
        finish();
    }


}