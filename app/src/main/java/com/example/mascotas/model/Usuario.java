package com.example.mascotas.model;

public class Usuario {
    private String dni;
    private String nombre;
    private String email;
    private String contrasena;
    private String telefono;

    public Usuario(String dni, String nombre, String email, String contrasena, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
    }

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public String getTelefono() { return telefono; }
}