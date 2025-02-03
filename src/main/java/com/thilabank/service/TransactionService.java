package com.thilabank.service;

import com.thilabank.dto.TransactionCreateDto;
import com.thilabank.dto.TransactionResponseDto;
import com.thilabank.entity.Account;
import com.thilabank.entity.Transaction;
import com.thilabank.entity.TransactionType;
import com.thilabank.exceptions.BadRequestException;
import com.thilabank.repository.AccountRepository;
import com.thilabank.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public TransactionResponseDto createTransaction(String email, TransactionCreateDto dto) {

        Account fromAcc = null;
        Account toAcc = null;

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant doit être supérieur à 0.");
        }

        switch (dto.getTransactionType()) {
            case DEPOSIT:
                if (dto.getToAccountNumber() == null) {
                    throw new IllegalArgumentException("toAccountNumber is required for DEPOSIT");
                }
                toAcc = findAccountAndCheckOwner(dto.getToAccountNumber(), email);
                break;

            case WITHDRAWAL:
                if (dto.getFromAccountNumber() == null) {
                    throw new IllegalArgumentException("fromAccountNumber is required for WITHDRAWAL");
                }
                fromAcc = findAccountAndCheckOwner(dto.getFromAccountNumber(), email);
                break;

            case TRANSFER:
                if (dto.getFromAccountNumber() == null || dto.getToAccountNumber() == null) {
                    throw new IllegalArgumentException("Both fromAccountNumber and toAccountNumber are required for TRANSFER");
                }
                fromAcc = findAccountAndCheckOwner(dto.getFromAccountNumber(), email);
                toAcc = accountRepository.findByAccountNumber(dto.getToAccountNumber());
                if (toAcc == null) {
                    throw new BadRequestException("Compte inconnu : " + dto.getToAccountNumber());
                }
                break;

            default:
                throw new IllegalStateException("Unsupported transaction type");
        }

        updateBalances(dto.getTransactionType(), fromAcc, toAcc, dto.getAmount());

        Transaction tx = new Transaction();
        tx.setTransactionType(dto.getTransactionType());
        tx.setAmount(dto.getAmount());
        tx.setCurrency(dto.getCurrency());
        tx.setDescription(dto.getDescription());
        tx.setFromAccount(dto.getFromAccountNumber());
        tx.setToAccount(dto.getToAccountNumber());

        transactionRepository.save(tx);

        return toResponseDto(tx);
    }

    public List<TransactionResponseDto> getAllTransactionsForUser(String email) {
        List<Account> accounts = accountRepository.findByOwnerEmail(email);

        List<String> accountNumbers = accounts.stream()
                .map(Account::getAccountNumber)
                .collect(Collectors.toList());

        List<Transaction> txs = transactionRepository
                .findByFromAccountInOrToAccountInOrderByTimestampDesc(accountNumbers);

        return txs.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDto> getAllTransactionsForAccount(String email, String accountNumber) {
        Account account = findAccountAndCheckOwner(accountNumber, email);

        List<Transaction> txs =
                transactionRepository.findByFromAccountOrToAccountOrderByTimestampDesc(
                        accountNumber, accountNumber
                );

        return txs.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private Account findAccountAndCheckOwner(String accountNumber, String email) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new EntityNotFoundException("Account not found : " + accountNumber);
        }
        if (!account.getOwner().getEmail().equals(email)) {
            throw new SecurityException("This account does not belong to you");
        }
        return account;
    }

    private void updateBalances(TransactionType type, Account fromAcc, Account toAcc, BigDecimal amount) {
        switch (type) {
            case DEPOSIT:
                toAcc.setBalance(toAcc.getBalance().add(amount));
                accountRepository.save(toAcc);
                break;

            case WITHDRAWAL:
                if (fromAcc.getBalance().compareTo(amount) < 0) {
                    throw new BadRequestException("Fonds insuffisants");
                }
                fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
                accountRepository.save(fromAcc);
                break;

            case TRANSFER:
                if (fromAcc.getBalance().compareTo(amount) < 0) {
                    throw new BadRequestException("Fonds insuffisants pour ce transfert");
                }
                fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
                accountRepository.save(fromAcc);

                toAcc.setBalance(toAcc.getBalance().add(amount));
                accountRepository.save(toAcc);
                break;
        }
    }

    private TransactionResponseDto toResponseDto(Transaction tx) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(tx.getId());
        dto.setTransactionType(tx.getTransactionType());
        dto.setAmount(tx.getAmount());
        dto.setCurrency(tx.getCurrency());
        dto.setDescription(tx.getDescription());
        dto.setCreatedAt(tx.getTimestamp());

        if (tx.getFromAccount() != null) {
            dto.setFromAccountNumber(tx.getFromAccount());
        }
        if (tx.getToAccount() != null) {
            dto.setToAccountNumber(tx.getToAccount());
        }

        return dto;
    }
}
