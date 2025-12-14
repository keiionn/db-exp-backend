package com.dbexp.db_experiment.dto.post;

import java.time.LocalDateTime;

public class CreatePostResponse {

    private Long postId;
    private Long userId;
    private Long communityId;
    private String postTitle;
    private String postContent;
    private LocalDateTime createdAt;

    // Constructors
    public CreatePostResponse(Long postId, Long userId, Long communityId, String postTitle, String postContent,
            LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.communityId = communityId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}