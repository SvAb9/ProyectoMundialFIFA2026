package dao;

import model.Confederacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ConfederacionDAO - Maneja todas las operaciones de la tabla CONFEDERACION.
 * CRUD completo. Las confederaciones se usan mucho en combobox de la vista.
 */
public class ConfederacionDAO {

    private final Connection conn;

    public ConfederacionDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    // ── INSERTAR ─────────────────────────────────────────────────────────
    public boolean insertar(Confederacion c) {
        String sql = "INSERT INTO confederacion (nombre) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar confederación: " + e.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    public boolean actualizar(Confederacion c) {
        String sql = "UPDATE confederacion SET nombre = ? WHERE id_confederacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setInt   (2, c.getIdConfederacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar confederación: " + e.getMessage());
            return false;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "DELETE FROM confederacion WHERE id_confederacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar confederación: " + e.getMessage());
            return false;
        }
    }

    // ── LISTAR TODOS ─────────────────────────────────────────────────────
    public List<Confederacion> listarTodos() {
        List<Confederacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM confederacion ORDER BY nombre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar confederaciones: " + e.getMessage());
        }
        return lista;
    }

    // ── BUSCAR POR ID ────────────────────────────────────────────────────
    public Confederacion buscarPorId(int id) {
        String sql = "SELECT * FROM confederacion WHERE id_confederacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar confederación: " + e.getMessage());
        }
        return null;
    }

    // ── MAPEAR ResultSet → Confederacion ─────────────────────────────────
    private Confederacion mapear(ResultSet rs) throws SQLException {
        return new Confederacion(
                rs.getInt   ("id_confederacion"),
                rs.getString("nombre")
        );
    }
}