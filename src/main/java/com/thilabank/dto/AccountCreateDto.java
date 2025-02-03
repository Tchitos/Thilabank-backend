package com.thilabank.dto;

import com.thilabank.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountCreateDto {

    @NotNull
    private AccountType type;

}
