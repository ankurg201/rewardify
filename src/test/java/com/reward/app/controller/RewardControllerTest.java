package com.reward.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reward.app.request.RewardCalculationRequest;
import com.reward.app.response.RewardCalculationResponse;
import com.reward.app.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RewardService rewardService;

    @Autowired
    private ObjectMapper objectMapper;

    private RewardCalculationRequest request;
    private RewardCalculationResponse expectedResponse;

    @BeforeEach
    void setUp() throws Exception {
        // Load request from JSON file
        String requestJson = new String(Files.readAllBytes(Paths.get("src/test/resources/json/request.json")));
        request = objectMapper.readValue(requestJson, RewardCalculationRequest.class);

        // Load expected response from JSON file
        String responseJson = new String(Files.readAllBytes(Paths.get("src/test/resources/json/expected-response.json")));
        expectedResponse = objectMapper.readValue(responseJson, RewardCalculationResponse.class);

        // Mock service response
        when(rewardService.getMonthlyRewards(any())).thenReturn(expectedResponse.getRewards());
    }

    @Test
    void testCalculatePoints() throws Exception {
        mockMvc.perform(post("/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(rewardService, times(1)).getMonthlyRewards(any());
    }

    // ✅ Register a mock bean in Spring Boot 3.2+
    @TestConfiguration
    static class RewardServiceTestConfig {
        @Bean
        public RewardService rewardService() {
            return mock(RewardService.class);
        }
    }
}
