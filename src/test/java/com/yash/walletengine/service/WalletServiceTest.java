package com.yash.walletengine.service;

import com.yash.walletengine.dto.CreateWalletRequest;
import com.yash.walletengine.dto.WalletResponse;
import com.yash.walletengine.exception.WalletNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WalletServiceTest {

    private final WalletService walletService = new WalletService();

    @Test
    void createWallet_shouldReturnWalletWithZeroBalance() {
        // Arrange
        CreateWalletRequest request = new CreateWalletRequest(123L);

        // Act
        WalletResponse response = walletService.createWallet(request);

        // Assert
        assertThat(response.id()).isNotNull();
        assertThat(response.userId()).isEqualTo(123L);
        assertThat(response.balance()).isEqualByComparingTo("0");
    }

    @Test
    void getWallet_shouldThrowWhenWalletDoesNotExist() {
        // Arrange
        UUID randomId = UUID.randomUUID();

        // Act + Assert
        assertThatThrownBy(() -> walletService.getWallet(randomId))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining(randomId.toString());
    }
}