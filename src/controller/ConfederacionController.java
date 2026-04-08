package controller;

import dao.*;
import model.*;
import util.SessionManager;
import java.util.List;

public class ConfederacionController {

        private final ConfederacionDAO confDAO = new ConfederacionDAO();

        public String insertar(Confederacion c) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (c.getNombre().isBlank())      return "El nombre es obligatorio.";
        return confDAO.insertar(c) ? "Confederación guardada." : "Error al guardar.";
    }

        public String actualizar(Confederacion c) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return confDAO.actualizar(c) ? "Confederación actualizada." : "Error al actualizar.";
    }

        public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return confDAO.eliminar(id) ? "Confederación eliminada." : "Error al eliminar.";
    }

        public List<Confederacion> listarTodos() { return confDAO.listarTodos(); }
        public Confederacion buscarPorId(int id)  { return confDAO.buscarPorId(id); }
}
