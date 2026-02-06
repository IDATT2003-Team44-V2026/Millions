package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.calculators.SaleCalculator;
import java.math.BigDecimal;

public class Sale extends Transaction {

    public Sale(Share share, int week) {
        super(share, week, new SaleCalculator(share));
    }

    @Override
    protected void executeTransaction(Player player) {
        if (!player.getPortfolio().contains(getShare())) {
            throw new IllegalStateException("Player does not own the share to sell");
        }

        BigDecimal totalValue = getCalculator().calculateTotal();

        player.addMoney(totalValue);
        player.getPortfolio().removeShare(getShare());
    }
}
