package com.reward.app.service;

import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.model.Transaction;
import com.reward.app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for calculating and managing reward points for a customer.
 * <p>
 * This service fetches transactions for a given customer within the last three months
 * and computes reward points based on the spending criteria.
 * </p>
 */
@Service
public class RewardServiceImpl implements RewardService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Calculates reward points based on the amount spent.
     * <p>
     * Reward calculation rules:
     * <ul>
     *     <li>For every dollar spent above $100, 2 points are awarded per dollar.</li>
     *     <li>For amounts between $50 and $100, 1 point is awarded per dollar.</li>
     *     <li>Amounts below $50 do not earn any points.</li>
     * </ul>
     * </p>
     *
     * @param amountSpent The amount spent in a transaction.
     * @return The calculated reward points.
     * @throws RewardProcessingException if the amount spent is negative.
     */
    private int calculatePoints(double amountSpent) {
        if (amountSpent < 0) {
            throw new RewardProcessingException("Amount spent cannot be negative: " + amountSpent);
        }
        return (amountSpent > 100 ? 2 * ((int) amountSpent - 100) : 0) +
                (amountSpent > 50 ? ((int) Math.min(amountSpent, 100) - 50) : 0);
    }

    /**
     * Fetches transactions for the last three months and calculates monthly and total reward points
     * for a given customer.
     * <p>
     * The method retrieves transactions, groups them by month, calculates reward points,
     * and returns a summary with total and monthly points.
     * </p>
     *
     * @param customerId The unique identifier of the customer whose transactions are being analyzed.
     * @return A {@link RewardPointsDTO} object containing total and monthly reward points.
     * @throws RewardProcessingException if no transactions are found for the customer.
     */
    @Override
    public RewardPointsDTO getMonthlyRewards(String customerId) {

        // Fetch transactions and handle if empty
        List<Transaction> transactions = Optional.ofNullable(fetchRecentTransactions(customerId))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new RewardProcessingException("No transactions found for customer: " + customerId));

        // Process transactions and calculate points
        Map<String, Integer> monthlyPoints = transactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getTransactionDate().format(MONTH_FORMATTER),
                        Collectors.summingInt(tx -> calculatePoints(tx.getAmountSpent()))
                ));

        // Calculate total points
        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

        // Return DTO with computed rewards
        return new RewardPointsDTO(customerId, totalPoints, monthlyPoints);
    }

    /**
     * Fetches transactions for a given customer within the last three months.
     * <p>
     * This method queries the database for transactions that occurred after the calculated date.
     * </p>
     *
     * @param customerId The unique identifier of the customer.
     * @return A list of {@link Transaction} objects representing the customer's recent transactions.
     */
    private List<Transaction> fetchRecentTransactions(String customerId) {
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        return transactionRepository.findByCustomerIdAndTransactionDateAfter(customerId, threeMonthsAgo);
    }
}
