package view.crud;

import controller.EquipoController;
import dao.ConfederacionDAO;
import model.Confederacion;
import model.Equipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * EquipoPanel - CRUD completo para equipos.
 * Estructura: tabla a la izquierda, formulario a la derecha.
 * Sirve de plantilla para los demás paneles CRUD.
 */
public class EquipoPanel extends JPanel {

    // ── Colores ──────────────────────────────────────────────────────────
    private static final Color BG        = new Color(0xF5F5F2);
    private static final Color CARD      = Color.WHITE;
    private static final Color ACCENT    = new Color(0x185FA5);
    private static final Color ACCENT_H  = new Color(0x0C447C);
    private static final Color DANGER    = new Color(0xA32D2D);
    private static final Color DANGER_H  = new Color(0x7A1F1F);
    private static final Color TEXT_PRI  = new Color(0x1A1A18);
    private static final Color TEXT_SEC  = new Color(0x6B6B67);
    private static final Color BORDER_N  = new Color(0xD3D1C7);
    private static final Color BORDER_F  = new Color(0x185FA5);
    private static final Color TH_BG     = new Color(0xF1EFE8);
    private static final Color SEL_BG    = new Color(0xE6F1FB);

    // ── Componentes ──────────────────────────────────────────────────────
    private JTable              tabla;
    private DefaultTableModel   modelo;
    private JTextField          txtNombre;
    private JTextField          txtValor;
    private JComboBox<Confederacion> cmbConf;
    private JLabel              lblMensaje;
    private JButton             btnGuardar;
    private JButton             btnEliminar;
    private JButton             btnNuevo;

    private final EquipoController  controller  = new EquipoController();
    private final ConfederacionDAO  confDAO     = new ConfederacionDAO();
    private int idSeleccionado = -1;   // -1 = modo insertar, >0 = modo editar

