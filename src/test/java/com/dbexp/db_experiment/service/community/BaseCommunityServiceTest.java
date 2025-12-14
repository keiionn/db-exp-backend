package com.dbexp.db_experiment.service.community;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.entity.Community;
import com.dbexp.db_experiment.repository.CommunityRepository;
import com.dbexp.db_experiment.service.AuthService;
import com.dbexp.db_experiment.service.CommunityServiceImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class BaseCommunityServiceTest {

    @Mock
    protected CommunityRepository communityRepository;

    protected CommunityServiceImpl communityService;

    @Mock
    protected AuthService authService;

    @Mock
    protected HttpSession session;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        communityService = new CommunityServiceImpl(communityRepository, authService);
    }

    protected Community createMockCommunity(Long communityId, String name, String description) {
        Community community = new Community(name, description);
        community.setCommunityId(communityId);
        community.setCreatedAt(java.time.LocalDateTime.now());
        return community;
    }

    protected void mockCommunityRepositoryFindById(Long communityId, Community community) {
        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
    }

    protected void mockCommunityRepositoryFindByIdNotFound(Long communityId) {
        when(communityRepository.findById(communityId)).thenReturn(Optional.empty());
    }

    protected void mockCommunityRepositoryFindByName(String name, Community community) {
        when(communityRepository.findByName(name)).thenReturn(Optional.of(community));
    }

    protected void mockCommunityRepositoryFindByNameNotFound(String name) {
        when(communityRepository.findByName(name)).thenReturn(Optional.empty());
    }

    protected void mockDescriptionUpdate(Long communityId, String newDescription, int affectedRows) {
        when(communityRepository.updateDescription(communityId, newDescription)).thenReturn(affectedRows);
    }

    protected void mockAuthenticatedUser(Long userId) {
        CurrentUserResponse response = new CurrentUserResponse(true, userId, "testuser", "test@example.com");
        when(authService.getCurrentUser(session)).thenReturn(response);
    }
}