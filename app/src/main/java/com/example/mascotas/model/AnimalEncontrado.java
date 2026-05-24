package com.example.mascotas.model;

public class AnimalEncontrado {
    private int idAnimalesEncontrado;
    private String nombre;
    private String tipo;
    private String raza;
    private String color;
    private String descripcion;
    private String fechaEncontrado;
    private String imagen;

    public AnimalEncontrado(int idAnimalesEncontrado, String nombre, String tipo, String raza, String color, String descripcion, String fechaEncontrado, String imagen) {
        this.idAnimalesEncontrado = idAnimalesEncontrado;
        this.nombre = nombre;
        this.tipo = tipo;
        this.raza = raza;
        this.color = color;
        this.descripcion = descripcion;
        this.fechaEncontrado = fechaEncontrado;
        this.imagen = imagen;
    }

    public int getIdAnimalesEncontrado() { return idAnimalesEncontrado; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getRaza() { return raza; }
    public String getColor() { return color; }
    public String getDescripcion() { return descripcion; }
    public String getFechaEncontrado() { return fechaEncontrado; }
    public String getImagen() { return imagen; }
}