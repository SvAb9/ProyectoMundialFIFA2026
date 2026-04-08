package controller;

import dao.UsuarioDAO;
import model.Usuario;
import util.SessionManager;
import java.util.List;

/**
 * UsuarioController - Maneja la gestión de usuarios.
 * Solo el ADMIN puede crear, editar y desactivar usuarios.
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // ── CREAR ────────────────────────────────────────────────────────────
    /**
     * Solo el admin puede crear usuarios.
     * Retorna un mensaje de resultado para mostrar en la vista.
     */
    public String crear(String username, String password, String rol) {
        if (!SessionManager.esAdmin()) {
            return "Sin permisos para crear usuarios.";
        }
        if (username.isBlank() || password.isBlank() || rol.isBlank()) {
            return "Todos los campos son obligatorios.";
        }
        if (username.length() < 4) {
            return "El usuario debe tener al menos 4 caracteres.";
        }
        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres.";
        }

        Usuario nuevo = new Usuario(username.trim(), password, rol);
        boolean ok = usuarioDAO.insertar(nuevo);
        return ok ? "Usuario creado correctamente." : "Error al crear el usuario.";
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    public String actualizar(Usuario u) {
        if (!SessionManager.esAdmin()) {
            return "Sin permisos para editar usuarios.";
        }
        boolean ok = usuarioDAO.actualizar(u);
        return ok ? "Usuario actualizado." : "Error al actualizar el usuario.";
    }

    // ── DESACTIVAR ───────────────────────────────────────────────────────
    public String desactivar(int idUsuario) {
        if (!SessionManager.esAdmin()) {
            return "Sin permisos para desactivar usuarios.";
        }
        // No se puede desactivar al propio admin logueado
        if (idUsuario == SessionManager.getUsuario().getIdUsuario()) {
            return "No puedes desactivar tu propia cuenta.";
        }
        boolean ok = usuarioDAO.desactivar(idUsuario);
        return ok ? "Usuario desactivado." : "Error al desactivar el usuario.";
    }

    // ── LISTAR ───────────────────────────────────────────────────────────
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }
}