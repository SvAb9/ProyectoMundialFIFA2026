package model;

public class Posicion {
    private int    idPosicion;
    private String nombre;
    private String descripcion;

    public Posicion() {}

    public Posicion(int idPosicion, String nombre, String descripcion) {
        this.idPosicion  = idPosicion;
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    public Posicion(String nombre, String descripcion) {
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    public int    getIdPosicion()                    { return idPosicion; }
    public void   setIdPosicion(int idPosicion)      { this.idPosicion = idPosicion; }
    public String getNombre()                        { return nombre; }
    public void   setNombre(String nombre)           { this.nombre = nombre; }
    public String getDescripcion()                   { return descripcion; }
    public void   setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() { return nombre; }
}