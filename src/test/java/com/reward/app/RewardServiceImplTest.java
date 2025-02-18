package com.reward.app;

import static org.junit.jupiter.api.Assertions.*;

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
    private List<Transaction> transactions; // Mock the transactions

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
        Transaction validTransaction = new Transaction("C001",120.0 , "2025-02-04");  // Valid transaction date
        List<Transaction> validTransactions = List.of(validTransaction);

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
        List<Transaction> transactions = List.of();

        // Expecting TransactionNotFoundException for empty list
        assertThrows(TransactionNotFoundException.class, () -> {
            rewardService.getMonthlyRewards(transactions);
        });
    }

    @Test
    void testGetMonthlyRewards_InvalidTransactionData() {
        // Transaction with missing fields (e.g., null customerId)
        Transaction t1 = new Transaction(null, 120.0, "2025-02-10");
        List<Transaction> transactions = List.of(t1);

        // Expecting RewardProcessingException due to missing required fields
        assertThrows(RewardProcessingException.class, () -> {
            rewardService.getMonthlyRewards(transactions);
        });
    }

    @Test
    public void testGetMonthlyRewards_invalidTransactionDate() {
        // Mock invalid transaction (invalid date format)
        Transaction invalidTransaction = new Transaction("C001",120.0 , "2025-02-30"); // Invalid date
        List<Transaction> invalidTransactions = List.of(invalidTransaction);

        // Try to call the method, and expect an exception
        Exception exception = assertThrows(RewardProcessingException.class, () -> {
            rewardService.getMonthlyRewards(invalidTransactions);
        });

        assertTrue(exception.getMessage().contains("Invalid date format"));
    }

    @Test
    public void testGetMonthlyRewards_multipleTransactions() {
        // Mock multiple valid transactions for the same customer in different months
        Transaction transaction1 = new Transaction("C001", 150.0, "2025-01-10");
        Transaction transaction2 = new Transaction("C001", 190.0, "2025-02-15");
        Transaction transaction3 = new Transaction("C002", 120.0, "2025-05-25");
        Transaction transaction4 = new Transaction("C002", 400.0, "2025-12-12");
        List<Transaction> transactions = List.of(transaction1, transaction2, transaction3, transaction4);

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
