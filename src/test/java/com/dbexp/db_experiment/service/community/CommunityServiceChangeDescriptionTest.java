package com.dbexp.db_experiment.service.community;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.community.ChangeDescriptionRequest;
import com.dbexp.db_experiment.dto.community.ChangeDescriptionResponse;
import com.dbexp.db_experiment.entity.Community;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Service - Change Description Tests")
public class CommunityServiceChangeDescriptionTest extends BaseCommunityServiceTest {
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Change Description Operations")
    class SuccessTests {
        @Test
        @DisplayName("Should change description successfully")
        void changeDescription_Success() {
            // Arrange
            Long userId = 1L;
            Long communityId = 1L;
            String oldDescription = "Old Description";
            String newDescription = "New Description";
            ChangeDescriptionRequest request = new ChangeDescriptionRequest(newDescription);

            mockAuthenticatedUser(userId);
            Community mockCommunity = createMockCommunity(communityId, "testcommunity", oldDescription);
            mockCommunityRepositoryFindById(communityId, mockCommunity);
            mockDescriptionUpdate(communityId, newDescription, 1);

            // Act
            ChangeDescriptionResponse response = communityService.changeDescription(session, communityId, request);

            // Assert
            assertNotNull(response);
            assertEquals(communityId, response.getCommunityId());
            assertEquals(oldDescription, response.getOldDescription());
            assertEquals(newDescription, response.getNewDescription());
            assertNotNull(response.getChangeTime());

            verify(communityRepository).findById(communityId);
            verify(communityRepository).updateDescription(communityId, newDescription);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {
        @Test
        @DisplayName("Should throw exception when community not found")
        void changeDescription_CommunityNotFound_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long communityId = 999L;
            ChangeDescriptionRequest request = new ChangeDescriptionRequest("New Description");

            mockAuthenticatedUser(userId);
            mockCommunityRepositoryFindByIdNotFound(communityId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                communityService.changeDescription(session, communityId, request);
            });

            assertEquals("Community not found", exception.getMessage());
            verify(communityRepository).findById(communityId);
            verify(communityRepository, never()).updateDescription(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when new description is same as old description")
        void changeDescription_SameDescription_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long communityId = 1L;
            String description = "Same Description";
            ChangeDescriptionRequest request = new ChangeDescriptionRequest(description);

            mockAuthenticatedUser(userId);
            Community mockCommunity = createMockCommunity(communityId, "testcommunity", description);
            mockCommunityRepositoryFindById(communityId, mockCommunity);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                communityService.changeDescription(session, communityId, request);
            });

            assertEquals("New description must be different from current description", exception.getMessage());
            verify(communityRepository).findById(communityId);
            verify(communityRepository, never()).updateDescription(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when update fails")
        void changeDescription_UpdateFails_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long communityId = 1L;
            String oldDescription = "Old Description";
            String newDescription = "New Description";
            ChangeDescriptionRequest request = new ChangeDescriptionRequest(newDescription);

            mockAuthenticatedUser(userId);
            Community mockCommunity = createMockCommunity(communityId, "testcommunity", oldDescription);
            mockCommunityRepositoryFindById(communityId, mockCommunity);
            mockDescriptionUpdate(communityId, newDescription, 0); // Simulate update failure

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                communityService.changeDescription(session, communityId, request);
            });

            assertEquals("Failed to update description", exception.getMessage());
            verify(communityRepository).findById(communityId);
            verify(communityRepository).updateDescription(communityId, newDescription);
        }
    }
}