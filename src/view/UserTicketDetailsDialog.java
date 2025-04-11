package view;

import models.Ticket;
import models.User;
import models.Comment;
import service.CommentService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserTicketDetailsDialog extends JDialog {
    private final Ticket ticket;
    private final User currentUser;
    private final CommentService commentService;

    private JTextArea commentArea;
    private JPanel commentsPanel;

    public UserTicketDetailsDialog(JFrame parent, Ticket ticket, User currentUser, CommentService commentService) {
        super(parent, "Ticket Details - #" + ticket.getTicketId(), true);
        this.ticket = ticket;
        this.currentUser = currentUser;
        this.commentService = commentService;

        setSize(600, 500);
        setLocationRelativeTo(parent);
        initializeUI();
        loadComments();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Ticket details
        JPanel ticketPanel = new JPanel(new GridLayout(0, 1));
        ticketPanel.setBorder(BorderFactory.createTitledBorder("Ticket Info"));
        ticketPanel.add(new JLabel("Title: " + ticket.getTitle()));
        ticketPanel.add(new JLabel("Description: " + ticket.getDescription()));
        ticketPanel.add(new JLabel("Status: " + ticket.getStatus()));
        ticketPanel.add(new JLabel("Priority: " + ticket.getPriority()));
        ticketPanel.add(new JLabel("Created At: " + ticket.getCreatedAt()));
        ticketPanel.add(new JLabel("Assigned To: " +
                (ticket.getAssignedTo() != null ? ticket.getAssignedTo().getFullName() : "Unassigned")));

        // Comments section
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        JScrollPane commentScroll = new JScrollPane(commentsPanel);
        commentScroll.setBorder(BorderFactory.createTitledBorder("Comments"));

        // Add comment section
        JPanel addCommentPanel = new JPanel(new BorderLayout());
        commentArea = new JTextArea(3, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JButton postBtn = new JButton("Post Comment");
        postBtn.addActionListener(e -> postComment());

        addCommentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);
        addCommentPanel.add(postBtn, BorderLayout.EAST);
        addCommentPanel.setBorder(BorderFactory.createTitledBorder("Add Comment"));

        add(ticketPanel, BorderLayout.NORTH);
        add(commentScroll, BorderLayout.CENTER);
        add(addCommentPanel, BorderLayout.SOUTH);
    }

    private void loadComments() {
        commentsPanel.removeAll();

        try {
            List<Comment> comments = commentService.getCommentsForTicket(ticket.getTicketId());
            for (Comment comment : comments) {
                JPanel commentBox = new JPanel(new BorderLayout());
                commentBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                commentBox.add(new JLabel(comment.getUser().getFullName() + " (" + comment.getCreatedAt() + ")"), BorderLayout.NORTH);
                JTextArea content = new JTextArea(comment.getMessage());
                content.setEditable(false);
                content.setLineWrap(true);
                content.setWrapStyleWord(true);
                commentBox.add(content, BorderLayout.CENTER);
                commentsPanel.add(commentBox);
                commentsPanel.add(Box.createVerticalStrut(5));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load comments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        commentsPanel.revalidate();
        commentsPanel.repaint();
    }

    private void postComment() {
        String message = commentArea.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment before submitting.");
            return;
        }

        try {
            if (commentService.addComment(ticket.getTicketId(), currentUser.getUserId(), message)) {
                commentArea.setText("");
                loadComments(); // Refresh after posting
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to post comment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
