package dao;

import models.Ticket;
import models.User;
import model.enums.Status;
import model.enums.Priority;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.enums.Role;

public class TicketDAOImpl implements TicketDAO {
    private final Connection connection;

    public TicketDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // Implement all required methods from TicketDAO interface
    @Override
    public Ticket getTicketById(int ticketId) throws SQLException {
        String sql = "SELECT t.*, u1.full_name as creator_name, u2.full_name as assignee_name " +
                    "FROM Ticket t " +
                    "LEFT JOIN User u1 ON t.created_by = u1.user_id " +
                    "LEFT JOIN User u2 ON t.assigned_to = u2.user_id " +
                    "WHERE t.ticket_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractTicketFromResultSet(rs);
            }
        }
        return null;
    }


@Override
public List<User> getAllAdmins() throws SQLException {
    List<User> admins = new ArrayList<>();
    String sql = "SELECT user_id, username, full_name, email, phone FROM User WHERE role = 'ADMIN'";
    
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            admins.add(new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                null, // No password
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                Role.ADMIN
            ));
        }
    }
    return admins;
}
    
    @Override
    public boolean changeTicketStatus(int ticketId, Status status) throws SQLException {
        String sql = "UPDATE Ticket SET status = ? WHERE ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, ticketId);
            return stmt.executeUpdate() > 0;
        }
    }


    @Override
    public List<Ticket> getAllTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, u1.full_name as creator_name, u2.full_name as assignee_name " +
                    "FROM Ticket t " +
                    "LEFT JOIN User u1 ON t.created_by = u1.user_id " +
                    "LEFT JOIN User u2 ON t.assigned_to = u2.user_id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> getTicketsByUser(int userId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, u1.full_name as creator_name, u2.full_name as assignee_name " +
                    "FROM Ticket t " +
                    "LEFT JOIN User u1 ON t.created_by = u1.user_id " +
                    "LEFT JOIN User u2 ON t.assigned_to = u2.user_id " +
                    "WHERE t.created_by = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> getTicketsByStatus(Status status) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, u1.full_name as creator_name, u2.full_name as assignee_name " +
                    "FROM Ticket t " +
                    "LEFT JOIN User u1 ON t.created_by = u1.user_id " +
                    "LEFT JOIN User u2 ON t.assigned_to = u2.user_id " +
                    "WHERE t.status = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> getTicketsAssignedTo(int userId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, u1.full_name as creator_name " +
                    "FROM Ticket t " +
                    "JOIN User u1 ON t.created_by = u1.user_id " +
                    "WHERE t.assigned_to = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    @Override
    public boolean addTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO Ticket (title, description, status, priority, created_by) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ticket.getTitle());
            stmt.setString(2, ticket.getDescription());
            stmt.setString(3, ticket.getStatus().name());
            stmt.setString(4, ticket.getPriority().name());
            stmt.setInt(5, ticket.getCreatedBy().getUserId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ticket.setTicketId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateTicket(Ticket ticket) throws SQLException {
        String sql = "UPDATE Ticket SET title=?, description=?, status=?, priority=?, assigned_to=? " +
                    "WHERE ticket_id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ticket.getTitle());
            stmt.setString(2, ticket.getDescription());
            stmt.setString(3, ticket.getStatus().name());
            stmt.setString(4, ticket.getPriority().name());
            stmt.setObject(5, ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUserId() : null);
            stmt.setInt(6, ticket.getTicketId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateTicketStatus(int ticketId, Status newStatus, String feedback) throws SQLException {
        String sql = "UPDATE Ticket SET status=?, feedback=?, updated_at=CURRENT_TIMESTAMP WHERE ticket_id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setString(2, feedback);
            stmt.setInt(3, ticketId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteTicket(int ticketId) throws SQLException {
        String sql = "DELETE FROM Ticket WHERE ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
         Ticket ticket = new Ticket();
    ticket.setTicketId(rs.getInt("ticket_id"));
        // Debug by printing all available columns first
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.println("Column " + i + ": " + metaData.getColumnName(i));
            }

            // Then proceed with your existing extraction logic
            User createdBy = new User();
            createdBy.setUserId(rs.getInt("created_by"));
        createdBy.setFullName(rs.getString("creator_name"));
        
        User assignedTo = null;
        if (rs.getObject("assigned_to") != null) {
            assignedTo = new User();
            assignedTo.setUserId(rs.getInt("assigned_to"));
          //  assignedTo.setFullName(rs.getString("assignee_name"));
        }
        
//        // Handle assigned_to
//        if (rs.getObject("assigned_to") != null) {
//            User assignedTo = new User();
//            assignedTo.setUserId(rs.getInt("assigned_to"));
//            // If you join with User table to get admin details:
//            if (columnExists(rs, "assigned_name")) {
//                assignedTo.setFullName(rs.getString("assigned_name"));
//            }
//            ticket.setAssignedTo(assignedTo);
//        }
        
        return new Ticket(
            rs.getInt("ticket_id"),
            rs.getString("title"),
            rs.getString("description"),
            Status.valueOf(rs.getString("status")),
            Priority.valueOf(rs.getString("priority")),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime(),
            createdBy,
            assignedTo
        );
    }

        // In TicketDAOImpl
        @Override
        public boolean assignTicket(int ticketId, int adminId) throws SQLException {
            String sql = "UPDATE Ticket SET assigned_to = ?, status = 'IN_PROGRESS', updated_at = CURRENT_TIMESTAMP WHERE ticket_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, adminId);
                stmt.setInt(2, ticketId);
                return stmt.executeUpdate() > 0;
            }
        }
        
      
        private boolean columnExists(ResultSet rs, String columnName) throws SQLException {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                if (meta.getColumnName(i).equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
            return false;
        }
}