package controller;

import dao.PartidoDAO;
import model.Partido;
import util.SessionManager;
import java.util.List;

/**
 * PartidoController - Lógica de negocio para partidos.
 */
public class PartidoController {

    private final PartidoDAO partidoDAO;

    public PartidoController() {
        this.partidoDAO = new PartidoDAO();
    }

    public String insertar(Partido p) {
        if (!SessionManager.puedeCRUD())  return "Sin permisos para esta acción.";
        if (p.getFecha() == null)          return "La fecha es obligatoria.";
        if (p.getHora().isBlank())         return "La hora es obligatoria.";
        if (p.getIdEstadio() <= 0)         return "Selecciona un estadio.";
        if (p.getIdEquipoLocal() <= 0 ||
                p.getIdEquipoVisitante() <= 0) return "Selecciona ambos equipos.";
        if (p.getIdEquipoLocal() ==
                p.getIdEquipoVisitante())      return "Los dos equipos no pueden ser el mismo.";
        return partidoDAO.insertar(p) ? "Partido guardado." : "Error al guardar el partido.";
    }

    public String actualizar(Partido p) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return partidoDAO.actualizar(p) ? "Partido actualizado." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return partidoDAO.eliminar(id) ? "Partido eliminado." : "Error al eliminar.";
    }

    public List<Partido> listarTodos() {
        return partidoDAO.listarTodos();
    }

    // ── Consultas especiales ─────────────────────────────────────────────
    public List<Partido> listarPorEstadio(int idEstadio) {
        return partidoDAO.listarPorEstadio(idEstadio);
    }

    public List<String[]> paisesPorSede() {
        return partidoDAO.paisesPorSede();
    }
}