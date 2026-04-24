package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;

/**
 * Creates {@link Transaction} objects.
 */
public class TransactionFactory {

    /**
     * Creates a transaction of the given type.
     *
     * @param type  the transaction type
     * @param share the share involved
     * @param week  the transaction week
     * @return a new transaction
     * @throws IllegalArgumentException if {@code type} is {@code null}
     */
    public Transaction createTransaction(
        TransactionType type,
        Share share,
        int week
    ) {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        return switch (type) {
            case PURCHASE -> new Purchase(share, week);
            case SALE -> new Sale(share, week);
        };
    }
}