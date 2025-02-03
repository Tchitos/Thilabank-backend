package com.thilabank.dto;

import com.thilabank.entity.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDto {

    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String fromAccountNumber;
    private String toAccountNumber;
    private LocalDateTime createdAt;
}
