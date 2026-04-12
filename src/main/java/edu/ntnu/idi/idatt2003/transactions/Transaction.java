package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.calculators.TransactionCalculator;

/**
 * Abstract base class for financial transactions in the stock-trading game.
 *
 * <p>A transaction is tied to a {@link Share} and a specific week, and uses a
 * {@link TransactionCalculator} to compute its financial values. Each transaction
 * is unique and can only be committed once; the {@code committed} flag prevents
 * duplicate execution.</p>
 *
 * <p>Subclasses must implement {@link #executeTransaction(Player)} to define the
 * concrete behaviour of the transaction (e.g. buying or selling).</p>
 *
 * @see Purchase
 * @see Sale
 */
public abstract class Transaction {
    private final Share share;
    private final int week;
    private final TransactionCalculator calculator;
    private boolean committed;

    /**
     * Creates a new transaction
     *
     * @param share      the share involved in the transaction; must not be {@code null}
     * @param week       the week number when the transaction takes place; must not be negative
     * @param calculator the calculator used to compute values and costs;
     *                   must not be {@code null}
     * @throws IllegalArgumentException if {@code share} is {@code null},
     *                                  if {@code week} is negative,
     *                                  or if {@code calculator} is {@code null}
     */
    protected Transaction(Share share, int week, TransactionCalculator calculator) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        if (week < 0) {
            throw new IllegalArgumentException("Week cannot be negative");
        }
        if (calculator == null) {
            throw new IllegalArgumentException("Calculator cannot be null");
        }
        
        this.share = share;
        this.week = week;
        this.calculator = calculator;
        this.committed = false;
    }

    /**
     * Returns the share involved in this transaction.
     *
     * @return the {@link Share}
     */
    public Share getShare() {
        return share;
    }

    /**
     * Returns the week number when this transaction takes place.
     *
     * @return the week number
     */
    public int getWeek() {
        return week;
    }

    /**
     * Returns the calculator used to compute the financial values of this transaction.
     *
     * @return the {@link TransactionCalculator}
     */
    public TransactionCalculator getCalculator() {
        return calculator;
    }

    /**
     * Checks whether this transaction has already been committed.
     *
     * @return {@code true} if the transaction has been committed, {@code false} otherwise
     */
    public boolean isCommitted() {
        return committed;
    }

    /**
     * Commits this transaction for the given player.
     *
     * <p>The method delegates to {@link #executeTransaction(Player)} to perform the
     * concrete transaction logic, then stores the transaction in the player's
     * {@link edu.ntnu.idi.idatt2003.transactions.TransactionArchive TransactionArchive}
     * and marks it as committed. A transaction can only be committed once.</p>
     *
     * @param player the player to commit the transaction for; must not be {@code null}
     * @throws IllegalStateException    if the transaction has already been committed
     * @throws IllegalArgumentException if {@code player} is {@code null}
     */
    public void commit(Player player) {
        if (committed) {
            throw new IllegalStateException("Transaction has already been committed");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        executeTransaction(player);

        player.getTransactionArchive().add(this);
        this.committed = true;
    }

    /**
     * Executes the concrete transaction logic for the given player.
     *
     * <p>Subclasses implement this method to define what happens when the
     * transaction is committed (e.g. adjusting the player's balance and portfolio).</p>
     *
     * @param player the player to execute the transaction for
     */
    protected abstract void executeTransaction(Player player);
}
