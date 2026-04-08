package dao;

import model.Ciudad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CiudadDAO - Maneja todas las operaciones de la tabla CIUDAD.
 */
public class CiudadDAO {

    private final Connection conn;

    public CiudadDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    public boolean insertar(Ciudad c) {
        String sql = "INSERT INTO ciudad (nombre, pais) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getPais());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar ciudad: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Ciudad c) {
        String sql = "UPDATE ciudad SET nombre = ?, pais = ? WHERE id_ciudad = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getPais());
            ps.setInt   (3, c.getIdCiudad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar ciudad: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM ciudad WHERE id_ciudad = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar ciudad: " + e.getMessage());
            return false;
        }
    }

    public List<Ciudad> listarTodos() {
        List<Ciudad> lista = new ArrayList<>();
        String sql = "SELECT * FROM ciudad ORDER BY pais, nombre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar ciudades: " + e.getMessage());
        }
        return lista;
    }

    public Ciudad buscarPorId(int id) {
        String sql = "SELECT * FROM ciudad WHERE id_ciudad = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar ciudad: " + e.getMessage());
        }
        return null;
    }

    private Ciudad mapear(ResultSet rs) throws SQLException {
        return new Ciudad(
                rs.getInt   ("id_ciudad"),
                rs.getString("nombre"),
                rs.getString("pais")
        );
    }
}