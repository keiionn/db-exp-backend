package com.dbexp.db_experiment.dto.comment;

import jakarta.validation.constraints.NotNull;

public class GetCommentByIdRequest {

    @NotNull(message = "Comment ID is required")
    private Long commentId;

    // Constructors
    public GetCommentByIdRequest(Long commentId) {
        this.commentId = commentId;
    }

    // Getters and Setters
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}