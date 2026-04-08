package dao;

import model.Partido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PartidoDAO - Maneja todas las operaciones de la tabla PARTIDO.
 * Incluye la consulta especial: partidos por estadio.
 */
public class PartidoDAO {

    private final Connection conn;

    public PartidoDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    // ── INSERTAR ─────────────────────────────────────────────────────────
    public boolean insertar(Partido p) {
        String sql = """
                INSERT INTO partido (fecha, hora, id_grupo, id_estadio,
                    id_equipo_local, id_equipo_visitante)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(p.getFecha().getTime()));
            ps.setString(2, p.getHora());
            ps.setInt   (3, p.getIdGrupo());
            ps.setInt   (4, p.getIdEstadio());
            ps.setInt   (5, p.getIdEquipoLocal());
            ps.setInt   (6, p.getIdEquipoVisitante());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar partido: " + e.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    public boolean actualizar(Partido p) {
        String sql = """
                UPDATE partido SET fecha = ?, hora = ?, id_grupo = ?,
                    id_estadio = ?, id_equipo_local = ?, id_equipo_visitante = ?
                WHERE id_partido = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(p.getFecha().getTime()));
            ps.setString(2, p.getHora());
            ps.setInt   (3, p.getIdGrupo());
            ps.setInt   (4, p.getIdEstadio());
            ps.setInt   (5, p.getIdEquipoLocal());
            ps.setInt   (6, p.getIdEquipoVisitante());
            ps.setInt   (7, p.getIdPartido());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar partido: " + e.getMessage());
            return false;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "DELETE FROM partido WHERE id_partido = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar partido: " + e.getMessage());
            return false;
        }
    }

    // ── LISTAR TODOS ─────────────────────────────────────────────────────
    public List<Partido> listarTodos() {
        List<Partido> lista = new ArrayList<>();
        String sql = """
                SELECT p.*,
                       g.nombre  AS nombre_grupo,
                       es.nombre AS nombre_estadio,
                       el.nombre AS nombre_local,
                       ev.nombre AS nombre_visitante
                FROM partido p
                JOIN grupo   g  ON p.id_grupo            = g.id_grupo
                JOIN estadio es ON p.id_estadio           = es.id_estadio
                JOIN equipo  el ON p.id_equipo_local      = el.id_equipo
                JOIN equipo  ev ON p.id_equipo_visitante  = ev.id_equipo
                ORDER BY p.fecha, p.hora
                """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Partido pa = mapear(rs);
                pa.setNombreGrupo          (rs.getString("nombre_grupo"));
                pa.setNombreEstadio        (rs.getString("nombre_estadio"));
                pa.setNombreEquipoLocal    (rs.getString("nombre_local"));
                pa.setNombreEquipoVisitante(rs.getString("nombre_visitante"));
                lista.add(pa);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar partidos: " + e.getMessage());
        }
        return lista;
    }

    // ── CONSULTA: partidos por estadio ───────────────────────────────────
    /**
     * Requerimiento del enunciado:
     * "Listar los partidos que se llevarán a cabo en un estadio
     *  cualquiera que el usuario elija."
     */
    public List<Partido> listarPorEstadio(int idEstadio) {
        List<Partido> lista = new ArrayList<>();
        String sql = """
                SELECT p.*,
                       g.nombre  AS nombre_grupo,
                       es.nombre AS nombre_estadio,
                       el.nombre AS nombre_local,
                       ev.nombre AS nombre_visitante
                FROM partido p
                JOIN grupo   g  ON p.id_grupo            = g.id_grupo
                JOIN estadio es ON p.id_estadio           = es.id_estadio
                JOIN equipo  el ON p.id_equipo_local      = el.id_equipo
                JOIN equipo  ev ON p.id_equipo_visitante  = ev.id_equipo
                WHERE p.id_estadio = ?
                ORDER BY p.fecha, p.hora
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstadio);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Partido pa = mapear(rs);
                pa.setNombreGrupo          (rs.getString("nombre_grupo"));
                pa.setNombreEstadio        (rs.getString("nombre_estadio"));
                pa.setNombreEquipoLocal    (rs.getString("nombre_local"));
                pa.setNombreEquipoVisitante(rs.getString("nombre_visitante"));
                lista.add(pa);
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta partidos por estadio: " + e.getMessage());
        }
        return lista;
    }

    // ── REPORTE: países por sede ──────────────────────────────────────────
    /**
     * Requerimiento del enunciado:
     * "Listar los países que se jugarán en cada país anfitrión."
     * Retorna filas con: país anfitrión, equipo local, equipo visitante.
     */
    public List<String[]> paisesPorSede() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
                SELECT ci.pais,
                       el.nombre AS equipo_local,
                       ev.nombre AS equipo_visitante
                FROM partido p
                JOIN estadio es ON p.id_estadio          = es.id_estadio
                JOIN ciudad  ci ON es.id_ciudad           = ci.id_ciudad
                JOIN equipo  el ON p.id_equipo_local      = el.id_equipo
                JOIN equipo  ev ON p.id_equipo_visitante  = ev.id_equipo
                ORDER BY ci.pais, el.nombre
                """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                        rs.getString("pais"),
                        rs.getString("equipo_local"),
                        rs.getString("equipo_visitante")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error en reporte países por sede: " + e.getMessage());
        }
        return lista;
    }

    // ── MAPEAR ResultSet → Partido ────────────────────────────────────────
    private Partido mapear(ResultSet rs) throws SQLException {
        Partido p = new Partido();
        p.setIdPartido        (rs.getInt   ("id_partido"));
        p.setFecha            (rs.getDate  ("fecha"));
        p.setHora             (rs.getString("hora"));
        p.setIdGrupo          (rs.getInt   ("id_grupo"));
        p.setIdEstadio        (rs.getInt   ("id_estadio"));
        p.setIdEquipoLocal    (rs.getInt   ("id_equipo_local"));
        p.setIdEquipoVisitante(rs.getInt   ("id_equipo_visitante"));
        return p;
    }
}