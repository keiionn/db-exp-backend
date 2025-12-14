package com.dbexp.db_experiment.dto.comment;

import java.time.LocalDateTime;

public class EditCommentResponse {

    private Long commentId;
    private String oldContent;
    private String newContent;
    private LocalDateTime editedAt;

    // Constructors
    public EditCommentResponse(Long commentId, String oldContent, String newContent, LocalDateTime editedAt) {
        this.commentId = commentId;
        this.oldContent = oldContent;
        this.newContent = newContent;
        this.editedAt = editedAt;
    }

    // Getters and Setters
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getOldContent() {
        return oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
}