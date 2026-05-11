package main;

import dao.ConexionDB;
import java.sql.Connection;

class TestConexion {
    public static void main(String[] args) {
        Connection conn = ConexionDB.getInstancia().getConexion();
        if (conn != null) {
            System.out.println("✅ Conexión exitosa a Oracle.");
        } else {
            System.out.println("❌ No se pudo conectar.");
        }
    }
}