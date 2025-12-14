package com.dbexp.db_experiment.dto.post;

import jakarta.validation.constraints.NotBlank;

public class GetPostByIdRequest {

    @NotBlank(message = "Post ID is required")
    private Long postId;

    // Constructors
    public GetPostByIdRequest(Long postId) {
        this.postId = postId;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}