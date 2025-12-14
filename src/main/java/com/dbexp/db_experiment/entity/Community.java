package com.dbexp.db_experiment.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Community")
public class Community {

    @Id
    @Column("community_id")
    private Long communityId;

    @Column("community_name")
    private String name;

    @Column("community_description")
    private String description;

    @Column("created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Community(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}