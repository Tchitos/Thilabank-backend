package com.thilabank.controller;

import com.thilabank.dto.TransactionCreateDto;
import com.thilabank.dto.TransactionResponseDto;
import com.thilabank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionCreateDto dto
    ) {
        String email = userDetails.getUsername();
        TransactionResponseDto created = transactionService.createTransaction(email, dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAllForUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        List<TransactionResponseDto> transactions = transactionService.getAllTransactionsForUser(email);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionResponseDto>> getAllForAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String accountNumber
    ) {
        String email = userDetails.getUsername();
        List<TransactionResponseDto> transactions = transactionService.getAllTransactionsForAccount(email, accountNumber);
        return ResponseEntity.ok(transactions);
    }
}
