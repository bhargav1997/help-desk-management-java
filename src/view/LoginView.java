/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import java.sql.Connection;
import dao.DatabaseConnection;
import dao.TicketDAOImpl;
import dao.UserDAOImpl;
import service.AuthService;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import model.enums.Role;
import dao.TicketDAO;
import service.TicketService;

/**
 *
 * @author Admin
 */
public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthService authService;
    private TicketService ticketService;

    public LoginView(AuthService authService) {
        this.authService = authService;
        initializeUI();
    }
    
    public LoginView(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Helpdesk System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::handleLogin);
        panel.add(loginButton, gbc);

        add(panel);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username and password cannot be empty", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User authenticatedUser = authService.login(username, password);
            
            openDashboard(authenticatedUser);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Login failed here: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            AuthService authService = new AuthService(new UserDAOImpl(conn));
            TicketService ticketService = new TicketService(new TicketDAOImpl(conn));

            if (user.getRole() == Role.ADMIN) {
                new AdminDashboard(user, authService, ticketService).setVisible(true);
            } else {
                new UserDashboard(user, authService, ticketService).setVisible(true);
            }
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error opening dashboard: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}