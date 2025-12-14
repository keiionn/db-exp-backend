package com.dbexp.db_experiment.dto.post;

import java.time.LocalDateTime;

public class EditPostResponse {

    private Long postId;
    private String oldPostTitle;
    private String newPostTitle;
    private String oldPostContent;
    private String newPostContent;
    private LocalDateTime updatedAt;

    // Constructors
    public EditPostResponse(Long postId, String oldPostTitle, String newPostTitle, String oldPostContent,
            String newPostContent, LocalDateTime updatedAt) {
        this.postId = postId;
        this.oldPostTitle = oldPostTitle;
        this.newPostTitle = newPostTitle;
        this.oldPostContent = oldPostContent;
        this.newPostContent = newPostContent;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getOldPostTitle() {
        return oldPostTitle;
    }

    public void setOldPostTitle(String oldPostTitle) {
        this.oldPostTitle = oldPostTitle;
    }

    public String getNewPostTitle() {
        return newPostTitle;
    }

    public void setNewPostTitle(String newPostTitle) {
        this.newPostTitle = newPostTitle;
    }

    public String getOldPostContent() {
        return oldPostContent;
    }

    public void setOldPostContent(String oldPostContent) {
        this.oldPostContent = oldPostContent;
    }

    public String getNewPostContent() {
        return newPostContent;
    }

    public void setNewPostContent(String newPostContent) {
        this.newPostContent = newPostContent;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}