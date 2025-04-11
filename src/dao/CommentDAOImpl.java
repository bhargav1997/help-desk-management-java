/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.Comment;
import models.Ticket;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class CommentDAOImpl implements CommentDAO {
    private final Connection connection;

    public CommentDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Comment> getCommentsByTicket(int ticketId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.full_name " +
                    "FROM Comment c " +
                    "JOIN User u ON c.user_id = u.user_id " +
                    "WHERE c.ticket_id = ? " +
                    "ORDER BY c.created_at";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comments.add(extractCommentFromResultSet(rs));
            }
        }
        return comments;
    }

    @Override
    public boolean addComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO Comment (ticket_id, user_id, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, comment.getTicket().getTicketId());
            stmt.setInt(2, comment.getUser().getUserId());
            stmt.setString(3, comment.getMessage());
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean addComment(int ticketId, int userId, String message) throws SQLException {
        String sql = "INSERT INTO Comment (ticket_id, user_id, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.setInt(2, userId);
            stmt.setString(3, message);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Comment> getCommentsByTicketId(int ticketId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name FROM Comment c JOIN User u ON c.user_id = u.user_id WHERE c.ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setTicketId(ticketId);
                
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                
                comment.setUser(user);
                comment.setMessage(rs.getString("message"));
                comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                comments.add(comment);
            }
        }
        return comments;
    }

    @Override
    public boolean deleteComment(int commentId) throws SQLException {
        String sql = "DELETE FROM Comment WHERE comment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Comment extractCommentFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        
        Ticket ticket = new Ticket();
        ticket.setTicketId(rs.getInt("ticket_id"));
        
        return new Comment(
            rs.getInt("comment_id"),
            ticket,
            user,
            rs.getString("message"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}