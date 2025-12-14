package com.dbexp.db_experiment.controller;

import org.springframework.test.web.servlet.ResultActions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public abstract class BaseCommunityControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUpBase() {
        super.setUpBase();
    }

    protected void assertSuccessCommunityResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.communityId").exists());
    }

    protected void assertCreatedCommunityResponse(ResultActions result) throws Exception {
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.communityId").exists());
    }
}