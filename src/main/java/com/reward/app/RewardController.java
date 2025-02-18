package com.reward.app;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle requests related to rewards processing.
 * Provides endpoints for calculating rewards based on transactions.
 */
@RestController
@RequestMapping("/rewards")
class RewardController {

    private final RewardService rewardService;

    /**
     * Constructor to inject the RewardService.
     *
     * @param rewardService the service that handles reward points calculations
     */
    RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Endpoint to calculate monthly rewards based on the list of transactions.
     * It processes each transaction, calculates points, and returns the reward points for each customer.
     *
     * @param transactions the list of transactions that need to be processed
     * @return a list of RewardPointsDTO containing the total and monthly reward points for each customer
     * @throws RewardProcessingException if an error occurs while processing the transactions
     */
    @PostMapping("/calculate")
    public List<RewardPointsDTO> calculatePoints(@RequestBody List<Transaction> transactions) {
        return rewardService.getMonthlyRewards(transactions);
    }
}
