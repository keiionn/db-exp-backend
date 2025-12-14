package com.dbexp.db_experiment.testutil;

import java.time.LocalDateTime;

import com.dbexp.db_experiment.entity.Comment;

public class CommentTestBuilder {

    private Long commentId = 1L;
    private Long userId = 1L;
    private Long postId = 1L;
    private Long parentCommentId = null;
    private String commentContent = "Test Comment Content";
    private LocalDateTime createdAt = LocalDateTime.now();

    public static CommentTestBuilder aComment() {
        return new CommentTestBuilder();
    }

    public CommentTestBuilder withCommentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }

    public CommentTestBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public CommentTestBuilder withPostId(Long postId) {
        this.postId = postId;
        return this;
    }

    public CommentTestBuilder withParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
        return this;
    }

    public CommentTestBuilder withCommentContent(String commentContent) {
        this.commentContent = commentContent;
        return this;
    }

    public CommentTestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Comment build() {
        Comment comment = new Comment(userId, postId, commentContent);
        comment.setCommentId(commentId);
        comment.setParentCommentId(parentCommentId);
        comment.setCreatedAt(createdAt);
        return comment;
    }
}