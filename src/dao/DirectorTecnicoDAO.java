package dao;

import model.DirectorTecnico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DirectorTecnicoDAO - Maneja todas las operaciones de la tabla DIRECTOR_TECNICO.
 */
public class DirectorTecnicoDAO {

    private final Connection conn;

    public DirectorTecnicoDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    public boolean insertar(DirectorTecnico dt) {
        String sql = "INSERT INTO director_tecnico (nombre, apellido, nacionalidad, id_equipo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dt.getNombre());
            ps.setString(2, dt.getApellido());
            ps.setString(3, dt.getNacionalidad());
            ps.setInt   (4, dt.getIdEquipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar director técnico: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(DirectorTecnico dt) {
        String sql = "UPDATE director_tecnico SET nombre = ?, apellido = ?, nacionalidad = ?, id_equipo = ? WHERE id_director = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dt.getNombre());
            ps.setString(2, dt.getApellido());
            ps.setString(3, dt.getNacionalidad());
            ps.setInt   (4, dt.getIdEquipo());
            ps.setInt   (5, dt.getIdDirector());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar director técnico: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM director_tecnico WHERE id_director = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar director técnico: " + e.getMessage());
            return false;
        }
    }

    public List<DirectorTecnico> listarTodos() {
        List<DirectorTecnico> lista = new ArrayList<>();
        String sql = """
                SELECT dt.*, e.nombre AS nombre_equipo
                FROM director_tecnico dt
                JOIN equipo e ON dt.id_equipo = e.id_equipo
                ORDER BY dt.apellido, dt.nombre
                """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                DirectorTecnico dt = mapear(rs);
                dt.setNombreEquipo(rs.getString("nombre_equipo"));
                lista.add(dt);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar directores técnicos: " + e.getMessage());
        }
        return lista;
    }

    public DirectorTecnico buscarPorId(int id) {
        String sql = "SELECT * FROM director_tecnico WHERE id_director = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar director técnico: " + e.getMessage());
        }
        return null;
    }

    private DirectorTecnico mapear(ResultSet rs) throws SQLException {
        return new DirectorTecnico(
                rs.getInt   ("id_director"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("nacionalidad"),
                rs.getInt   ("id_equipo")
        );
    }
}