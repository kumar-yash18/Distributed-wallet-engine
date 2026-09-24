package com.yash.walletengine.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        Long userId,
        BigDecimal balance
) {}