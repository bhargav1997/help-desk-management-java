package view;

import models.Ticket;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class AdminTicketTableModel extends AbstractTableModel {
    private final List<Ticket> tickets;
    private final String[] columnNames = {
        "ID", "Title", "Status", "Priority", "Created By", "Created At", "Last Updated"
    };

    public AdminTicketTableModel(List<Ticket> tickets) {
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
        return switch (columnIndex) {
            case 0 -> ticket.getTicketId();
            case 1 -> ticket.getTitle();
            case 2 -> ticket.getStatus();
            case 3 -> ticket.getPriority();
            case 4 -> ticket.getCreatedBy().getFullName();
            case 5 -> ticket.getCreatedAt().toString();
            case 6 -> ticket.getUpdatedAt() != null ? ticket.getUpdatedAt().toString() : "N/A";
            default -> null;
        };
    }

    public Ticket getTicketAt(int rowIndex) {
        return tickets.get(rowIndex);
    }
}