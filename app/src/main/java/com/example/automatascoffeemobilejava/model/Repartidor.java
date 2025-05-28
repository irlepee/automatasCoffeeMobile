package com.example.automatascoffeemobilejava.model;

public class Repartidor {
    private int id;
    private int id_usuario;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String telefono;
    private String curp;
    private String rfc;
    private String tipo_sangre;
    private String nss;
    private String vigencia_licencia;
    private int estatus_repartiendo;
    private int estatus;
    private String ubicacion;
    private String usuario;

    // Getters
    public int getId() { return id; }
    public int getId_usuario() { return id_usuario; }
    public String getNombre() { return nombre; }
    public String getApellido1() { return apellido1; }
    public String getApellido2() { return apellido2; }
    public String getTelefono() { return telefono; }
    public String getCurp() { return curp; }
    public String getRfc() { return rfc; }
    public String getTipo_sangre() { return tipo_sangre; }
    public String getNss() { return nss; }
    public String getVigencia_licencia() { return vigencia_licencia; }
    public int getEstatus_repartiendo() { return estatus_repartiendo; }
    public int getEstatus() { return estatus; }
    public String getUbicacion() { return ubicacion; }
    public String getUsuario() { return usuario; }
}