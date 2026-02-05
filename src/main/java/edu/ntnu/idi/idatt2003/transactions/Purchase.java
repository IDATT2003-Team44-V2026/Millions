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
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        java.math.BigDecimal totalCost = getCalculator().calculateTotal();
        
        if (player.getMoney().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient funds to complete purchase");
        }
        
        player.withdrawMoney(totalCost);
        player.getPortfolio().addShare(getShare());
        player.getTransactionArchive().add(this);
        
        setCommitted();
    }
}
