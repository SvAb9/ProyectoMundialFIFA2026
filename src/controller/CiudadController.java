package controller;

import dao.*;
import model.*;
import util.SessionManager;
import java.util.List;

public class CiudadController {

    private final CiudadDAO ciudadDAO = new CiudadDAO();

    public String insertar(Ciudad c) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (c.getNombre().isBlank())      return "El nombre es obligatorio.";
        if (c.getPais().isBlank())        return "Selecciona un país.";
        return ciudadDAO.insertar(c) ? "Ciudad guardada." : "Error al guardar.";
    }

    public String actualizar(Ciudad c) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return ciudadDAO.actualizar(c) ? "Ciudad actualizada." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return ciudadDAO.eliminar(id) ? "Ciudad eliminada." : "Error al eliminar.";
    }

    public List<Ciudad> listarTodos() { return ciudadDAO.listarTodos(); }
    public Ciudad buscarPorId(int id)  { return ciudadDAO.buscarPorId(id); }
}
