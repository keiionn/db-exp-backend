package com.dbexp.db_experiment.dto.comment;

import java.time.LocalDateTime;

public class CreateCommentResponse {

    private Long commentId;
    private String content;
    private Long userId;
    private Long postId;
    private Long parentCommentId;
    private LocalDateTime createdAt;

    // Constructors
    public CreateCommentResponse(Long commentId, String content, Long userId, Long postId, Long parentCommentId,
            LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}