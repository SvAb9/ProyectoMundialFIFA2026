package view.consultas;

import controller.EquipoController;
import model.Equipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * ConsultaEquipoCostosoPanel
 * "Determinar el equipo más costoso de los que van a jugar
 *  en cada país (México, USA, Canadá) en la fase de grupos."
 */
public class ConsultaEquipoCostosoPanel extends JPanel {

    private static final Color BG      = new Color(0xF5F5F2);
    private static final Color CARD    = Color.WHITE;
    private static final Color ACCENT  = new Color(0x185FA5);
    private static final Color ACCENT_H= new Color(0x0C447C);
    private static final Color TEXT_PRI= new Color(0x1A1A18);
    private static final Color TEXT_SEC= new Color(0x6B6B67);
    private static final Color BORDER_N= new Color(0xD3D1C7);

    private JLabel lblMexico, lblUSA, lblCanada;
    private JLabel lblValMexico, lblValUSA, lblValCanada;
    private JLabel lblConfMexico, lblConfUSA, lblConfCanada;

    private final EquipoController controller = new EquipoController();

    public ConsultaEquipoCostosoPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buildHeader(),     BorderLayout.NORTH);
        add(buildResultados(), BorderLayout.CENTER);
        add(buildBoton(),      BorderLayout.SOUTH);
        ejecutarConsulta();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JLabel t = new JLabel("Equipo más costoso por país anfitrión");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(TEXT_PRI);
        JLabel s = new JLabel("Equipo con mayor valor total en cada sede");
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        s.setForeground(TEXT_SEC);
        JPanel tp = new JPanel();
        tp.setLayout(new BoxLayout(tp, BoxLayout.Y_AXIS));
        tp.setBackground(BG);
        tp.add(t); tp.add(s);
        p.add(tp, BorderLayout.WEST);
        return p;
    }

    private JPanel buildResultados() {
        JPanel p = new JPanel(new GridLayout(1, 3, 16, 0));
        p.setBackground(BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        lblMexico    = new JLabel("—");
        lblUSA       = new JLabel("—");
        lblCanada    = new JLabel("—");
        lblValMexico = new JLabel("—");
        lblValUSA    = new JLabel("—");
        lblValCanada = new JLabel("—");
        lblConfMexico= new JLabel("—");
        lblConfUSA   = new JLabel("—");
        lblConfCanada= new JLabel("—");

        p.add(buildCard("🇲🇽 México",  lblMexico,  lblConfMexico, lblValMexico,  new Color(0x006847)));
        p.add(buildCard("🇺🇸 USA",     lblUSA,     lblConfUSA,    lblValUSA,     new Color(0x3C3B6E)));
        p.add(buildCard("🇨🇦 Canadá",  lblCanada,  lblConfCanada, lblValCanada,  new Color(0xC8102E)));
        return p;
    }

    private JPanel buildCard(String pais, JLabel nombre, JLabel conf, JLabel valor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 6, 12, 12);
                g2.fillRect(0, 0, getWidth(), 6);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblPais = new JLabel(pais);
        lblPais.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPais.setForeground(TEXT_SEC);
        lblPais.setAlignmentX(LEFT_ALIGNMENT);

        nombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nombre.setForeground(TEXT_PRI);
        nombre.setAlignmentX(LEFT_ALIGNMENT);

        conf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        conf.setForeground(TEXT_SEC);
        conf.setAlignmentX(LEFT_ALIGNMENT);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valor.setForeground(ACCENT);
        valor.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lblPais);
        card.add(Box.createVerticalStrut(12));
        card.add(nombre);
        card.add(Box.createVerticalStrut(4));
        card.add(conf);
        card.add(Box.createVerticalStrut(8));
        card.add(valor);
        return card;
    }

    private JPanel buildBoton() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(BG);
        JButton btn = makeButton("Actualizar consulta");
        btn.addActionListener(e -> ejecutarConsulta());
        p.add(btn);
        return p;
    }

    private void ejecutarConsulta() {
        actualizarCard("Mexico",  lblMexico,  lblConfMexico, lblValMexico);
        actualizarCard("USA",     lblUSA,     lblConfUSA,    lblValUSA);
        actualizarCard("Canada",  lblCanada,  lblConfCanada, lblValCanada);
    }

    private void actualizarCard(String pais, JLabel nombre, JLabel conf, JLabel valor) {
        Equipo e = controller.equipoMasCostosoPorPais(pais);
        if (e != null) {
            nombre.setText(e.getNombre());
            conf.setText(e.getNombreConfederacion());
            valor.setText(String.format("€ %,.2f", e.getValorTotal()));
        } else {
            nombre.setText("Sin datos");
            conf.setText("—");
            valor.setText("—");
        }
    }

    private JButton makeButton(String texto) {
        JButton btn = new JButton(texto) {
            private boolean hovered = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? ACCENT_H : ACCENT);
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
        btn.setPreferredSize(new Dimension(200, 38));
        return btn;
    }
}