/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.Comment;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface CommentDAO {
    List<Comment> getCommentsByTicket(int ticketId) throws SQLException;
    boolean addComment(Comment comment) throws SQLException;
    boolean deleteComment(int commentId) throws SQLException;
}