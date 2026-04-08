package dao;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UsuarioDAO - Maneja todas las operaciones de la tabla USUARIO.
 * Login, crear usuarios (solo admin), listar, activar/desactivar.
 */
public class UsuarioDAO {

    // Conexión compartida desde el singleton
    private final Connection conn;

    public UsuarioDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    // ── LOGIN ────────────────────────────────────────────────────────────
    /**
     * Verifica las credenciales del usuario.
     * Retorna el Usuario si existe y está activo, null si no.
     */
    public Usuario login(String username, String password) {
        String sql = "SELECT * FROM usuario WHERE username = ? AND password = ? AND activo = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    // ── INSERTAR ─────────────────────────────────────────────────────────
    /**
     * Crea un nuevo usuario. Solo lo llama el AdminController.
     */
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (username, password, rol) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRol());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET username = ?, password = ?, rol = ?, activo = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRol());
            ps.setInt   (4, u.isActivo() ? 1 : 0);
            ps.setInt   (5, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────────
    /**
     * En vez de borrar físicamente, desactiva el usuario (activo = 0).
     */
    public boolean desactivar(int idUsuario) {
        String sql = "UPDATE usuario SET activo = 0 WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // ── LISTAR TODOS ─────────────────────────────────────────────────────
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY username";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // ── BUSCAR POR ID ────────────────────────────────────────────────────
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    // ── MAPEAR ResultSet → Usuario ───────────────────────────────────────
    /**
     * Convierte una fila del ResultSet en un objeto Usuario.
     * Se reutiliza en todos los métodos de este DAO.
     */
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt   ("id_usuario"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("rol"),
                rs.getInt   ("activo") == 1
        );
    }
}