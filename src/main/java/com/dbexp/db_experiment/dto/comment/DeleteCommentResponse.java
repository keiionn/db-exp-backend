package com.dbexp.db_experiment.dto.comment;

import java.time.LocalDateTime;

public class DeleteCommentResponse {

    private Long commentId;
    private LocalDateTime deletedAt;
    private String message;

    // Constructors
    public DeleteCommentResponse(Long commentId, LocalDateTime deletedAt, String message) {
        this.commentId = commentId;
        this.deletedAt = deletedAt;
        this.message = message;
    }

    // Getters and Setters
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}