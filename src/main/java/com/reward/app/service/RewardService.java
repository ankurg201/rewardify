package com.reward.app.service;

import com.reward.app.dto.RewardPointsDTO;
import com.reward.app.dto.TransactionDTO;

import java.util.List;

public interface RewardService {

    List<RewardPointsDTO> getMonthlyRewards(List<TransactionDTO> transactions);
}
