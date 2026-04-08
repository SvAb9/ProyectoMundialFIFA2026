package controller;

import dao.*;
import model.*;
import util.SessionManager;
import java.util.List;

public class DirectorTecnicoController {
    private final DirectorTecnicoDAO dtDAO = new DirectorTecnicoDAO();

    public String insertar(DirectorTecnico dt) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (dt.getNombre().isBlank())     return "El nombre es obligatorio.";
        if (dt.getApellido().isBlank())   return "El apellido es obligatorio.";
        if (dt.getIdEquipo() <= 0)        return "Selecciona un equipo.";
        return dtDAO.insertar(dt) ? "Director técnico guardado." : "Error al guardar.";
    }

    public String actualizar(DirectorTecnico dt) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return dtDAO.actualizar(dt) ? "Director técnico actualizado." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return dtDAO.eliminar(id) ? "Director técnico eliminado." : "Error al eliminar.";
    }

    public List<DirectorTecnico> listarTodos() { return dtDAO.listarTodos(); }
    public DirectorTecnico buscarPorId(int id)  { return dtDAO.buscarPorId(id); }
}
