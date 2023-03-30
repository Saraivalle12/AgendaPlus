package com.example.agendaplus.configuracion;

public class Transacciones
{
    // Nombre de la bd
    public static final String NameDatabase = "AgendaPlus";

    public static final String tabla_usuario = "Usuario";

    public static String id = "id";
    public static String nombre = "nombre";

    public static String apellido = "apellido";
    public static String edad = "edad";
    public static String genero = "genero";
    public static String correo = "correo";
    public static String telefono = "telefono";
    public static String imagen = "imagen";

    public static final String tabla_concacto = "Contacto";

    /* Campos de la tabla personas */
    public static String id_ct = "id";
    public static String nombre_ct = "nombre";
    public static String telefono_ct = "telefono";
    public static String imagen_ct = "imagen";
    // Consultas SQL DDL
    public static String CreateTBUsuario = "CREATE TABLE \"Usuario\" (\n" +
            "\t\"id\"\tINTEGER NOT NULL,\n" +
            "\t\"nombre\"\tTEXT NOT NULL,\n" +
            "\t\"apellido\"\tTEXT NOT NULL,\n" +
            "\t\"edad\"\tNUMERIC NOT NULL,\n" +
            "\t\"genero\"\tTEXT NOT NULL,\n" +
            "\t\"telefono\"\tTEXT NOT NULL,\n" +
            "\t\"correo\"\tTEXT NOT NULL,\n" +
            "\t\"imagen\"\tTEXT NOT NULL,\n" +
            "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
            ");";

    public static String CreateTBContacto = "CREATE TABLE \"Contacto\" (\n" +
            "\t\"id\"\tINTEGER NOT NULL,\n" +
            "\t\"nombre\"\tTEXT NOT NULL,\n" +
            "\t\"apellido\"\tTEXT NOT NULL,\n" +
            "\t\"edad\"\tNUMERIC NOT NULL,\n" +
            "\t\"genero\"\tTEXT NOT NULL,\n" +
            "\t\"telefono\"\tTEXT NOT NULL,\n" +
            "\t\"correo\"\tTEXT NOT NULL,\n" +
            "\t\"imagen\"\tTEXT NOT NULL,\n" +
            "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
            ");";

    public static String DropTBContacto = "DROP TABLE IF EXISTS Usuario";
}
