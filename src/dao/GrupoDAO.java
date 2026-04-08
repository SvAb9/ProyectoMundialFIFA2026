package dao;

import model.Grupo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GrupoDAO - Maneja todas las operaciones de la tabla GRUPO.
 * Los 12 grupos (A-L) de la fase de grupos del mundial.
 */
public class GrupoDAO {

    private final Connection conn;

    public GrupoDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    public boolean insertar(Grupo g) {
        String sql = "INSERT INTO grupo (nombre) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Grupo g) {
        String sql = "UPDATE grupo SET nombre = ? WHERE id_grupo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getNombre());
            ps.setInt   (2, g.getIdGrupo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM grupo WHERE id_grupo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar grupo: " + e.getMessage());
            return false;
        }
    }

    public List<Grupo> listarTodos() {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM grupo ORDER BY nombre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar grupos: " + e.getMessage());
        }
        return lista;
    }

    public Grupo buscarPorId(int id) {
        String sql = "SELECT * FROM grupo WHERE id_grupo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar grupo: " + e.getMessage());
        }
        return null;
    }

    // ── Agregar equipo a grupo (tabla intermedia equipo_grupo) ────────────
    public boolean agregarEquipo(int idEquipo, int idGrupo) {
        String sql = "INSERT INTO equipo_grupo (id_equipo, id_grupo) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idGrupo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar equipo al grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean quitarEquipo(int idEquipo, int idGrupo) {
        String sql = "DELETE FROM equipo_grupo WHERE id_equipo = ? AND id_grupo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idGrupo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al quitar equipo del grupo: " + e.getMessage());
            return false;
        }
    }

    private Grupo mapear(ResultSet rs) throws SQLException {
        return new Grupo(
                rs.getInt   ("id_grupo"),
                rs.getString("nombre")
        );
    }
}