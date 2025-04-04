/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import models.User;
import models.Ticket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import service.AuthService;
import service.TicketService;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Admin
 */
public class UserDashboard extends JFrame {
       private final User currentUser;
    private final AuthService authService;
    private final TicketService ticketService;
    private JTable ticketsTable;

    public UserDashboard(User user, AuthService authService, TicketService ticketService) {
        this.currentUser = user;
        this.authService = authService;
        this.ticketService = ticketService;
        initializeUI();
        refreshTickets();
    }

    private void initializeUI() {
    setTitle("User Dashboard - " + currentUser.getFullName());
    setSize(1000, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());
    
    // Top panel with user info
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(new JLabel("Welcome, " + currentUser.getFullName()), BorderLayout.WEST);
    
    JButton logoutBtn = new JButton("Logout");
    logoutBtn.addActionListener(this::handleLogout);
    topPanel.add(logoutBtn, BorderLayout.EAST);
    
    // Ticket table setup
    ticketsTable = new JTable();
    ticketsTable.setRowHeight(30);
    ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(ticketsTable);
    
    // Mouse listener for button clicks and row selection
    ticketsTable.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = ticketsTable.rowAtPoint(e.getPoint());
            int col = ticketsTable.columnAtPoint(e.getPoint());
            
            if (row >= 0 && row < ticketsTable.getRowCount()) {
                if (col == 6) { // Action button column
                    handleAssignAction(row);
                } else if (e.getClickCount() == 2) { // Double-click
                    showTicketDetails(getTicketAtRow(row));
                }
            }
        }
    });
    
    // Bottom panel
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton createBtn = new JButton("Create New Ticket");
    createBtn.addActionListener(this::handleCreateTicket);
    bottomPanel.add(createBtn);
    
    // Assemble components
    mainPanel.add(topPanel, BorderLayout.NORTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    
    add(mainPanel);
    
    // Initial data load
    refreshTickets();
}

