package com.example.pruebarecycler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetallePersona extends AppCompatActivity {
    TextView etqDetalle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_persona);

        etqDetalle = findViewById(R.id.etqDetalle);
        Bundle datos = getIntent().getExtras();
        etqDetalle.setText(datos.getString("nombre")+"\n"+datos.getString("telefono"));


    }
}