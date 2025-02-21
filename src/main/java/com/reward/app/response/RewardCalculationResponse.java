package com.reward.app.response;

import com.reward.app.dto.RewardPointsDTO;

import java.util.List;

public class RewardCalculationResponse {
    private List<RewardPointsDTO> rewards;

    public RewardCalculationResponse() {
    }

    public RewardCalculationResponse(List<RewardPointsDTO> rewards) {
        this.rewards = rewards;
    }

    public List<RewardPointsDTO> getRewards() {
        return rewards;
    }

    public void setRewards(List<RewardPointsDTO> rewards) {
        this.rewards = rewards;
    }
}
