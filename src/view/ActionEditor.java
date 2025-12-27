package view;

import java.awt.Component;
import javax.swing.*;

public class ActionEditor extends DefaultCellEditor {
    private JPanel panel;
    private final ActionPanelProvider provider;

    public ActionEditor(ActionPanelProvider provider, JTable table) {
        super(new JCheckBox());
        this.provider = provider;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        panel = provider.createActionPanel(row);
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Actions";
    }
}
