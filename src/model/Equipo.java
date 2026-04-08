package model;

/**
 * Representa un equipo (selección nacional) que participa en el mundial.
 * Cada equipo pertenece a una confederación y tiene un valor total
 * que es la suma del valor de todos sus jugadores.
 */
public class Equipo {

    private int    idEquipo;
    private String nombre;
    private double valorTotal;
    private int    idConfederacion;

    // Nombre de la confederación (para mostrar en la vista sin hacer JOIN en Java)
    private String nombreConfederacion;

    public Equipo() {}

    public Equipo(int idEquipo, String nombre, double valorTotal, int idConfederacion) {
        this.idEquipo        = idEquipo;
        this.nombre          = nombre;
        this.valorTotal      = valorTotal;
        this.idConfederacion = idConfederacion;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Equipo(String nombre, double valorTotal, int idConfederacion) {
        this.nombre          = nombre;
        this.valorTotal      = valorTotal;
        this.idConfederacion = idConfederacion;
    }

    public int    getIdEquipo()                          { return idEquipo; }
    public void   setIdEquipo(int idEquipo)              { this.idEquipo = idEquipo; }
    public String getNombre()                            { return nombre; }
    public void   setNombre(String nombre)               { this.nombre = nombre; }
    public double getValorTotal()                        { return valorTotal; }
    public void   setValorTotal(double valorTotal)       { this.valorTotal = valorTotal; }
    public int    getIdConfederacion()                   { return idConfederacion; }
    public void   setIdConfederacion(int id)             { this.idConfederacion = id; }
    public String getNombreConfederacion()               { return nombreConfederacion; }
    public void   setNombreConfederacion(String nombre)  { this.nombreConfederacion = nombre; }

    @Override
    public String toString() { return nombre; }
}