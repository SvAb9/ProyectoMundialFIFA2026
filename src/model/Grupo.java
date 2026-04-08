package model;

/**
 * Representa uno de los 12 grupos de la fase de grupos del mundial.
 * Los grupos van de la A a la L y cada uno tiene 4 equipos.
 */
public class Grupo {

    private int    idGrupo;
    private String nombre;  // "A", "B", "C" ... "L"

    public Grupo() {}

    public Grupo(int idGrupo, String nombre) {
        this.idGrupo = idGrupo;
        this.nombre  = nombre;
    }

    public Grupo(String nombre) {
        this.nombre = nombre;
    }

    public int    getIdGrupo()             { return idGrupo; }
    public void   setIdGrupo(int idGrupo)  { this.idGrupo = idGrupo; }
    public String getNombre()              { return nombre; }
    public void   setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() { return "Grupo " + nombre; }
}