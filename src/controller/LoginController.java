package controller;

import dao.BitacoraDAO;
import dao.UsuarioDAO;
import model.Usuario;
import util.SessionManager;

/**
 * LoginController - Maneja la lógica del login y logout.
 * La vista LoginFrame llama a este controller, nunca al DAO directamente.
 */
public class LoginController {

    private final UsuarioDAO  usuarioDAO;
    private final BitacoraDAO bitacoraDAO;

    public LoginController() {
        this.usuarioDAO  = new UsuarioDAO();
        this.bitacoraDAO = new BitacoraDAO();
    }

    // ── LOGIN ────────────────────────────────────────────────────────────
    /**
     * Intenta iniciar sesión con las credenciales dadas.
     * Si es exitoso guarda la sesión y registra la entrada en bitácora.
     *
     * @return El usuario logueado, o null si las credenciales son incorrectas.
     */
    public Usuario login(String username, String password) {
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            return null;
        }

        Usuario usuario = usuarioDAO.login(username.trim(), password);

        if (usuario != null) {
            // Guardar sesión
            SessionManager.setUsuario(usuario);

            // Registrar entrada en bitácora
            int idBitacora = bitacoraDAO.registrarEntrada(usuario.getIdUsuario());
            SessionManager.setIdBitacora(idBitacora);
        }

        return usuario;
    }

    // ── LOGOUT ───────────────────────────────────────────────────────────
    /**
     * Cierra la sesión del usuario actual.
     * Registra la salida en bitácora y limpia la sesión.
     */
    public void logout() {
        int idBitacora = SessionManager.getIdBitacora();
        if (idBitacora != -1) {
            bitacoraDAO.registrarSalida(idBitacora);
        }
        SessionManager.cerrarSesion();
    }
}