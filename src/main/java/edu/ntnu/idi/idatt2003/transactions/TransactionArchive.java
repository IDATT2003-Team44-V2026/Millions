package edu.ntnu.idi.idatt2003.transactions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An archive that stores all completed {@link Transaction} objects for a player.
 *
 * <p>The archive provides methods to retrieve transactions filtered by week and
 * type, as well as a count of distinct trading weeks.</p>
 */
public class TransactionArchive {

  private final List<Transaction> transactions;

  /**
   * Creates a new, empty transaction archive.
   */
  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }

  /**
   * Adds a transaction to the archive.
   *
   * @param transaction the transaction to add; must not be {@code null}
   * @return {@code true} if the transaction was successfully added
   * @throws IllegalArgumentException if {@code transaction} is {@code null}
   */
  public boolean add(Transaction transaction) {
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null");
    }
    return transactions.add(transaction);
  }

  /**
   * Checks whether the archive contains any transactions.
   *
   * @return {@code true} if the archive is empty, {@code false} otherwise
   */
  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  /**
   * Returns all transactions that occurred in the given week.
   *
   * @param week the week number to filter by; must not be negative
   * @return a list of transactions from the specified week
   * @throws IllegalArgumentException if {@code week} is negative
   */
  public List<Transaction> getTransactions(int week) {
    if (week < 0) {
      throw new IllegalArgumentException("Week cannot be negative");
    }
    return transactions.stream()
        .filter(t -> t.getWeek() == week)
        .toList();
  }

  /**
   * Returns all purchase transactions that occurred in the given week.
   *
   * @param week the week number to filter by; must not be negative
   * @return a list of {@link Purchase} transactions from the specified week
   * @throws IllegalArgumentException if {@code week} is negative
   */
  public List<Purchase> getPurchases(int week) {
    if (week < 0) {
      throw new IllegalArgumentException("Week cannot be negative");
    }
    return transactions.stream()
        .filter(t -> t instanceof Purchase && t.getWeek() == week)
        .map(t -> (Purchase) t)
        .toList();
  }

  /**
   * Returns all sale transactions that occurred in the given week.
   *
   * @param week the week number to filter by; must not be negative
   * @return a list of {@link Sale} transactions from the specified week
   * @throws IllegalArgumentException if {@code week} is negative
   */
  public List<Sale> getSales(int week) {
    if (week < 0) {
      throw new IllegalArgumentException("Week cannot be negative");
    }
    return transactions.stream()
        .filter(t -> t instanceof Sale && t.getWeek() == week)
        .map(t -> (Sale) t)
        .toList();
  }

  /**
   * Counts the number of distinct weeks in which transactions have been recorded.
   *
   * @return the number of unique trading weeks
   */
  public int countDistinctWeeks() {
    return (int) transactions.stream()
        .map(Transaction::getWeek)
        .distinct()
        .count();
  }

  /**
   * Returns all transactions in the archive, sorted by week descending.
   *
   * <p>Within the same week, insertion order is preserved. The returned list
   * is unmodifiable.</p>
   *
   * @return an unmodifiable list of all transactions, newest week first
   */
  public List<Transaction> getAllTransactions() {
    return transactions.stream()
        .sorted(Comparator.comparing(Transaction::getWeek).reversed())
        .toList();
  }
}
