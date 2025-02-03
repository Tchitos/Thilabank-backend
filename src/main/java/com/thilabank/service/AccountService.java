package com.thilabank.service;

import com.thilabank.dto.AccountCreateDto;
import com.thilabank.dto.AccountResponseDto;
import com.thilabank.entity.Account;
import com.thilabank.entity.Transaction;
import com.thilabank.entity.User;
import com.thilabank.exceptions.BadRequestException;
import com.thilabank.repository.AccountRepository;
import com.thilabank.repository.TransactionRepository;
import com.thilabank.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public AccountResponseDto createAccount(String email, AccountCreateDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Account account = new Account();
        account.setOwner(user);
        account.setType(dto.getType());

        accountRepository.save(account);

        return toResponseDto(account);
    }

    public void deleteAccount(String email, String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BadRequestException("Account not found");
        }
        if (!account.getOwner().getEmail().equals(email)) {
            throw new SecurityException("You are not the owner of this account");
        }

        List<Transaction> referencingTx = transactionRepository.findByFromAccountOrToAccountOrderByTimestampDesc(accountNumber, accountNumber);

        List<Transaction> toRemove = new ArrayList<>();

        for (Transaction tx : referencingTx) {
            String fromAcc = tx.getFromAccount();
            String toAcc = tx.getToAccount();

            if (accountNumber.equals(fromAcc)) {
                if (toAcc == null || accountRepository.findByAccountNumber(toAcc) == null) {
                    toRemove.add(tx);
                }
            }

            if (accountNumber.equals(toAcc)) {
                if (fromAcc == null || accountRepository.findByAccountNumber(fromAcc) == null) {
                    toRemove.add(tx);
                }
            }
        }
        if (!toRemove.isEmpty()) {
            transactionRepository.deleteAll(toRemove);
        }
        accountRepository.delete(account);
    }

    public List<AccountResponseDto> getAccounts(String email) {
        List<Account> accounts = accountRepository.findByOwnerEmail(email);

        return accounts.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private AccountResponseDto toResponseDto(Account account) {
        AccountResponseDto dto = new AccountResponseDto();
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setType(account.getType());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }
}
