package view.reportes;

import dao.BitacoraDAO;
import model.Bitacora;
import util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ReporteIngresosPanel
 * "Listar los usuarios que hayan ingresado y salido de la aplicación
 *  en una fecha y hora específica."
 */
public class ReporteIngresosPanel extends JPanel {

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
    private JTextField        txtDesde, txtHasta;

    private final BitacoraDAO dao = new BitacoraDAO();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ReporteIngresosPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTabla(),   BorderLayout.CENTER);
        add(buildFiltro(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JLabel t = new JLabel("Reporte: ingresos y salidas del sistema");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(TEXT_PRI);
        JLabel s = new JLabel("Filtra por rango de fecha y hora (dd/MM/yyyy HH:mm)");
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
                new String[]{"Usuario", "Fecha entrada", "Fecha salida"}, 0) {
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

    private JPanel buildFiltro() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(8, 0, 0, 0));

        txtDesde = makeTextField("01/01/2026 00:00");
        txtHasta = makeTextField("31/12/2026 23:59");
        txtDesde.setPreferredSize(new Dimension(180, 38));
        txtDesde.setMaximumSize(new Dimension(180, 38));
        txtHasta.setPreferredSize(new Dimension(180, 38));
        txtHasta.setMaximumSize(new Dimension(180, 38));

        JButton btnBuscar  = makeButton("Buscar",          ACCENT,               ACCENT_H);
        JButton btnPDF     = makeButton("Generar PDF",     new Color(0x0F6E56),  new Color(0x085041));

        btnBuscar.addActionListener(e -> ejecutarConsulta());
        btnPDF.addActionListener   (e -> generarPDF());

        JLabel lblDesde = makeLabel("Desde:");
        JLabel lblHasta = makeLabel("Hasta:");

        p.add(lblDesde);
        p.add(Box.createHorizontalStrut(6));
        p.add(txtDesde);
        p.add(Box.createHorizontalStrut(12));
        p.add(lblHasta);
        p.add(Box.createHorizontalStrut(6));
        p.add(txtHasta);
        p.add(Box.createHorizontalStrut(12));
        p.add(btnBuscar);
        p.add(Box.createHorizontalStrut(8));
        p.add(btnPDF);
        return p;
    }

    private void ejecutarConsulta() {
        try {
            Timestamp desde = new Timestamp(SDF.parse(txtDesde.getText().trim()).getTime());
            Timestamp hasta = new Timestamp(SDF.parse(txtHasta.getText().trim()).getTime());
            modelo.setRowCount(0);
            List<Bitacora> lista = dao.listarPorFecha(desde, hasta);
            for (Bitacora b : lista) {
                modelo.addRow(new Object[]{
                        b.getUsernameUsuario(),
                        b.getFechaEntrada() != null ? SDF.format(b.getFechaEntrada()) : "—",
                        b.getFechaSalida()  != null ? SDF.format(b.getFechaSalida())  : "Activo"
                });
            }
            if (lista.isEmpty())
                JOptionPane.showMessageDialog(this, "Sin resultados para ese rango.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido.\nUsa: dd/MM/yyyy HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarPDF() {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero ejecuta la consulta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("reporte_ingresos.pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            List<String[]> filas = new ArrayList<>();
            for (int i = 0; i < modelo.getRowCount(); i++)
                filas.add(new String[]{
                        String.valueOf(modelo.getValueAt(i, 0)),
                        String.valueOf(modelo.getValueAt(i, 1)),
                        String.valueOf(modelo.getValueAt(i, 2))
                });
            PDFGenerator.generar(
                    fc.getSelectedFile().getAbsolutePath(),
                    "Reporte de ingresos y salidas",
                    "Rango: " + txtDesde.getText() + " — " + txtHasta.getText(),
                    new String[]{"Usuario", "Fecha entrada", "Fecha salida"},
                    filas
            );
            JOptionPane.showMessageDialog(this, "PDF generado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel makeLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SEC);
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder) {
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
        tf.setBorder(new EmptyBorder(8, 10, 8, 10));
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
        btn.setPreferredSize(new Dimension(150, 38));
        btn.setMaximumSize(new Dimension(150, 38));
        return btn;
    }
}