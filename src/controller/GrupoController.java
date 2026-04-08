package controller;

import dao.*;
import model.*;
import util.SessionManager;
import java.util.List;

public class GrupoController {

        private final GrupoDAO grupoDAO = new GrupoDAO();

        public String insertar(Grupo g) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (g.getNombre().isBlank())      return "El nombre del grupo es obligatorio.";
        return grupoDAO.insertar(g) ? "Grupo guardado." : "Error al guardar.";
    }

        public String actualizar(Grupo g) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return grupoDAO.actualizar(g) ? "Grupo actualizado." : "Error al actualizar.";
    }

        public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return grupoDAO.eliminar(id) ? "Grupo eliminado." : "Error al eliminar.";
    }

        public String agregarEquipo(int idEquipo, int idGrupo) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return grupoDAO.agregarEquipo(idEquipo, idGrupo)
                ? "Equipo agregado al grupo." : "Error al agregar equipo.";
    }

        public List<Grupo> listarTodos() { return grupoDAO.listarTodos(); }
        public Grupo buscarPorId(int id)  { return grupoDAO.buscarPorId(id); }
}

