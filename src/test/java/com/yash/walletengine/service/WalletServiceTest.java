package com.yash.walletengine.service;
import com.yash.walletengine.dto.LedgerEntryResponse;
import com.yash.walletengine.dto.PageResponse;
import com.yash.walletengine.entity.LedgerEntryType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import com.yash.walletengine.dto.DepositRequest;
import com.yash.walletengine.entity.LedgerEntry;
import com.yash.walletengine.repository.LedgerEntryRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.yash.walletengine.dto.CreateWalletRequest;
import com.yash.walletengine.dto.WalletResponse;
import com.yash.walletengine.entity.Wallet;
import com.yash.walletengine.exception.WalletNotFoundException;
import com.yash.walletengine.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void createWallet_savesWalletWithZeroBalance() {
        UUID id = UUID.randomUUID();
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet w = invocation.getArgument(0);
            w.setId(id);
            return w;
        });

        WalletResponse response = walletService.createWallet(new CreateWalletRequest(42L));

        assertEquals(id, response.id());
        assertEquals(42L, response.userId());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.balance()));
    }

    @Test
    void getWallet_existingId_returnsWallet() {
        UUID id = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setUserId(7L);
        wallet.setBalance(new BigDecimal("100.00"));
        when(walletRepository.findById(id)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getWallet(id);

        assertEquals(id, response.id());
        assertEquals(7L, response.userId());
        assertEquals(0, new BigDecimal("100.00").compareTo(response.balance()));
    }

    @Test
    void getWallet_unknownId_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(walletRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.getWallet(id));
    }
    @Test
    void deposit_existingWallet_writesLedgerAndUpdatesBalance() {
        UUID id = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.ZERO);
        when(walletRepository.findById(id)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

        WalletResponse response = walletService.deposit(id, new DepositRequest(new BigDecimal("100")));

        assertEquals(0, new BigDecimal("100").compareTo(response.balance()));
        verify(ledgerEntryRepository).save(any(LedgerEntry.class));
    }

    @Test
    void deposit_unknownWallet_throwsAndWritesNothing() {
        UUID id = UUID.randomUUID();
        when(walletRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class,
                () -> walletService.deposit(id, new DepositRequest(new BigDecimal("100"))));

        verifyNoInteractions(ledgerEntryRepository);
    }
    @Test
    void getLedger_unknownWallet_throwsAndQueriesNoEntries() {
        UUID id = UUID.randomUUID();
        when(walletRepository.existsById(id)).thenReturn(false);

        assertThrows(WalletNotFoundException.class,
                () -> walletService.getLedger(id, PageRequest.of(0, 20)));

        verifyNoInteractions(ledgerEntryRepository);
    }

    @Test
    void getLedger_existingWallet_returnsPage() {
        UUID id = UUID.randomUUID();
        LedgerEntry entry = new LedgerEntry(id, new BigDecimal("100"), LedgerEntryType.DEPOSIT);
        Pageable pageable = PageRequest.of(0, 20);
        when(walletRepository.existsById(id)).thenReturn(true);
        when(ledgerEntryRepository.findByWalletId(id, pageable))
                .thenReturn(new PageImpl<>(List.of(entry), pageable, 1));

        PageResponse<LedgerEntryResponse> response = walletService.getLedger(id, pageable);

        assertEquals(1, response.content().size());
        assertEquals(1, response.totalElements());
    }
}