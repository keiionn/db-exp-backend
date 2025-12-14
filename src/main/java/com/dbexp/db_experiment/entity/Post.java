package com.dbexp.db_experiment.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Post")
public class Post {

    @Id
    @Column("post_id")
    private Long postId;

    @Column("user_id")
    private Long userId;

    @Column("community_id")
    private Long communityId;

    @Column("post_title")
    private String postTitle;

    @Column("post_content")
    private String postContent;

    @Column("created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Post(Long userId, Long communityId, String postTitle, String postContent) {
        this.userId = userId;
        this.communityId = communityId;
        this.postTitle = postTitle;
        this.postContent = postContent;
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