    public EquipoPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildTabla(),     BorderLayout.CENTER);
        add(buildFormulario(), BorderLayout.EAST);

        cargarTabla();
    }

    // ── Header ────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);

        JLabel titulo = new JLabel("Equipos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT_PRI);

        JLabel sub = new JLabel("Gestión de equipos participantes");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SEC);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setBackground(BG);
        titles.add(titulo);
        titles.add(sub);

        p.add(titles, BorderLayout.WEST);
        return p;
    }

    // ── Tabla ─────────────────────────────────────────────────────────────
    private JScrollPane buildTabla() {
        String[] columnas = {"ID", "Nombre", "Confederación", "Valor total (€)"};
        modelo = new DefaultTableModel(columnas, 0) {
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
        tabla.setSelectionBackground(SEL_BG);
        tabla.setSelectionForeground(TEXT_PRI);
        tabla.setFocusable(false);

        // Header de la tabla
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(TH_BG);
        tabla.getTableHeader().setForeground(TEXT_SEC);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_N));

        // Ocultar columna ID (se usa internamente)
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        // Al seleccionar una fila, cargar datos en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarEnFormulario();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_N, 1, true));
        scroll.getViewport().setBackground(CARD);
        return scroll;
    }

    // ── Formulario ────────────────────────────────────────────────────────
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
        form.setBorder(new EmptyBorder(24, 20, 24, 20));
        form.setPreferredSize(new Dimension(280, 0));

        JLabel titulo = new JLabel("Datos del equipo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        txtNombre = makeTextField();
        txtValor  = makeTextField();

        // Cargar confederaciones en el combo
        cmbConf = new JComboBox<>();
        cmbConf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbConf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbConf.setAlignmentX(LEFT_ALIGNMENT);
        List<Confederacion> confs = confDAO.listarTodos();
        for (Confederacion c : confs) cmbConf.addItem(c);

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setAlignmentX(LEFT_ALIGNMENT);

        btnGuardar  = makeButton("Guardar",  ACCENT,  ACCENT_H);
        btnEliminar = makeButton("Eliminar", DANGER,  DANGER_H);
        btnNuevo    = makeButton("Nuevo",    new Color(0x4B5563), new Color(0x374151));

        btnGuardar.addActionListener (e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnNuevo.addActionListener   (e -> limpiarFormulario());

        btnEliminar.setEnabled(false);  // solo habilitado al seleccionar fila

        form.add(titulo);
        form.add(Box.createVerticalStrut(20));
        form.add(makeLabel("Nombre del equipo"));
        form.add(Box.createVerticalStrut(6));
        form.add(txtNombre);
        form.add(Box.createVerticalStrut(14));
        form.add(makeLabel("Confederación"));
        form.add(Box.createVerticalStrut(6));
        form.add(cmbConf);
        form.add(Box.createVerticalStrut(14));
        form.add(makeLabel("Valor total (€)"));
        form.add(Box.createVerticalStrut(6));
        form.add(txtValor);
        form.add(Box.createVerticalStrut(16));
        form.add(lblMensaje);
        form.add(Box.createVerticalStrut(12));
        form.add(btnGuardar);
        form.add(Box.createVerticalStrut(8));
        form.add(btnEliminar);
        form.add(Box.createVerticalStrut(8));
        form.add(btnNuevo);

        return form;
    }

    // ── Acciones ─────────────────────────────────────────────────────────
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String valorStr = txtValor.getText().trim();
        Confederacion conf = (Confederacion) cmbConf.getSelectedItem();

        if (nombre.isEmpty()) { mostrarMensaje("El nombre es obligatorio.", true); return; }
        if (conf == null)     { mostrarMensaje("Selecciona una confederación.", true); return; }

        double valor = 0;
        if (!valorStr.isEmpty()) {
            try { valor = Double.parseDouble(valorStr); }
            catch (NumberFormatException e) {
                mostrarMensaje("El valor debe ser un número.", true); return;
            }
        }

        String resultado;
        if (idSeleccionado == -1) {
            // Insertar nuevo
            Equipo e = new Equipo(nombre, valor, conf.getIdConfederacion());
            resultado = controller.insertar(e);
        } else {
            // Actualizar existente
            Equipo e = new Equipo(idSeleccionado, nombre, valor, conf.getIdConfederacion());
            resultado = controller.actualizar(e);
        }

        mostrarMensaje(resultado, resultado.startsWith("Error") || resultado.startsWith("Sin"));
        cargarTabla();
        limpiarFormulario();
    }

    private void eliminar() {
        if (idSeleccionado == -1) return;
        int confirm = JOptionPane.showConfirmDialog(
                this, "¿Eliminar este equipo?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String resultado = controller.eliminar(idSeleccionado);
        mostrarMensaje(resultado, resultado.startsWith("Error") || resultado.startsWith("Sin"));
        cargarTabla();
        limpiarFormulario();
    }

    // ── Cargar / limpiar ─────────────────────────────────────────────────
    private void cargarTabla() {
        modelo.setRowCount(0);
        List<Equipo> lista = controller.listarTodos();
        for (Equipo e : lista) {
            modelo.addRow(new Object[]{
                    e.getIdEquipo(),
                    e.getNombre(),
                    e.getNombreConfederacion(),
                    String.format("%.2f", e.getValorTotal())
            });
        }
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        Equipo e = controller.buscarPorId(idSeleccionado);
        if (e == null) return;

        txtNombre.setText(e.getNombre());
        txtValor.setText(String.valueOf(e.getValorTotal()));

        // Seleccionar la confederación correcta en el combo
        for (int i = 0; i < cmbConf.getItemCount(); i++) {
            if (cmbConf.getItemAt(i).getIdConfederacion() == e.getIdConfederacion()) {
                cmbConf.setSelectedIndex(i);
                break;
            }
        }

        btnEliminar.setEnabled(true);
        lblMensaje.setText(" ");
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtValor.setText("");
        if (cmbConf.getItemCount() > 0) cmbConf.setSelectedIndex(0);
        btnEliminar.setEnabled(false);
        lblMensaje.setText(" ");
        tabla.clearSelection();
    }

    private void mostrarMensaje(String msg, boolean esError) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(esError ? DANGER : new Color(0x0F6E56));
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────
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
            {
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) { focused = true;  repaint(); }
                    public void focusLost (FocusEvent e)  { focused = false; repaint(); }
                });
            }
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
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = !isEnabled() ? new Color(0xD3D1C7) : (hovered ? hover : bg);
                g2.setColor(c);
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