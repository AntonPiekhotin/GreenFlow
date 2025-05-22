package org.greenflow.billing.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.billing.model.entity.UserBalance;
import org.greenflow.billing.output.persistent.UserBalanceRepository;
import org.greenflow.common.model.dto.event.BalanceChangeMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBalanceService {

    private final UserBalanceRepository userBalanceRepository;

    public void changeBalance(BalanceChangeMessage message) {
        log.debug("Changing balance for user {}", message.userId());
        UserBalance userBalance = userBalanceRepository.findById(message.userId())
                .orElseThrow(() -> new GreenFlowException(400, "User balance not found"));
        userBalance.setBalance(userBalance.getBalance().add(message.amount()));
        userBalanceRepository.save(userBalance);
        log.info("User balance updated: {}", userBalance);
    }

    public BigDecimal getUserBalance(@NotBlank String userId) {
        return userBalanceRepository.findById(userId)
                .map(UserBalance::getBalance)
                .orElseThrow(() -> new GreenFlowException(400, "User balance not found"));
    }
}
