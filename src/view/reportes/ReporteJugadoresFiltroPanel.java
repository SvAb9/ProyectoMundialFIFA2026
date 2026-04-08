package view.reportes;

import controller.JugadorController;
import dao.EquipoDAO;
import model.Equipo;
import model.Jugador;
import util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * ReporteJugadoresFiltroPanel
 * "Listar los jugadores cuyo peso, estatura y equipo están dentro de lo solicitado."
 */
public class ReporteJugadoresFiltroPanel extends JPanel {

    private static final Color BG      = new Color(0xF5F5F2);
    private static final Color CARD    = Color.WHITE;
    private static final Color ACCENT  = new Color(0x185FA5);
    private static final Color ACCENT_H= new Color(0x0C447C);
    private static final Color TEXT_PRI= new Color(0x1A1A18);
    private static final Color TEXT_SEC= new Color(0x6B6B67);
    private static final Color BORDER_N= new Color(0xD3D1C7);
    private static final Color BORDER_F= new Color(0x185FA5);
    private static final Color TH_BG   = new Color(0xF1EFE8);
    private static final Color SEL_BG  = new Color(0xE6F1FB);

    private JTable            tabla;
    private DefaultTableModel modelo;
    private JTextField        txtPesoMin, txtPesoMax, txtEstMin, txtEstMax;
    private JComboBox<String> cmbEquipo;

    private final JugadorController controller = new JugadorController();
    private final EquipoDAO         equipoDAO  = new EquipoDAO();
    private List<Equipo>            equipos;

    public ReporteJugadoresFiltroPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buildHeader(),   BorderLayout.NORTH);
        add(buildTabla(),    BorderLayout.CENTER);
        add(buildFiltros(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JLabel t = new JLabel("Reporte: jugadores por filtro");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(TEXT_PRI);
        JLabel s = new JLabel("Filtra por peso (kg), estatura (m) y equipo");
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
                new String[]{"Nombre", "Apellido", "Equipo", "Posición", "Peso", "Estatura", "Valor (€)"}, 0) {
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

    private JPanel buildFiltros() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(8, 0, 0, 0));

        txtPesoMin = makeTextField("50");
        txtPesoMax = makeTextField("120");
        txtEstMin  = makeTextField("1.50");
        txtEstMax  = makeTextField("2.20");
        for (JTextField tf : new JTextField[]{txtPesoMin, txtPesoMax, txtEstMin, txtEstMax}) {
            tf.setPreferredSize(new Dimension(70, 38));
            tf.setMaximumSize(new Dimension(70, 38));
        }

        // Combo equipo — "Todos" como primera opción
        equipos = equipoDAO.listarTodos();
        cmbEquipo = new JComboBox<>();
        cmbEquipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEquipo.addItem("Todos");
        for (Equipo e : equipos) cmbEquipo.addItem(e.getNombre());
        cmbEquipo.setPreferredSize(new Dimension(160, 38));
        cmbEquipo.setMaximumSize(new Dimension(160, 38));

        JButton btnBuscar = makeButton("Buscar",       ACCENT,              ACCENT_H);
        JButton btnPDF    = makeButton("Generar PDF",  new Color(0x0F6E56), new Color(0x085041));

        btnBuscar.addActionListener(e -> ejecutarConsulta());
        btnPDF.addActionListener   (e -> generarPDF());

        p.add(makeLabel("Peso min:")); p.add(Box.createHorizontalStrut(4)); p.add(txtPesoMin);
        p.add(Box.createHorizontalStrut(8));
        p.add(makeLabel("máx:"));     p.add(Box.createHorizontalStrut(4)); p.add(txtPesoMax);
        p.add(Box.createHorizontalStrut(12));
        p.add(makeLabel("Est. min:")); p.add(Box.createHorizontalStrut(4)); p.add(txtEstMin);
        p.add(Box.createHorizontalStrut(8));
        p.add(makeLabel("máx:"));     p.add(Box.createHorizontalStrut(4)); p.add(txtEstMax);
        p.add(Box.createHorizontalStrut(12));
        p.add(makeLabel("Equipo:"));  p.add(Box.createHorizontalStrut(4)); p.add(cmbEquipo);
        p.add(Box.createHorizontalStrut(12));
        p.add(btnBuscar);
        p.add(Box.createHorizontalStrut(8));
        p.add(btnPDF);
        return p;
    }

    private void ejecutarConsulta() {
        try {
            double pesoMin = Double.parseDouble(txtPesoMin.getText().trim());
            double pesoMax = Double.parseDouble(txtPesoMax.getText().trim());
            double estMin  = Double.parseDouble(txtEstMin.getText().trim());
            double estMax  = Double.parseDouble(txtEstMax.getText().trim());

            int idEquipo = 0;
            int selIdx = cmbEquipo.getSelectedIndex();
            if (selIdx > 0) idEquipo = equipos.get(selIdx - 1).getIdEquipo();

            modelo.setRowCount(0);
            List<Jugador> lista = controller.buscarPorFiltro(pesoMin, pesoMax, estMin, estMax, idEquipo);
            for (Jugador j : lista) {
                modelo.addRow(new Object[]{
                        j.getNombre(), j.getApellido(), j.getNombreEquipo(),
                        j.getPosicion(),
                        j.getPeso() + " kg",
                        j.getEstatura() + " m",
                        String.format("€ %,.2f", j.getValor())
                });
            }
            if (lista.isEmpty())
                JOptionPane.showMessageDialog(this, "Sin resultados para ese filtro.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarPDF() {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero ejecuta la consulta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("reporte_jugadores.pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            List<String[]> filas = new ArrayList<>();
            for (int i = 0; i < modelo.getRowCount(); i++)
                filas.add(new String[]{
                        String.valueOf(modelo.getValueAt(i, 0)),
                        String.valueOf(modelo.getValueAt(i, 1)),
                        String.valueOf(modelo.getValueAt(i, 2)),
                        String.valueOf(modelo.getValueAt(i, 3)),
                        String.valueOf(modelo.getValueAt(i, 4)),
                        String.valueOf(modelo.getValueAt(i, 5)),
                        String.valueOf(modelo.getValueAt(i, 6))
                });
            PDFGenerator.generar(
                    fc.getSelectedFile().getAbsolutePath(),
                    "Reporte de jugadores por filtro",
                    "Peso: " + txtPesoMin.getText() + "-" + txtPesoMax.getText() +
                            " kg | Estatura: " + txtEstMin.getText() + "-" + txtEstMax.getText() + " m",
                    new String[]{"Nombre", "Apellido", "Equipo", "Posición", "Peso", "Estatura", "Valor"},
                    filas
            );
            JOptionPane.showMessageDialog(this, "PDF generado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel makeLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SEC);
        return lbl;
    }

    private JTextField makeTextField(String valor) {
        JTextField tf = new JTextField(valor) {
            private boolean focused = false;
            { addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent e) { focused = true;  repaint(); }
                public void focusLost (java.awt.event.FocusEvent e)  { focused = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(focused ? BORDER_F : BORDER_N);
                g2.setStroke(new java.awt.BasicStroke(focused ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(8, 8, 8, 8));
        return tf;
    }

    private JButton makeButton(String texto, Color bg, Color hover) {
        JButton btn = new JButton(texto) {
            private boolean hovered = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? hover : bg);
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
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setMaximumSize(new Dimension(140, 38));
        return btn;
    }
}