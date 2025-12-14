package com.dbexp.db_experiment.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {

    @NotNull(message = "Community ID is required")
    private Long communityId;

    @NotBlank(message = "Post title is required")
    @Size(min = 1, max = 255, message = "Post title must be between 1 and 255 characters")
    private String postTitle;

    @NotBlank(message = "Post content is required")
    @Size(min = 1, message = "Post content cannot be empty")
    private String postContent;

    // Constructors
    public CreatePostRequest(Long communityId, String postTitle, String postContent) {
        this.communityId = communityId;
        this.postTitle = postTitle;
        this.postContent = postContent;
    }

    // Getters and Setters
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
}