package com.reward.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.dto.TransactionDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.request.RewardCalculationRequest;
import com.reward.app.response.RewardCalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for {@link RewardServiceImpl}.
 * <p>
 * This class verifies the correctness of reward point calculations, error handling, and transaction processing logic.
 * </p>
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RewardServiceImplTest {

    private RewardServiceImpl rewardService;
    private ObjectMapper objectMapper;

    private List<TransactionDTO> transactions;
    private List<RewardPointsDTO> expectedRewards;

    /**
     * Sets up test data before each test case execution.
     * <p>
     * Loads transaction request and expected response JSON files to simulate real transactions.
     * </p>
     *
     * @throws IOException if file reading or JSON parsing fails
     */
    @BeforeEach
    public void setUp() throws IOException {
        rewardService = new RewardServiceImpl();
        objectMapper = new ObjectMapper();

        // Load test request JSON
        String requestJson = new String(Files.readAllBytes(Paths.get("src/test/resources/json/request.json")));
        RewardCalculationRequest request = objectMapper.readValue(requestJson, RewardCalculationRequest.class);
        transactions = request.getTransactions();

        // Load expected response JSON
        String responseJson = new String(Files.readAllBytes(Paths.get("src/test/resources/json/expected-response.json")));
        RewardCalculationResponse response = objectMapper.readValue(responseJson, RewardCalculationResponse.class);
        expectedRewards = response.getRewards();
    }

    /**
     * Reads a JSON file from the given path as a string.
     *
     * @param path the file path
     * @return the JSON content as a string
     * @throws Exception if file reading fails
     */
    private String readJsonFile(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    /**
     * Tests reward calculation when a valid amount is provided.
     */
    @Test
    void testCalculatePoints_ValidAmount() {
        assertEquals(90, rewardService.calculatePoints(120.0));
    }

    /**
     * Tests reward calculation when the amount is below the minimum reward threshold.
     */
    @Test
    void testCalculatePoints_AmountBelow50() {
        assertEquals(0, rewardService.calculatePoints(40.0));
    }

    /**
     * Tests reward calculation when the amount is negative.
     * <p>
     * Expects an exception to be thrown.
     * </p>
     */
    @Test
    void testCalculatePoints_AmountNegative() {
        assertThrows(RewardProcessingException.class, () -> rewardService.calculatePoints(-10.0));
    }

    /**
     * Tests reward calculation for a list of valid transactions.
     * <p>
     * Verifies that the computed rewards match the expected rewards.
     * </p>
     */
    @Test
    public void testGetMonthlyRewards_validTransactions() {
        List<RewardPointsDTO> result = rewardService.getMonthlyRewards(transactions);

        assertNotNull(result);
        assertEquals(expectedRewards.size(), result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(expectedRewards.get(i).getCustomerId(), result.get(i).getCustomerId());
            assertEquals(expectedRewards.get(i).getTotalPoints(), result.get(i).getTotalPoints());
            assertEquals(expectedRewards.get(i).getMonthlyPoints(), result.get(i).getMonthlyPoints());
        }
    }

    /**
     * Tests reward calculation when the transaction list is empty.
     * <p>
     * Expects a {@link RewardProcessingException} to be thrown.
     * </p>
     */
    @Test
    void testGetMonthlyRewards_EmptyTransactionList() {
        assertThrows(RewardProcessingException.class, () -> rewardService.getMonthlyRewards(List.of()));
    }

    /**
     * Tests reward calculation when a transaction contains invalid data (e.g., missing customer ID).
     * <p>
     * Expects a {@link RewardProcessingException} to be thrown.
     * </p>
     */
    @Test
    void testGetMonthlyRewards_InvalidTransactionData() {
        TransactionDTO invalidTransaction = new TransactionDTO(null, 120.0, "2025-02-10");
        assertThrows(RewardProcessingException.class, () -> rewardService.getMonthlyRewards(List.of(invalidTransaction)));
    }

    /**
     * Tests reward calculation when a transaction has an invalid date format.
     * <p>
     * Expects a {@link RewardProcessingException} due to incorrect date.
     * </p>
     */
    @Test
    public void testGetMonthlyRewards_InvalidTransactionDate() {
        TransactionDTO invalidTransaction = new TransactionDTO("C001", 120.0, "2025-02-30");
        assertThrows(RewardProcessingException.class, () -> rewardService.getMonthlyRewards(List.of(invalidTransaction)));
    }

}

