package com.thilabank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        balance = BigDecimal.valueOf(0);
        accountNumber = "THILAB" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS")) + owner.getId();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Account{" +
                "updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", owner=" + owner +
                ", balance=" + balance +
                ", accountNumber='" + accountNumber + '\'' +
                ", id=" + id +
                '}';
    }
}
