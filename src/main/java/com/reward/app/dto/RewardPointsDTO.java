package com.reward.app.dto;

import java.util.Map;

public class RewardPointsDTO {
    private String customerId;
    private Integer totalPoints;
    private Map<String, Integer> monthlyPoints;

    public RewardPointsDTO(String customerId, Integer totalPoints, Map<String, Integer> monthlyPoints) {
        this.customerId = customerId;
        this.totalPoints = totalPoints;
        this.monthlyPoints = monthlyPoints;
    }

    public RewardPointsDTO() {

    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Map<String, Integer> getMonthlyPoints() {
        return monthlyPoints;
    }

    public void setMonthlyPoints(Map<String, Integer> monthlyPoints) {
        this.monthlyPoints = monthlyPoints;
    }
}
