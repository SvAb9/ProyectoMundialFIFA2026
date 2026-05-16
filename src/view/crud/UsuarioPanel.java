package view.crud;

import controller.UsuarioController;
import model.Usuario;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * UsuarioPanel - Gestión de usuarios del sistema.
 * SOLO accesible por el rol ADMIN.
 * Permite crear, editar y desactivar usuarios.
 */
public class UsuarioPanel extends JPanel {

    // ── Colores (misma paleta del proyecto) ──────────────────────────────
    private static final Color BG       = new Color(0xF5F5F2);
    private static final Color CARD     = Color.WHITE;
    private static final Color ACCENT   = new Color(0x185FA5);
    private static final Color ACCENT_H = new Color(0x0C447C);
    private static final Color DANGER   = new Color(0xA32D2D);
    private static final Color DANGER_H = new Color(0x7A1F1F);
    private static final Color WARNING  = new Color(0xB45309);
    private static final Color WARNING_H= new Color(0x92400E);
    private static final Color SUCCESS  = new Color(0x0F6E56);
    private static final Color TEXT_PRI = new Color(0x1A1A18);
    private static final Color TEXT_SEC = new Color(0x6B6B67);
    private static final Color BORDER_N = new Color(0xD3D1C7);
    private static final Color BORDER_F = new Color(0x185FA5);
    private static final Color TH_BG    = new Color(0xF1EFE8);
    private static final Color SEL_BG   = new Color(0xE6F1FB);
    private static final Color INACTIVE = new Color(0xFEF2F2);

    // ── Componentes ──────────────────────────────────────────────────────
    private JTable            tabla;
    private DefaultTableModel modelo;

    private JTextField        txtUsername;
    private JPasswordField    txtPassword;
    private JComboBox<String> cmbRol;
    private JCheckBox         chkActivo;
    private JLabel            lblMensaje;

    private JButton           btnGuardar;
    private JButton           btnDesactivar;
    private JButton           btnNuevo;
    private JButton           chkMostrarPass;   // botón para mostrar/ocultar contraseña

    private final UsuarioController controller = new UsuarioController();
    private int idSeleccionado = -1;
    private boolean modoEdicion = false;

    // ── Constructor ──────────────────────────────────────────────────────
    public UsuarioPanel() {
        // Verificación de seguridad: solo ADMIN puede ver este panel
        if (!SessionManager.esAdmin()) {
            setLayout(new GridBagLayout());
            setBackground(BG);
            JLabel lbl = new JLabel("Acceso denegado. Solo el administrador puede gestionar usuarios.");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(DANGER);
            add(lbl);
            return;
        }

        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(),     BorderLayout.NORTH);
        add(buildTabla(),      BorderLayout.CENTER);
        add(buildFormulario(), BorderLayout.EAST);

