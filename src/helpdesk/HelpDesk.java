/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package helpdesk;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import dao.DatabaseConnection;
import dao.TicketDAOImpl;
import dao.UserDAO;
import dao.UserDAOImpl;
import model.enums.Role;
import service.AuthService;
import service.TicketService;
import view.LoginView;

import models.User;

/**
 *
 * @author Admin
 */
public class HelpDesk {
    
    public static void main(String[] args) {
        // Initialize the database and create default users
    //    initializeDatabase();
        
        // Start the Swing application on the Event Dispatch Thread

        SwingUtilities.invokeLater(() -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                AuthService authService = new AuthService(new UserDAOImpl(conn));
                TicketService ticketService = new TicketService(new TicketDAOImpl(conn));
                
                new LoginView(authService, ticketService).setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                    "Failed to initialize application: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });

    }
    
    private static void initializeDatabase() {
        try {
            // Create tables and add default users if they don't exist
            Connection conn = DatabaseConnection.getConnection();
            UserDAO userDao = new UserDAOImpl(conn);
            
            // Create admin user if doesn't exist
            if (userDao.getUserByUsername("admin") == null) {
                User admin = new User(
                    "admin",
                    "admin123", // In production, use hashed password
                    "System Administrator",
                    "admin@helpdesk.com",
                        Role.ADMIN
                );
                userDao.addUser(admin);
            }
            
            // Create regular user if doesn't exist
            if (userDao.getUserByUsername("user1") == null) {
                User regularUser = new User(
                    "user1",
                    "user123", // In production, use hashed password
                    "John Doe",
                    "user@helpdesk.com",
                    Role.USER
                );
                userDao.addUser(regularUser);
            }
            
        } catch (SQLException e) {
            showFatalError("Database Initialization Failed", 
                "Failed to initialize database: " + e.getMessage());
        }
    }
    
    private static void showFatalError(String title, String message) {
        JOptionPane.showMessageDialog(null, 
            message, 
            title, 
            JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
