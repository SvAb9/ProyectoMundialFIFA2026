package view;

import controller.LoginController;
import util.SessionManager;
import view.consultas.ConsultaEquipoCostosoPanel;
import view.consultas.ConsultaJugadorCostosoPanel;
import view.consultas.ConsultaMenores21Panel;
import view.consultas.ConsultaPartidosEstadioPanel;
import view.crud.*;
import view.reportes.ReporteIngresosPanel;
import view.reportes.ReporteJugadoresFiltroPanel;
import view.reportes.ReportePaisesSedPanel;
import view.reportes.ReporteValorEquipoPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * MenuPrincipalFrame - Ventana principal despues del login.
 *
 * Roles:
 *   ADMIN       -> todo: CRUD + consultas + reportes + gestion de usuarios
 *   TRADICIONAL -> CRUD + consultas + reportes
 *   ESPORADICO  -> solo consultas y reportes
 */
public class MenuPrincipalFrame extends JFrame {

    // Paleta
    private static final Color BG         = new Color(0xF4F3EF);
    private static final Color SIDEBAR    = new Color(0x14213D);
    private static final Color SIDEBAR_HV = new Color(0x1E3260);
    private static final Color SIDEBAR_ACT= new Color(0x185FA5);
    private static final Color ACCENT     = new Color(0x185FA5);
    private static final Color TEXT_W     = Color.WHITE;
    private static final Color TEXT_M     = new Color(0x8A9BB8);
    private static final Color CARD       = Color.WHITE;
    private static final Color TEXT_PRI   = new Color(0x1A1A18);
    private static final Color TEXT_SEC   = new Color(0x6B6B67);

    private JPanel  contentPanel;
    private JButton btnActivo = null;   // boton actualmente seleccionado

