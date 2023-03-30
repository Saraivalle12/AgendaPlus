package com.example.agendaplus;

public class Contacto {
    private Integer id;
    private String nombre;
    private String apellido;
    private String telefono;
    private int edad;
    private char genero;
    private String correo;
    private String imagen;

    public Contacto() {
        this.id = -1;
        this.nombre = "";
        this.apellido = "";
        this.telefono = "";
        this.edad = 0;
        this.genero = ' ';
        this.correo = "";
        this.imagen = "";
    }

    public Contacto(String nombre, String apellido, String telefono, int edad, char genero, String correo, String imagen) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.edad = edad;
        this.genero = genero;
        this.correo = correo;
        this.imagen = imagen;
    }

    public Contacto(Integer id, String nombre, String apellido, String telefono, int edad, char genero, String correo, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.edad = edad;
        this.genero = genero;
        this.correo = correo;
        this.imagen = imagen;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public char getGenero() {
        return genero;
    }

    public void setGenero(char genero) {
        this.genero = genero;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
