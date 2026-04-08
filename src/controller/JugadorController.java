package controller;

import dao.JugadorDAO;
import model.Jugador;
import util.SessionManager;
import java.util.List;

/**
 * JugadorController - Lógica de negocio para jugadores.
 */
public class JugadorController {

    private final JugadorDAO jugadorDAO;

    public JugadorController() {
        this.jugadorDAO = new JugadorDAO();
    }

    public String insertar(Jugador j) {
        if (!SessionManager.puedeCRUD())      return "Sin permisos para esta acción.";
        if (j.getNombre().isBlank())           return "El nombre es obligatorio.";
        if (j.getApellido().isBlank())         return "El apellido es obligatorio.";
        if (j.getFechaNacimiento() == null)    return "La fecha de nacimiento es obligatoria.";
        if (j.getIdEquipo() <= 0)              return "Selecciona un equipo.";
        return jugadorDAO.insertar(j) ? "Jugador guardado." : "Error al guardar el jugador.";
    }

    public String actualizar(Jugador j) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        if (j.getNombre().isBlank())      return "El nombre es obligatorio.";
        return jugadorDAO.actualizar(j) ? "Jugador actualizado." : "Error al actualizar.";
    }

    public String eliminar(int id) {
        if (!SessionManager.puedeCRUD()) return "Sin permisos para esta acción.";
        return jugadorDAO.eliminar(id) ? "Jugador eliminado." : "Error al eliminar.";
    }

    public List<Jugador> listarTodos() {
        return jugadorDAO.listarTodos();
    }

    public Jugador buscarPorId(int id) {
        return jugadorDAO.buscarPorId(id);
    }

    // ── Consultas especiales ─────────────────────────────────────────────
    public List<Jugador> jugadorMasCostosoPorConfederacion() {
        return jugadorDAO.jugadorMasCostosoPorConfederacion();
    }

    public List<String[]> jugadoresMenores21PorEquipo() {
        return jugadorDAO.jugadoresMenores21PorEquipo();
    }

    public List<Jugador> buscarPorFiltro(double pesoMin, double pesoMax,
                                         double estaturaMin, double estaturaMax,
                                         int idEquipo) {
        return jugadorDAO.buscarPorFiltro(pesoMin, pesoMax, estaturaMin, estaturaMax, idEquipo);
    }
}