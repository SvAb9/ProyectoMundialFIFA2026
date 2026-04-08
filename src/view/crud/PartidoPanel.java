package view.crud;

import controller.PartidoController;
import dao.EquipoDAO;
import dao.EstadioDAO;
import dao.GrupoDAO;
import model.Equipo;
import model.Estadio;
import model.Grupo;
import model.Partido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * PartidoPanel - CRUD de partidos de la fase de grupos.
 */
public class PartidoPanel extends JPanel {

    private static final Color BG       = new Color(0xF5F5F2);
    private static final Color CARD     = Color.WHITE;
    private static final Color ACCENT   = new Color(0x185FA5);
    private static final Color ACCENT_H = new Color(0x0C447C);
    private static final Color DANGER   = new Color(0xA32D2D);
    private static final Color DANGER_H = new Color(0x7A1F1F);
    private static final Color TEXT_PRI = new Color(0x1A1A18);
    private static final Color TEXT_SEC = new Color(0x6B6B67);
    private static final Color BORDER_N = new Color(0xD3D1C7);
    private static final Color BORDER_F = new Color(0x185FA5);
    private static final Color TH_BG    = new Color(0xF1EFE8);
    private static final Color SEL_BG   = new Color(0xE6F1FB);

    private JTable              tabla;
    private DefaultTableModel   modelo;

    private JTextField          txtFecha, txtHora;
    private JComboBox<Grupo>    cmbGrupo;
    private JComboBox<Estadio>  cmbEstadio;
    private JComboBox<Equipo>   cmbLocal, cmbVisitante;
    private JLabel              lblMensaje;
    private JButton             btnGuardar, btnEliminar, btnNuevo;

