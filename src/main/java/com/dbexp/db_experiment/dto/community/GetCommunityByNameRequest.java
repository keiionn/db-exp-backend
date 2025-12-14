package com.dbexp.db_experiment.dto.community;

public class GetCommunityByNameRequest {
    private String name;

    public GetCommunityByNameRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}