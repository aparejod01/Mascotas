package com.example.mascotas.model;

public class AnimalPerdido {
    private int idAnimalPerdido;
    private String nombre;
    private String tipo;
    private String raza;
    private String color;
    private String descripcion;
    private String fechaPerdido;
    private String imagen;

    public AnimalPerdido(int idAnimalPerdido, String nombre, String tipo, String raza, String color, String descripcion, String fechaPerdido, String imagen) {
        this.idAnimalPerdido = idAnimalPerdido;
        this.nombre = nombre;
        this.tipo = tipo;
        this.raza = raza;
        this.color = color;
        this.descripcion = descripcion;
        this.fechaPerdido = fechaPerdido;
        this.imagen = imagen;
    }

    public int getIdAnimalPerdido() { return idAnimalPerdido; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getRaza() { return raza; }
    public String getColor() { return color; }
    public String getDescripcion() { return descripcion; }
    public String getFechaPerdido() { return fechaPerdido; }
    public String getImagen() { return imagen; }
}