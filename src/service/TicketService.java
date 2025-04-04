/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.TicketDAO;
import models.Ticket;
import models.User;
import model.enums.Status;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class TicketService {
    private final TicketDAO ticketDao;

    public TicketService(TicketDAO ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Ticket createTicket(String title, String description, User createdBy) throws SQLException {
        Ticket ticket = new Ticket(title, description, createdBy);
        if (ticketDao.addTicket(ticket)) {
            return ticket;
        }
        return null;
    }

    public List<Ticket> getAllTickets() throws SQLException {
        return ticketDao.getAllTickets();
    }


    public List<Ticket> getTicketsByUser(int userId) throws SQLException {
        // Initialize empty list as fallback
        List<Ticket> tickets = new ArrayList<>();
        
        try {
            // Get tickets from DAO
            tickets = ticketDao.getTicketsByUser(userId);
        } catch (SQLException e) {
            System.err.println("Error fetching tickets: " + e.getMessage());
            throw e; // Re-throw to handle in calling method
        }
        
        return tickets;
    }

    
    public List<Ticket> getUserTickets(int userId) throws SQLException {
        return ticketDao.getTicketsByUser(userId);
    }

    public List<Ticket> getTicketsByStatus(Status status) throws SQLException {
        return ticketDao.getTicketsByStatus(status);
    }

    public boolean updateTicket(Ticket ticket) throws SQLException {
        return ticketDao.updateTicket(ticket);
    }


    public boolean changeTicketStatus(int ticketId, Status status) throws SQLException {
        return ticketDao.changeTicketStatus(ticketId, status);
    }

    public boolean deleteTicket(int ticketId) throws SQLException {
        return ticketDao.deleteTicket(ticketId);
    }
    
    public List<Ticket> getAssignedTickets(int adminId) throws SQLException {
        return ticketDao.getTicketsAssignedTo(adminId);
    }


    public boolean updateTicketStatus(int ticketId, Status newStatus, String feedback) throws SQLException {
        return ticketDao.updateTicketStatus(ticketId, newStatus, feedback);
    }
    
    // In TicketService
    public boolean assignTicket(int ticketId, int adminId) throws SQLException {
        return ticketDao.assignTicket(ticketId, adminId);
    }

    public List<User> getAllAdmins() throws SQLException {
        return ticketDao.getAllAdmins();
    }
}