package com.example.app_pregunta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_pregunta.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrearCuestionario extends AppCompatActivity {

    String id_usuario;
    TextView etq_nombre_usuario;
    TextView etq_fecha_inicio;
    Config dataConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuestionario);

        dataConfig = new Config(getApplicationContext());
        etq_nombre_usuario = findViewById(R.id.etq_nombre_usuario);
        etq_fecha_inicio = findViewById(R.id.etq_fecha);

        SharedPreferences archivo = getSharedPreferences("app_preguntas", Context.MODE_PRIVATE);
        String nombre_usuario = archivo.getString("nombres",null);
        id_usuario = archivo.getString("id_usuario",null);
        etq_nombre_usuario.setText(nombre_usuario);

        // Obtener la fecha y hora actual utilizando LocalDateTime (Java 8+)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaActual = dateFormat.format(calendar.getTime());
        etq_fecha_inicio.setText(fechaActual);


    }

    public void create_cuestion(View view){
        System.out.println("Iniciando consumo");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = dataConfig.getEndPoint("/createCuestionario.php");

        StringRequest solicitud =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    System.out.println(response);
                    JSONObject datos = new JSONObject(response);
                    System.out.println("conversion del objeto create cuestion");
                    if (datos.getBoolean("status")){
                        String id_cuestionario = datos.getString("id_cuestionario");
                        System.out.println(id_cuestionario);
                        changeActivity(id_cuestionario);
                    }else{
                        Toast.makeText(getApplicationContext(),"Error al iniciar el cuestionario,Intentelo de nuevo",Toast.LENGTH_LONG).show();
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
                params.put("id_usuario", id_usuario);
                return params;
            }
        };

        queue.add(solicitud);
    }

    public void changeActivity(String id_cuestionario){

        Intent intent = new Intent(getApplicationContext(), Pregunta.class);
        intent.putExtra("id_cuestionario",id_cuestionario);
        startActivity(intent);
        finish();
    }

}