package controller;

import dao.*;
import model.*;
import util.SessionManager;
import java.util.List;

public class EstadioController {
    private final EstadioDAO estadioDAO = new EstadioDAO();

    public String insertar(Estadio e) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (e.getNombre().isBlank())      return "El nombre es obligatorio.";
        if (e.getIdCiudad() <= 0)         return "Selecciona una ciudad.";
        return estadioDAO.insertar(e) ? "Estadio guardado." : "Error al guardar.";
    }

    public String actualizar(Estadio e) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return estadioDAO.actualizar(e) ? "Estadio actualizado." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return estadioDAO.eliminar(id) ? "Estadio eliminado." : "Error al eliminar.";
    }

    public List<Estadio> listarTodos() { return estadioDAO.listarTodos(); }
    public Estadio buscarPorId(int id)  { return estadioDAO.buscarPorId(id); }
}
