package com.yash.walletengine.controller;
import com.yash.walletengine.dto.LedgerEntryResponse;
import com.yash.walletengine.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.yash.walletengine.dto.CreateWalletRequest;
import com.yash.walletengine.dto.DepositRequest;
import com.yash.walletengine.dto.WalletResponse;
import com.yash.walletengine.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse wallet = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID id) {
        return ResponseEntity.ok(walletService.getWallet(id));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<WalletResponse> deposit(@PathVariable UUID id,
                                                  @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(id, request));
    }
    @GetMapping("/{id}/ledger")
    public ResponseEntity<PageResponse<LedgerEntryResponse>> getLedger(
            @PathVariable UUID id,
            @PageableDefault(size = 20, sort = {"createdAt", "id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(walletService.getLedger(id, pageable));
    }
}