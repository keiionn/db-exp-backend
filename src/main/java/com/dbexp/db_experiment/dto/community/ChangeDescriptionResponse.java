package com.dbexp.db_experiment.dto.community;

import java.time.LocalDateTime;

public class ChangeDescriptionResponse {
    private Long communityId;
    private String oldDescription;
    private String newDescription;
    private LocalDateTime changeTime;

    public ChangeDescriptionResponse(Long communityId, String oldDescription, String newDescription,
            LocalDateTime changeTime) {
        this.communityId = communityId;
        this.oldDescription = oldDescription;
        this.newDescription = newDescription;
        this.changeTime = changeTime;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public void setOldDescription(String oldDescription) {
        this.oldDescription = oldDescription;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public LocalDateTime getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }
}