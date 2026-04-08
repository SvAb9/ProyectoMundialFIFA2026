package view.consultas;

import controller.JugadorController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * ConsultaMenores21Panel
 * "Determinar la cantidad de jugadores por equipo que tienen menos de 21 años."
 */
public class ConsultaMenores21Panel extends JPanel {

    private static final Color BG      = new Color(0xF5F5F2);
    private static final Color CARD    = Color.WHITE;
    private static final Color ACCENT  = new Color(0x185FA5);
    private static final Color ACCENT_H= new Color(0x0C447C);
    private static final Color TEXT_PRI= new Color(0x1A1A18);
    private static final Color TEXT_SEC= new Color(0x6B6B67);
    private static final Color BORDER_N= new Color(0xD3D1C7);
    private static final Color TH_BG   = new Color(0xF1EFE8);
    private static final Color SEL_BG  = new Color(0xE6F1FB);

    private JTable            tabla;
    private DefaultTableModel modelo;

    private final JugadorController controller = new JugadorController();

    public ConsultaMenores21Panel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabla(),  BorderLayout.CENTER);
        add(buildBoton(),  BorderLayout.SOUTH);
        ejecutarConsulta();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JLabel t = new JLabel("Jugadores menores de 21 años por equipo");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(TEXT_PRI);
        JLabel s = new JLabel("Cantidad de jugadores sub-21 en cada selección");
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
                new String[]{"Equipo", "Jugadores menores de 21"}, 0) {
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

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_N, 1, true));
        scroll.getViewport().setBackground(CARD);
        return scroll;
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
        modelo.setRowCount(0);
        List<String[]> lista = controller.jugadoresMenores21PorEquipo();
        for (String[] fila : lista) {
            modelo.addRow(new Object[]{fila[0], fila[1]});
        }
        if (lista.isEmpty()) {
            modelo.addRow(new Object[]{"Sin datos", "0"});
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