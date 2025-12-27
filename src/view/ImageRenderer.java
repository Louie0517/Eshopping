package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import resources.colors.ProductColors;

public class ImageRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setOpaque(true);
        
        if (!isSelected) {
            label.setBackground(row % 2 == 0 ? Color.WHITE : ProductColors.BACKGROUND);
        } else {
            label.setBackground(table.getSelectionBackground());
        }
        
        if (value != null && !value.toString().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(value.toString());
                Image scaledImg = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImg));
            } catch (Exception e) {
                label.setText("🖼️");
                label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
            }
        } else {
            label.setText("🖼️");
            label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        }
        
        return label;
    }

}
