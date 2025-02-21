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
 * REST Controller for handling reward-related operations.
 * <p>
 * This controller provides endpoints to process customer transactions and compute
 * reward points based on predefined business rules.
 * </p>
 */
@RestController
@RequestMapping("/rewards")
class RewardController {

    private final RewardService rewardService;

    /**
     * Constructs a new {@code RewardController} and injects the required {@link RewardService}.
     *
     * @param rewardService the service responsible for reward points calculation
     */
    RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Calculates monthly reward points based on customer transactions.
     * <p>
     * This endpoint processes a list of transactions, computes the reward points for each customer,
     * and returns the total and monthly breakdown of reward points.
     * </p>
     *
     * @param request The request object containing the list of transactions to process.
     * @return A {@link ResponseEntity} containing {@link RewardCalculationResponse},
     * which includes reward details for each customer.
     * @throws RewardProcessingException If an error occurs while processing the transactions.
     */
    @PostMapping("/calculate")
    public ResponseEntity<RewardCalculationResponse> calculateRewards(
            @RequestBody RewardCalculationRequest request) {

        return ResponseEntity.ok(
                new RewardCalculationResponse(rewardService.getMonthlyRewards(request.getTransactions()))
        );
    }
}