    private final PartidoController controller = new PartidoController();
    private final GrupoDAO          grupoDAO   = new GrupoDAO();
    private final EstadioDAO        estadioDAO = new EstadioDAO();
    private final EquipoDAO         equipoDAO  = new EquipoDAO();
    private int idSeleccionado = -1;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public PartidoPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buildHeader(),     BorderLayout.NORTH);
        add(buildTabla(),      BorderLayout.CENTER);
        add(buildFormulario(), BorderLayout.EAST);
        cargarTabla();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JLabel t = new JLabel("Partidos");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(TEXT_PRI);
        JLabel s = new JLabel("Partidos de la fase de grupos");
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        s.setForeground(TEXT_SEC);
        JPanel tp = new JPanel();
        tp.setLayout(new BoxLayout(tp, BoxLayout.Y_AXIS));
        tp.setBackground(BG);
        tp.add(t); tp.add(s);
        p.add(tp, BorderLayout.WEST);
        return p;
    }

    private JScrollPane buildTabla() {
        modelo = new DefaultTableModel(
                new String[]{"ID", "Fecha", "Hora", "Grupo", "Estadio", "Local", "Visitante"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo) {
            @Override public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row) ? SEL_BG : (row % 2 == 0 ? CARD : new Color(0xFAFAF8)));
                c.setForeground(TEXT_PRI);
                return c;
            }
        };
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(36);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFocusable(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(TH_BG);
        tabla.getTableHeader().setForeground(TEXT_SEC);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarEnFormulario();
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_N, 1, true));
        scroll.getViewport().setBackground(CARD);
        return scroll;
    }

    private JPanel buildFormulario() {
        JPanel form = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setPreferredSize(new Dimension(290, 0));

        JLabel titulo = new JLabel("Datos del partido");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        txtFecha = makeTextField();
        txtHora  = makeTextField();  // HH:MM

        cmbGrupo = new JComboBox<>();
        cmbGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbGrupo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbGrupo.setAlignmentX(LEFT_ALIGNMENT);
        for (Grupo g : grupoDAO.listarTodos()) cmbGrupo.addItem(g);

        cmbEstadio = new JComboBox<>();
        cmbEstadio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEstadio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbEstadio.setAlignmentX(LEFT_ALIGNMENT);
        for (Estadio e : estadioDAO.listarTodos()) cmbEstadio.addItem(e);

        cmbLocal = new JComboBox<>();
        cmbVisitante = new JComboBox<>();
        cmbLocal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbVisitante.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbLocal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbVisitante.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbLocal.setAlignmentX(LEFT_ALIGNMENT);
        cmbVisitante.setAlignmentX(LEFT_ALIGNMENT);
        for (Equipo e : equipoDAO.listarTodos()) {
            cmbLocal.addItem(e);
            cmbVisitante.addItem(e);
        }

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMensaje.setAlignmentX(LEFT_ALIGNMENT);

        btnGuardar  = makeButton("Guardar",  ACCENT,  ACCENT_H);
        btnEliminar = makeButton("Eliminar", DANGER,  DANGER_H);
        btnNuevo    = makeButton("Nuevo",    new Color(0x4B5563), new Color(0x374151));

        btnGuardar.addActionListener (e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnNuevo.addActionListener   (e -> limpiarFormulario());
        btnEliminar.setEnabled(false);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.add(titulo);
        inner.add(Box.createVerticalStrut(16));
        inner.add(makeLabel("Fecha (dd/MM/yyyy)"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtFecha);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Hora (HH:MM)"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtHora);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Grupo"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(cmbGrupo);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Estadio"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(cmbEstadio);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Equipo local"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(cmbLocal);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Equipo visitante"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(cmbVisitante);
        inner.add(Box.createVerticalStrut(12));
        inner.add(lblMensaje);
        inner.add(Box.createVerticalStrut(10));
        inner.add(btnGuardar);
        inner.add(Box.createVerticalStrut(6));
        inner.add(btnEliminar);
        inner.add(Box.createVerticalStrut(6));
        inner.add(btnNuevo);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        form.add(scroll);
        return form;
    }

    private void guardar() {
        String fechaStr = txtFecha.getText().trim();
        String hora     = txtHora.getText().trim();
        Grupo   grupo     = (Grupo)   cmbGrupo.getSelectedItem();
        Estadio estadio   = (Estadio) cmbEstadio.getSelectedItem();
        Equipo  local     = (Equipo)  cmbLocal.getSelectedItem();
        Equipo  visitante = (Equipo)  cmbVisitante.getSelectedItem();

        if (fechaStr.isEmpty()) { mostrarMensaje("La fecha es obligatoria.", true); return; }
        if (hora.isEmpty())     { mostrarMensaje("La hora es obligatoria (HH:MM).", true); return; }
        if (grupo == null)      { mostrarMensaje("Selecciona un grupo.", true); return; }
        if (estadio == null)    { mostrarMensaje("Selecciona un estadio.", true); return; }
        if (local == null || visitante == null) { mostrarMensaje("Selecciona los equipos.", true); return; }

        java.util.Date fecha;
        try {
            SDF.setLenient(false);
            fecha = SDF.parse(fechaStr);
        } catch (ParseException e) {
            mostrarMensaje("Fecha inválida. Usa dd/MM/yyyy", true); return;
        }

        String resultado;
        if (idSeleccionado == -1) {
            resultado = controller.insertar(new Partido(
                    fecha, hora, grupo.getIdGrupo(), estadio.getIdEstadio(),
                    local.getIdEquipo(), visitante.getIdEquipo()));
        } else {
            resultado = controller.actualizar(new Partido(
                    idSeleccionado, fecha, hora, grupo.getIdGrupo(), estadio.getIdEstadio(),
                    local.getIdEquipo(), visitante.getIdEquipo()));
        }
        mostrarMensaje(resultado, resultado.startsWith("Error") || resultado.startsWith("Sin"));
        cargarTabla(); limpiarFormulario();
    }

    private void eliminar() {
        if (idSeleccionado == -1) return;
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar este partido?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        String resultado = controller.eliminar(idSeleccionado);
        mostrarMensaje(resultado, resultado.startsWith("Error") || resultado.startsWith("Sin"));
        cargarTabla(); limpiarFormulario();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Partido p : controller.listarTodos())
            modelo.addRow(new Object[]{
                    p.getIdPartido(),
                    p.getFecha() != null ? SDF.format(p.getFecha()) : "",
                    p.getHora(),
                    "Grupo " + p.getNombreGrupo(),
                    p.getNombreEstadio(),
                    p.getNombreEquipoLocal(),
                    p.getNombreEquipoVisitante()
            });
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        Partido p = null;
        for (Partido pa : controller.listarTodos()) {
            if (pa.getIdPartido() == idSeleccionado) { p = pa; break; }
        }
        if (p == null) return;
        txtFecha.setText(p.getFecha() != null ? SDF.format(p.getFecha()) : "");
        txtHora.setText(p.getHora());
        for (int i = 0; i < cmbGrupo.getItemCount(); i++) {
            if (cmbGrupo.getItemAt(i).getIdGrupo() == p.getIdGrupo()) { cmbGrupo.setSelectedIndex(i); break; }
        }
        for (int i = 0; i < cmbEstadio.getItemCount(); i++) {
            if (cmbEstadio.getItemAt(i).getIdEstadio() == p.getIdEstadio()) { cmbEstadio.setSelectedIndex(i); break; }
        }
        for (int i = 0; i < cmbLocal.getItemCount(); i++) {
            if (cmbLocal.getItemAt(i).getIdEquipo() == p.getIdEquipoLocal()) { cmbLocal.setSelectedIndex(i); break; }
        }
        for (int i = 0; i < cmbVisitante.getItemCount(); i++) {
            if (cmbVisitante.getItemAt(i).getIdEquipo() == p.getIdEquipoVisitante()) { cmbVisitante.setSelectedIndex(i); break; }
        }
        btnEliminar.setEnabled(true);
        lblMensaje.setText(" ");
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtFecha.setText(""); txtHora.setText("");
        if (cmbGrupo.getItemCount()    > 0) cmbGrupo.setSelectedIndex(0);
        if (cmbEstadio.getItemCount()  > 0) cmbEstadio.setSelectedIndex(0);
        if (cmbLocal.getItemCount()    > 0) cmbLocal.setSelectedIndex(0);
        if (cmbVisitante.getItemCount()> 0) cmbVisitante.setSelectedIndex(0);
        btnEliminar.setEnabled(false);
        lblMensaje.setText(" ");
        tabla.clearSelection();
    }

    private void mostrarMensaje(String msg, boolean esError) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(esError ? DANGER : new Color(0x0F6E56));
    }

    private JLabel makeLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SEC);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField() {
        JTextField tf = new JTextField() {
            private boolean focused = false;
            { addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { focused = true;  repaint(); }
                public void focusLost (FocusEvent e)  { focused = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(focused ? BORDER_F : BORDER_N);
                g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(8, 10, 8, 10));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setAlignmentX(LEFT_ALIGNMENT);
        return tf;
    }

    private JButton makeButton(String texto, Color bg, Color hover) {
        JButton btn = new JButton(texto) {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(!isEnabled() ? new Color(0xD3D1C7) : (hovered ? hover : bg));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        return btn;
    }
}