package view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundTextField extends JTextField {
    private static final Color BORDER_N = new Color(0xD3D1C7);
    private static final Color BORDER_F = new Color(0x185FA5);
    private boolean focused = false;

    public RoundTextField() {
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setOpaque(false);
        setBorder(new EmptyBorder(8, 10, 8, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        setAlignmentX(LEFT_ALIGNMENT);
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { focused = true;  repaint(); }
            public void focusLost (FocusEvent e)  { focused = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        g2.setColor(focused ? BORDER_F : BORDER_N);
        g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                getWidth()-1, getHeight()-1, 8, 8));
        g2.dispose();
        super.paintComponent(g);
    }
}