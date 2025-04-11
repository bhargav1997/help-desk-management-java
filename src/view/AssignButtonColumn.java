package view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class AssignButtonColumn extends AbstractCellEditor 
        implements TableCellRenderer, TableCellEditor {
    private final JTable table;
    private final AssignAction action;
    private final JButton renderButton;
    private final JButton editButton;
    private int currentRow;

    public AssignButtonColumn(JTable table, AssignAction action, int column) {
        this.table = table;
        this.action = action;
        
        renderButton = new JButton("Assign");
        editButton = new JButton("Assign");
        
        // Style buttons for better appearance
        styleButton(renderButton);
        styleButton(editButton);
        
        editButton.addActionListener(e -> {
            fireEditingStopped();
            action.assignTicket(currentRow);
        });
        
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    private void styleButton(JButton button) {
        button.setMargin(new Insets(2, 5, 2, 5));
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, 
            boolean hasFocus, int row, int column) {
        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        currentRow = table.convertRowIndexToModel(row); // Fix: Convert view row to model row
        return editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return "Assign";
    }

    public interface AssignAction {
        void assignTicket(int row);
    }
}