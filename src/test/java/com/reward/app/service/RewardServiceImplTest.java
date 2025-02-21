/*
package com.reward.app.service;

import static org.junit.jupiter.api.Assertions.*;

import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.dto.TransactionDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.exception.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RewardServiceImplTest {

    private RewardServiceImpl rewardService;

    @Mock
    private List<TransactionDTO> transactions; // Mock the transactions

    @BeforeEach
    public void setUp() {
        rewardService = new RewardServiceImpl();
    }

    @Test
    void testCalculatePoints_ValidAmount() {
        // Valid transaction where amount spent is 120
        int points = rewardService.calculatePoints(120.0);
        assertEquals(90, points);  // (120-100)*2 + (100-50) = 40
    }

    @Test
    void testCalculatePoints_AmountBelow50() {
        // Valid transaction where amount spent is less than 50
        int points = rewardService.calculatePoints(40.0);
        assertEquals(0, points);  // No points for amount less than 50
    }

    @Test
    void testCalculatePoints_AmountNegative() {
        // Test for negative amount (should throw RewardProcessingException)
        assertThrows(RewardProcessingException.class, () -> {
            rewardService.calculatePoints(-10.0);
        });
    }

    @Test
    public void testGetMonthlyRewards_validTransactions() {
        // Mock valid transactions
        TransactionDTO validTransaction = new TransactionDTO("C001",120.0 , "2025-02-04");  // Valid transaction date
        List<TransactionDTO> validTransactions = List.of(validTransaction);

        // Call the method
        List<RewardPointsDTO> result = rewardService.getMonthlyRewards(validTransactions);

        // Assert result
        assertNotNull(result);
        assertEquals(1, result.size()); // 1 customer should be returned
        RewardPointsDTO rewardPointsDTO = result.get(0);
        assertEquals("C001", rewardPointsDTO.getCustomerId());
        assertEquals(90, rewardPointsDTO.getTotalPoints());  // Points calculation based on the logic
    }

    @Test
    void testGetMonthlyRewards_EmptyTransactionList() {
        // Empty list of transactions
        List<TransactionDTO> transactions = List.of();

        // Expecting TransactionNotFoundException for empty list
        assertThrows(TransactionNotFoundException.class, () -> {
            rewardService.getMonthlyRewards(transactions);
        });
    }

    @Test
    void testGetMonthlyRewards_InvalidTransactionData() {
        // Transaction with missing fields (e.g., null customerId)
        TransactionDTO t1 = new TransactionDTO(null, 120.0, "2025-02-10");
        List<TransactionDTO> transactions = List.of(t1);

        // Expecting RewardProcessingException due to missing required fields
        assertThrows(RewardProcessingException.class, () -> {
            rewardService.getMonthlyRewards(transactions);
        });
    }

    @Test
    public void testGetMonthlyRewards_invalidTransactionDate() {
        // Mock invalid transaction (invalid date format)
        TransactionDTO invalidTransaction = new TransactionDTO("C001",120.0 , "2025-02-30"); // Invalid date
        List<TransactionDTO> invalidTransactions = List.of(invalidTransaction);

        // Try to call the method, and expect an exception
        Exception exception = assertThrows(RewardProcessingException.class, () -> {
            rewardService.getMonthlyRewards(invalidTransactions);
        });

        assertTrue(exception.getMessage().contains("Invalid date format"));
    }

    @Test
    public void testGetMonthlyRewards_multipleTransactions() {
        // Mock multiple valid transactions for the same customer in different months
        TransactionDTO transaction1 = new TransactionDTO("C001", 150.0, "2025-01-10");
        TransactionDTO transaction2 = new TransactionDTO("C001", 190.0, "2025-02-15");
        TransactionDTO transaction3 = new TransactionDTO("C002", 120.0, "2025-05-25");
        TransactionDTO transaction4 = new TransactionDTO("C002", 400.0, "2025-12-12");
        List<TransactionDTO> transactions = List.of(transaction1, transaction2, transaction3, transaction4);

        // Call the method
        List<RewardPointsDTO> result = rewardService.getMonthlyRewards(transactions);

        // Assert result
        assertNotNull(result);
        assertEquals(2, result.size()); // 1 customer should be returned
        RewardPointsDTO rewardPointsDTO = result.get(1);
        assertEquals("C001", rewardPointsDTO.getCustomerId());
        assertEquals(380, rewardPointsDTO.getTotalPoints());  // Sum of points for both months

        RewardPointsDTO rewardPointsDTO1 = result.get(0);
        assertEquals("C002", rewardPointsDTO1.getCustomerId());
        assertEquals(740, rewardPointsDTO1.getTotalPoints());  // Sum of points for both months

        // Check month-wise breakdown
        Map<String, Integer> monthWisePoints = rewardPointsDTO.getMonthlyPoints();
        assertEquals(2, monthWisePoints.size());  // Two months: Jan and Feb
        assertTrue(monthWisePoints.containsKey("2025-01"));
        assertTrue(monthWisePoints.containsKey("2025-02"));
    }
}
*/

