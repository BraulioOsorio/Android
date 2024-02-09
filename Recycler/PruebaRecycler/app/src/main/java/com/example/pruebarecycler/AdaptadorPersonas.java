package com.example.pruebarecycler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdaptadorPersonas extends RecyclerView.Adapter<AdaptadorPersonas.ViewHolder> {

    List<Persona> listaPersonas;

    public AdaptadorPersonas(List<Persona> listaPersonas ){
        this.listaPersonas = listaPersonas;
    }

    @NonNull
    @Override
    public AdaptadorPersonas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personas,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPersonas.ViewHolder holder, int position) {
        Persona temporal = listaPersonas.get(position);
        holder.cargarDatos(temporal);

    }

    @Override
    public int getItemCount() {
        return listaPersonas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPersona;
        TextView etqNombres;
        TextView etqEdad;
        TextView etqGenero;
        TextView etqTelefono;
        TextView etqEmail;
        TextView etqEstado;
        FloatingActionButton btnBuscar;
        Context contexto;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            imgPersona = itemView.findViewById(R.id.imgPersona);
            etqEmail = itemView.findViewById(R.id.etqEmail);
            etqEstado = itemView.findViewById(R.id.etqEstado);
            etqTelefono = itemView.findViewById(R.id.etqTelefono);
            etqNombres = itemView.findViewById(R.id.etqNombres);
            etqEdad = itemView.findViewById(R.id.etqEdad);
            etqGenero = itemView.findViewById(R.id.etqGenero);
            btnBuscar = itemView.findViewById(R.id.btnBuscar);
            contexto = itemView.getContext();
        }

        public void cargarDatos(Persona personaTem){
            etqNombres.setText(personaTem.getNombres());
            etqGenero.setText(personaTem.getGenero());
            etqEdad.setText(personaTem.getEdad());
            etqEstado.setText(personaTem.getEstado());
            etqEmail.setText(personaTem.getEmail());
            etqTelefono.setText(personaTem.getTelefono());

            if (personaTem.getEstado().equalsIgnoreCase("ACTIVO")){
                etqEstado.setTextColor(Color.GREEN);
            }else{
                etqEstado.setTextColor(Color.RED);
            }

            if(personaTem.getGenero().equalsIgnoreCase("MASCULINO")){
                imgPersona.setImageResource(R.mipmap.ic_masculino_round);
            }else{
                imgPersona.setImageResource(R.mipmap.ic_femenino_round);
            }
            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String nombres = personaTem.getNombres();
                    String edad = personaTem.getEdad();
                    String telefono = personaTem.getTelefono();
                    String estado = personaTem.getEstado();
                    String email = personaTem.getEmail();
                    String genero = personaTem.getGenero();

                    Intent intencion = new Intent(contexto,DetallePersona.class);
                    intencion.putExtra("nombre",nombres);
                    intencion.putExtra("telefono",telefono);
                    intencion.putExtra("edad",edad);
                    intencion.putExtra("estado",estado);
                    intencion.putExtra("email",email);
                    intencion.putExtra("genero",genero);

                    contexto.startActivity(intencion);
                }
            });

        }
    }
}
