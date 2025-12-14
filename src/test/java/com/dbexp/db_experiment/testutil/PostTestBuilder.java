package com.dbexp.db_experiment.testutil;

import java.time.LocalDateTime;

import com.dbexp.db_experiment.entity.Post;

public class PostTestBuilder {

    private Long postId = 1L;
    private Long userId = 1L;
    private Long communityId = 1L;
    private String postTitle = "Test Post Title";
    private String postContent = "Test Post Content";
    private LocalDateTime createdAt = LocalDateTime.now();

    public static PostTestBuilder aPost() {
        return new PostTestBuilder();
    }

    public PostTestBuilder withPostId(Long postId) {
        this.postId = postId;
        return this;
    }

    public PostTestBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public PostTestBuilder withCommunityId(Long communityId) {
        this.communityId = communityId;
        return this;
    }

    public PostTestBuilder withPostTitle(String postTitle) {
        this.postTitle = postTitle;
        return this;
    }

    public PostTestBuilder withPostContent(String postContent) {
        this.postContent = postContent;
        return this;
    }

    public PostTestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Post build() {
        Post post = new Post(userId, communityId, postTitle, postContent);
        post.setPostId(postId);
        post.setCreatedAt(createdAt);
        return post;
    }
}