package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.calculators.PurchaseCalculator;
import java.math.BigDecimal;


public class Purchase extends Transaction {

    public Purchase(Share share, int week) {
        super(share, week, new PurchaseCalculator(share));
    }

    @Override
    protected void executeTransaction(Player player) {
        BigDecimal totalCost = getCalculator().calculateTotal();

        if (player.getMoney().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient funds to complete purchase");
        }

        player.withdrawMoney(totalCost);
        player.getPortfolio().addShare(getShare());
    }
}
