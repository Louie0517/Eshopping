package ui.components;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(ImageIcon icon, int radius) {
        super(icon);
        this.radius = radius;
        setContentAreaFilled(false); 
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false); 
    }

    public RoundedButton(String txt, int radius) {
        super(txt);
        this.radius = radius;
        setContentAreaFilled(false); 
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

      
        super.paintComponent(g2);
        g2.dispose();
    }
}
