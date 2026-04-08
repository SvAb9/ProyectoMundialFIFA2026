package model;

/**
 * Representa una confederación de fútbol.
 * Ejemplos: UEFA, CONMEBOL, CONCACAF, AFC, CAF, OFC
 */
public class Confederacion {

    private int    idConfederacion;
    private String nombre;

    public Confederacion() {}

    public Confederacion(int idConfederacion, String nombre) {
        this.idConfederacion = idConfederacion;
        this.nombre          = nombre;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Confederacion(String nombre) {
        this.nombre = nombre;
    }

    public int    getIdConfederacion()               { return idConfederacion; }
    public void   setIdConfederacion(int id)         { this.idConfederacion = id; }
    public String getNombre()                        { return nombre; }
    public void   setNombre(String nombre)           { this.nombre = nombre; }

    @Override
    public String toString() { return nombre; }
}