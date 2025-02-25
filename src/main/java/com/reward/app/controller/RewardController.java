package com.reward.app.controller;

import com.reward.app.exception.RewardProcessingException;
import com.reward.app.response.RewardCalculationResponse;
import com.reward.app.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
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
            @PathVariable(required = false) String customerId) {
        System.out.println("Controller method called: customerId = " + customerId);

        if (customerId == null || customerId.trim().isEmpty()) {
            throw new RewardProcessingException("Customer ID cannot be null or empty", HttpStatus.BAD_REQUEST);
        }

        try {
            return ResponseEntity.ok(new RewardCalculationResponse(rewardService.getMonthlyRewards(customerId)));
        } catch (RewardProcessingException ex) {
            throw ex; // Rethrow to be handled by GlobalExceptionHandler
        } catch (Exception ex) {
            throw new RewardProcessingException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