        cargarTabla();
    }

    // ── Header ────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);

        JLabel titulo = new JLabel("Gestión de Usuarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT_PRI);

        JLabel sub = new JLabel("Solo el administrador puede crear, editar y desactivar usuarios");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SEC);

        // Badge de advertencia
        JLabel badge = new JLabel("  Area restringida — ADMIN  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBackground(DANGER);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG);
        left.add(titulo);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        p.add(left,  BorderLayout.WEST);
        p.add(badge, BorderLayout.EAST);
        return p;
    }

    // ── Tabla de usuarios ─────────────────────────────────────────────────
    private JScrollPane buildTabla() {
        modelo = new DefaultTableModel(
                new String[]{"ID", "Usuario", "Rol", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                // Filas de usuarios inactivos se colorean en rosa claro
                String estado = (String) modelo.getValueAt(row, 3);
                if ("Inactivo".equals(estado) && !isRowSelected(row)) {
                    c.setBackground(INACTIVE);
                } else {
                    c.setBackground(isRowSelected(row) ? SEL_BG
                            : (row % 2 == 0 ? CARD : new Color(0xFAFAF8)));
                }
                c.setForeground(TEXT_PRI);

                // Colorear la columna "Rol"
                if (col == 2 && !isRowSelected(row)) {
                    String rol = (String) modelo.getValueAt(row, 2);
                    if ("ADMIN".equals(rol))       c.setForeground(DANGER);
                    else if ("TRADICIONAL".equals(rol)) c.setForeground(ACCENT);
                    else                           c.setForeground(new Color(0x5B6B7C));
                }
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

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(TH_BG);
        tabla.getTableHeader().setForeground(TEXT_SEC);

        // Ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        // Anchos de columnas
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(90);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarEnFormulario();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_N, 1, true));
        scroll.getViewport().setBackground(CARD);
        return scroll;
    }

    // ── Formulario lateral ────────────────────────────────────────────────
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
        form.setPreferredSize(new Dimension(300, 0));

        // Título del formulario
        JLabel titulo = new JLabel("Datos del usuario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        // ── Campo: Nombre de usuario ──
        txtUsername = makeTextField();

        // ── Campo: Contraseña ──
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setEchoChar('•');
        txtPassword.setOpaque(false);
        txtPassword.setBorder(new EmptyBorder(8, 10, 8, 10));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);

        // Botón mostrar/ocultar contraseña
        chkMostrarPass = new JButton("Mostrar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0xEEECE6));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chkMostrarPass.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkMostrarPass.setForeground(TEXT_SEC);
        chkMostrarPass.setBorderPainted(false);
        chkMostrarPass.setContentAreaFilled(false);
        chkMostrarPass.setFocusPainted(false);
        chkMostrarPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chkMostrarPass.setMaximumSize(new Dimension(100, 26));
        chkMostrarPass.setAlignmentX(LEFT_ALIGNMENT);
        chkMostrarPass.addActionListener(e -> {
            if (txtPassword.getEchoChar() == '•') {
                txtPassword.setEchoChar((char) 0);
                chkMostrarPass.setText("Ocultar");
            } else {
                txtPassword.setEchoChar('•');
                chkMostrarPass.setText("Mostrar");
            }
        });

        // ── Campo: Rol ──
        cmbRol = new JComboBox<>(new String[]{"TRADICIONAL", "ESPORADICO", "ADMIN"});
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbRol.setAlignmentX(LEFT_ALIGNMENT);

        // ── Campo: Activo (solo visible en modo edición) ──
        chkActivo = new JCheckBox("Usuario activo");
        chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkActivo.setForeground(TEXT_PRI);
        chkActivo.setOpaque(false);
        chkActivo.setAlignmentX(LEFT_ALIGNMENT);
        chkActivo.setSelected(true);
        chkActivo.setVisible(false); // se muestra solo en edición

        // ── Nota informativa sobre contraseña en edición ──
        JLabel lblNotaPass = new JLabel("* Dejar vacío para mantener la contraseña actual");
        lblNotaPass.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblNotaPass.setForeground(TEXT_SEC);
        lblNotaPass.setAlignmentX(LEFT_ALIGNMENT);
        lblNotaPass.setName("notaPass");
        lblNotaPass.setVisible(false);

        // ── Mensaje de resultado ──
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setAlignmentX(LEFT_ALIGNMENT);

        // ── Botones ──
        btnGuardar   = makeButton("Guardar",   ACCENT,   ACCENT_H);
        btnDesactivar= makeButton("Desactivar",DANGER,   DANGER_H);
        btnNuevo     = makeButton("Nuevo",     new Color(0x4B5563), new Color(0x374151));

        btnGuardar.addActionListener   (e -> guardar());
        btnDesactivar.addActionListener(e -> desactivar());
        btnNuevo.addActionListener     (e -> limpiarFormulario());

        btnDesactivar.setEnabled(false);

        // ── Ensamblar formulario ──
        form.add(titulo);
        form.add(Box.createVerticalStrut(20));

        form.add(makeLabel("Nombre de usuario"));
        form.add(Box.createVerticalStrut(6));
        form.add(txtUsername);
        form.add(Box.createVerticalStrut(14));

        form.add(makeLabel("Contraseña"));
        form.add(Box.createVerticalStrut(6));
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(4));
        form.add(chkMostrarPass);
        form.add(lblNotaPass);
        form.add(Box.createVerticalStrut(14));

        form.add(makeLabel("Rol"));
        form.add(Box.createVerticalStrut(6));
        form.add(cmbRol);
        form.add(Box.createVerticalStrut(12));

        form.add(chkActivo);
        form.add(Box.createVerticalStrut(16));

        form.add(lblMensaje);
        form.add(Box.createVerticalStrut(12));
        form.add(btnGuardar);
        form.add(Box.createVerticalStrut(8));
        form.add(btnDesactivar);
        form.add(Box.createVerticalStrut(8));
        form.add(btnNuevo);

        return form;
    }

    // ── Lógica: Guardar (crear o actualizar) ─────────────────────────────
    private void guardar() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String rol      = (String) cmbRol.getSelectedItem();

        if (username.isEmpty()) {
            mostrarMensaje("El nombre de usuario es obligatorio.", true);
            return;
        }

        String resultado;

        if (idSeleccionado == -1) {
            // ── MODO CREAR ──
            if (password.isEmpty()) {
                mostrarMensaje("La contraseña es obligatoria para usuarios nuevos.", true);
                return;
            }
            resultado = controller.crear(username, password, rol);

        } else {
            // ── MODO EDITAR ──
            Usuario u = controller.buscarPorId(idSeleccionado);
            if (u == null) {
                mostrarMensaje("Usuario no encontrado.", true);
                return;
            }
            u.setUsername(username);
            u.setRol(rol);
            u.setActivo(chkActivo.isSelected());

            // Solo actualiza la contraseña si se escribió una nueva
            if (!password.isEmpty()) {
                if (password.length() < 6) {
                    mostrarMensaje("La contraseña debe tener al menos 6 caracteres.", true);
                    return;
                }
                u.setPassword(password);
            }
            resultado = controller.actualizar(u);
        }

        boolean esError = resultado.startsWith("Error") || resultado.startsWith("Sin");
        mostrarMensaje(resultado, esError);
        if (esError) {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo guardar el usuario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cargarTabla();
        limpiarFormulario();
    }

    // ── Lógica: Desactivar usuario ────────────────────────────────────────
    private void desactivar() {
        if (idSeleccionado == -1) return;

        // Verificar que no se esté intentando desactivar al admin actual
        if (idSeleccionado == SessionManager.getUsuario().getIdUsuario()) {
            mostrarMensaje("No puedes desactivar tu propia cuenta.", true);
            return;
        }

        Usuario u = controller.buscarPorId(idSeleccionado);
        String nombre = u != null ? u.getUsername() : "este usuario";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desactivar al usuario \"" + nombre + "\"?\n"
                + "El usuario no podrá iniciar sesión, pero sus datos se conservan.",
                "Confirmar desactivación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String resultado = controller.desactivar(idSeleccionado);
        boolean esErrorElim = resultado.startsWith("Error") || resultado.startsWith("Sin");
        mostrarMensaje(resultado, esErrorElim);
        if (esErrorElim) {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo eliminar el usuario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cargarTabla();
        limpiarFormulario();
    }

    // ── Cargar tabla desde BD ─────────────────────────────────────────────
    private void cargarTabla() {
        modelo.setRowCount(0);
        List<Usuario> lista = controller.listarTodos();

        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getUsername(),
                    u.getRol(),
                    u.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    // ── Cargar usuario seleccionado en el formulario ──────────────────────
    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        Usuario u = controller.buscarPorId(idSeleccionado);
        if (u == null) return;

        modoEdicion = true;

        txtUsername.setText(u.getUsername());
        txtPassword.setText("");   // no mostrar la contraseña actual por seguridad
        cmbRol.setSelectedItem(u.getRol());
        chkActivo.setSelected(u.isActivo());

        // Mostrar elementos de edición
        chkActivo.setVisible(true);
        mostrarNotaPassword(true);

        // Controles de botones
        btnDesactivar.setEnabled(u.isActivo()
                && u.getIdUsuario() != SessionManager.getUsuario().getIdUsuario());

        // No se puede editar al admin desde su propia sesión el campo rol
        boolean esMismoAdmin = u.getIdUsuario() == SessionManager.getUsuario().getIdUsuario();
        cmbRol.setEnabled(!esMismoAdmin);

        lblMensaje.setText(" ");
        revalidate();
        repaint();
    }

    // ── Limpiar formulario (modo nuevo) ───────────────────────────────────
    private void limpiarFormulario() {
        idSeleccionado = -1;
        modoEdicion    = false;

        txtUsername.setText("");
        txtPassword.setText("");
        txtPassword.setEchoChar('•');
        chkMostrarPass.setText("Mostrar");
        cmbRol.setSelectedIndex(0);
        cmbRol.setEnabled(true);
        chkActivo.setSelected(true);
        chkActivo.setVisible(false);
        mostrarNotaPassword(false);

        btnDesactivar.setEnabled(false);
        lblMensaje.setText(" ");
        tabla.clearSelection();

        revalidate();
        repaint();
    }

    // ── Mostrar/ocultar nota de contraseña ────────────────────────────────
    private void mostrarNotaPassword(boolean visible) {
        // Buscar el label por nombre en el formulario
        for (Component c : getComponentsRecursivo(this)) {
            if (c instanceof JLabel && "notaPass".equals(c.getName())) {
                c.setVisible(visible);
            }
        }
    }

    private java.util.List<Component> getComponentsRecursivo(Container cont) {
        java.util.List<Component> lista = new java.util.ArrayList<>();
        for (Component c : cont.getComponents()) {
            lista.add(c);
            if (c instanceof Container) {
                lista.addAll(getComponentsRecursivo((Container) c));
            }
        }
        return lista;
    }

    // ── Mostrar mensaje de resultado ──────────────────────────────────────
    private void mostrarMensaje(String msg, boolean esError) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(esError ? DANGER : SUCCESS);
    }

    // ── Helpers UI ────────────────────────────────────────────────────────
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