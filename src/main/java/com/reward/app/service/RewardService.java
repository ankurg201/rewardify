package com.reward.app.service;

import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.dto.TransactionDTO;

import java.util.List;

/**
 * Service interface for calculating and retrieving reward points.
 * <p>
 * This interface defines the contract for processing transactions
 * and computing reward points for customers based on their spending.
 * </p>
 */
public interface RewardService {

    /**
     * Calculates and retrieves monthly reward points for each customer
     * based on the provided transaction list.
     *
     * @param transactions the list of transactions to process
     * @return a list of {@link RewardPointsDTO} containing reward details for each customer
     * @throws com.reward.app.exception.RewardProcessingException if transaction data is invalid, empty or null
     */
    List<RewardPointsDTO> getMonthlyRewards(List<TransactionDTO> transactions);
}
