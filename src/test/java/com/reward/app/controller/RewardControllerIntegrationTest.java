package com.reward.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reward.app.model.Transaction;
import com.reward.app.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link RewardController}.
 * <p>
 * These tests validate the end-to-end flow of the reward calculation API, including
 * interaction with the database via {@link TransactionRepository}.
 * </p>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest // Starts the full application context
@AutoConfigureMockMvc // Configures MockMvc for testing
class RewardControllerIntegrationTest {

    private static final String CUSTOMER_ID = "C123";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Sets up test data in the H2 database before each test execution.
     * The database is cleared before each test to ensure consistent test behavior.
     */
    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll(); // Clean database before each test
        System.out.println("Database cleared before integration test execution.");

        // Insert sample transactions
        transactionRepository.save(new Transaction(1L, CUSTOMER_ID, 120.0, LocalDate.now().minusMonths(1))); // Should get 90 points
        transactionRepository.save(new Transaction(2L, CUSTOMER_ID, 75.0, LocalDate.now().minusMonths(2)));  // Should get 25 points

        // This record is 8 months old, so it will not be fetched from the database.
        transactionRepository.save(new Transaction(3L, CUSTOMER_ID, 150, LocalDate.now().minusMonths(8)));

        // This record has an amount of 30, which is below 50, so it will not be counted in the reward points.
        transactionRepository.save(new Transaction(4L, CUSTOMER_ID, 30.0, LocalDate.now().minusMonths(2)));

        System.out.println("Sample records inserted into the database.");
    }

    /**
     * Tests the reward calculation API when valid transactions exist for the given customer.
     * <p>
     * The test verifies:
     * <ul>
     *     <li>HTTP 200 (OK) response status</li>
     *     <li>Correct customer ID in the response</li>
     *     <li>Correct total reward points calculation</li>
     *     <li>Monthly reward points breakdown</li>
     * </ul>
     * </p>
     *
     * @throws Exception if the request execution fails
     */
    @Test
    void testGetRewards_Success() throws Exception {
        mockMvc.perform(get("/rewards/calculate/" + CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$.reward.customerId", is(CUSTOMER_ID)))
                .andExpect(jsonPath("$.reward.totalPoints", is(115))) // (90+25)
                .andExpect(jsonPath("$.reward.monthlyPoints['2025-01']", is(90))) // 120 spent = 90 points
                .andExpect(jsonPath("$.reward.monthlyPoints['2024-12']", is(25))); // 75 spent = 25 points
    }

    /**
     * Tests the reward calculation API when no transactions exist for the given customer.
     * <p>
     * The test verifies:
     * <ul>
     *     <li>HTTP 404 (Not Found) response status</li>
     *     <li>Error message indicating no transactions found</li>
     * </ul>
     * </p>
     *
     * @throws Exception if the request execution fails
     */
    @Test
    void testGetRewards_CustomerNotFound() throws Exception {
        mockMvc.perform(get("/rewards/calculate/UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404
                .andExpect(jsonPath("$.message", is("No transactions found")));
    }

}