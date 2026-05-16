package dao;

import model.Jugador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {

    private final Connection conn;

    public JugadorDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    // ── INSERTAR ─────────────────────────────────────────────────────────
    public boolean insertar(Jugador j) {
        String sql = """
                INSERT INTO jugador (nombre, apellido, fecha_nacimiento,
                    id_posicion, peso, estatura, valor, id_equipo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getApellido());
            ps.setDate  (3, new java.sql.Date(j.getFechaNacimiento().getTime()));
            ps.setInt(4, j.getIdPosicion());
            ps.setDouble(5, j.getPeso());
            ps.setDouble(6, j.getEstatura());
            ps.setDouble(7, j.getValor());
            ps.setInt   (8, j.getIdEquipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar jugador: " + e.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    public boolean actualizar(Jugador j) {
        String sql = """
                UPDATE jugador SET nombre = ?, apellido = ?, fecha_nacimiento = ?,
                    id_posicion = ?, peso = ?, estatura = ?, valor = ?, id_equipo = ?
                WHERE id_jugador = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getApellido());
            ps.setDate  (3, new java.sql.Date(j.getFechaNacimiento().getTime()));
            ps.setInt   (4, j.getIdPosicion());
            ps.setDouble(5, j.getPeso());
            ps.setDouble(6, j.getEstatura());
            ps.setDouble(7, j.getValor());
            ps.setInt   (8, j.getIdEquipo());
            ps.setInt   (9, j.getIdJugador());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar jugador: " + e.getMessage());
            return false;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "DELETE FROM jugador WHERE id_jugador = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar jugador: " + e.getMessage());
            return false;
        }
    }

    // ── LISTAR TODOS ─────────────────────────────────────────────────────
    public List<Jugador> listarTodos() {
        List<Jugador> lista = new ArrayList<>();
        String sql = """
            SELECT j.*, e.nombre AS nombre_equipo,
                c.nombre AS nombre_confederacion,
                p.nombre AS nombre_posicion
            FROM jugador j
            JOIN equipo e ON j.id_equipo = e.id_equipo
            JOIN confederacion c ON e.id_confederacion = c.id_confederacion
            LEFT JOIN posicion p ON j.id_posicion = p.id_posicion
            ORDER BY j.apellido, j.nombre
            """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Jugador jug = mapear(rs);
                jug.setNombreEquipo        (rs.getString("nombre_equipo"));
                jug.setNombreConfederacion (rs.getString("nombre_confederacion"));
                jug.setNombrePosicion      (rs.getString("nombre_posicion"));
                lista.add(jug);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar jugadores: " + e.getMessage());
        }

        return lista;
    }

    // ── BUSCAR POR ID ────────────────────────────────────────────────────
    public Jugador buscarPorId(int id) {
        String sql = """
                SELECT j.*, e.nombre AS nombre_equipo,
                       p.nombre AS nombre_posicion
                FROM jugador j
                JOIN equipo   e ON j.id_equipo  = e.id_equipo
                JOIN posicion p ON j.id_posicion = p.id_posicion
                WHERE j.id_jugador = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Jugador jug = mapear(rs);
                jug.setNombreEquipo   (rs.getString("nombre_equipo"));
                jug.setNombrePosicion (rs.getString("nombre_posicion"));
                return jug;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar jugador: " + e.getMessage());
        }
        return null;
    }

    // ── CONSULTA: jugador más costoso por confederación ──────────────────
    public List<Jugador> jugadorMasCostosoPorConfederacion() {
        List<Jugador> lista = new ArrayList<>();
        String sql = """
                SELECT j.*, e.nombre AS nombre_equipo,
                       c.nombre AS nombre_confederacion,
                       p.nombre AS nombre_posicion
                FROM jugador j
                JOIN equipo        e ON j.id_equipo        = e.id_equipo
                JOIN confederacion c ON e.id_confederacion  = c.id_confederacion
                JOIN posicion      p ON j.id_posicion       = p.id_posicion
                WHERE j.valor = (
                    SELECT MAX(j2.valor)
                    FROM jugador j2
                    JOIN equipo e2 ON j2.id_equipo = e2.id_equipo
                    WHERE e2.id_confederacion = e.id_confederacion
                )
                ORDER BY c.nombre
                """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Jugador jug = mapear(rs);
                jug.setNombreEquipo       (rs.getString("nombre_equipo"));
                jug.setNombreConfederacion(rs.getString("nombre_confederacion"));
                jug.setNombrePosicion     (rs.getString("nombre_posicion"));
                lista.add(jug);
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta jugador más costoso: " + e.getMessage());
        }
        return lista;
    }

    // ── CONSULTA: jugadores menores de 21 por equipo ─────────────────────
    // La edad se calcula al 11/06/2026 (fecha de inicio del mundial)
    public List<String[]> jugadoresMenores21PorEquipo() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
                SELECT e.nombre AS equipo,
                       COUNT(j.id_jugador) AS cantidad,
                       LISTAGG(j.nombre || ' ' || j.apellido, ', ')
                           WITHIN GROUP (ORDER BY j.apellido) AS jugadores
                FROM jugador j
                JOIN equipo e ON j.id_equipo = e.id_equipo
                WHERE FLOOR(MONTHS_BETWEEN(
                          TO_DATE('11/06/2026', 'DD/MM/YYYY'),
                          j.fecha_nacimiento) / 12) < 21
                GROUP BY e.nombre
                ORDER BY cantidad DESC
                """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                        rs.getString("equipo"),
                        rs.getString("cantidad"),
                        rs.getString("jugadores")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta menores 21: " + e.getMessage());
        }
        return lista;
    }

    // ── REPORTE: jugadores por peso, estatura y equipo ───────────────────
    public List<Jugador> buscarPorFiltro(double pesoMin, double pesoMax,
                                         double estaturaMin, double estaturaMax,
                                         int idEquipo) {
        List<Jugador> lista = new ArrayList<>();
        String sql = """
                SELECT j.*, e.nombre AS nombre_equipo,
                       c.nombre AS nombre_confederacion,
                       p.nombre AS nombre_posicion
                FROM jugador j
                JOIN equipo e ON j.id_equipo = e.id_equipo
                JOIN confederacion c ON e.id_confederacion = c.id_confederacion
                LEFT JOIN posicion p ON j.id_posicion = p.id_posicion
                WHERE j.peso BETWEEN ? AND ?
                  AND j.estatura BETWEEN ? AND ?
                  AND (? = 0 OR j.id_equipo = ?)
                ORDER BY j.apellido
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, pesoMin);
            ps.setDouble(2, pesoMax);
            ps.setDouble(3, estaturaMin);
            ps.setDouble(4, estaturaMax);
            ps.setInt   (5, idEquipo);
            ps.setInt   (6, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Jugador jug = mapear(rs);
                jug.setNombreEquipo       (rs.getString("nombre_equipo"));
                jug.setNombreConfederacion(rs.getString("nombre_confederacion"));
                jug.setNombrePosicion     (rs.getString("nombre_posicion"));
                lista.add(jug);
            }
        } catch (SQLException e) {
            System.err.println("Error en reporte jugadores por filtro: " + e.getMessage());
        }
        return lista;
    }

    // ── MAPEAR ResultSet → Jugador ────────────────────────────────────────
    private Jugador mapear(ResultSet rs) throws SQLException {
        Jugador j = new Jugador();
        j.setIdJugador      (rs.getInt   ("id_jugador"));
        j.setNombre         (rs.getString("nombre"));
        j.setApellido       (rs.getString("apellido"));
        j.setFechaNacimiento(rs.getDate  ("fecha_nacimiento"));
        j.setIdPosicion     (rs.getInt   ("id_posicion"));
        j.setPeso           (rs.getDouble("peso"));
        j.setEstatura       (rs.getDouble("estatura"));
        j.setValor          (rs.getDouble("valor"));
        j.setIdEquipo       (rs.getInt   ("id_equipo"));

        return j;
    }
}