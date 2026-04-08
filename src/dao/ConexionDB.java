package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConexionDB - Singleton para la conexión a Oracle
 * Solo existe una conexión activa en toda la app.
 */
public class ConexionDB {

    // ── Datos de conexión ────────────────────────────────────────────────
    private static final String URL      = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USUARIO  = "mundial";
    private static final String PASSWORD = "mundial123";

    // ── Instancia única ──────────────────────────────────────────────────
    private static ConexionDB instancia;
    private Connection conexion;

    // ── Constructor privado (nadie puede hacer new ConexionDB()) ─────────
    private ConexionDB() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión exitosa a Oracle.");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC no encontrado. Verifica que ojdbc11.jar está en lib/");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar con Oracle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Obtener la instancia única ───────────────────────────────────────
    public static ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    // ── Obtener la conexión para usarla en los DAO ───────────────────────
    public Connection getConexion() {
        try {
            // Si la conexión se cerró o perdió, reconecta
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar la conexión: " + e.getMessage());
        }
        return conexion;
    }

    // ── Cerrar la conexión al salir de la app ────────────────────────────
    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
