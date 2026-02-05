package edu.ntnu.idi.idatt2003.transactions;

import java.util.ArrayList;
import java.util.List;

public class TransactionArchive {
    private final List<Transaction> transactions;

    public TransactionArchive() {
        this.transactions = new ArrayList<>();
    }

    public boolean add(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        return transactions.add(transaction);
    }

    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    public List<Transaction> getTransactions(int week) {
        if (week < 0) {
            throw new IllegalArgumentException("Week cannot be negative");
        }
        return transactions.stream()
                .filter(t -> t.getWeek() == week)
                .toList();
    }

    public List<Purchase> getPurchases(int week) {
        if (week < 0) {
            throw new IllegalArgumentException("Week cannot be negative");
        }
        return transactions.stream()
                .filter(t -> t instanceof Purchase && t.getWeek() == week)
                .map(t -> (Purchase) t)
                .toList();
    }

    public List<Sale> getSales(int week) {
        if (week < 0) {
            throw new IllegalArgumentException("Week cannot be negative");
        }
        return transactions.stream()
                .filter(t -> t instanceof Sale && t.getWeek() == week)
                .map(t -> (Sale) t)
                .toList();
    }

    public int countDistinctWeeks() {
        return (int) transactions.stream()
                .map(Transaction::getWeek)
                .distinct()
                .count();
    }
}
