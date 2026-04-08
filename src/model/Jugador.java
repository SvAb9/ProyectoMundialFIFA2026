package model;

import java.util.Date;

/**
 * Representa un jugador que participa en el mundial.
 * Tiene atributos físicos (peso, estatura) y económicos (valor)
 * que son necesarios para las consultas y reportes del enunciado.
 */
public class Jugador {

    private int    idJugador;
    private String nombre;
    private String apellido;
    private Date   fechaNacimiento;
    private String posicion;
    private double peso;       // en kg
    private double estatura;   // en metros
    private double valor;      // valor en euros
    private int    idEquipo;

    // Campos extra para mostrar en la vista sin hacer JOIN en Java
    private String nombreEquipo;
    private String nombreConfederacion;

    public Jugador() {}

    public Jugador(int idJugador, String nombre, String apellido, Date fechaNacimiento,
                   String posicion, double peso, double estatura, double valor, int idEquipo) {
        this.idJugador       = idJugador;
        this.nombre          = nombre;
        this.apellido        = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.posicion        = posicion;
        this.peso            = peso;
        this.estatura        = estatura;
        this.valor           = valor;
        this.idEquipo        = idEquipo;
    }

    // Constructor sin ID (para insertar nuevo registro)
    public Jugador(String nombre, String apellido, Date fechaNacimiento,
                   String posicion, double peso, double estatura, double valor, int idEquipo) {
        this.nombre          = nombre;
        this.apellido        = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.posicion        = posicion;
        this.peso            = peso;
        this.estatura        = estatura;
        this.valor           = valor;
        this.idEquipo        = idEquipo;
    }

    public int    getIdJugador()                             { return idJugador; }
    public void   setIdJugador(int idJugador)                { this.idJugador = idJugador; }
    public String getNombre()                                { return nombre; }
    public void   setNombre(String nombre)                   { this.nombre = nombre; }
    public String getApellido()                              { return apellido; }
    public void   setApellido(String apellido)               { this.apellido = apellido; }
    public Date   getFechaNacimiento()                       { return fechaNacimiento; }
    public void   setFechaNacimiento(Date fechaNacimiento)   { this.fechaNacimiento = fechaNacimiento; }
    public String getPosicion()                              { return posicion; }
    public void   setPosicion(String posicion)               { this.posicion = posicion; }
    public double getPeso()                                  { return peso; }
    public void   setPeso(double peso)                       { this.peso = peso; }
    public double getEstatura()                              { return estatura; }
    public void   setEstatura(double estatura)               { this.estatura = estatura; }
    public double getValor()                                 { return valor; }
    public void   setValor(double valor)                     { this.valor = valor; }
    public int    getIdEquipo()                              { return idEquipo; }
    public void   setIdEquipo(int idEquipo)                  { this.idEquipo = idEquipo; }
    public String getNombreEquipo()                          { return nombreEquipo; }
    public void   setNombreEquipo(String nombreEquipo)       { this.nombreEquipo = nombreEquipo; }
    public String getNombreConfederacion()                   { return nombreConfederacion; }
    public void   setNombreConfederacion(String nombre)      { this.nombreConfederacion = nombre; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}