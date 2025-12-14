package com.dbexp.db_experiment.dto.community;

import java.time.LocalDateTime;

public class CreateCommunityResponse {
    private Long communityId;
    private String name;
    private String description;
    private LocalDateTime creationTime;

    public CreateCommunityResponse(Long communityId, String name, String description,
            LocalDateTime creationTime) {
        this.communityId = communityId;
        this.name = name;
        this.description = description;
        this.creationTime = creationTime;
    }

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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}