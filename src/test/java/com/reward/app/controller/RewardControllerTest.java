package com.reward.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reward.app.JsonDataLoader;
import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.response.RewardCalculationResponse;
import com.reward.app.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Unit test for {@link RewardController}.
 * <p>
 * This test verifies the behavior of the reward calculation endpoint by simulating HTTP requests
 * and mocking the {@link RewardService}.
 * </p>
 */
@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonDataLoader jsonDataLoader; // Prevents real execution

    @MockBean
    private RewardService rewardService;

    @Autowired
    private ObjectMapper objectMapper;

    private RewardCalculationResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // Create mock RewardPointsDTO
        RewardPointsDTO rewardPointsDTO = new RewardPointsDTO("C001", 150,
                Map.of("January", 50, "February", 60, "March", 40));

        // Wrap in RewardCalculationResponse
        expectedResponse = new RewardCalculationResponse(rewardPointsDTO);

        when(rewardService.getMonthlyRewards("C001")).thenReturn(expectedResponse.getReward());
    }

    /**
     * Tests the endpoint with a valid customer ID.
     */
    @Test
    void testCalculateRewards_ValidCustomer() throws Exception {
        mockMvc.perform(get("/rewards/calculate/C001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(rewardService, times(1)).getMonthlyRewards("C001");
    }

    /**
     * Tests the endpoint with a missing customer ID.
     */
    @Test
    void testCalculateRewards_MissingCustomerId() throws Exception {
        mockMvc.perform(get("/rewards/calculate/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Missing PathVariable
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(rewardService, never()).getMonthlyRewards(anyString());
    }

    /**
     * Tests when customer ID is invalid (empty).
     */
    @Test
    void testCalculateRewards_InvalidCustomerId() throws Exception {
        mockMvc.perform(get("/rewards/calculate/ ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Reward Processing Error"))
                .andExpect(jsonPath("$.message").value("Customer ID cannot be null or empty"));

        verify(rewardService, never()).getMonthlyRewards(anyString());
    }

    /**
     * Tests when the service throws an exception.
     */
    @Test
    void testCalculateRewards_ServiceException() throws Exception {
        when(rewardService.getMonthlyRewards("C002"))
                .thenThrow(new RuntimeException("Unexpected Error"));

        mockMvc.perform(get("/rewards/calculate/C002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Reward Processing Error"))
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
        verify(rewardService, times(1)).getMonthlyRewards("C002");
    }
}
