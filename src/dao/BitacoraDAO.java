package dao;

import model.Bitacora;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BitacoraDAO - Maneja todas las operaciones de la tabla BITACORA.
 * Registra entrada y salida de usuarios automáticamente.
 */
public class BitacoraDAO {

    private final Connection conn;

    public BitacoraDAO() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    // ── REGISTRAR ENTRADA ────────────────────────────────────────────────
    /**
     * Se llama justo después de que el login es exitoso.
     * Retorna el id del registro creado (lo necesitamos para luego cerrar la salida).
     */
    public int registrarEntrada(int idUsuario) {
        String sql = "INSERT INTO bitacora (id_usuario, fecha_entrada) VALUES (?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                new String[]{"id_bitacora"})) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al registrar entrada: " + e.getMessage());
        }
        return -1;
    }

    // ── REGISTRAR SALIDA ─────────────────────────────────────────────────
    /**
     * Se llama cuando el usuario cierra la aplicación o hace logout.
     * Actualiza el registro de entrada con la fecha de salida.
     */
    public boolean registrarSalida(int idBitacora) {
        String sql = "UPDATE bitacora SET fecha_salida = CURRENT_TIMESTAMP WHERE id_bitacora = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBitacora);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar salida: " + e.getMessage());
            return false;
        }
    }

    // ── LISTAR POR RANGO DE FECHAS (para el reporte) ─────────────────────
    /**
     * Consulta usada en el reporte: usuarios que ingresaron y salieron
     * en una fecha y hora específica.
     */
    public List<Bitacora> listarPorFecha(Timestamp desde, Timestamp hasta) {
        List<Bitacora> lista = new ArrayList<>();
        String sql = """
                SELECT b.*, u.username
                FROM bitacora b
                JOIN usuario u ON b.id_usuario = u.id_usuario
                WHERE b.fecha_entrada BETWEEN ? AND ?
                ORDER BY b.fecha_entrada
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, desde);
            ps.setTimestamp(2, hasta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bitacora b = mapear(rs);
                b.setUsernameUsuario(rs.getString("username"));
                lista.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar bitácora: " + e.getMessage());
        }
        return lista;
    }

    // ── MAPEAR ResultSet → Bitacora ──────────────────────────────────────
    private Bitacora mapear(ResultSet rs) throws SQLException {
        Bitacora b = new Bitacora();
        b.setIdBitacora(rs.getInt("id_bitacora"));
        b.setIdUsuario (rs.getInt("id_usuario"));
        b.setFechaEntrada(rs.getTimestamp("fecha_entrada"));
        b.setFechaSalida (rs.getTimestamp("fecha_salida"));
        return b;
    }
}