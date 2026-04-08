package model;

import java.util.Date;

/**
 * Representa un partido de la fase de grupos.
 * Relaciona dos equipos, un grupo, un estadio, una fecha y una hora.
 */
public class Partido {

    private int    idPartido;
    private Date   fecha;
    private String hora;           // formato "HH:MM"
    private int    idGrupo;
    private int    idEstadio;
    private int    idEquipoLocal;
    private int    idEquipoVisitante;

    // Campos extra para mostrar en la vista sin hacer JOIN en Java
    private String nombreGrupo;
    private String nombreEstadio;
    private String nombreEquipoLocal;
    private String nombreEquipoVisitante;

    public Partido() {}

    public Partido(int idPartido, Date fecha, String hora, int idGrupo,
                   int idEstadio, int idEquipoLocal, int idEquipoVisitante) {
        this.idPartido          = idPartido;
        this.fecha              = fecha;
        this.hora               = hora;
        this.idGrupo            = idGrupo;
        this.idEstadio          = idEstadio;
        this.idEquipoLocal      = idEquipoLocal;
        this.idEquipoVisitante  = idEquipoVisitante;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Partido(Date fecha, String hora, int idGrupo,
                   int idEstadio, int idEquipoLocal, int idEquipoVisitante) {
        this.fecha              = fecha;
        this.hora               = hora;
        this.idGrupo            = idGrupo;
        this.idEstadio          = idEstadio;
        this.idEquipoLocal      = idEquipoLocal;
        this.idEquipoVisitante  = idEquipoVisitante;
    }

    public int    getIdPartido()                                  { return idPartido; }
    public void   setIdPartido(int idPartido)                     { this.idPartido = idPartido; }
    public Date   getFecha()                                      { return fecha; }
    public void   setFecha(Date fecha)                            { this.fecha = fecha; }
    public String getHora()                                       { return hora; }
    public void   setHora(String hora)                            { this.hora = hora; }
    public int    getIdGrupo()                                    { return idGrupo; }
    public void   setIdGrupo(int idGrupo)                         { this.idGrupo = idGrupo; }
    public int    getIdEstadio()                                  { return idEstadio; }
    public void   setIdEstadio(int idEstadio)                     { this.idEstadio = idEstadio; }
    public int    getIdEquipoLocal()                              { return idEquipoLocal; }
    public void   setIdEquipoLocal(int idEquipoLocal)             { this.idEquipoLocal = idEquipoLocal; }
    public int    getIdEquipoVisitante()                          { return idEquipoVisitante; }
    public void   setIdEquipoVisitante(int idEquipoVisitante)     { this.idEquipoVisitante = idEquipoVisitante; }
    public String getNombreGrupo()                                { return nombreGrupo; }
    public void   setNombreGrupo(String nombreGrupo)              { this.nombreGrupo = nombreGrupo; }
    public String getNombreEstadio()                              { return nombreEstadio; }
    public void   setNombreEstadio(String nombreEstadio)          { this.nombreEstadio = nombreEstadio; }
    public String getNombreEquipoLocal()                          { return nombreEquipoLocal; }
    public void   setNombreEquipoLocal(String nombre)             { this.nombreEquipoLocal = nombre; }
    public String getNombreEquipoVisitante()                      { return nombreEquipoVisitante; }
    public void   setNombreEquipoVisitante(String nombre)         { this.nombreEquipoVisitante = nombre; }

    @Override
    public String toString() {
        return nombreEquipoLocal + " vs " + nombreEquipoVisitante;
    }
}