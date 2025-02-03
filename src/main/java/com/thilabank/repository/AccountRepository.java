package com.thilabank.repository;

import com.thilabank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByOwnerEmail(String email);

    Account findByAccountNumber(String accountNumber);
}
