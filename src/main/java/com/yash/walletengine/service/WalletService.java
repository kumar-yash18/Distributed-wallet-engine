package com.yash.walletengine.service;

import com.yash.walletengine.dto.CreateWalletRequest;
import com.yash.walletengine.dto.DepositRequest;
import com.yash.walletengine.dto.LedgerEntryResponse;
import com.yash.walletengine.dto.PageResponse;
import com.yash.walletengine.dto.WalletResponse;
import com.yash.walletengine.dto.WithdrawRequest;
import com.yash.walletengine.entity.LedgerEntry;
import com.yash.walletengine.entity.LedgerEntryType;
import com.yash.walletengine.entity.Wallet;
import com.yash.walletengine.exception.InsufficientBalanceException;
import com.yash.walletengine.exception.WalletNotFoundException;
import com.yash.walletengine.repository.LedgerEntryRepository;
import com.yash.walletengine.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return toResponse(findWalletOrThrow(id));
    }
    @Transactional
    public WalletResponse deposit(UUID walletId, DepositRequest request) {
        Wallet wallet = findWalletOrThrow(walletId);

        // WRITE 1: ledger row
        ledgerEntryRepository.save(new LedgerEntry(walletId, request.amount(), LedgerEntryType.DEPOSIT));



        // WRITE 2: balance update
        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        // NOTE: read-modify-write with no lock. Concurrent deposits can still overwrite
        // each other even inside a transaction. Pessimistic locking fixes that later.
        return toResponse(wallet);
    }
    @Transactional
    public WalletResponse withdraw(UUID walletId, WithdrawRequest request) {
        Wallet wallet = findWalletOrThrow(walletId);

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException(
                    "Wallet " + walletId + " has insufficient balance for withdrawal of " + request.amount());
        }

        // WRITE 1: ledger row
        ledgerEntryRepository.save(new LedgerEntry(walletId, request.amount(), LedgerEntryType.WITHDRAWAL));



        // WRITE 2: balance update
        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletRepository.save(wallet);

        // NOTE: the balance check above is check-then-act with no lock; two concurrent
        // withdrawals could both pass it. Pessimistic locking fixes that later.
        return toResponse(wallet);
    }

    @Transactional(readOnly = true)
    public PageResponse<LedgerEntryResponse> getLedger(UUID walletId, Pageable pageable) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException("Wallet with id " + walletId + " not found");
        }
        Page<LedgerEntryResponse> page = ledgerEntryRepository
                .findByWalletId(walletId, pageable)
                .map(this::toLedgerResponse);
        return PageResponse.from(page);
    }

    private Wallet findWalletOrThrow(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet with id " + walletId + " not found"));
    }

    // TEMPORARY fault injection: delete after Day 10


    private WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getUserId(), wallet.getBalance());
    }

    private LedgerEntryResponse toLedgerResponse(LedgerEntry entry) {
        return new LedgerEntryResponse(
                entry.getId(),
                entry.getWalletId(),
                entry.getAmount(),
                entry.getType(),
                entry.getCreatedAt()
        );
    }
}