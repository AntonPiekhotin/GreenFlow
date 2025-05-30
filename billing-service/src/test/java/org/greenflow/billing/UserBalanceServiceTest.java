package org.greenflow.billing;

import org.greenflow.billing.model.entity.UserBalance;
import org.greenflow.billing.output.persistent.UserBalanceRepository;
import org.greenflow.billing.service.UserBalanceService;
import org.greenflow.common.model.dto.event.BalanceChangeMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBalanceServiceTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @InjectMocks
    private UserBalanceService userBalanceService;

    @Test
    void registerUser_createsUserBalanceWithZeroBalance() {
        String userId = "user123";
        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(userId);
        userBalance.setBalance(BigDecimal.ZERO);

        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        Boolean result = userBalanceService.registerUser(userId);

        assertTrue(result);
        verify(userBalanceRepository).save(any(UserBalance.class));
    }

    @Test
    void changeBalance_updatesUserBalanceSuccessfully() {
        String userId = "user123";
        BigDecimal initialBalance = BigDecimal.valueOf(50.0);
        BigDecimal changeAmount = BigDecimal.valueOf(20.0);
        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(userId);
        userBalance.setBalance(initialBalance);

        when(userBalanceRepository.findById(userId)).thenReturn(Optional.of(userBalance));

        BalanceChangeMessage message = new BalanceChangeMessage(userId, changeAmount, "Test balance change");
        userBalanceService.changeBalance(message);

        assertEquals(initialBalance.add(changeAmount), userBalance.getBalance());
        verify(userBalanceRepository).save(userBalance);
    }

    @Test
    void changeBalance_throwsExceptionWhenUserBalanceNotFound() {
        String userId = "user123";
        BalanceChangeMessage message = new BalanceChangeMessage(userId, BigDecimal.valueOf(20.0),
                "Test balance change");

        when(userBalanceRepository.findById(userId)).thenReturn(Optional.empty());

        GreenFlowException exception = assertThrows(GreenFlowException.class, () ->
                userBalanceService.changeBalance(message)
        );

        assertEquals(400, exception.getStatusCode());
        verify(userBalanceRepository, never()).save(any(UserBalance.class));
    }

    @Test
    void getUserBalance_returnsCurrentBalance() {
        String userId = "user123";
        BigDecimal balance = BigDecimal.valueOf(100.0);
        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(userId);
        userBalance.setBalance(balance);

        when(userBalanceRepository.findById(userId)).thenReturn(Optional.of(userBalance));

        BigDecimal result = userBalanceService.getUserBalance(userId);

        assertEquals(balance, result);
    }

    @Test
    void getUserBalance_throwsExceptionWhenUserBalanceNotFound() {
        String userId = "user123";

        when(userBalanceRepository.findById(userId)).thenReturn(Optional.empty());

        GreenFlowException exception = assertThrows(GreenFlowException.class, () ->
                userBalanceService.getUserBalance(userId)
        );

        assertEquals(400, exception.getStatusCode());
    }
}