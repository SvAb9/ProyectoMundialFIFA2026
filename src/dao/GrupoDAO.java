package dao;

import model.Grupo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    /** Retorna los equipos que pertenecen a un grupo: nombre equipo + confederacion */
    public List<String[]> listarEquiposPorGrupo(int idGrupo) {
        List<String[]> lista = new ArrayList<>();
        String sql = """
                SELECT e.nombre AS equipo, c.nombre AS confederacion
                FROM equipo_grupo eg
                JOIN equipo e        ON eg.id_equipo        = e.id_equipo
                JOIN confederacion c ON e.id_confederacion  = c.id_confederacion
                WHERE eg.id_grupo = ?
                ORDER BY e.nombre
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("equipo"),
                    rs.getString("confederacion")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos del grupo: " + e.getMessage());
        }
        return lista;
    }

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