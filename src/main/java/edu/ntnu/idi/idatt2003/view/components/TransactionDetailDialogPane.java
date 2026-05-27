package edu.ntnu.idi.idatt2003.view.components;

import edu.ntnu.idi.idatt2003.calculators.TransactionCalculator;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.transactions.Purchase;
import edu.ntnu.idi.idatt2003.transactions.Transaction;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Read-only detail modal showing the full financial breakdown of a historical transaction.
 */
public final class TransactionDetailDialogPane {

  private final VBox root;
  private final Label badgeLabel;
  private final Label titleLabel;
  private final Label subtitleLabel;
  private final VBox summaryCard;

  /**
   * Creates a transaction detail dialog pane.
   *
   * @param onClose action invoked when the Close button is pressed
   */
  public TransactionDetailDialogPane(Runnable onClose) {
    badgeLabel = new Label();
    badgeLabel.getStyleClass().add("tx-detail-badge");

    titleLabel = new Label();
    titleLabel.getStyleClass().add("modal-title");

    subtitleLabel = new Label();
    subtitleLabel.getStyleClass().add("modal-subtitle");
    subtitleLabel.setWrapText(true);

    VBox heading = new VBox(6, badgeLabel, titleLabel, subtitleLabel);
    heading.getStyleClass().add("modal-heading");

    Region divider = new Region();
    divider.getStyleClass().add("receipt-divider");

    summaryCard = new VBox(6);
    summaryCard.getStyleClass().add("summary-card");

    Button closeButton = new Button("Close");
    closeButton.getStyleClass().add("primary-button");
    closeButton.setMinWidth(120);
    closeButton.setOnAction(e -> onClose.run());

    HBox actions = new HBox(closeButton);
    actions.getStyleClass().add("modal-actions");
    actions.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, heading, divider, summaryCard, actions);
    root.getStyleClass().addAll("modal-card", "receipt-card");
    root.setMaxSize(400, Region.USE_PREF_SIZE);
  }

  private static String format(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
  }

  /**
   * Returns the modal root node.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Populates the pane with data from the given transaction.
   *
   * @param transaction the transaction to display
   */
  public void setTransaction(Transaction transaction) {
    boolean isPurchase = transaction instanceof Purchase;
    Share share = transaction.getShare();
    TransactionCalculator calc = transaction.getCalculator();

    badgeLabel.setText(isPurchase ? "Purchase" : "Sale");
    badgeLabel.getStyleClass().removeAll("tx-badge-purchase", "tx-badge-sale");
    badgeLabel.getStyleClass().add(isPurchase ? "tx-badge-purchase" : "tx-badge-sale");

    titleLabel.setText(share.stock().getSymbol() + " — " + share.stock().getCompany());
    subtitleLabel.setText("Week " + transaction.getWeek());

    summaryCard.getChildren().clear();

    BigDecimal quantity = share.quantity();
    BigDecimal gross = calc.calculateGross();
    BigDecimal pricePerShare = quantity.compareTo(BigDecimal.ZERO) == 0
        ? BigDecimal.ZERO
        : gross.divide(quantity, 2, RoundingMode.HALF_UP);

    addRow("Price per share", format(pricePerShare), false);
    addRow("Quantity", quantity.stripTrailingZeros().toPlainString(), false);
    addRow(isPurchase ? "Gross cost" : "Gross value", format(gross), false);
    addRow(isPurchase ? "Commission (0.5 %)" : "Commission (1 %)",
        format(calc.calculateCommission()), false);

    BigDecimal tax = calc.calculateTax();
    if (!isPurchase) {
      addRow("Tax (30 % of profit)", format(tax), false);
    }

    addRow(isPurchase ? "Total paid" : "Total received", format(calc.calculateTotal()), true);
  }

  private void addRow(String label, String value, boolean total) {
    Label valueLabel = new Label(value);
    HBox row = SummaryRows.row(label, valueLabel);
    if (total) {
      row.getStyleClass().add("summary-row-total");
    }
    summaryCard.getChildren().add(row);
  }
}
