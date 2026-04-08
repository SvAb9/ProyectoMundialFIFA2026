package dao;

import model.Estadio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EstadioDAO - Maneja todas las operaciones de la tabla ESTADIO.
 * Los estadios se usan en la consulta de partidos por estadio.
 */
public class EstadioDAO {

    private final Connection conn;

    public EstadioDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    public boolean insertar(Estadio e) {
        String sql = "INSERT INTO estadio (nombre, capacidad, id_ciudad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setInt   (2, e.getCapacidad());
            ps.setInt   (3, e.getIdCiudad());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al insertar estadio: " + ex.getMessage());
            return false;
        }
    }

    public boolean actualizar(Estadio e) {
        String sql = "UPDATE estadio SET nombre = ?, capacidad = ?, id_ciudad = ? WHERE id_estadio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setInt   (2, e.getCapacidad());
            ps.setInt   (3, e.getIdCiudad());
            ps.setInt   (4, e.getIdEstadio());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar estadio: " + ex.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM estadio WHERE id_estadio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar estadio: " + e.getMessage());
            return false;
        }
    }

    public List<Estadio> listarTodos() {
        List<Estadio> lista = new ArrayList<>();
        String sql = """
                SELECT es.*, c.nombre AS nombre_ciudad, c.pais AS pais_ciudad
                FROM estadio es
                JOIN ciudad c ON es.id_ciudad = c.id_ciudad
                ORDER BY es.nombre
                """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Estadio es = mapear(rs);
                es.setNombreCiudad(rs.getString("nombre_ciudad"));
                es.setPaisCiudad  (rs.getString("pais_ciudad"));
                lista.add(es);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar estadios: " + e.getMessage());
        }
        return lista;
    }

    public Estadio buscarPorId(int id) {
        String sql = "SELECT * FROM estadio WHERE id_estadio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar estadio: " + e.getMessage());
        }
        return null;
    }

    private Estadio mapear(ResultSet rs) throws SQLException {
        return new Estadio(
                rs.getInt   ("id_estadio"),
                rs.getString("nombre"),
                rs.getInt   ("capacidad"),
                rs.getInt   ("id_ciudad")
        );
    }
}