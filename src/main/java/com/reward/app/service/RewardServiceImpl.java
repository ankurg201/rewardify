package com.reward.app.service;

import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.dto.TransactionDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.exception.TransactionNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for managing and processing reward points.
 * This service provides functionality to calculate reward points based on the amount spent by customers
 * and return the monthly rewards for each customer.
 */
@Service
public class RewardServiceImpl implements RewardService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Calculates reward points based on the amount spent.
     *
     * <p>
     * If the amount spent exceeds $100, the points are calculated as twice the amount above $100.
     * If the amount is between $50 and $100, the points are calculated as the amount above $50.
     * </p>
     *
     * @param amountSpent the amount spent by the customer
     * @return the calculated reward points for the given amount spent
     * @throws RewardProcessingException if the amount spent is negative
     */
    public int calculatePoints(double amountSpent) {
        if (amountSpent < 0) {
            throw new RewardProcessingException("Amount spent cannot be negative: " + amountSpent);
        }

        return (amountSpent > 100 ? 2 * ((int) amountSpent - 100) : 0) +
                (amountSpent > 50 ? ((int) Math.min(amountSpent, 100) - 50) : 0);
    }

    /**
     * Processes a list of transactions and calculates the monthly rewards for each customer.
     *
     * <p>
     * The method iterates through the transactions, calculates the points for each transaction,
     * and aggregates the results for each customer. It also handles invalid or missing transaction data.
     * </p>
     *
     * @param transactions a list of transactions to process
     * @return a list of RewardPointsDTO objects, each containing the reward details for a customer
     * @throws TransactionNotFoundException if the provided list of transactions is null or empty
     * @throws RewardProcessingException    if a transaction contains invalid data such as missing fields or an invalid date format
     */
    public List<RewardPointsDTO> getMonthlyRewards(List<TransactionDTO> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found to process rewards.");
        }

        Map<String, RewardPointsDTO> tempMap = new HashMap<>();

        for (TransactionDTO item : transactions) {
            try {
                // Null checks before processing
                if (item.getCustomerId() == null || item.getTransactionDate() == null) {
                    throw new RewardProcessingException("Transaction data is missing required fields.");
                }

                int points = calculatePoints(item.getAmountSpent());

                // Convert "YYYY-MM-DD" to "YYYY-MM" safely
                String monthKey;
                try {
                    monthKey = LocalDate.parse(item.getTransactionDate()).format(MONTH_FORMATTER);
                } catch (DateTimeParseException e) {
                    throw new RewardProcessingException("Invalid date format: " + item.getTransactionDate() + " for " + item.getCustomerId());
                }

                // Fetch or create RewardPointsDTO using computeIfAbsent
                RewardPointsDTO rewardPointsDTO = tempMap.computeIfAbsent(item.getCustomerId(), k -> {
                    RewardPointsDTO dto = new RewardPointsDTO();
                    dto.setCustomerId(k);
                    dto.setTotalPoints(0);
                    dto.setMonthlyPoints(new HashMap<>());
                    return dto;
                });

                // Update total points
                rewardPointsDTO.setTotalPoints(rewardPointsDTO.getTotalPoints() + points);

                // Update monthly points
                Map<String, Integer> monthWiseMap = rewardPointsDTO.getMonthlyPoints();
                monthWiseMap.put(monthKey, monthWiseMap.getOrDefault(monthKey, 0) + points);

            } catch (RewardProcessingException e) {
                // Log and continue processing remaining transactions
                System.err.println("failed transaction due to error: " + e.getMessage());
                throw e;
            }
        }
        return new ArrayList<>(tempMap.values());
    }
}