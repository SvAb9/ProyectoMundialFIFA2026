package view.crud;

import controller.GrupoController;
import model.Grupo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * GrupoPanel - CRUD de grupos (A-L).
 */
public class GrupoPanel extends JPanel {

    private static final Color BG       = new Color(0xF5F5F2);
    private static final Color CARD     = Color.WHITE;
    private static final Color ACCENT   = new Color(0x185FA5);
    private static final Color ACCENT_H = new Color(0x0C447C);
    private static final Color DANGER   = new Color(0xA32D2D);
    private static final Color DANGER_H = new Color(0x7A1F1F);
    private static final Color TEXT_PRI = new Color(0x1A1A18);
    private static final Color TEXT_SEC = new Color(0x6B6B67);
    private static final Color BORDER_N = new Color(0xD3D1C7);
    private static final Color TH_BG    = new Color(0xF1EFE8);
    private static final Color SEL_BG   = new Color(0xE6F1FB);

    private JTable              tabla;
    private DefaultTableModel   modelo;
    private JComboBox<String>   cmbNombre;
    private JLabel              lblMensaje;
    private JButton             btnGuardar, btnEliminar, btnNuevo;

    private final GrupoController controller = new GrupoController();
    private int idSeleccionado = -1;

    public GrupoPanel() {
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
        JLabel t = new JLabel("Grupos");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(TEXT_PRI);
        JLabel s = new JLabel("12 grupos de la fase de grupos (A-L)");
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
        modelo = new DefaultTableModel(new String[]{"ID", "Grupo"}, 0) {
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
        form.setBorder(new EmptyBorder(24, 20, 24, 20));
        form.setPreferredSize(new Dimension(280, 0));

        JLabel titulo = new JLabel("Datos del grupo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        cmbNombre = new JComboBox<>(new String[]{"A","B","C","D","E","F","G","H","I","J","K","L"});
        cmbNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbNombre.setAlignmentX(LEFT_ALIGNMENT);

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setAlignmentX(LEFT_ALIGNMENT);

        btnGuardar  = makeButton("Guardar",  ACCENT,  ACCENT_H);
        btnEliminar = makeButton("Eliminar", DANGER,  DANGER_H);
        btnNuevo    = makeButton("Nuevo",    new Color(0x4B5563), new Color(0x374151));

        btnGuardar.addActionListener (e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnNuevo.addActionListener   (e -> limpiarFormulario());
        btnEliminar.setEnabled(false);

        form.add(titulo);
        form.add(Box.createVerticalStrut(20));
        form.add(makeLabel("Letra del grupo"));
        form.add(Box.createVerticalStrut(6));
        form.add(cmbNombre);
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

    private void guardar() {
        String nombre = (String) cmbNombre.getSelectedItem();
        String resultado;
        if (idSeleccionado == -1) {
            resultado = controller.insertar(new Grupo(nombre));
        } else {
            resultado = controller.actualizar(new Grupo(idSeleccionado, nombre));
        }
        mostrarMensaje(resultado, resultado.startsWith("Error") || resultado.startsWith("Sin"));
        cargarTabla(); limpiarFormulario();
    }

    private void eliminar() {
        if (idSeleccionado == -1) return;
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar este grupo?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        String resultado = controller.eliminar(idSeleccionado);
        mostrarMensaje(resultado, resultado.startsWith("Error") || resultado.startsWith("Sin"));
        cargarTabla(); limpiarFormulario();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Grupo g : controller.listarTodos())
            modelo.addRow(new Object[]{g.getIdGrupo(), "Grupo " + g.getNombre()});
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        Grupo g = controller.buscarPorId(idSeleccionado);
        if (g == null) return;
        cmbNombre.setSelectedItem(g.getNombre());
        btnEliminar.setEnabled(true);
        lblMensaje.setText(" ");
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        cmbNombre.setSelectedIndex(0);
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