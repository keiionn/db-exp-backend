package com.dbexp.db_experiment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.community.ChangeDescriptionRequest;
import com.dbexp.db_experiment.dto.community.ChangeDescriptionResponse;
import com.dbexp.db_experiment.dto.community.CreateCommunityRequest;
import com.dbexp.db_experiment.dto.community.CreateCommunityResponse;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdResponse;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameResponse;
import com.dbexp.db_experiment.entity.Community;
import com.dbexp.db_experiment.exception.ConflictException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.repository.CommunityRepository;

@Service
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;

    private final AuthService authService;

    public CommunityServiceImpl(CommunityRepository communityRepository, AuthService authService) {
        this.communityRepository = communityRepository;
        this.authService = authService;
    }

    @Override
    public GetCommunityByIdResponse getCommunityById(GetCommunityByIdRequest request) {
        if (request.getCommunityId() == null) {
            throw new IllegalArgumentException("Community ID is required");
        }

        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        return new GetCommunityByIdResponse(
                community.getCommunityId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt());
    }

    @Override
    public GetCommunityByNameResponse getCommunityByName(GetCommunityByNameRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Community name is required");
        }

        Community community = communityRepository.findByName(request.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        return new GetCommunityByNameResponse(
                community.getCommunityId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt());
    }

    @Override
    public CreateCommunityResponse createCommunity(HttpSession session, CreateCommunityRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUserResponse = authService.getCurrentUser(session);
        if (!currentUserResponse.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        if (request.getName() == null) {
            throw new IllegalArgumentException("Name is required");
        }
        if (request.getDescription() == null) {
            throw new IllegalArgumentException("Description is required");
        }

        if (communityRepository.findByName(request.getName()).isPresent()) {
            throw new ConflictException("Community name already exists");
        }

        Community community = new Community(
                request.getName(),
                request.getDescription());
        community.setCreatedAt(LocalDateTime.now());

        Community savedCommunity = communityRepository.save(community);

        return new CreateCommunityResponse(
                savedCommunity.getCommunityId(),
                savedCommunity.getName(),
                savedCommunity.getDescription(),
                savedCommunity.getCreatedAt());
    }

    @Override
    @Transactional
    public ChangeDescriptionResponse changeDescription(HttpSession session, Long communityId,
            ChangeDescriptionRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUserResponse = authService.getCurrentUser(session);
        if (!currentUserResponse.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (community.getDescription().equals(request.getNewDescription())) {
            throw new IllegalArgumentException("New description must be different from current description");
        }

        String oldDescription = community.getDescription();
        int rowsUpdated = communityRepository.updateDescription(communityId, request.getNewDescription());

        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("Failed to update description");
        }

        return new ChangeDescriptionResponse(
                communityId,
                oldDescription,
                request.getNewDescription(),
                LocalDateTime.now());
    }
    @Override
    public List<GetCommunityByIdResponse> getLatestCommunities() {
        List<Community> communities = communityRepository.findTop20ByOrderByCreatedAtDesc();
        List<GetCommunityByIdResponse> responses = new ArrayList<>();
        
        for (Community community : communities) {
            responses.add(new GetCommunityByIdResponse(
                community.getCommunityId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt()
            ));
        }
        
        return responses;
    }
}