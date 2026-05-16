package model;

/**
 * Representa un país anfitrión del Mundial FIFA 2026.
 * Los tres países son: México, USA y Canadá.
 */
public class Pais {

    private int    idPais;
    private String nombre;
    private String codigoIso;

    public Pais() {}

    public Pais(int idPais, String nombre, String codigoIso) {
        this.idPais    = idPais;
        this.nombre    = nombre;
        this.codigoIso = codigoIso;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Pais(String nombre, String codigoIso) {
        this.nombre    = nombre;
        this.codigoIso = codigoIso;
    }

    public int    getIdPais()                    { return idPais; }
    public void   setIdPais(int idPais)          { this.idPais = idPais; }
    public String getNombre()                    { return nombre; }
    public void   setNombre(String nombre)       { this.nombre = nombre; }
    public String getCodigoIso()                 { return codigoIso; }
    public void   setCodigoIso(String c)         { this.codigoIso = c; }

    @Override
    public String toString() { return nombre + " (" + codigoIso + ")"; }
}