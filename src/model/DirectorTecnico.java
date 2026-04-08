package model;

/**
 * Representa el director técnico (entrenador) de un equipo.
 * Cada equipo tiene exactamente un director técnico.
 */
public class DirectorTecnico {

    private int    idDirector;
    private String nombre;
    private String apellido;
    private String nacionalidad;
    private int    idEquipo;

    // Nombre del equipo (para mostrar en la vista sin hacer JOIN en Java)
    private String nombreEquipo;

    public DirectorTecnico() {}

    public DirectorTecnico(int idDirector, String nombre, String apellido,
                           String nacionalidad, int idEquipo) {
        this.idDirector   = idDirector;
        this.nombre       = nombre;
        this.apellido     = apellido;
        this.nacionalidad = nacionalidad;
        this.idEquipo     = idEquipo;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public DirectorTecnico(String nombre, String apellido,
                           String nacionalidad, int idEquipo) {
        this.nombre       = nombre;
        this.apellido     = apellido;
        this.nacionalidad = nacionalidad;
        this.idEquipo     = idEquipo;
    }

    public int    getIdDirector()                      { return idDirector; }
    public void   setIdDirector(int idDirector)        { this.idDirector = idDirector; }
    public String getNombre()                          { return nombre; }
    public void   setNombre(String nombre)             { this.nombre = nombre; }
    public String getApellido()                        { return apellido; }
    public void   setApellido(String apellido)         { this.apellido = apellido; }
    public String getNacionalidad()                    { return nacionalidad; }
    public void   setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public int    getIdEquipo()                        { return idEquipo; }
    public void   setIdEquipo(int idEquipo)            { this.idEquipo = idEquipo; }
    public String getNombreEquipo()                    { return nombreEquipo; }
    public void   setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}