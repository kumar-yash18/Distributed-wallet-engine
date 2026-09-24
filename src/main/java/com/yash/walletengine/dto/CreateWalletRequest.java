package com.yash.walletengine.dto;

import jakarta.validation.constraints.NotNull;

public record CreateWalletRequest(
        @NotNull Long userId
) {}
