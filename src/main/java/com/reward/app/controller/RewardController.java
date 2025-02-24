package com.reward.app.controller;

import com.reward.app.exception.RewardProcessingException;
import com.reward.app.response.RewardCalculationResponse;
import com.reward.app.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling customer reward calculations.
 * <p>
 * This controller provides an endpoint to compute reward points for a given customer,
 * based on their transactions over the last three months.
 * </p>
 */
@RestController
@RequestMapping("/rewards")
class RewardController {

    private final RewardService rewardService;

    /**
     * Constructs a new {@code RewardController} and injects the required {@link RewardService}.
     *
     * @param rewardService the service responsible for computing reward points
     */
    RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Calculates monthly and total reward points for a given customer.
     * <p>
     * This endpoint retrieves the customer's transactions for the last three months,
     * computes their reward points, and returns the monthly breakdown along with the total points.
     * </p>
     *
     * @param customerId the unique identifier of the customer whose rewards are to be calculated
     * @return a {@link ResponseEntity} containing {@link RewardCalculationResponse},
     * which includes the total and monthly reward details
     * @throws RewardProcessingException if an error occurs while processing the customer's transactions
     */
    @GetMapping("/calculate/{customerId}")
    public ResponseEntity<RewardCalculationResponse> calculateRewards(
            @PathVariable String customerId) {

        return ResponseEntity.ok(
                new RewardCalculationResponse(rewardService.getMonthlyRewards(customerId))
        );
    }
}
