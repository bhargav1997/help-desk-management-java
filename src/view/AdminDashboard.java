package view;

import models.Ticket;
import models.User;
import model.enums.Status;
import service.AuthService;
import service.TicketService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import service.CommentService;

public class AdminDashboard extends JFrame {
    private final User currentAdmin;
    private final TicketService ticketService;
    private JTable ticketsTable;
    private final AuthService authService;
    private final CommentService commentService;

    public AdminDashboard(User admin, AuthService authService, TicketService ticketService, CommentService commentService) {
        this.currentAdmin = admin;
        this.authService = authService;
        this.ticketService = ticketService;
        this.commentService = commentService;
        initializeUI();
        refreshTickets();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - " + currentAdmin.getFullName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Top panel with admin info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Admin: " + currentAdmin.getFullName()), BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(this::handleLogout);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        
        // Tickets table
        ticketsTable = new JTable();
        ticketsTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTickets());
        controlPanel.add(refreshBtn);
        
        // Assemble components
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Add right-click menu for ticket actions
        addTicketContextMenu();
        
        add(mainPanel);
    }

    private void refreshTickets() {
        try {
            List<Ticket> assignedTickets = ticketService.getAssignedTickets(currentAdmin.getUserId());
            ticketsTable.setModel(new AdminTicketTableModel(assignedTickets));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading tickets: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTicketContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem viewItem = new JMenuItem("View Details");
        viewItem.addActionListener(e -> {
            Ticket ticket = getSelectedTicket();
            if (ticket != null) {
                showTicketDetails(ticket);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No ticket selected.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem inProgressItem = new JMenuItem("Mark In Progress");
        inProgressItem.addActionListener(e -> updateTicketStatus(getSelectedTicket(), Status.IN_PROGRESS));
        
        JMenuItem resolveItem = new JMenuItem("Resolve Ticket");
        resolveItem.addActionListener(e -> updateTicketStatus(getSelectedTicket(), Status.RESOLVED));
        
        JMenuItem closeItem = new JMenuItem("Close Ticket");
        closeItem.addActionListener(e -> updateTicketStatus(getSelectedTicket(), Status.CLOSED));
        
        popupMenu.add(viewItem);
        popupMenu.addSeparator();
        popupMenu.add(inProgressItem);
        popupMenu.add(resolveItem);
        popupMenu.add(closeItem);
        
        ticketsTable.setComponentPopupMenu(popupMenu);
    }

    private Ticket getSelectedTicket() {
        int row = ticketsTable.getSelectedRow();
        if (row >= 0) {
            AdminTicketTableModel model = (AdminTicketTableModel) ticketsTable.getModel();
            return model.getTicketAt(ticketsTable.convertRowIndexToModel(row));
        }
        return null;
    }

    private void updateTicketStatus(Ticket ticket, Status newStatus) {
        if (ticket == null) return;
        
        String feedback = JOptionPane.showInputDialog(this, 
            "Enter feedback/comments:", 
            "Ticket #" + ticket.getTicketId() + " - " + newStatus,
            JOptionPane.PLAIN_MESSAGE);
        
        if (feedback != null) {
            try {
                if (ticketService.updateTicketStatus(ticket.getTicketId(), newStatus, feedback)) {
                    refreshTickets();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error updating ticket: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTicketDetails(Ticket ticket) {
        // Show the reusable dialog with comment support
        UserTicketDetailsDialog dialog = new UserTicketDetailsDialog(this, ticket, currentAdmin, commentService);
        dialog.setVisible(true);
    }

    private void handleLogout(ActionEvent e) {
        this.dispose();
        // Return to login screen
    }
}