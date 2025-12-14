package com.dbexp.db_experiment.service;

import java.util.List;
import jakarta.servlet.http.HttpSession;

import com.dbexp.db_experiment.dto.community.ChangeDescriptionRequest;
import com.dbexp.db_experiment.dto.community.ChangeDescriptionResponse;
import com.dbexp.db_experiment.dto.community.CreateCommunityRequest;
import com.dbexp.db_experiment.dto.community.CreateCommunityResponse;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdResponse;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameResponse;

public interface CommunityService {
    GetCommunityByIdResponse getCommunityById(GetCommunityByIdRequest request);

    GetCommunityByNameResponse getCommunityByName(GetCommunityByNameRequest request);

    CreateCommunityResponse createCommunity(HttpSession session, CreateCommunityRequest request);

    ChangeDescriptionResponse changeDescription(HttpSession session, Long communityId,
            ChangeDescriptionRequest request);
            
    List<GetCommunityByIdResponse> getLatestCommunities();
}