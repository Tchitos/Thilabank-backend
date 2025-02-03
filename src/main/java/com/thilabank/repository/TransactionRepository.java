package com.thilabank.repository;

import com.thilabank.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccountOrderByTimestampDesc(
            String fromAccNumber,
            String toAccNumber
    );

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.fromAccount IN :accNumbers
       OR t.toAccount IN :accNumbers
    ORDER BY t.timestamp DESC
""")
    List<Transaction> findByFromAccountInOrToAccountInOrderByTimestampDesc(@Param("accNumbers") List<String> accNumbers);
}
