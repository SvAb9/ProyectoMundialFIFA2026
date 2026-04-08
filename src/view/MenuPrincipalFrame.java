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
 * MenuPrincipalFrame - Ventana principal después del login.
 * Muestra u oculta opciones según el rol del usuario logueado.
 *
 * Roles:
 *   ADMIN       → todo: CRUD + consultas + reportes + gestión de usuarios
 *   TRADICIONAL → CRUD + consultas + reportes
 *   ESPORADICO  → solo consultas y reportes
 */
public class MenuPrincipalFrame extends JFrame {

    // ── Colores ──────────────────────────────────────────────────────────
    private static final Color BG         = new Color(0xF5F5F2);
    private static final Color SIDEBAR    = new Color(0x1A2744);
    private static final Color SIDEBAR_HV = new Color(0x243560);
    private static final Color ACCENT     = new Color(0x3B82C4);
    private static final Color TEXT_W     = Color.WHITE;
    private static final Color TEXT_M     = new Color(0xA8B4C8);
    private static final Color CARD       = Color.WHITE;
    private static final Color TEXT_PRI   = new Color(0x1A1A18);
    private static final Color TEXT_SEC   = new Color(0x6B6B67);

    private JPanel contentPanel;

    public MenuPrincipalFrame() {
        setTitle("Mundial FIFA 2026 — " + SessionManager.getUsuario().getUsername());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Confirmar antes de cerrar
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                cerrarApp();
            }
        });

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        // Mostrar pantalla de bienvenida al iniciar
        mostrarBienvenida();
    }

    // ── Sidebar ──────────────────────────────────────────────────────────
    // ── Sidebar ──────────────────────────────────────────────────────────
    private JScrollPane buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 0, 24, 0));

        // Logo / título
        JLabel logo = new JLabel("⚽ FIFA 2026", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(TEXT_W);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 0, 6, 0));

        String rol = SessionManager.getRol();
        JLabel lblRol = new JLabel(rol, SwingConstants.CENTER);
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRol.setForeground(new Color(0x3B82C4));
        lblRol.setAlignmentX(CENTER_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(lblRol);
        sidebar.add(Box.createVerticalStrut(24));
        sidebar.add(makeSeparator());

        // ── Sección CRUD (admin y tradicional) ──
        if (SessionManager.puedeCRUD()) {
            sidebar.add(makeSectionLabel("GESTIÓN"));
            sidebar.add(makeNavButton("Confederaciones", () -> abrirPanel("confederaciones")));
            sidebar.add(makeNavButton("Equipos",         () -> abrirPanel("equipos")));
            sidebar.add(makeNavButton("Jugadores",       () -> abrirPanel("jugadores")));
            sidebar.add(makeNavButton("Dir. Técnicos",   () -> abrirPanel("directores")));
            sidebar.add(makeNavButton("Ciudades",        () -> abrirPanel("ciudades")));
            sidebar.add(makeNavButton("Estadios",        () -> abrirPanel("estadios")));
            sidebar.add(makeNavButton("Grupos",          () -> abrirPanel("grupos")));
            sidebar.add(makeNavButton("Partidos",        () -> abrirPanel("partidos")));
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(makeSeparator());
        }

        // ── Sección Consultas (todos) ──
        sidebar.add(makeSectionLabel("CONSULTAS"));
        sidebar.add(makeNavButton("Jugador más costoso",  () -> abrirPanel("c_jugador_costoso")));
        sidebar.add(makeNavButton("Partidos por estadio", () -> abrirPanel("c_partidos_estadio")));
        sidebar.add(makeNavButton("Equipo más costoso",   () -> abrirPanel("c_equipo_costoso")));
        sidebar.add(makeNavButton("Menores de 21",        () -> abrirPanel("c_menores21")));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeSeparator());

        // ── Sección Reportes (todos) ──
        sidebar.add(makeSectionLabel("REPORTES"));
        sidebar.add(makeNavButton("Ingresos al sistema",  () -> abrirPanel("r_ingresos")));
        sidebar.add(makeNavButton("Jugadores por filtro", () -> abrirPanel("r_jugadores_filtro")));
        sidebar.add(makeNavButton("Valor por equipo",     () -> abrirPanel("r_valor_equipo")));
        sidebar.add(makeNavButton("Países por sede",      () -> abrirPanel("r_paises_sede")));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeSeparator());

        // ── Gestión usuarios (solo admin) ──
        if (SessionManager.esAdmin()) {
            sidebar.add(makeSectionLabel("ADMINISTRACIÓN"));
            sidebar.add(makeNavButton("Usuarios", () -> abrirPanel("usuarios")));
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(makeSeparator());
        }

        sidebar.add(Box.createVerticalStrut(16));

        // Botón salir
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalir.setForeground(new Color(0xFCA5A5));
        btnSalir.setBackground(new Color(0x2D1F1F));
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSalir.setAlignmentX(CENTER_ALIGNMENT);
        btnSalir.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnSalir.addActionListener(e -> cerrarApp());
        btnSalir.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnSalir.setBackground(new Color(0x3D2222)); }
            public void mouseExited (MouseEvent e) { btnSalir.setBackground(new Color(0x2D1F1F)); }
        });

        sidebar.add(btnSalir);
        sidebar.add(Box.createVerticalStrut(8));

        // ── Envolver en ScrollPane ──
        JScrollPane scroll = new JScrollPane(sidebar);
        scroll.setPreferredSize(new Dimension(220, 0));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(SIDEBAR);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setBackground(SIDEBAR);
        return scroll;
    }

    // ── Panel de contenido ────────────────────────────────────────────────
    private JPanel buildContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG);
        return contentPanel;
    }

    // ── Bienvenida ────────────────────────────────────────────────────────
    private void mostrarBienvenida() {
        JPanel welcome = new JPanel(new GridBagLayout());
        welcome.setBackground(BG);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));
        card.setPreferredSize(new Dimension(420, 260));

        JLabel ico = new JLabel("⚽", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        ico.setAlignmentX(CENTER_ALIGNMENT);

        JLabel saludo = new JLabel("Bienvenido, " + SessionManager.getUsuario().getUsername());
        saludo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        saludo.setForeground(TEXT_PRI);
        saludo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel info = new JLabel("Selecciona una opción del menú lateral.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setForeground(TEXT_SEC);
        info.setAlignmentX(CENTER_ALIGNMENT);

        // Mostrar rol con color
        String rolTexto = switch (SessionManager.getRol()) {
            case "ADMIN"       -> "Administrador — acceso total";
            case "TRADICIONAL" -> "Usuario tradicional — gestión de datos";
            case "ESPORADICO"  -> "Usuario esporádico — solo consultas";
            default            -> SessionManager.getRol();
        };
        JLabel lblRolInfo = new JLabel(rolTexto, SwingConstants.CENTER);
        lblRolInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRolInfo.setForeground(ACCENT);
        lblRolInfo.setAlignmentX(CENTER_ALIGNMENT);

        card.add(ico);
        card.add(Box.createVerticalStrut(16));
        card.add(saludo);
        card.add(Box.createVerticalStrut(8));
        card.add(info);
        card.add(Box.createVerticalStrut(6));
        card.add(lblRolInfo);

        welcome.add(card);
        contentPanel.removeAll();
        contentPanel.add(welcome, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Navegación ────────────────────────────────────────────────────────
    private void abrirPanel(String seccion) {
        JPanel panel = switch (seccion) {
            case "equipos" -> new EquipoPanel();
            case "confederaciones" -> new ConfederacionPanel();
            case "ciudades"        -> new CiudadPanel();
            case "estadios"        -> new EstadioPanel();
            case "grupos"          -> new GrupoPanel();
            case "directores"      -> new DirectorTecnicoPanel();
            case "jugadores" -> new JugadorPanel();
            case "partidos"  -> new PartidoPanel();
            case "c_jugador_costoso"  -> new ConsultaJugadorCostosoPanel();
            case "c_partidos_estadio" -> new ConsultaPartidosEstadioPanel();
            case "c_equipo_costoso"   -> new ConsultaEquipoCostosoPanel();
            case "c_menores21"        -> new ConsultaMenores21Panel();
            case "r_ingresos"        -> new ReporteIngresosPanel();
            case "r_jugadores_filtro"-> new ReporteJugadoresFiltroPanel();
            case "r_valor_equipo"    -> new ReporteValorEquipoPanel();
            case "r_paises_sede"     -> new ReportePaisesSedPanel();
            default -> {
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(BG);
                JLabel lbl = new JLabel("Panel: " + seccion + " — próximamente");
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
                "¿Deseas cerrar sesión y salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginController().logout();   // registra salida en bitácora
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

    private JButton makeNavButton(String texto, Runnable accion) {
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
                g2.setColor(hovered ? SIDEBAR_HV : SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
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
        btn.setBorder(new EmptyBorder(9, 20, 9, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2D3D5A));
        sep.setBackground(SIDEBAR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}