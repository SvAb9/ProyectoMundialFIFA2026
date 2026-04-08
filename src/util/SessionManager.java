package util;

import model.Usuario;

/**
 * SessionManager - Guarda el usuario que está logueado actualmente.
 * Es una clase estática, cualquier parte de la app puede consultarla
 * sin necesidad de pasar el usuario como parámetro de pantalla en pantalla.
 *
 * Uso:
 *   SessionManager.setUsuario(usuario);       // al hacer login
 *   SessionManager.getUsuario();              // para saber quién está logueado
 *   SessionManager.esAdmin();                 // para controlar permisos
 *   SessionManager.cerrarSesion();            // al salir
 */
public class SessionManager {

    private static Usuario usuarioActual;
    private static int     idBitacoraActual = -1;  // para registrar la salida

    // ── Guardar sesión al hacer login ────────────────────────────────────
    public static void setUsuario(Usuario u) {
        usuarioActual = u;
    }

    public static void setIdBitacora(int id) {
        idBitacoraActual = id;
    }

    // ── Consultar sesión desde cualquier pantalla ────────────────────────
    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static int getIdBitacora() {
        return idBitacoraActual;
    }

    public static String getRol() {
        return usuarioActual != null ? usuarioActual.getRol() : "";
    }

    // ── Verificadores de rol ─────────────────────────────────────────────
    public static boolean esAdmin() {
        return "ADMIN".equals(getRol());
    }

    public static boolean esTradicional() {
        return "TRADICIONAL".equals(getRol());
    }

    public static boolean esEsporadico() {
        return "ESPORADICO".equals(getRol());
    }

    /** Retorna true si el usuario puede hacer CRUD (admin o tradicional) */
    public static boolean puedeCRUD() {
        return esAdmin() || esTradicional();
    }

    // ── Limpiar sesión al salir ──────────────────────────────────────────
    public static void cerrarSesion() {
        usuarioActual    = null;
        idBitacoraActual = -1;
    }

    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
}