package com.dbexp.db_experiment.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {

    @NotNull(message = "Post ID is required")
    private Long postId;

    private Long parentCommentId;

    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment content must not exceed 1000 characters")
    private String content;

    // Constructors
    public CreateCommentRequest(Long postId, Long parentCommentId, String content) {
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}