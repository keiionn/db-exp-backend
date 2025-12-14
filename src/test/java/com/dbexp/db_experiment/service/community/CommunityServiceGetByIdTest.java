package com.dbexp.db_experiment.service.community;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.community.GetCommunityByIdRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdResponse;
import com.dbexp.db_experiment.entity.Community;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Service - Get By ID Tests")
public class CommunityServiceGetByIdTest extends BaseCommunityServiceTest {
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Get By ID Operations")
    class SuccessTests {
        @Test
        @DisplayName("Should retrieve community successfully by ID")
        void getById_Success() {
            // Arrange
            Long communityId = 1L;
            GetCommunityByIdRequest request = new GetCommunityByIdRequest(communityId);

            Community mockCommunity = createMockCommunity(communityId, "testcommunity", "Test Description");
            mockCommunityRepositoryFindById(communityId, mockCommunity);

            // Act
            GetCommunityByIdResponse response = communityService.getCommunityById(request);

            // Assert
            assertNotNull(response);
            assertEquals(mockCommunity.getCommunityId(), response.getCommunityId());
            assertEquals(mockCommunity.getName(), response.getName());
            assertEquals(mockCommunity.getDescription(), response.getDescription());
            assertNotNull(response.getCreationTime());

            verify(communityRepository).findById(communityId);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {
        @Test
        @DisplayName("Should throw exception when community ID is null")
        void getCommunityById_NullCommunityId_ThrowsException() {
            // Arrange
            GetCommunityByIdRequest request = new GetCommunityByIdRequest(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                communityService.getCommunityById(request);
            });

            assertEquals("Community ID is required", exception.getMessage());
            verify(communityRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw exception when community not found")
        void getCommunityById_CommunityNotFound_ThrowsException() {
            // Arrange
            Long communityId = 999L;
            GetCommunityByIdRequest request = new GetCommunityByIdRequest(communityId);

            mockCommunityRepositoryFindByIdNotFound(communityId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                communityService.getCommunityById(request);
            });

            assertEquals("Community not found", exception.getMessage());
            verify(communityRepository).findById(communityId);
        }
    }
}