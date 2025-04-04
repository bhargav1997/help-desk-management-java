package view;

import models.Ticket;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TicketTableModel extends AbstractTableModel {
    private final List<Ticket> tickets;
    private final String[] columnNames = {
        "ID", "Title", "Status", "Priority", "Created", "Assigned To", "Action"
    };

    public TicketTableModel(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public int getRowCount() {
        return tickets.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ticket ticket = tickets.get(rowIndex);
        switch (columnIndex) {
            case 0: return ticket.getTicketId();
            case 1: return ticket.getTitle();
            case 2: return ticket.getStatus();
            case 3: return ticket.getPriority();
            case 4: return ticket.getCreatedAt().toString();
             case 5: // Assigned To column
                if (ticket.getAssignedTo() != null) {
                    return ticket.getAssignedTo().getFullName();
                } else {
                    return "Unassigned";
                }
            case 6: // Action column
                return "Assign";
            default: return null;
        }
    }

    public Ticket getTicketAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tickets.size()) {
            return null;
        }
        return tickets.get(rowIndex);
    }
}