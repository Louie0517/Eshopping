package util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

public class NotifBadgeUtil {
    public JLayeredPane panel;
    public JLabel badge;

    public NotifBadgeUtil(JLayeredPane panel, JLabel badge) {
        this.panel = panel;
        this.badge = badge;
    }


    public static NotifBadgeUtil createNotifBadge(JButton baseButton, int count) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(baseButton.getPreferredSize());

        baseButton.setBounds(0, 0,
                baseButton.getPreferredSize().width,
                baseButton.getPreferredSize().height);

        JLabel badge = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(new Color(220, 38, 38));
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBounds(
                baseButton.getPreferredSize().width - 22,
                4,
                18,
                18
        );
        badge.setVisible(count > 0);

        layeredPane.add(baseButton, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(badge, JLayeredPane.PALETTE_LAYER);

        return new NotifBadgeUtil(layeredPane, badge);
    }

}
