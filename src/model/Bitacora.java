package model;

import java.util.Date;

/**
 * Representa un registro de la bitácora del sistema.
 * Cada vez que un usuario entra o sale de la app se guarda un registro aquí.
 * La fecha_salida puede ser null si el usuario todavía está dentro del sistema.
 */
public class Bitacora {

    private int    idBitacora;
    private int    idUsuario;
    private Date   fechaEntrada;
    private Date   fechaSalida;   // null mientras el usuario esté activo

    // Campo extra para mostrar en la vista sin hacer JOIN en Java
    private String usernameUsuario;

    public Bitacora() {}

    public Bitacora(int idBitacora, int idUsuario, Date fechaEntrada, Date fechaSalida) {
        this.idBitacora   = idBitacora;
        this.idUsuario    = idUsuario;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida  = fechaSalida;
    }

    // Constructor para registrar entrada (sin fecha de salida aún)
    public Bitacora(int idUsuario, Date fechaEntrada) {
        this.idUsuario    = idUsuario;
        this.fechaEntrada = fechaEntrada;
    }

    public int    getIdBitacora()                        { return idBitacora; }
    public void   setIdBitacora(int idBitacora)          { this.idBitacora = idBitacora; }
    public int    getIdUsuario()                         { return idUsuario; }
    public void   setIdUsuario(int idUsuario)            { this.idUsuario = idUsuario; }
    public Date   getFechaEntrada()                      { return fechaEntrada; }
    public void   setFechaEntrada(Date fechaEntrada)     { this.fechaEntrada = fechaEntrada; }
    public Date   getFechaSalida()                       { return fechaSalida; }
    public void   setFechaSalida(Date fechaSalida)       { this.fechaSalida = fechaSalida; }
    public String getUsernameUsuario()                   { return usernameUsuario; }
    public void   setUsernameUsuario(String username)    { this.usernameUsuario = username; }

    @Override
    public String toString() { return usernameUsuario + " — " + fechaEntrada; }
}