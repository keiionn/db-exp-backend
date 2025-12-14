package com.dbexp.db_experiment.service.community;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.community.GetCommunityByNameRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameResponse;
import com.dbexp.db_experiment.entity.Community;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Service - Get By Name Tests")
public class CommunityServiceGetByNameTest extends BaseCommunityServiceTest {
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Get By Name Operations")
    class SuccessTests {
        @Test
        @DisplayName("Should retrieve community successfully by name")
        void getByName_Success() {
            // Arrange
            String name = "testcommunity";
            GetCommunityByNameRequest request = new GetCommunityByNameRequest(name);

            Community mockCommunity = createMockCommunity(1L, name, "Test Description");
            mockCommunityRepositoryFindByName(name, mockCommunity);

            // Act
            GetCommunityByNameResponse response = communityService.getCommunityByName(request);

            // Assert
            assertNotNull(response);
            assertEquals(mockCommunity.getCommunityId(), response.getCommunityId());
            assertEquals(mockCommunity.getName(), response.getName());
            assertEquals(mockCommunity.getDescription(), response.getDescription());
            assertNotNull(response.getCreationTime());

            verify(communityRepository).findByName(name);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {
        @Test
        @DisplayName("Should throw exception when community name is null")
        void getCommunityByName_NullName_ThrowsException() {
            // Arrange
            GetCommunityByNameRequest request = new GetCommunityByNameRequest(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                communityService.getCommunityByName(request);
            });

            assertEquals("Community name is required", exception.getMessage());
            verify(communityRepository, never()).findByName(any());
        }

        @Test
        @DisplayName("Should throw exception when community name is empty")
        void getCommunityByName_EmptyName_ThrowsException() {
            // Arrange
            GetCommunityByNameRequest request = new GetCommunityByNameRequest("");

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                communityService.getCommunityByName(request);
            });

            assertEquals("Community name is required", exception.getMessage());
            verify(communityRepository, never()).findByName(any());
        }

        @Test
        @DisplayName("Should throw exception when community not found")
        void getCommunityByName_CommunityNotFound_ThrowsException() {
            // Arrange
            String name = "nonexistent";
            GetCommunityByNameRequest request = new GetCommunityByNameRequest(name);

            mockCommunityRepositoryFindByNameNotFound(name);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                communityService.getCommunityByName(request);
            });

            assertEquals("Community not found", exception.getMessage());
            verify(communityRepository).findByName(name);
        }
    }
}