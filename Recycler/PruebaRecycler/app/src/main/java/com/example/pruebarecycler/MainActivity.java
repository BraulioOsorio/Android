package com.example.pruebarecycler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText campoBusqueda;
    FloatingActionButton btnBuscar;
    RecyclerView recyclerPersonas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        campoBusqueda = findViewById(R.id.campoBusqueda);
        btnBuscar = findViewById(R.id.btnBuscar);

        List<Persona> listaPersonas = new ArrayList<>();
        listaPersonas.add(new Persona("David Osorio","333333333","20","MASCULINO","braulio@gmail.com","ACTIVO"));
        listaPersonas.add(new Persona("Xiomara cano","3333335","18","FEMENINO","xiomy@gmail.com","INANCTIVO"));

        recyclerPersonas = findViewById(R.id.recyclerPersonas);
        recyclerPersonas.setLayoutManager(new LinearLayoutManager((getApplicationContext())));

        AdaptadorPersonas adaptador = new AdaptadorPersonas(listaPersonas);
        recyclerPersonas.setAdapter(adaptador);
    }

}