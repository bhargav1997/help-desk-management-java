/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.time.LocalDateTime;

public class Comment {
    private int commentId;
    private int ticketId;
    private Ticket ticket;
    private User user;
    private String message;
    private LocalDateTime createdAt;

    public Comment(){}
    
    public Comment(int commentID, Ticket ticket, User user, String message, LocalDateTime createdAT){
        this.commentId = commentID;
        this.ticket = ticket;
        this.user = user;
        this.message = message;
        this.createdAt = createdAT;
    }
    
    // Constructor
    public Comment(Ticket ticket, User user, String message) {
        this.ticket = ticket;
        this.user = user;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }
    
    

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }   
}