package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.calculators.SaleCalculator;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import java.math.BigDecimal;

/**
 * Represents a sale transaction where a player sells a share.
 *
 * <p>When committed, the sale adds the total sale value to the player's balance
 * and removes the share from the player's portfolio. Uses a {@link SaleCalculator} to compute the
 * financial values.</p>
 *
 * <p>The player must own the share being sold; otherwise the transaction
 * will fail with an exception.</p>
 */
public class Sale extends Transaction {

  /**
   * Creates a new sale transaction for the given share and week.
   *
   * @param share the share to sell; must not be {@code null}
   * @param week  the week number when the sale takes place; must not be negative
   * @throws IllegalArgumentException if {@code share} is {@code null} or if {@code week} is
   *                                  negative
   */
  public Sale(Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  /**
   * {@inheritDoc}
   *
   * <p>Completes the sale by:
   * <ol>
   *   <li>Adding the total sale value to the player's balance</li>
   *   <li>Removing the share from the player's portfolio</li>
   * </ol>
   *
   * @throws IllegalStateException if the player does not own the share
   */
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
