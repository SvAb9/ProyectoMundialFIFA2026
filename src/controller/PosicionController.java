package controller;

import dao.PosicionDAO;
import model.Posicion;
import util.SessionManager;
import java.util.List;

public class PosicionController {
    private final PosicionDAO posicionDAO = new PosicionDAO();

    public String insertar(Posicion p) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos.";
        if (p.getNombre().isBlank())     return "El nombre es obligatorio.";
        return posicionDAO.insertar(p) ? "Posición guardada." : "Error al guardar.";
    }

    public String actualizar(Posicion p) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos.";
        return posicionDAO.actualizar(p) ? "Posición actualizada." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos.";
        return posicionDAO.eliminar(id) ? "Posición eliminada." : "Error al eliminar.";
    }

    public List<Posicion> listarTodos() { return posicionDAO.listarTodos(); }
    public Posicion buscarPorId(int id)  { return posicionDAO.buscarPorId(id); }
}