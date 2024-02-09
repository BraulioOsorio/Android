package com.example.pruebarecycler;

public class Persona {
    String nombres;
    String telefono;
    String edad;
    String genero;
    String email;
    String estado;


    public Persona(String nombres, String telefono, String edad, String genero, String email, String estado) {
        this.nombres = nombres;
        this.telefono = telefono;
        this.edad = edad;
        this.genero = genero;
        this.email = email;
        this.estado = estado;
    }

    public String getNombres() {
        return nombres;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEdad() {
        return edad;
    }

    public String getGenero() {
        return genero;
    }

    public String getEmail() {
        return email;
    }

    public String getEstado() {
        return estado;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
