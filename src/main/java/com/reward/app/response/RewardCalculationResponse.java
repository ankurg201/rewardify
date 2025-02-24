package com.reward.app.response;

import com.reward.app.dto.RewardPointsDTO;

public class RewardCalculationResponse {
    private RewardPointsDTO reward;

    public RewardCalculationResponse() {
    }

    public RewardCalculationResponse(RewardPointsDTO reward) {
        this.reward = reward;
    }

    public RewardPointsDTO getReward() {
        return reward;
    }

    public void setRewards(RewardPointsDTO reward) {
        this.reward = reward;
    }
}
