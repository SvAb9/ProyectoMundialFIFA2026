package model;

/**
 * Representa un estadio donde se juegan los partidos de la fase de grupos.
 * Cada estadio está ubicado en una ciudad sede.
 */
public class Estadio {

    private int    idEstadio;
    private String nombre;
    private int    capacidad;
    private int    idCiudad;

    // Campos extra para mostrar en la vista sin hacer JOIN en Java
    private String nombreCiudad;
    private String paisCiudad;

    public Estadio() {}

    public Estadio(int idEstadio, String nombre, int capacidad, int idCiudad) {
        this.idEstadio  = idEstadio;
        this.nombre     = nombre;
        this.capacidad  = capacidad;
        this.idCiudad   = idCiudad;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Estadio(String nombre, int capacidad, int idCiudad) {
        this.nombre    = nombre;
        this.capacidad = capacidad;
        this.idCiudad  = idCiudad;
    }

    public int    getIdEstadio()                   { return idEstadio; }
    public void   setIdEstadio(int idEstadio)       { this.idEstadio = idEstadio; }
    public String getNombre()                       { return nombre; }
    public void   setNombre(String nombre)          { this.nombre = nombre; }
    public int    getCapacidad()                    { return capacidad; }
    public void   setCapacidad(int capacidad)       { this.capacidad = capacidad; }
    public int    getIdCiudad()                     { return idCiudad; }
    public void   setIdCiudad(int idCiudad)         { this.idCiudad = idCiudad; }
    public String getNombreCiudad()                 { return nombreCiudad; }
    public void   setNombreCiudad(String nombre)    { this.nombreCiudad = nombre; }
    public String getPaisCiudad()                   { return paisCiudad; }
    public void   setPaisCiudad(String pais)        { this.paisCiudad = pais; }

    @Override
    public String toString() { return nombre; }
}