package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.calculators.PurchaseCalculator;

public class Purchase extends Transaction {

    public Purchase(Share share, int week) {
        super(share, week, new PurchaseCalculator(share));
    }

    @Override
    public void commit(Player player) {
        if (isCommitted()) {
            throw new IllegalStateException("Transaction has already been committed");
        }
        // TODO: Implement purchase logic
        setCommitted();
    }
}
