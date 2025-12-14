package com.dbexp.db_experiment.service.community;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.community.CreateCommunityRequest;
import com.dbexp.db_experiment.dto.community.CreateCommunityResponse;
import com.dbexp.db_experiment.entity.Community;
import com.dbexp.db_experiment.exception.ConflictException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Service - Create Tests")
public class CommunityServiceCreateTest extends BaseCommunityServiceTest {
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Create Operations")
    class SuccessTests {
        @Test
        @DisplayName("Should create community successfully")
        void createCommunity_Success() {
            // Arrange
            Long userId = 1L;
            String name = "testcommunity";
            String description = "Test Description";
            CreateCommunityRequest request = new CreateCommunityRequest(name, description);

            mockAuthenticatedUser(userId);
            Community mockCommunity = createMockCommunity(1L, name, description);
            when(communityRepository.save(any(Community.class))).thenReturn(mockCommunity);

            // Act
            CreateCommunityResponse response = communityService.createCommunity(session, request);

            // Assert
            assertNotNull(response);
            assertEquals(mockCommunity.getCommunityId(), response.getCommunityId());
            assertEquals(mockCommunity.getName(), response.getName());
            assertEquals(mockCommunity.getDescription(), response.getDescription());
            assertNotNull(response.getCreationTime());

            verify(communityRepository).save(any(Community.class));
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {
        @Test
        @DisplayName("Should throw exception when name is null")
        void createCommunity_NullName_ThrowsException() {
            // Arrange
            Long userId = 1L;
            CreateCommunityRequest request = new CreateCommunityRequest(null, "Test Description");

            mockAuthenticatedUser(userId);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                communityService.createCommunity(session, request);
            });

            assertEquals("Name is required", exception.getMessage());
            verify(communityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when description is null")
        void createCommunity_NullDescription_ThrowsException() {
            // Arrange
            Long userId = 1L;
            CreateCommunityRequest request = new CreateCommunityRequest("testcommunity", null);

            mockAuthenticatedUser(userId);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                communityService.createCommunity(session, request);
            });

            assertEquals("Description is required", exception.getMessage());
            verify(communityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when community name already exists")
        void createCommunity_ExistingName_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String name = "existingcommunity";
            CreateCommunityRequest request = new CreateCommunityRequest(name, "Test Description");

            mockAuthenticatedUser(userId);
            Community existingCommunity = createMockCommunity(1L, name, "Existing Description");
            when(communityRepository.findByName(name)).thenReturn(java.util.Optional.of(existingCommunity));

            // Act & Assert
            ConflictException exception = assertThrows(ConflictException.class, () -> {
                communityService.createCommunity(session, request);
            });

            assertEquals("Community name already exists", exception.getMessage());
            verify(communityRepository).findByName(name);
            verify(communityRepository, never()).save(any());
        }
    }
}