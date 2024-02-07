package com.example.app_pregunta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_pregunta.utils.Config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class Pregunta extends AppCompatActivity {
    Config dataConfig;
    ImageView imagen;
    RadioGroup radioGroup;
    String id_cuestionario;
    TextView etq_titulo_pregunta;
    Button btn_siguiente;
    LinearLayout linearLayoutImage;
    ProgressBar progressBar;
    TextView etq_name;
    TextView etq_fecha_inicio;
    String respuesta_usuario;
    String id_pregunta;
    String id_correcta;
    String id_opcion;

    ArrayList<Integer> listIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        dataConfig = new Config(getApplicationContext());

        btn_siguiente = findViewById(R.id.btn_siguiente);
        //imagen = findViewById(R.id.etq_img);
        radioGroup = findViewById(R.id.radioGroup);
        etq_titulo_pregunta = findViewById(R.id.etq_titulo_pregunta);
        linearLayoutImage = findViewById(R.id.linear_layout_img);
        etq_name = findViewById(R.id.etq_nombre_user);
        etq_fecha_inicio = findViewById(R.id.fecha_inicio);

        listIDs = new ArrayList<>();

        // Obtener la fecha y hora actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaActual = dateFormat.format(calendar.getTime());
        etq_fecha_inicio.setText(fechaActual);

        SharedPreferences archivo = getSharedPreferences("app_preguntas", Context.MODE_PRIVATE);
        String nombre_usuario = archivo.getString("nombres",null);
        etq_name.setText(nombre_usuario);

        Intent intent = getIntent();
        id_cuestionario = intent.getStringExtra("id_cuestionario");

        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar_respuesta(id_cuestionario,id_pregunta,respuesta_usuario);
            }
        });

        FloatingActionButton btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });

        apiCargarPregunta(id_cuestionario);
    }


    public void apiCargarPregunta(String id_cuestion){
        System.out.println("Iniciando consumo");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = dataConfig.getEndPoint("/getOtherPregunta.php");

        StringRequest solicitud =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    System.out.println(response);
                    JSONObject datos = new JSONObject(response);

                    JSONArray arrayPregunta = datos.getJSONArray("pregunta");
                    JSONObject objectPregunta = arrayPregunta.getJSONObject(0);

                    String id_pregunta_generada = objectPregunta.getString("id");

                    if (listIDs.size() == 10){
                        regresar();
                    }else{
                        if (datos.getBoolean("status")){
                            if (validar_id(id_pregunta_generada)){
                                cargarPreguntas(datos);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Error al iniciar el cuestionario,Intentelo de nuevo",Toast.LENGTH_LONG).show();
                        }
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
                params.put("id_cuestionario",id_cuestion );
                return params;
            }
        };

        queue.add(solicitud);

    }

    public void cargarPreguntas(JSONObject datos) {
        try {

            linearLayoutImage.removeAllViews();
            radioGroup.removeAllViews();

            System.out.println("convirtiendo el JSON");
            JSONArray arrayPregunta = datos.getJSONArray("pregunta");


            JSONObject objectPregunta = arrayPregunta.getJSONObject(0);

            String titlePre = objectPregunta.getString("descripcion");
            etq_titulo_pregunta.setText(titlePre);

            // obtener id_pregunta
            id_pregunta = objectPregunta.getString("id");
            // obtener id_correcta
            id_correcta = objectPregunta.getString("id_correcta");

            System.out.println("colocando la img");
            String rutaImg = objectPregunta.getString("url_imagen");

            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayoutCompat.LayoutParams imageLayoutParams = new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            );

            imageLayoutParams.setMargins(0, 16, 0, 4);
            imageView.setLayoutParams(imageLayoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            consumoImagen(imageView, rutaImg);

            linearLayoutImage.addView(imageView);

            // colocando las opciones
            JSONArray arrayOptions = datos.getJSONArray("opciones");
            for (int i = 0; i < arrayOptions.length() ; i++) {
                JSONObject objectOption = arrayOptions.getJSONObject(i);
                String opcion = objectOption.getString("descripcion");

                RadioButton radioButton = new RadioButton(getApplicationContext());
                radioButton.setId(i + 1);  // Asegúrate de asignar IDs únicos
                radioButton.setText( opcion );
                radioButton.setTextColor(Color.BLACK);


                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // Captura la respuesta seleccionada
                            String respuestaSeleccionada = buttonView.getText().toString();
                            respuesta_usuario = respuestaSeleccionada;
                            try {
                                id_opcion = objectOption.getString("id");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                });


                radioGroup.addView(radioButton);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void consumoImagen(ImageView imageView, String ruta){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url =  ruta;

        ImageRequest solicitud = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // Setea la imagen en el ImageView
                        imageView.setImageBitmap(bitmap);
                    }
                },
                0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja el error.
                    }
                });

        queue.add(solicitud);
    }

    public void regresar(){
        Intent intent = new Intent(getApplicationContext(),ResumenUsuario.class);
        startActivity(intent);
        finish();
    }

    public boolean validar_id(String id_pregunta_s){
        int id_pregunta = Integer.parseInt(id_pregunta_s);
        if (listIDs.contains(id_pregunta)){
            apiCargarPregunta(id_cuestionario);
            return false;
        }else{
            listIDs.add(id_pregunta);
            return  true;
        }


    }


    public void registrar_respuesta(String id_cuestionario,String id_pregunta,String respuesta){
        String estado;
        if (id_opcion.equalsIgnoreCase(id_correcta)){
            estado = "OK";
        }else{
            estado = "ERROR";
        }

        System.out.println("Iniciando consumo");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = dataConfig.getEndPoint("/registrarRespuesta.php");

        StringRequest solicitud =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    System.out.println(response);
                    JSONObject datos = new JSONObject(response);

                    if (datos.getBoolean("status")){
                        apiCargarPregunta(id_cuestionario);
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
                params.put("id_cuestionario",id_cuestionario );
                params.put("id_pregunta",id_pregunta );
                params.put("respuesta",respuesta );
                params.put("estado",estado );
                return params;
            }
        };

        queue.add(solicitud);


    }

}