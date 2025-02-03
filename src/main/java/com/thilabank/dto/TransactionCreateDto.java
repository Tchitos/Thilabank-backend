package com.thilabank.dto;

import com.thilabank.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionCreateDto {

    @NotNull
    private TransactionType transactionType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    private String description;

    private String fromAccountNumber;
    private String toAccountNumber;
}
