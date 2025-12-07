package ui.components;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ScrollBar {
    public static void styleScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            private final Dimension d = new Dimension();
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override    
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(d);
                button.setMinimumSize(d);
                button.setMaximumSize(d);
                return button;
            }
            
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(180, 180, 200);
                this.thumbDarkShadowColor = new Color(180, 180, 200);
                this.thumbHighlightColor = new Color(180, 180, 200);
                this.thumbLightShadowColor = new Color(180, 180, 200);
                this.trackColor = new Color(240, 240, 245);
                this.trackHighlightColor = new Color(240, 240, 245);
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color = null;
                JScrollBar sb = (JScrollBar) c;
                if (!sb.isEnabled() || thumbBounds.width > thumbBounds.height) {
                    return;
                }
                
                if (isDragging) {
                    color = new Color(140, 140, 170);
                } else if (isThumbRollover()) {
                    color = new Color(160, 160, 190);
                } else {
                    color = new Color(180, 180, 200);
                }
                
                g2.setPaint(color);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                               thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new Color(245, 245, 250));
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });
        
        verticalBar.setUnitIncrement(16);
        verticalBar.setPreferredSize(new Dimension(8, 0));
    }
}