private void handleAssignAction(int viewRowIndex) {
    int modelRow = ticketsTable.convertRowIndexToModel(viewRowIndex);
    Ticket ticket = getTicketAtRow(modelRow);
    
    if (ticket == null) {
        JOptionPane.showMessageDialog(this,
            "No ticket selected or ticket data is unavailable",
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    showAssignmentDialog(ticket);
}

private Ticket getTicketAtRow(int row) {
    TicketTableModel model = (TicketTableModel) ticketsTable.getModel();
    return model.getTicketAt(ticketsTable.convertRowIndexToModel(row));
}
    
//    private void initializeUI() {
//        setTitle("User Dashboard - " + currentUser.getFullName());
//        setSize(1000, 600);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        // Main panel with BorderLayout
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        
//        // Top panel with user info
//        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.add(new JLabel("Welcome, " + currentUser.getFullName()), BorderLayout.WEST);
//        
//        JButton logoutBtn = new JButton("Logout");
//        logoutBtn.addActionListener(this::handleLogout);
//        topPanel.add(logoutBtn, BorderLayout.EAST);
//        
//        // Ticket table
//        ticketsTable = new JTable() {
//            @Override
//            public Class<?> getColumnClass(int column) {
//                return column == 6 ? JButton.class : Object.class;
//            }
//        };
//        ticketsTable.setRowHeight(30);        
//        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//        JScrollPane scrollPane = new JScrollPane(ticketsTable);
//        
//
//        
//        // Add this after table initialization:
//        ticketsTable.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//                int row = ticketsTable.rowAtPoint(evt.getPoint());
//                int col = ticketsTable.columnAtPoint(evt.getPoint());
//
//                if (row >= 0 && col == 6) { // If assign button column clicked
//                    handleAssignTicket(row);
//                } else if (evt.getClickCount() == 2 && row >= 0) { // Double click
//                    TicketTableModel model = (TicketTableModel)ticketsTable.getModel();
//                    Ticket ticket = model.getTicketAt(ticketsTable.convertRowIndexToModel(row));
//                    showTicketDetails(ticket);
//                }
//            }
//        });
//        
//        // Bottom panel
//        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton createBtn = new JButton("Create New Ticket");
//        createBtn.addActionListener(this::handleCreateTicket);
//        bottomPanel.add(createBtn);
//   
//        // Assemble components
//        mainPanel.add(topPanel, BorderLayout.NORTH);
//        mainPanel.add(scrollPane, BorderLayout.CENTER);
//        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
//        
//        add(mainPanel);
//    }
//

private void showAssignmentDialog(Ticket ticket) {
    try {
        List<User> admins = ticketService.getAllAdmins();
        if (admins.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No administrators available for assignment",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Assign Ticket #" + ticket.getTicketId(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setPreferredSize(new Dimension(400, 300));

        // Admin selection combo box
        JComboBox<User> adminCombo = new JComboBox<>();
        DefaultComboBoxModel<User> model = new DefaultComboBoxModel<>();
        admins.forEach(model::addElement);
        adminCombo.setModel(model);
        
        adminCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User admin = (User) value;
                    setText(admin.getFullName() + " (" + admin.getEmail() + ")");
                }
                return this;
            }
        });

        // Assignment notes (optional)
        JTextArea notesArea = new JTextArea(5, 30);
        notesArea.setLineWrap(true);

        // Submit button
        JButton assignBtn = new JButton("Assign Ticket");
        assignBtn.addActionListener(e -> {
            User selectedAdmin = (User) adminCombo.getSelectedItem();
            try {
                if (ticketService.assignTicket(ticket.getTicketId(), selectedAdmin.getUserId())) {
                    JOptionPane.showMessageDialog(dialog,
                        "Ticket successfully assigned to " + selectedAdmin.getFullName(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTickets();
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to assign ticket: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Layout components
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Select Administrator:"));
        formPanel.add(adminCombo);
        formPanel.add(new JLabel("Assignment Notes (Optional):"));
        formPanel.add(new JScrollPane(notesArea));

        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(assignBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error loading administrator list: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
//    private void showAssignmentDialog(Ticket ticket) {
//        try {
//            List<User> admins = ticketService.getAllAdmins();
//            if (admins.isEmpty()) {
//                JOptionPane.showMessageDialog(this,
//                    "No administrators available",
//                    "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            JDialog dialog = new JDialog(this, "Assign Ticket #" + ticket.getTicketId(), true);
//            dialog.setLayout(new BorderLayout());
//            dialog.setPreferredSize(new Dimension(400, 300));
//
//            // Admin selection
//            JComboBox<User> adminCombo = new JComboBox<>();
//            for (User admin : admins) {
//                adminCombo.addItem(admin);
//            }
//            
//            adminCombo.setRenderer(new DefaultListCellRenderer() {
//                @Override
//                public Component getListCellRendererComponent(JList<?> list, Object value, 
//                        int index, boolean isSelected, boolean cellHasFocus) {
//                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//                    if (value instanceof User) {
//                        User admin = (User) value;
//                        setText(admin.getFullName() + " (" + admin.getEmail() + ")");
//                    }
//                    return this;
//                }
//            });
//            
//            // Assignment notes
//            JTextArea notesArea = new JTextArea(5, 30);
//            notesArea.setLineWrap(true);
//            
//            // Submit button
//            JButton assignBtn = new JButton("Assign Ticket");
//            assignBtn.addActionListener(e -> {
//                User selectedAdmin = (User) adminCombo.getSelectedItem();
//                try {
//                    if (ticketService.assignTicket(
//                            ticket.getTicketId(), 
//                            selectedAdmin.getUserId())) {
//                        JOptionPane.showMessageDialog(dialog,
//                            "Ticket assigned to " + selectedAdmin.getFullName(),
//                            "Success", JOptionPane.INFORMATION_MESSAGE);
//                        refreshTickets();
//                        dialog.dispose();
//                    }
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(dialog,
//                        "Assignment failed: " + ex.getMessage(),
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            });
//            
//            // Layout components
//            JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
//            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//            
//            formPanel.add(new JLabel("Select Administrator:"));
//            formPanel.add(adminCombo);
//            formPanel.add(new JLabel("Assignment Notes:"));
//            formPanel.add(new JScrollPane(notesArea));
//            
//            dialog.add(formPanel, BorderLayout.CENTER);
//            
//            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//            buttonPanel.add(assignBtn);
//            dialog.add(buttonPanel, BorderLayout.SOUTH);
//            
//            dialog.pack();
//            dialog.setLocationRelativeTo(this);
//            dialog.setVisible(true);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this,
//                "Error loading administrators: " + ex.getMessage(),
//                "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    private void refreshTickets() {
        try {
            List<Ticket> tickets = ticketService.getTicketsByUser(currentUser.getUserId());
            ticketsTable.setModel(new TicketTableModel(tickets));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading tickets: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateTicket(ActionEvent e) {
        JDialog createDialog = new JDialog(this, "Create New Ticket", true);
        createDialog.setSize(500, 300);
        createDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField titleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScroll);
        
        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(ev -> {
            try {
                Ticket newTicket = ticketService.createTicket(
                    titleField.getText(),
                    descriptionArea.getText(),
                    currentUser
                );
                
                if (newTicket != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Ticket created successfully! ID: " + newTicket.getTicketId());
                    refreshTickets();
                    createDialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error creating ticket: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(new JLabel());
        panel.add(submitBtn);
        
        createDialog.add(panel);
        createDialog.setVisible(true);
    }


private void showTicketDetails(Ticket ticket) {
    
     if (ticket == null) {
        JOptionPane.showMessageDialog(this,
            "No ticket selected or ticket data is unavailable",
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    JDialog detailsDialog = new JDialog(this, "Ticket Details", true);
    detailsDialog.setSize(600, 400);
    detailsDialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new BorderLayout());
    
    // Ticket information
    JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
    infoPanel.add(new JLabel("Ticket ID:"));
    infoPanel.add(new JLabel(String.valueOf(ticket.getTicketId())));
    infoPanel.add(new JLabel("Title:"));
    infoPanel.add(new JLabel(ticket.getTitle()));
    infoPanel.add(new JLabel("Status:"));
    infoPanel.add(new JLabel(ticket.getStatus().toString()));
    infoPanel.add(new JLabel("Priority:"));
    infoPanel.add(new JLabel(ticket.getPriority().toString()));
    infoPanel.add(new JLabel("Created:"));
    infoPanel.add(new JLabel(ticket.getCreatedAt().toString()));
    infoPanel.add(new JLabel("Assigned To:"));
    infoPanel.add(new JLabel(ticket.getAssignedTo() != null ? 
        ticket.getAssignedTo().getFullName() : "Unassigned"));
    
    // Description area
    JTextArea descriptionArea = new JTextArea(ticket.getDescription());
    descriptionArea.setEditable(false);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    
    panel.add(infoPanel, BorderLayout.NORTH);
    panel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
    
    // Add close button
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> detailsDialog.dispose());
    panel.add(closeButton, BorderLayout.SOUTH);
    
    detailsDialog.add(panel);
    detailsDialog.setVisible(true);
}

    private void handleLogout(ActionEvent e) {
        this.dispose();
        new LoginView(authService, ticketService).setVisible(true);
    }
    

     private void handleAssignTicket(int row) {
        TicketTableModel model = (TicketTableModel) ticketsTable.getModel();
        Ticket ticket = model.getTicketAt(ticketsTable.convertRowIndexToModel(row));
        
        if (ticket == null) {
            JOptionPane.showMessageDialog(this,
                "No ticket selected or ticket data is unavailable",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            List<User> admins = ticketService.getAllAdmins();
            if (admins.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No administrators available",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog(this, "Assign Ticket #" + ticket.getTicketId(), true);
            dialog.setLayout(new BorderLayout());
            dialog.setPreferredSize(new Dimension(400, 300));
            
            // Admin selection
            JComboBox<User> adminCombo = new JComboBox<>();
            for (User admin : admins) {
                adminCombo.addItem(admin);
            }
            
            adminCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, 
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof User) {
                        User admin = (User) value;
                        setText(admin.getFullName() + " (" + admin.getEmail() + ")");
                    }
                    return this;
                }
            });
            
            // Assignment notes
            JTextArea notesArea = new JTextArea(5, 30);
            notesArea.setLineWrap(true);
            
            // Submit button
            JButton assignBtn = new JButton("Assign Ticket");
            assignBtn.addActionListener(e -> {
                User selectedAdmin = (User) adminCombo.getSelectedItem();
                try {
                    if (ticketService.assignTicket(ticket.getTicketId(), 
                            selectedAdmin.getUserId())) {
                        JOptionPane.showMessageDialog(dialog,
                            "Ticket assigned to " + selectedAdmin.getFullName(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshTickets();
                        dialog.dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Assignment failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Layout components
            JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            formPanel.add(new JLabel("Select Administrator:"));
            formPanel.add(adminCombo);
            formPanel.add(new JLabel("Assignment Notes:"));
            formPanel.add(new JScrollPane(notesArea));
            
            dialog.add(formPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(assignBtn);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading administrators: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
  
}

