package view;

import java.awt.Component;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import resources.colors.ProductColors;

public class ActionRenderer implements TableCellRenderer {
    private final ActionPanelProvider provider;

    public ActionRenderer(ActionPanelProvider provider) {
        this.provider = provider;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JPanel panel = provider.createActionPanel(row);

        if (!isSelected) {
            panel.setBackground(row % 2 == 0 ? Color.WHITE : ProductColors.BACKGROUND);
        } else {
            panel.setBackground(table.getSelectionBackground());
        }
        return panel;
    }
}

