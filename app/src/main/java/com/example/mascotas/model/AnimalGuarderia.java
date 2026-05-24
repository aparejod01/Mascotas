package com.example.mascotas.model;

public class AnimalGuarderia {
    private int idAnimalGuarderia;
    private String nombre;
    private String tipo;
    private String raza;
    private String color;
    private String descripcion;
    private String fechaEntrada;
    private String fechaSalida;
    private String imagen;

    public AnimalGuarderia(int idAnimalGuarderia, String nombre, String tipo, String raza, String color, String descripcion, String fechaEntrada, String fechaSalida, String imagen) {
        this.idAnimalGuarderia = idAnimalGuarderia;
        this.nombre = nombre;
        this.tipo = tipo;
        this.raza = raza;
        this.color = color;
        this.descripcion = descripcion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.imagen = imagen;
    }

    public int getIdAnimalGuarderia() { return idAnimalGuarderia; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getRaza() { return raza; }
    public String getColor() { return color; }
    public String getDescripcion() { return descripcion; }
    public String getFechaEntrada() { return fechaEntrada; }
    public String getFechaSalida() { return fechaSalida; }
    public String getImagen() { return imagen; }
}