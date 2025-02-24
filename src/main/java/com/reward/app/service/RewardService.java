package com.reward.app.service;

import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.exception.RewardProcessingException;

/**
 * Service interface for calculating and retrieving reward points.
 * <p>
 * This interface defines the contract for processing transactions
 * and computing reward points for a given customer based on their spending history.
 * </p>
 */
public interface RewardService {

    /**
     * Calculates and retrieves monthly and total reward points for a given customer.
     * <p>
     * The method fetches the customer's transactions for the last three months,
     * calculates reward points per month, and returns a summary.
     * </p>
     *
     * @param customerId the unique identifier of the customer
     * @return a {@link RewardPointsDTO} containing total and monthly reward details
     * @throws RewardProcessingException if no transactions are found for the given customer ID
     */
    RewardPointsDTO getMonthlyRewards(String customerId);
}
