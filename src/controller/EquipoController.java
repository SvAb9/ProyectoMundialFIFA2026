package controller;

import dao.EquipoDAO;
import model.Equipo;
import util.SessionManager;
import java.util.List;

/**
 * EquipoController - Lógica de negocio para equipos.
 * Valida permisos y datos antes de llamar al DAO.
 */
public class EquipoController {

    private final EquipoDAO equipoDAO;

    public EquipoController() {
        this.equipoDAO = new EquipoDAO();
    }

    public String insertar(Equipo e) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (e.getNombre().isBlank())      return "El nombre del equipo es obligatorio.";
        if (e.getIdConfederacion() <= 0)  return "Selecciona una confederación.";
        return equipoDAO.insertar(e) ? "Equipo guardado." : "Error al guardar el equipo.";
    }

    public String actualizar(Equipo e) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (e.getNombre().isBlank())      return "El nombre del equipo es obligatorio.";
        return equipoDAO.actualizar(e) ? "Equipo actualizado." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return equipoDAO.eliminar(id) ? "Equipo eliminado." : "Error al eliminar.";
    }

    public List<Equipo> listarTodos() {
        return equipoDAO.listarTodos();
    }

    public Equipo buscarPorId(int id) {
        return equipoDAO.buscarPorId(id);
    }

    // ── Consulta especial ────────────────────────────────────────────────
    public Equipo equipoMasCostosoPorPais(String pais) {
        return equipoDAO.equipoMasCostosoPorPais(pais);
    }

    public List<Equipo> valorTotalPorConfederacion(int idConfederacion) {
        return equipoDAO.valorTotalPorConfederacion(idConfederacion);
    }
}