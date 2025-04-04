/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import models.Ticket;
import model.enums.Status;
import java.sql.SQLException;
import java.util.List;
import models.User;

/**
 *
 * @author Admin
 */
public interface TicketDAO {
    Ticket getTicketById(int ticketId) throws SQLException;
    List<Ticket> getAllTickets() throws SQLException;
    List<Ticket> getTicketsByUser(int userId) throws SQLException;
    List<Ticket> getTicketsByStatus(Status status) throws SQLException;
    boolean addTicket(Ticket ticket) throws SQLException;
    boolean updateTicket(Ticket ticket) throws SQLException;
    boolean deleteTicket(int ticketId) throws SQLException;
    boolean assignTicket(int ticketId, int adminId) throws SQLException;
    boolean changeTicketStatus(int ticketId, Status status) throws SQLException;
    List<Ticket> getTicketsAssignedTo(int userId) throws SQLException;
    List<User> getAllAdmins() throws SQLException;
    boolean updateTicketStatus(int ticketId, Status newStatus, String feedback) throws SQLException;
}