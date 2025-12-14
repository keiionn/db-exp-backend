package com.dbexp.db_experiment.dto.post;

import java.time.LocalDateTime;

public class DeletePostResponse {

    private Long postId;
    private LocalDateTime deletedAt;
    private String message;

    // Constructors
    public DeletePostResponse(Long postId, LocalDateTime deletedAt, String message) {
        this.postId = postId;
        this.deletedAt = deletedAt;
        this.message = message;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
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