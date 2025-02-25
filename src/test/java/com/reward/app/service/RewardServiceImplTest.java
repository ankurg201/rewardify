package com.reward.app.service;

import com.reward.app.JsonDataLoader;
import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.model.Transaction;
import com.reward.app.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link RewardServiceImpl}.
 * <p>
 * This class tests the reward calculation service by mocking the transaction repository
 * and verifying the expected behavior under various conditions.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class RewardServiceImplTest {

    private static final String CUSTOMER_ID = "12345";

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Mock
    private TransactionRepository transactionRepository;

    @MockBean
    private JsonDataLoader jsonDataLoader; // Prevents real execution

    /**
     * Set up method executed before each test case.
     * Resets mocks to avoid test interference.
     */
    @BeforeEach
    void setUp() {
        Mockito.reset(transactionRepository);
    }

    /**
     * Tests reward calculation for a valid set of transactions.
     * Ensures that total and monthly points are calculated correctly.
     */
    @Test
    void testGetMonthlyRewards_Success() {
        // Given valid transactions
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, CUSTOMER_ID, 120.0, LocalDate.now().minusMonths(1)),
                new Transaction(2L, CUSTOMER_ID, 75.0, LocalDate.now().minusMonths(2)),
                new Transaction(3L, CUSTOMER_ID, 50.0, LocalDate.now().minusMonths(2))
        );

        when(transactionRepository.findByCustomerIdAndTransactionDateAfter(anyString(), any()))
                .thenReturn(transactions);

        // When
        RewardPointsDTO result = rewardService.getMonthlyRewards(CUSTOMER_ID);

        // Then
        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getCustomerId());
        assertEquals(115, result.getTotalPoints()); // (40+50) from 120, (25) from 75, (0) from 50
        assertEquals(2, result.getMonthlyPoints().size());
    }

    /**
     * Tests behavior when no transactions are found for the customer.
     * Ensures that an exception is thrown with the correct status and message.
     */
    @Test
    void testGetMonthlyRewards_NoTransactionsFound() {
        // Given no transactions found
        when(transactionRepository.findByCustomerIdAndTransactionDateAfter(anyString(), any()))
                .thenReturn(Collections.emptyList());

        // When & Then
        RewardProcessingException exception = assertThrows(RewardProcessingException.class, () ->
                rewardService.getMonthlyRewards(CUSTOMER_ID));

        assertEquals("No transactions found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    /**
     * Tests behavior when a database error occurs while fetching transactions.
     * Ensures that a service-unavailable exception is thrown.
     */
    @Test
    void testGetMonthlyRewards_DatabaseAccessException() {
        // Given database error
        when(transactionRepository.findByCustomerIdAndTransactionDateAfter(anyString(), any()))
                .thenThrow(new DataAccessException("Database Error") {
                });

        // When & Then
        RewardProcessingException exception = assertThrows(RewardProcessingException.class, () ->
                rewardService.getMonthlyRewards(CUSTOMER_ID));

        assertEquals("Database unavailable", exception.getMessage());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    /**
     * Tests handling of negative transaction amounts.
     * Ensures that an exception is thrown for invalid data.
     */
    @Test
    void testCalculatePoints_NegativeAmountSpent() {
        // Given a transaction with a negative amount
        List<Transaction> transactions = Collections.singletonList(
                new Transaction(1L, CUSTOMER_ID, -10.0, LocalDate.now().minusMonths(1))
        );

        when(transactionRepository.findByCustomerIdAndTransactionDateAfter(anyString(), any()))
                .thenReturn(transactions);

        // When & Then
        RewardProcessingException exception = assertThrows(RewardProcessingException.class, () ->
                rewardService.getMonthlyRewards(CUSTOMER_ID));

        assertTrue(exception.getMessage().contains("Invalid data: Amount spent cannot be negative"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    /**
     * Tests scenario where transactions exist but do not generate any reward points.
     * Ensures that the total points remain zero.
     */
    @Test
    public void testGetMonthlyRewards_TransactionsWithNoRewards() {
        List<Transaction> transactionsWithNoRewards = List.of(
                new Transaction(3L, CUSTOMER_ID, 30.0, LocalDate.parse("2025-01-20")), // Below 50, should get 0 points
                new Transaction(4L, CUSTOMER_ID, 45.0, LocalDate.parse("2025-02-05"))  // Below 50, should get 0 points
        );

        when(transactionRepository.findByCustomerIdAndTransactionDateAfter(eq(CUSTOMER_ID), any(LocalDate.class)))
                .thenReturn(transactionsWithNoRewards);

        RewardPointsDTO result = rewardService.getMonthlyRewards(CUSTOMER_ID);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getCustomerId());
        assertEquals(0, result.getTotalPoints()); // Total points should be 0
        assertTrue(result.getMonthlyPoints().values().stream().allMatch(points -> points == 0));

        verify(transactionRepository, times(1)).findByCustomerIdAndTransactionDateAfter(eq(CUSTOMER_ID), any(LocalDate.class));
    }
}
