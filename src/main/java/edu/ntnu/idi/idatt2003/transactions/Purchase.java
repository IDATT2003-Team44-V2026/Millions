package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.calculators.PurchaseCalculator;
import java.math.BigDecimal;

/**
 * Represents a purchase transaction where a player buys a share.
 *
 * <p>When committed, the purchase deducts the total cost from the player's balance
 * and adds the share to the player's portfolio. Uses a {@link PurchaseCalculator}
 * to compute the financial values.</p>
 *
 * <p>The player must have sufficient funds to cover the total cost; otherwise
 * the transaction will fail with an exception.</p>
 */
public class Purchase extends Transaction {

    /**
     * Creates a new purchase transaction for the given share and week.
     *
     * @param share the share to purchase; must not be {@code null}
     * @param week  the week number when the purchase takes place; must not be negative
     * @throws IllegalArgumentException if {@code share} is {@code null}
     *                                  or if {@code week} is negative
     */
    public Purchase(Share share, int week) {
        super(share, week, new PurchaseCalculator(share));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Completes the purchase by:
     * <ol>
     *   <li>Deducting the total cost from the player's balance</li>
     *   <li>Adding the share to the player's portfolio</li>
     * </ol>
     *
     * @throws IllegalStateException if the player has insufficient funds
     */
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
