/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.UserDAO;
import models.User;
import java.sql.SQLException;


/**
 *
 * @author Admin
 */
public class AuthService {
    private final UserDAO userDao;
    
    public AuthService(UserDAO userDao) {
        this.userDao = userDao;
    }
    
     public User login(String username, String password) {
        try {
            User user = userDao.authenticate(username, password);
            if (user == null) {
                throw new IllegalArgumentException("Invalid username or password");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during login: " + e.getMessage(), e);
        }
    }
    
    public boolean register(User user) throws SQLException {
        return userDao.addUser(user);
    }
    
    public boolean changePassword(int userId, String newPassword) throws SQLException {
        User user = userDao.getUserById(userId);
        if (user != null) {
            user.setPassword(newPassword);
            return userDao.updateUser(user);
        }
        return false;
    }
}