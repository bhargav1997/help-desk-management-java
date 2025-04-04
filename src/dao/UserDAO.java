/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.User;
import java.sql.*;
import java.util.List;

/**
 *
 * @author Admin
 */
public abstract class UserDAO {
    // Abstract methods
    public abstract User getUserById(int userId) throws SQLException;
    public abstract User getUserByUsername(String username) throws SQLException;
    public abstract List<User> getAllUsers() throws SQLException;
    public abstract boolean addUser(User user) throws SQLException;
    public abstract boolean updateUser(User user) throws SQLException;
    public abstract boolean deleteUser(int userId) throws SQLException;
    
    // Common method for authentication
    public User authenticate(String username, String password) throws SQLException {
        // Implementation
        return null;
        // Implementation
    }
}