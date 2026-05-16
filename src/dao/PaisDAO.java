package dao;

import model.Pais;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PaisDAO - Maneja todas las operaciones de la tabla PAIS.
 * CRUD completo. Los países se usan en combobox al crear/editar ciudades.
 */
public class PaisDAO {

    private final Connection conn;

    public PaisDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    // ── INSERTAR ─────────────────────────────────────────────────────────
    public boolean insertar(Pais p) {
        String sql = "INSERT INTO pais (nombre, codigo_iso) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCodigoIso());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar país: " + e.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    public boolean actualizar(Pais p) {
        String sql = "UPDATE pais SET nombre = ?, codigo_iso = ? WHERE id_pais = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCodigoIso());
            ps.setInt   (3, p.getIdPais());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar país: " + e.getMessage());
            return false;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "DELETE FROM pais WHERE id_pais = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar país: " + e.getMessage());
            return false;
        }
    }

    // ── LISTAR TODOS ─────────────────────────────────────────────────────
    public List<Pais> listarTodos() {
        List<Pais> lista = new ArrayList<>();
        String sql = "SELECT * FROM pais ORDER BY nombre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar países: " + e.getMessage());
        }
        return lista;
    }

    // ── BUSCAR POR ID ────────────────────────────────────────────────────
    public Pais buscarPorId(int id) {
        String sql = "SELECT * FROM pais WHERE id_pais = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar país: " + e.getMessage());
        }
        return null;
    }

    // ── MAPEAR ResultSet → Pais ──────────────────────────────────────────
    private Pais mapear(ResultSet rs) throws SQLException {
        return new Pais(
                rs.getInt   ("id_pais"),
                rs.getString("nombre"),
                rs.getString("codigo_iso")
        );
    }
}