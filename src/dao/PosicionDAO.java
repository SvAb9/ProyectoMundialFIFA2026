package dao;

import model.Posicion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PosicionDAO {
    private final Connection conn;

    public PosicionDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    public List<Posicion> listarTodos() {
        List<Posicion> lista = new ArrayList<>();
        String sql = "SELECT * FROM posicion ORDER BY nombre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar posiciones: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Posicion p) {
        String sql = "INSERT INTO posicion (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar posición: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Posicion p) {
        String sql = "UPDATE posicion SET nombre=?, descripcion=? WHERE id_posicion=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setInt   (3, p.getIdPosicion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar posición: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM posicion WHERE id_posicion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar posición: " + e.getMessage());
            return false;
        }
    }

    public Posicion buscarPorId(int id) {
        String sql = "SELECT * FROM posicion WHERE id_posicion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar posición: " + e.getMessage());
        }
        return null;
    }

    private Posicion mapear(ResultSet rs) throws SQLException {
        return new Posicion(
            rs.getInt   ("id_posicion"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        );
    }
}