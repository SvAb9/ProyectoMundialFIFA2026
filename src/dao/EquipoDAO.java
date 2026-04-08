package dao;

import model.Equipo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EquipoDAO - Maneja todas las operaciones de la tabla EQUIPO.
 * Incluye la consulta especial: equipo más costoso por país anfitrión.
 */
public class EquipoDAO {

    private final Connection conn;

    public EquipoDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    // ── INSERTAR ─────────────────────────────────────────────────────────
    public boolean insertar(Equipo e) {
        String sql = "INSERT INTO equipo (nombre, valor_total, id_confederacion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setDouble(2, e.getValorTotal());
            ps.setInt   (3, e.getIdConfederacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al insertar equipo: " + ex.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    public boolean actualizar(Equipo e) {
        String sql = "UPDATE equipo SET nombre = ?, valor_total = ?, id_confederacion = ? WHERE id_equipo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setDouble(2, e.getValorTotal());
            ps.setInt   (3, e.getIdConfederacion());
            ps.setInt   (4, e.getIdEquipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar equipo: " + ex.getMessage());
            return false;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "DELETE FROM equipo WHERE id_equipo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar equipo: " + ex.getMessage());
            return false;
        }
    }

    // ── LISTAR TODOS ─────────────────────────────────────────────────────
    public List<Equipo> listarTodos() {
        List<Equipo> lista = new ArrayList<>();
        String sql = """
                SELECT e.*, c.nombre AS nombre_confederacion
                FROM equipo e
                JOIN confederacion c ON e.id_confederacion = c.id_confederacion
                ORDER BY e.nombre
                """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Equipo eq = mapear(rs);
                eq.setNombreConfederacion(rs.getString("nombre_confederacion"));
                lista.add(eq);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
        }
        return lista;
    }

    // ── BUSCAR POR ID ────────────────────────────────────────────────────
    public Equipo buscarPorId(int id) {
        String sql = "SELECT * FROM equipo WHERE id_equipo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar equipo: " + e.getMessage());
        }
        return null;
    }

    // ── CONSULTA: equipo más costoso por país anfitrión ──────────────────
    /**
     * Requerimiento del enunciado:
     * "Determinar el equipo más costoso de los que van a jugar
     *  en cada país (México, USA, Canadá) en la fase de grupos."
     *
     * Busca el equipo con mayor valor_total que juega en el país indicado.
     */
    public Equipo equipoMasCostosoPorPais(String pais) {
        String sql = """
                SELECT e.*, c.nombre AS nombre_confederacion
                FROM equipo e
                JOIN confederacion c ON e.id_confederacion = c.id_confederacion
                WHERE e.id_equipo IN (
                    SELECT DISTINCT p.id_equipo_local FROM partido p
                    JOIN estadio es ON p.id_estadio = es.id_estadio
                    JOIN ciudad ci ON es.id_ciudad = ci.id_ciudad
                    WHERE ci.pais = ?
                    UNION
                    SELECT DISTINCT p.id_equipo_visitante FROM partido p
                    JOIN estadio es ON p.id_estadio = es.id_estadio
                    JOIN ciudad ci ON es.id_ciudad = ci.id_ciudad
                    WHERE ci.pais = ?
                )
                AND e.valor_total = (
                    SELECT MAX(e2.valor_total)
                    FROM equipo e2
                    WHERE e2.id_equipo IN (
                        SELECT DISTINCT p2.id_equipo_local FROM partido p2
                        JOIN estadio es2 ON p2.id_estadio = es2.id_estadio
                        JOIN ciudad ci2 ON es2.id_ciudad = ci2.id_ciudad
                        WHERE ci2.pais = ?
                        UNION
                        SELECT DISTINCT p2.id_equipo_visitante FROM partido p2
                        JOIN estadio es2 ON p2.id_estadio = es2.id_estadio
                        JOIN ciudad ci2 ON es2.id_ciudad = ci2.id_ciudad
                        WHERE ci2.pais = ?
                    )
                )
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pais);
            ps.setString(2, pais);
            ps.setString(3, pais);
            ps.setString(4, pais);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Equipo eq = mapear(rs);
                eq.setNombreConfederacion(rs.getString("nombre_confederacion"));
                return eq;
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta equipo más costoso: " + e.getMessage());
        }
        return null;
    }

    // ── REPORTE: valor total por equipo de una confederación ─────────────
    /**
     * Requerimiento del enunciado:
     * "Determinar el valor total de los jugadores por equipo
     *  que pertenecen a una confederación específica."
     */
    public List<Equipo> valorTotalPorConfederacion(int idConfederacion) {
        List<Equipo> lista = new ArrayList<>();
        String sql = """
                SELECT e.*, c.nombre AS nombre_confederacion
                FROM equipo e
                JOIN confederacion c ON e.id_confederacion = c.id_confederacion
                WHERE e.id_confederacion = ?
                ORDER BY e.valor_total DESC
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConfederacion);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Equipo eq = mapear(rs);
                eq.setNombreConfederacion(rs.getString("nombre_confederacion"));
                lista.add(eq);
            }
        } catch (SQLException e) {
            System.err.println("Error en reporte valor por confederación: " + e.getMessage());
        }
        return lista;
    }

    // ── MAPEAR ResultSet → Equipo ─────────────────────────────────────────
    private Equipo mapear(ResultSet rs) throws SQLException {
        return new Equipo(
                rs.getInt   ("id_equipo"),
                rs.getString("nombre"),
                rs.getDouble("valor_total"),
                rs.getInt   ("id_confederacion")
        );
    }
}