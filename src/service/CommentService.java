/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.CommentDAO;
import models.Comment;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Admin
 */
public class CommentService {
    private final CommentDAO commentDao;

    public CommentService(CommentDAO commentDao) {
        this.commentDao = commentDao;
    }

    public List<Comment> getTicketComments(int ticketId) throws SQLException {
        return commentDao.getCommentsByTicket(ticketId);
    }

    public boolean addComment(Comment comment) throws SQLException {
        return commentDao.addComment(comment);
    }

    public boolean deleteComment(int commentId) throws SQLException {
        return commentDao.deleteComment(commentId);
    }
}