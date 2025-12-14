package com.dbexp.db_experiment.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ForumComment")
public class Comment {

    @Id
    @Column("comment_id")
    private Long commentId;

    @Column("comment_content")
    private String commentContent;

    @Column("user_id")
    private Long userId;

    @Column("post_id")
    private Long postId;

    @Column("parent_comment_id")
    private Long parentCommentId;

    @Column("created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Comment(Long userId, Long postId, String commentContent) {
        this.userId = userId;
        this.postId = postId;
        this.commentContent = commentContent;
    }

    // Getters and Setters
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
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