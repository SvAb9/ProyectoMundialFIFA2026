package view;

import controller.LoginController;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * LoginFrame - Pantalla de inicio de sesión.
 * Llama a LoginController, nunca al DAO directamente.
 */
public class LoginFrame extends JFrame {

    // ── Colores ──────────────────────────────────────────────────────────
    private static final Color BG         = new Color(0xF5F5F2);
    private static final Color CARD       = Color.WHITE;
    private static final Color ACCENT     = new Color(0x185FA5);
    private static final Color ACCENT_HOV = new Color(0x0C447C);
    private static final Color TEXT_PRI   = new Color(0x1A1A18);
    private static final Color TEXT_SEC   = new Color(0x6B6B67);
    private static final Color BORDER_N   = new Color(0xD3D1C7);
    private static final Color BORDER_F   = new Color(0x185FA5);
    private static final Color ERR_BG     = new Color(0xFCEBEB);
    private static final Color ERR_TEXT   = new Color(0xA32D2D);

    // ── Componentes ──────────────────────────────────────────────────────
    private RoundTextField    txtUsuario;
    private RoundPasswordField txtPassword;
    private JLabel            lblError;
    private RoundButton       btnIngresar;
    private JCheckBox         chkMostrar;

    private final LoginController loginController = new LoginController();

    public LoginFrame() {
        setTitle("Mundial FIFA 2026");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new GridBagLayout());
        add(buildCard());
        pack();
        setMinimumSize(new Dimension(420, 500));
        setLocationRelativeTo(null);
    }

    // ── Card ─────────────────────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(0, 0, 0, 15));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 44, 40, 44));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(380, 460));

        // Encabezado
        JLabel ico = new JLabel("⚽", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        ico.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Mundial FIFA 2026", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sistema de gestión", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SEC);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Campos
        txtUsuario  = new RoundTextField("Nombre de usuario");
        txtPassword = new RoundPasswordField();

        chkMostrar = new JCheckBox("Mostrar contraseña");
        chkMostrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMostrar.setForeground(TEXT_SEC);
        chkMostrar.setOpaque(false);
        chkMostrar.setAlignmentX(LEFT_ALIGNMENT);
        chkMostrar.addActionListener(e ->
                txtPassword.setEchoChar(chkMostrar.isSelected() ? (char) 0 : '•'));

        // Error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(ERR_TEXT);
        lblError.setOpaque(false);
        lblError.setBorder(new EmptyBorder(0, 4, 0, 4));
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        // Botón
        btnIngresar = new RoundButton("Ingresar");
        btnIngresar.addActionListener(e -> intentarLogin());

        // Enter en cualquier campo
        KeyAdapter enter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        };
        txtUsuario.addKeyListener(enter);
        txtPassword.addKeyListener(enter);

        // Ensamblar
        card.add(ico);
        card.add(Box.createVerticalStrut(10));
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(makeLabel("Usuario"));
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsuario);
        card.add(Box.createVerticalStrut(16));
        card.add(makeLabel("Contraseña"));
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(8));
        card.add(chkMostrar);
        card.add(Box.createVerticalStrut(6));
        card.add(lblError);
        card.add(Box.createVerticalStrut(20));
        card.add(btnIngresar);

        return card;
    }

    private JLabel makeLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_PRI);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Lógica ───────────────────────────────────────────────────────────
    private void intentarLogin() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }

        Usuario usuario = loginController.login(user, pass);

        if (usuario == null) {
            mostrarError("Usuario o contraseña incorrectos.");
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

    // ════════════════════════════════════════════════════════════════════
    // Componentes reutilizables
    // ════════════════════════════════════════════════════════════════════

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
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(focused ? BORDER_F : BORDER_N);
            g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 10, 10));
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(getFont());
                g3.setColor(new Color(0xAEADA6));
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
            setEchoChar('•');
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
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(focused ? BORDER_F : BORDER_N);
            g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundButton extends JButton {
        private boolean hovered = false;

        RoundButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(LEFT_ALIGNMENT);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hovered ? ACCENT_HOV : ACCENT);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}