    public MenuPrincipalFrame() {
        setTitle("Mundial FIFA 2026  |  " + SessionManager.getUsuario().getUsername());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { cerrarApp(); }
        });

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        mostrarBienvenida();
    }

    // ── Sidebar ───────────────────────────────────────────────────────────
    private JScrollPane buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Cabecera del sidebar
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x185FA5), 0, getHeight(), SIDEBAR);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 0, 20, 0));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel logo = new JLabel("FIFA 2026", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(TEXT_W);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblRol = new JLabel(SessionManager.getRol(), SwingConstants.CENTER);
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRol.setForeground(new Color(0xA8D4FF));
        lblRol.setAlignmentX(CENTER_ALIGNMENT);
        lblRol.setBorder(new EmptyBorder(4, 0, 0, 0));

        header.add(logo);
        header.add(lblRol);

        sidebar.add(header);
        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        // Seccion CRUD
        if (SessionManager.puedeCRUD()) {
            sidebar.add(makeSectionLabel("GESTION"));
            sidebar.add(makeNavButton("Confederaciones", "confederaciones"));
            sidebar.add(makeNavButton("Equipos",         "equipos"));
            sidebar.add(makeNavButton("Jugadores",       "jugadores"));
            sidebar.add(makeNavButton("Dir. Tecnicos",   "directores"));
            sidebar.add(makeNavButton("Países",          "paises"));
            sidebar.add(makeNavButton("Ciudades",        "ciudades"));
            sidebar.add(makeNavButton("Estadios",        "estadios"));
            sidebar.add(makeNavButton("Grupos",          "grupos"));
            sidebar.add(makeNavButton("Partidos",        "partidos"));
            sidebar.add(makeNavButton("Posiciones",      "posiciones"));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(makeSeparator());
            sidebar.add(Box.createVerticalStrut(4));
        }

        // Seccion Consultas
        sidebar.add(makeSectionLabel("CONSULTAS"));
        sidebar.add(makeNavButton("Jugador mas costoso",  "c_jugador_costoso"));
        sidebar.add(makeNavButton("Partidos por estadio", "c_partidos_estadio"));
        sidebar.add(makeNavButton("Equipo mas costoso",   "c_equipo_costoso"));
        sidebar.add(makeNavButton("Menores de 21",        "c_menores21"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(4));

        // Seccion Reportes
        sidebar.add(makeSectionLabel("REPORTES"));
        sidebar.add(makeNavButton("Ingresos al sistema",  "r_ingresos"));
        sidebar.add(makeNavButton("Jugadores por filtro", "r_jugadores_filtro"));
        sidebar.add(makeNavButton("Valor por equipo",     "r_valor_equipo"));
        sidebar.add(makeNavButton("Paises por sede",      "r_paises_sede"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(4));

        // Administracion (solo admin)
        if (SessionManager.esAdmin()) {
            sidebar.add(makeSectionLabel("ADMINISTRACION"));
            sidebar.add(makeNavButton("Usuarios", "usuarios"));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(makeSeparator());
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());

        // Boton cerrar sesion
        JButton btnSalir = new JButton("Cerrar sesion") {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(hov ? new Color(0x5C1A1A) : new Color(0x3D1515));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalir.setForeground(new Color(0xFCA5A5));
        btnSalir.setHorizontalAlignment(SwingConstants.LEFT);
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnSalir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSalir.setAlignmentX(LEFT_ALIGNMENT);
        btnSalir.addActionListener(e -> cerrarApp());

        sidebar.add(btnSalir);

        JScrollPane scroll = new JScrollPane(sidebar);
        scroll.setPreferredSize(new Dimension(220, 0));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(SIDEBAR);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setBackground(SIDEBAR);
        // Quitar borde del scrollbar
        scroll.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── Panel de contenido ────────────────────────────────────────────────
    private JPanel buildContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG);
        return contentPanel;
    }

    // ── Pantalla de bienvenida ────────────────────────────────────────────
    private void mostrarBienvenida() {
        JPanel welcome = new JPanel(new GridBagLayout());
        welcome.setBackground(BG);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fill(new RoundRectangle2D.Float(4, 6, getWidth() - 4, getHeight() - 4, 18, 18));
                // Card
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(44, 56, 44, 56));
        card.setPreferredSize(new Dimension(460, 300));

        // Barra de acento superior
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), 0, new Color(0x3B82C4));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        bar.setAlignmentX(LEFT_ALIGNMENT);

        JLabel saludo = new JLabel("Bienvenido, " + SessionManager.getUsuario().getUsername());
        saludo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        saludo.setForeground(TEXT_PRI);
        saludo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel info = new JLabel("Selecciona una opcion del menu lateral.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setForeground(TEXT_SEC);
        info.setAlignmentX(CENTER_ALIGNMENT);

        String rolTexto = switch (SessionManager.getRol()) {
            case "ADMIN"       -> "Administrador  —  acceso total";
            case "TRADICIONAL" -> "Usuario tradicional  —  gestion de datos";
            case "ESPORADICO"  -> "Usuario esporadico  —  solo consultas";
            default            -> SessionManager.getRol();
        };
        JLabel lblRolInfo = new JLabel(rolTexto, SwingConstants.CENTER);
        lblRolInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRolInfo.setForeground(ACCENT);
        lblRolInfo.setAlignmentX(CENTER_ALIGNMENT);

        // Linea divisora
        JPanel div = new JPanel();
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setBackground(new Color(0xE8E6DF));

        card.add(bar);
        card.add(Box.createVerticalStrut(28));
        card.add(saludo);
        card.add(Box.createVerticalStrut(10));
        card.add(info);
        card.add(Box.createVerticalStrut(8));
        card.add(lblRolInfo);
        card.add(Box.createVerticalStrut(24));
        card.add(div);
        card.add(Box.createVerticalStrut(16));

        // Estadisticas rapidas (decorativas)
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(CENTER_ALIGNMENT);
        stats.add(makeStatChip("32", "Equipos"));
        stats.add(makeStatChip("16", "Grupos"));
        stats.add(makeStatChip("48", "Partidos"));
        card.add(stats);

        welcome.add(card);
        contentPanel.removeAll();
        contentPanel.add(welcome, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Chip de estadistica para la bienvenida
    private JPanel makeStatChip(String numero, String etiqueta) {
        JPanel chip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xEEF4FB));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBorder(new EmptyBorder(10, 18, 10, 18));

        JLabel num = new JLabel(numero, SwingConstants.CENTER);
        num.setFont(new Font("Segoe UI", Font.BOLD, 20));
        num.setForeground(ACCENT);
        num.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(etiqueta, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SEC);
        lbl.setAlignmentX(CENTER_ALIGNMENT);

        chip.add(num);
        chip.add(lbl);
        return chip;
    }

    // ── Navegacion ────────────────────────────────────────────────────────
    private void abrirPanel(String seccion) {
        JPanel panel = switch (seccion) {
            case "equipos"            -> new EquipoPanel();
            case "confederaciones"    -> new ConfederacionPanel();
            case "paises"             -> new PaisPanel();
            case "ciudades"           -> new CiudadPanel();
            case "estadios"           -> new EstadioPanel();
            case "grupos"             -> new GrupoPanel();
            case "directores"         -> new DirectorTecnicoPanel();
            case "jugadores"          -> new JugadorPanel();
            case "partidos"           -> new PartidoPanel();
            case "posiciones"         -> new PosicionPanel();
            case "c_jugador_costoso"  -> new ConsultaJugadorCostosoPanel();
            case "c_partidos_estadio" -> new ConsultaPartidosEstadioPanel();
            case "c_equipo_costoso"   -> new ConsultaEquipoCostosoPanel();
            case "c_menores21"        -> new ConsultaMenores21Panel();
            case "r_ingresos"         -> new ReporteIngresosPanel();
            case "r_jugadores_filtro" -> new ReporteJugadoresFiltroPanel();
            case "r_valor_equipo"     -> new ReporteValorEquipoPanel();
            case "r_paises_sede"      -> new ReportePaisesSedPanel();
            case "usuarios"           -> new UsuarioPanel();
            default -> {
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(BG);
                JLabel lbl = new JLabel("Panel: " + seccion + " — proximamente");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                lbl.setForeground(TEXT_SEC);
                p.add(lbl);
                yield p;
            }
        };

        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Cerrar app ────────────────────────────────────────────────────────
    private void cerrarApp() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseas cerrar sesion y salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginController().logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────
    private JLabel makeSectionLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_M);
        lbl.setBorder(new EmptyBorder(10, 20, 4, 20));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton makeNavButton(String texto, String seccion) {
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
                boolean activo = (this == btnActivo);
                if (activo) {
                    // Indicador lateral izquierdo
                    g2.setColor(SIDEBAR_ACT);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(0x3B82C4));
                    g2.fillRect(0, 0, 3, getHeight());
                } else {
                    g2.setColor(hovered ? SIDEBAR_HV : SIDEBAR);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_W);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 22, 9, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> {
            btnActivo = btn;
            abrirPanel(seccion);
            // Repintar sidebar para actualizar estado activo
            SwingUtilities.invokeLater(() -> {
                Container p = btn.getParent();
                if (p != null) p.repaint();
            });
        });
        return btn;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x253A5E));
        sep.setBackground(SIDEBAR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}
