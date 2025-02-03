package com.thilabank.controller;

import com.thilabank.dto.AccountCreateDto;
import com.thilabank.dto.AccountResponseDto;
import com.thilabank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AccountCreateDto createDto
    ) {
        String email = userDetails.getUsername();
        AccountResponseDto responseDto = accountService.createAccount(email, createDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Map<String, String>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String accountNumber
    ) {
        String email = userDetails.getUsername();
        accountService.deleteAccount(email, accountNumber);
        return ResponseEntity.ok(Collections.emptyMap());
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getUserAccounts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        List<AccountResponseDto> accounts = accountService.getAccounts(email);
        return ResponseEntity.ok(accounts);
    }
}
