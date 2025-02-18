package com.reward.app;

import java.util.List;

public interface RewardService {

    List<RewardPointsDTO> getMonthlyRewards(List<Transaction> transactions);

    //public List<RewardPointsDTO> getMonthlyRewards_old(List<Transaction> transactions);
}
