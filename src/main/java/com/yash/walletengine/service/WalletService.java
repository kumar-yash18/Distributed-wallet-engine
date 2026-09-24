
package com.yash.walletengine.service;

import com.yash.walletengine.dto.CreateWalletRequest;
import com.yash.walletengine.dto.WalletResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WalletService {

    private final Map<UUID, WalletResponse> wallets = new ConcurrentHashMap<>();

    public WalletResponse createWallet(CreateWalletRequest request) {
        UUID id = UUID.randomUUID();
        WalletResponse wallet = new WalletResponse(id, request.userId(), BigDecimal.ZERO);
        wallets.put(id, wallet);
        return wallet;
    }

    public WalletResponse getWallet(UUID id) {
        WalletResponse wallet = wallets.get(id);
        if (wallet == null) {
            throw new NoSuchElementException("Wallet not found: " + id);
        }
        return wallet;
    }
}