package view.crud;

import controller.JugadorController;
import dao.EquipoDAO;
import dao.PosicionDAO;
import model.Equipo;
import model.Jugador;
import model.Posicion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class JugadorPanel extends JPanel {

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

    private JTable            tabla;
    private DefaultTableModel modelo;

    // Campos del formulario
    private JTextField        txtNombre, txtApellido, txtFecha;
    private JTextField        txtPeso, txtEstatura, txtValor;
    private JComboBox<Posicion> cmbPosicion;
    private final PosicionDAO posicionDAO = new PosicionDAO();
    private JComboBox<Equipo> cmbEquipo;
    private JLabel            lblMensaje;
    private JButton           btnGuardar, btnEliminar, btnNuevo;

    private final JugadorController controller = new JugadorController();
    private final EquipoDAO         equipoDAO  = new EquipoDAO();
    private int idSeleccionado = -1;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public JugadorPanel() {
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
        JLabel t = new JLabel("Jugadores");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(TEXT_PRI);
        JLabel s = new JLabel("Gestión de jugadores del mundial");
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
                new String[]{"ID", "Nombre", "Apellido", "Posición", "Equipo", "Valor (€)"}, 0) {
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

        JLabel titulo = new JLabel("Datos del jugador");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        txtNombre   = makeTextField();
        txtApellido = makeTextField();
        txtFecha    = makeTextField();  // formato dd/MM/yyyy

        cmbPosicion = new JComboBox<>();
        cmbPosicion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbPosicion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbPosicion.setAlignmentX(LEFT_ALIGNMENT);
        cargarPosiciones();

        txtPeso     = makeTextField();
        txtEstatura = makeTextField();
        txtValor    = makeTextField();

        cmbEquipo = new JComboBox<>();
        cmbEquipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEquipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbEquipo.setAlignmentX(LEFT_ALIGNMENT);
        for (Equipo e : equipoDAO.listarTodos()) cmbEquipo.addItem(e);

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

        // Scroll para el formulario porque tiene muchos campos
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.add(titulo);
        inner.add(Box.createVerticalStrut(16));
        inner.add(makeLabel("Nombre"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtNombre);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Apellido"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtApellido);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Fecha nacimiento (dd/MM/yyyy)"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtFecha);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Posición"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(cmbPosicion);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Peso (kg)"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtPeso);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Estatura (m)"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtEstatura);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Valor (€)"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(txtValor);
        inner.add(Box.createVerticalStrut(10));
        inner.add(makeLabel("Equipo"));
        inner.add(Box.createVerticalStrut(5));
        inner.add(cmbEquipo);
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

    private void cargarPosiciones() {
        cmbPosicion.removeAllItems();
        java.util.List<Posicion> posiciones = posicionDAO.listarTodos();
        if (posiciones.isEmpty()) {
            // Fallback: posiciones base para que el combo nunca quede vacio
            cmbPosicion.addItem(new Posicion(1, "Portero",        "Guarda la porteria"));
            cmbPosicion.addItem(new Posicion(2, "Defensa",        "Defiende el area propia"));
            cmbPosicion.addItem(new Posicion(3, "Centrocampista", "Controla el medio del campo"));
            cmbPosicion.addItem(new Posicion(4, "Delantero",      "Ataca y genera goles"));
            System.err.println("[JugadorPanel] AVISO: posicionDAO devolvio lista vacia. " +
                    "Verifique la conexion a BD. Se usaron posiciones por defecto.");
        } else {
            for (Posicion p : posiciones) cmbPosicion.addItem(p);
        }
        if (cmbPosicion.getItemCount() > 0) cmbPosicion.setSelectedIndex(0);
    }

    private void guardar() {
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        Equipo equipo   = (Equipo) cmbEquipo.getSelectedItem();

        if (nombre.isEmpty())   { mostrarMensaje("El nombre es obligatorio.", true);    return; }
        if (apellido.isEmpty()) { mostrarMensaje("El apellido es obligatorio.", true);  return; }
        if (fechaStr.isEmpty()) { mostrarMensaje("La fecha es obligatoria (dd/MM/yyyy).", true); return; }
        if (equipo == null)     { mostrarMensaje("Selecciona un equipo.", true);        return; }

        java.util.Date fecha;
        try {
            SDF.setLenient(false);
            fecha = SDF.parse(fechaStr);
        } catch (ParseException e) {
            mostrarMensaje("Fecha inválida. Usa dd/MM/yyyy", true); return;
        }

        double peso = 0, estatura = 0, valor = 0;
        try {
            if (!txtPeso.getText().isBlank())     peso     = Double.parseDouble(txtPeso.getText().trim());
            if (!txtEstatura.getText().isBlank()) estatura = Double.parseDouble(txtEstatura.getText().trim());
            if (!txtValor.getText().isBlank())    valor    = Double.parseDouble(txtValor.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Peso, estatura y valor deben ser números.", true); return;
        }

        Posicion posicion = (Posicion) cmbPosicion.getSelectedItem();

        String resultado;
        if (idSeleccionado == -1) {
            // Constructor sin ID: (nombre, apellido, fecha, idPosicion, peso, estatura, valor, idEquipo)
            resultado = controller.insertar(new Jugador(
                    nombre, apellido, fecha,
                    posicion.getIdPosicion(), peso, estatura, valor, equipo.getIdEquipo()));
        } else {
            // Constructor con ID: (idJugador, nombre, apellido, fecha, posicion, peso, estatura, valor, idEquipo)
            resultado = controller.actualizar(new Jugador(
                    idSeleccionado, nombre, apellido, fecha, posicion, peso, estatura, valor, equipo.getIdEquipo()));
        }
        boolean esError = resultado.startsWith("Error") || resultado.startsWith("Sin");
        mostrarMensaje(resultado, esError);
        if (esError) {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo guardar el jugador", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cargarTabla();
        limpiarFormulario();
    }

    private void eliminar() {
        if (idSeleccionado == -1) return;
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar este jugador?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        String resultado = controller.eliminar(idSeleccionado);
        boolean esErrorElim = resultado.startsWith("Error") || resultado.startsWith("Sin");
        mostrarMensaje(resultado, esErrorElim);
        if (esErrorElim) {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo eliminar el jugador", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cargarTabla();
        limpiarFormulario();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Jugador j : controller.listarTodos())
            modelo.addRow(new Object[]{
                    j.getIdJugador(), j.getNombre(), j.getApellido(),
                    j.getIdPosicion(), j.getNombreEquipo(),
                    String.format("%.2f", j.getValor())
            });
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        Jugador j = controller.buscarPorId(idSeleccionado);
        if (j == null) return;
        txtNombre.setText(j.getNombre());
        txtApellido.setText(j.getApellido());
        txtFecha.setText(j.getFechaNacimiento() != null ? SDF.format(j.getFechaNacimiento()) : "");
        txtPeso.setText(String.valueOf(j.getPeso()));
        txtEstatura.setText(String.valueOf(j.getEstatura()));
        txtValor.setText(String.valueOf(j.getValor()));
        cmbPosicion.setSelectedItem(j.getIdPosicion());
        for (int i = 0; i < cmbEquipo.getItemCount(); i++) {
            if (cmbEquipo.getItemAt(i).getIdEquipo() == j.getIdEquipo()) {
                cmbEquipo.setSelectedIndex(i); break;
            }
        }
        btnEliminar.setEnabled(true);
        lblMensaje.setText(" ");
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText(""); txtApellido.setText("");
        txtFecha.setText(""); txtPeso.setText("");
        txtEstatura.setText(""); txtValor.setText("");
        cmbPosicion.setSelectedIndex(0);
        if (cmbEquipo.getItemCount() > 0) cmbEquipo.setSelectedIndex(0);
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