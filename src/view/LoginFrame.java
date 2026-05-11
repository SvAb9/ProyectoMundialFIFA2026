package view;

import controller.LoginController;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * LoginFrame - Pantalla de inicio de sesion.
 */
public class LoginFrame extends JFrame {

    // Paleta de colores
    private static final Color BG         = new Color(0x1A2744);   // azul oscuro fondo
    private static final Color BG2        = new Color(0x243560);   // azul medio
    private static final Color CARD       = new Color(0xFFFFFF);
    private static final Color ACCENT     = new Color(0x185FA5);
    private static final Color ACCENT_HOV = new Color(0x0C447C);
    private static final Color TEXT_PRI   = new Color(0x1A1A18);
    private static final Color TEXT_SEC   = new Color(0x6B6B67);
    private static final Color BORDER_N   = new Color(0xD3D1C7);
    private static final Color BORDER_F   = new Color(0x185FA5);
    private static final Color ERR_BG     = new Color(0xFCEBEB);
    private static final Color ERR_TEXT   = new Color(0xA32D2D);

    private RoundTextField     txtUsuario;
    private RoundPasswordField txtPassword;
    private JLabel             lblError;
    private RoundButton        btnIngresar;
    private JCheckBox          chkMostrar;

    private final LoginController loginController = new LoginController();

    public LoginFrame() {
        setTitle("Mundial FIFA 2026");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(false);

        // Fondo degradado
        JPanel fondo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG, getWidth(), getHeight(), BG2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        fondo.setLayout(new GridBagLayout());
        setContentPane(fondo);

        fondo.add(buildCard());
        pack();
        setMinimumSize(new Dimension(440, 540));
        setLocationRelativeTo(null);
    }

    // Card central
    private JPanel buildCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(4, 6, getWidth() - 4, getHeight() - 4, 20, 20));
                // Card blanca
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(44, 48, 44, 48));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 500));

        // Barra de color superior
        JPanel topBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), 0, new Color(0x3B82C4));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 4));
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        topBar.setAlignmentX(LEFT_ALIGNMENT);

        // Titulo
        JLabel titulo = new JLabel("Mundial FIFA 2026", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sistema de gestion", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SEC);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Linea divisora decorativa
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0xE8E6DF));
                g2.fillRect(0, getHeight() / 2, getWidth(), 1);
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        divider.setAlignmentX(LEFT_ALIGNMENT);

        // Campos
        txtUsuario  = new RoundTextField("Nombre de usuario");
        txtPassword = new RoundPasswordField();

        chkMostrar = new JCheckBox("Mostrar contrasena");
        chkMostrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMostrar.setForeground(TEXT_SEC);
        chkMostrar.setOpaque(false);
        chkMostrar.setAlignmentX(LEFT_ALIGNMENT);
        chkMostrar.addActionListener(e ->
                txtPassword.setEchoChar(chkMostrar.isSelected() ? (char) 0 : '\u2022'));

        // Error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(ERR_TEXT);
        lblError.setOpaque(false);
        lblError.setBorder(new EmptyBorder(0, 4, 0, 4));
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        // Boton
        btnIngresar = new RoundButton("Ingresar");
        btnIngresar.addActionListener(e -> intentarLogin());

        KeyAdapter enter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        };
        txtUsuario.addKeyListener(enter);
        txtPassword.addKeyListener(enter);

        // Ensamblar
        card.add(topBar);
        card.add(Box.createVerticalStrut(28));
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(divider);
        card.add(Box.createVerticalStrut(20));
        card.add(makeLabel("Usuario"));
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsuario);
        card.add(Box.createVerticalStrut(16));
        card.add(makeLabel("Contrasena"));
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(8));
        card.add(chkMostrar);
        card.add(Box.createVerticalStrut(6));
        card.add(lblError);
        card.add(Box.createVerticalStrut(20));
        card.add(btnIngresar);
        card.add(Box.createVerticalStrut(4));

        // Pie de pagina
        JLabel footer = new JLabel("FIFA World Cup 2026", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(0xBBB9B2));
        footer.setAlignmentX(CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(16));
        card.add(footer);

        return card;
    }

    private JLabel makeLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_PRI);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    // Logica de login
    private void intentarLogin() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }

        Usuario usuario = loginController.login(user, pass);

        if (usuario == null) {
            mostrarError("Usuario o contrasena incorrectos.");
            txtPassword.setText("");
            txtPassword.requestFocus();
        } else {
            dispose();
            new MenuPrincipalFrame().setVisible(true);
        }
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setOpaque(true);
        lblError.setBackground(ERR_BG);
        lblError.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xF09595), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        pack();
    }

    // ── Componentes reutilizables ─────────────────────────────────────────

    static class RoundTextField extends JTextField {
        private final String placeholder;
        private boolean focused = false;

        RoundTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_PRI);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(LEFT_ALIGNMENT);
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { focused = true;  repaint(); }
                public void focusLost (FocusEvent e)  { focused = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(focused ? new Color(0xF7FAFF) : Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(focused ? BORDER_F : BORDER_N);
            g2.setStroke(new BasicStroke(focused ? 2f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(getFont());
                g3.setColor(new Color(0xBBB9B2));
                FontMetrics fm = g3.getFontMetrics();
                g3.drawString(placeholder, 14, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g3.dispose();
            }
        }
    }

    static class RoundPasswordField extends JPasswordField {
        private boolean focused = false;

        RoundPasswordField() {
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_PRI);
            setEchoChar('\u2022');
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(LEFT_ALIGNMENT);
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { focused = true;  repaint(); }
                public void focusLost (FocusEvent e)  { focused = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(focused ? new Color(0xF7FAFF) : Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(focused ? BORDER_F : BORDER_N);
            g2.setStroke(new BasicStroke(focused ? 2f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundButton extends JButton {
        private boolean hovered  = false;
        private boolean pressed  = false;

        RoundButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setAlignmentX(LEFT_ALIGNMENT);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e)  { hovered = false; pressed = false; repaint(); }
                public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
                public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = pressed ? new Color(0x083060) : (hovered ? ACCENT_HOV : ACCENT);
            // Degradado en el boton
            GradientPaint gp = new GradientPaint(0, 0, base.brighter(), 0, getHeight(), base);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
