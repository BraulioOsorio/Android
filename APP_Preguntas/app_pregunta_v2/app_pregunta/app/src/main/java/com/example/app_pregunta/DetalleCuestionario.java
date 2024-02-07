package com.example.app_pregunta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetalleCuestionario extends AppCompatActivity {


    String datosJSON;
    TextView etq_resumen;
    TextView etq_nombre_user;
    LinearLayout layoutPreguntas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cuestionario);
        layoutPreguntas = findViewById(R.id.layoutPreguntas);
        etq_nombre_user = findViewById(R.id.etq_nombre_user);

        SharedPreferences archivo = getSharedPreferences("app_preguntas", Context.MODE_PRIVATE);
        String nombre_usuario = archivo.getString("nombres",null);
        etq_nombre_user.setText(nombre_usuario);

        Intent intent = getIntent();
        datosJSON = intent.getStringExtra("datosJSON");
        System.out.println(datosJSON);

        String resumen_cuestionario = intent.getStringExtra("resumen_cuestionario");

        etq_resumen = findViewById(R.id.etq_resumen);
        etq_resumen.setText(resumen_cuestionario);

        FloatingActionButton btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });

        cargar_detalle();
    }

    public void regresar() {
        Intent intencion = new Intent(getApplicationContext(), ResumenUsuario.class);
        startActivity(intencion);
        finish();
    }

    public void cargar_detalle(){
        try {
            System.out.println("iniciando carga");
            JSONObject datos = new JSONObject(datosJSON);
            JSONArray arrayDatos = datos.getJSONArray("respuestas");

            for (int i = 0; i < arrayDatos.length(); i++) {
                JSONObject pregunta = arrayDatos.getJSONObject(i);
                JSONObject objectPregunta = pregunta.getJSONObject("pregunta");

                // respuesta user
                String respuesta_user = objectPregunta.getString("respuesta");

                // ID de la respuesta correcta
                int idRespuestaCorrecta = Integer.parseInt(objectPregunta.getString("id_correcta"));



                // etiqueta nº pregunta
                TextView etq_n_pregunta = new TextView( getApplicationContext() );
                etq_n_pregunta.append("Pregunta :"+(i+1));
                etq_n_pregunta.setTextColor(Color.BLACK);
                etq_n_pregunta.setTypeface(null, Typeface.BOLD);

                // etiqueta descripcion de la pregunta
                TextView etq_descripcion_p = new TextView( getApplicationContext() );
                etq_descripcion_p.setText(objectPregunta.getString("descripcion"));
                etq_descripcion_p.setTextColor(Color.BLACK);

                // etiqueta opciones
                LinearLayout opcionesLayout = new LinearLayout(getApplicationContext());
                opcionesLayout.setOrientation(LinearLayout.VERTICAL);

                JSONArray arrayOpciones = pregunta.getJSONArray("opciones");

                for (int j = 0; j < arrayOpciones.length(); j++) {
                    JSONObject object_option = arrayOpciones.getJSONObject(j);

                    // ID de la opcion
                    int id_opcion = object_option.getInt("id");
                    String descripcion = object_option.getString("descripcion");


                    // Crear un TextView para cada opción
                    TextView opcionTextView = new TextView(getApplicationContext());
                    opcionTextView.setText(descripcion);
                    opcionTextView.setTextColor(Color.BLACK);
                    // Aplicar viñetas directamente al TextView de la opción
                    BulletSpan bulletSpan = new BulletSpan(10, getResources().getColor(R.color.black));
                    String textoConOpciones = opcionTextView.getText().toString();
                    opcionTextView.setText(getBulletString(textoConOpciones, bulletSpan));

                    if (descripcion.equalsIgnoreCase(respuesta_user)){
                        if (id_opcion == idRespuestaCorrecta){
                            opcionTextView.setTextColor(Color.GREEN);
                        }else{
                            opcionTextView.setTextColor(Color.RED);
                        }
                    }

                    opcionesLayout.addView(opcionTextView);
                }

                layoutPreguntas.addView(etq_n_pregunta);
                layoutPreguntas.addView(etq_descripcion_p);
                layoutPreguntas.addView(opcionesLayout);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private CharSequence getBulletString(String text, BulletSpan bulletSpan) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(bulletSpan, 0, spannableString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}