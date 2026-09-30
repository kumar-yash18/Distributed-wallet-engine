package com.yash.walletengine.service;

import com.yash.walletengine.dto.CreateWalletRequest;
import com.yash.walletengine.dto.DepositRequest;
import com.yash.walletengine.dto.WalletResponse;
import com.yash.walletengine.entity.LedgerEntry;
import com.yash.walletengine.entity.LedgerEntryType;
import com.yash.walletengine.entity.Wallet;
import com.yash.walletengine.exception.WalletNotFoundException;
import com.yash.walletengine.repository.LedgerEntryRepository;
import com.yash.walletengine.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setUserId(request.userId());
        wallet.setBalance(BigDecimal.ZERO);
        Wallet saved = walletRepository.save(wallet);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet with id " + id + " not found"));
        return toResponse(wallet);
    }

    // DELIBERATELY NO @Transactional on this method (Day 8). Day 9 fixes this.
    public WalletResponse deposit(UUID walletId, DepositRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet with id " + walletId + " not found"));

        // WRITE 1: ledger row. save() commits in its OWN mini-transaction, right now.
        LedgerEntry entry = new LedgerEntry(walletId, request.amount(), LedgerEntryType.DEPOSIT);
        ledgerEntryRepository.save(entry);

        // >>> TRANSACTION BOUNDARY NEEDED HERE <<<
        // If the app crashes, or anything throws, between WRITE 1 and WRITE 2,
        // the ledger says +amount but the balance never changed.
        // Both writes must commit together or not at all.

        // WRITE 2: balance update. A SEPARATE commit.
        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        // ALSO UNSAFE: findById above and save() here are a read-modify-write with no lock.
        // Two concurrent deposits can read the same old balance and overwrite each other.
        // The fix (pessimistic locking, SELECT ... FOR UPDATE) comes in a later day.

        return toResponse(wallet);
    }

    private WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getUserId(), wallet.getBalance());
    }
}