package com.thilabank.dto;

import com.thilabank.entity.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AccountResponseDto {
    private String accountNumber;
    private BigDecimal balance;
    private AccountType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
