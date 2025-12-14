package com.dbexp.db_experiment.dto.community;

public class GetCommunityByIdRequest {
    private Long communityId;

    public GetCommunityByIdRequest(Long communityId) {
        this.communityId = communityId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }
}