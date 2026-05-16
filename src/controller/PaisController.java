package controller;

import dao.PaisDAO;
import model.Pais;
import util.SessionManager;
import java.util.List;

public class PaisController {

    private final PaisDAO paisDAO = new PaisDAO();

    public String insertar(Pais p) {
        if (!SessionManager.puedeCRUD())   return "Sin permisos para esta acción.";
        if (p.getNombre().isBlank())        return "El nombre es obligatorio.";
        if (p.getCodigoIso().isBlank())     return "El código ISO es obligatorio.";
        if (p.getCodigoIso().length() > 3)  return "El código ISO debe tener máximo 3 caracteres.";
        return paisDAO.insertar(p) ? "País guardado." : "Error al guardar.";
    }

    public String actualizar(Pais p) {
        if (!SessionManager.puedeCRUD())   return "Sin permisos para esta acción.";
        if (p.getNombre().isBlank())        return "El nombre es obligatorio.";
        if (p.getCodigoIso().isBlank())     return "El código ISO es obligatorio.";
        return paisDAO.actualizar(p) ? "País actualizado." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return paisDAO.eliminar(id) ? "País eliminado." : "Error al eliminar (puede tener ciudades asociadas).";
    }

    public List<Pais> listarTodos() { return paisDAO.listarTodos(); }
    public Pais buscarPorId(int id)  { return paisDAO.buscarPorId(id); }
}