package com.reward.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.dto.TransactionDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.exception.TransactionNotFoundException;
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

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RewardServiceImplTest {

    private RewardServiceImpl rewardService;
    private ObjectMapper objectMapper;

    private List<TransactionDTO> transactions;
    private List<RewardPointsDTO> expectedRewards;

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

    // Utility method to read JSON file as String
    private String readJsonFile(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    @Test
    void testCalculatePoints_ValidAmount() {
        assertEquals(90, rewardService.calculatePoints(120.0));
    }

    @Test
    void testCalculatePoints_AmountBelow50() {
        assertEquals(0, rewardService.calculatePoints(40.0));
    }

    @Test
    void testCalculatePoints_AmountNegative() {
        assertThrows(RewardProcessingException.class, () -> rewardService.calculatePoints(-10.0));
    }

   /* @Test
    public void testGetMonthlyRewards_ValidTransactions_FromJson() throws Exception {
        // Load transactions from JSON file
        String requestJson = readJsonFile("src/test/resources/request.json");
        RewardCalculationRequest request = objectMapper.readValue(requestJson, RewardCalculationRequest.class);
        List<TransactionDTO> transactions = request.getTransactions();

        // Load expected rewards from JSON file
        String responseJson = readJsonFile("src/test/resources/expected-response.json");
        RewardCalculationResponse response = objectMapper.readValue(responseJson, RewardCalculationResponse.class);
        List<RewardPointsDTO> expectedRewards = response.getRewards();

        // Call the service method
        List<RewardPointsDTO> actualRewards = rewardService.getMonthlyRewards(transactions);

        // Convert lists to JSON for comparison
        JsonNode actualJson = objectMapper.readTree(objectMapper.writeValueAsString(actualRewards));
        JsonNode expectedJsonNode = objectMapper.readTree(objectMapper.writeValueAsString(expectedRewards));

        // Validate output matches expected JSON
        assertThat(actualJson).isEqualTo(expectedJsonNode);
    }*/

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

    @Test
    void testGetMonthlyRewards_EmptyTransactionList() {
        assertThrows(TransactionNotFoundException.class, () -> rewardService.getMonthlyRewards(List.of()));
    }

    @Test
    void testGetMonthlyRewards_InvalidTransactionData() {
        TransactionDTO invalidTransaction = new TransactionDTO(null, 120.0, "2025-02-10");
        assertThrows(RewardProcessingException.class, () -> rewardService.getMonthlyRewards(List.of(invalidTransaction)));
    }

    @Test
    public void testGetMonthlyRewards_InvalidTransactionDate() {
        TransactionDTO invalidTransaction = new TransactionDTO("C001", 120.0, "2025-02-30");
        assertThrows(RewardProcessingException.class, () -> rewardService.getMonthlyRewards(List.of(invalidTransaction)));
    }

}

