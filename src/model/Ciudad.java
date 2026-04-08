package model;

/**
 * Representa una ciudad sede del mundial.
 * Solo puede pertenecer a uno de los tres países anfitriones:
 * Mexico, USA o Canada.
 */
public class Ciudad {

    private int    idCiudad;
    private String nombre;
    private String pais;   // "Mexico", "USA" o "Canada"

    public Ciudad() {}

    public Ciudad(int idCiudad, String nombre, String pais) {
        this.idCiudad = idCiudad;
        this.nombre   = nombre;
        this.pais     = pais;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Ciudad(String nombre, String pais) {
        this.nombre = nombre;
        this.pais   = pais;
    }

    public int    getIdCiudad()            { return idCiudad; }
    public void   setIdCiudad(int id)      { this.idCiudad = id; }
    public String getNombre()              { return nombre; }
    public void   setNombre(String nombre) { this.nombre = nombre; }
    public String getPais()                { return pais; }
    public void   setPais(String pais)     { this.pais = pais; }

    @Override
    public String toString() { return nombre + " (" + pais + ")"; }
}