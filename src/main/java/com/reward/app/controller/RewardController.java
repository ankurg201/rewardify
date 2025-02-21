package com.reward.app.controller;

import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.dto.TransactionDTO;
import com.reward.app.exception.RewardProcessingException;
import com.reward.app.request.RewardCalculationRequest;
import com.reward.app.response.RewardCalculationResponse;
import com.reward.app.service.RewardService;
import org.springframework.http.ResponseEntity;
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
     * @param request the list of transactions that need to be processed
     * @return a list of RewardPointsDTO containing the total and monthly reward points for each customer
     * @throws RewardProcessingException if an error occurs while processing the transactions
     */
    @PostMapping("/calculate")
    public ResponseEntity<RewardCalculationResponse> calculateRewards(
            @RequestBody RewardCalculationRequest request) {

        return ResponseEntity.ok(
                new RewardCalculationResponse(rewardService.getMonthlyRewards(request.getTransactions()))
        );
    }
}
