package edu.ntnu.idi.idatt2003.model;

import java.math.BigDecimal;

public class Player {
    private final String name;
    private final BigDecimal startingMoney;
    private BigDecimal money;
    private final Portfolio portfolio;
    // private final TransactionArchive transactionArchive; - not yet implemented

    public Player(String name, BigDecimal startingMoney) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (startingMoney == null) {
            throw new IllegalArgumentException("Starting money cannot be null");
        }
        if (startingMoney.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Starting money cannot be negative");
        }
        
        this.name = name.trim();
        this.startingMoney = startingMoney;
        this.money = startingMoney;
        this.portfolio = new Portfolio();
        // this.transactionArchive = new TransactionArchive(); - not yet implemented
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void addMoney(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.money = this.money.add(amount);
    }

    public void withdrawMoney(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.money.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.money = this.money.subtract(amount);
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    // public TransactionArchive getTransactionArchive() { - not yet implemented
    //     return transactionArchive;
    // }
}
