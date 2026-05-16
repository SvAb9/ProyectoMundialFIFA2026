package model;

/**
 * Representa una ciudad sede del mundial.
 * Ahora referencia la tabla PAIS mediante id_pais (FK normalizada).
 */
public class Ciudad {

    private int    idCiudad;
    private String nombre;
    private int    idPais;
    private String nombrePais; // para mostrar en pantalla sin JOIN extra en Java

    public Ciudad() {}

    public Ciudad(int idCiudad, String nombre, int idPais, String nombrePais) {
        this.idCiudad   = idCiudad;
        this.nombre     = nombre;
        this.idPais     = idPais;
        this.nombrePais = nombrePais;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Ciudad(String nombre, int idPais) {
        this.nombre = nombre;
        this.idPais = idPais;
    }

    public int    getIdCiudad()                    { return idCiudad; }
    public void   setIdCiudad(int id)              { this.idCiudad = id; }
    public String getNombre()                      { return nombre; }
    public void   setNombre(String nombre)         { this.nombre = nombre; }
    public int    getIdPais()                      { return idPais; }
    public void   setIdPais(int idPais)            { this.idPais = idPais; }
    public String getNombrePais()                  { return nombrePais; }
    public void   setNombrePais(String nombrePais) { this.nombrePais = nombrePais; }

    @Override
    public String toString() { return nombre + " (" + nombrePais + ")"; }
}