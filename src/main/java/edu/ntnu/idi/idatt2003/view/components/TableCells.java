package edu.ntnu.idi.idatt2003.view.components;

import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import java.math.BigDecimal;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

/**
 * Shared table cell factories for game views.
 */
public final class TableCells {

  private static final String POSITIVE_CHANGE_CLASS = "positive-change";
  private static final String NEGATIVE_CHANGE_CLASS = "negative-change";
  private static final String NEUTRAL_CHANGE_CLASS = "neutral-change";

  private TableCells() {
  }

  /**
   * Creates a right-aligned currency cell.
   *
   * @param <T> the row type
   * @return the cell factory
   */
  public static <T> TableCell<T, BigDecimal> currency() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : formatCurrency(item));
        setAlignment(Pos.CENTER_RIGHT);
      }
    };
  }

  /**
   * Creates a right-aligned signed currency change cell.
   *
   * @param <T> the row type
   * @return the cell factory
   */
  public static <T> TableCell<T, BigDecimal> signedChange() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeAll(
            POSITIVE_CHANGE_CLASS,
            NEGATIVE_CHANGE_CLASS,
            NEUTRAL_CHANGE_CLASS
        );

        if (empty || item == null) {
          setText(null);
          return;
        }

        setText(formatSignedChange(item));
        setAlignment(Pos.CENTER_RIGHT);
        if (item.compareTo(BigDecimal.ZERO) > 0) {
          getStyleClass().add(POSITIVE_CHANGE_CLASS);
        } else if (item.compareTo(BigDecimal.ZERO) < 0) {
          getStyleClass().add(NEGATIVE_CHANGE_CLASS);
        } else {
          getStyleClass().add(NEUTRAL_CHANGE_CLASS);
        }
      }
    };
  }

  /**
   * Creates a right-aligned quantity cell.
   *
   * @param <T> the row type
   * @return the cell factory
   */
  public static <T> TableCell<T, BigDecimal> quantity() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : formatQuantity(item));
        setAlignment(Pos.CENTER_RIGHT);
      }
    };
  }

  /**
   * Creates a right-aligned integer cell.
   *
   * @param <T> the row type
   * @return the cell factory
   */
  public static <T> TableCell<T, Integer> integer() {
    return new TableCell<>() {
      @Override
      protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.toString());
        setAlignment(Pos.CENTER_RIGHT);
      }
    };
  }

  /**
   * Creates a transaction total cell colored by buy (negative) or sell (positive).
   *
   * @param <T> the row type
   * @param isSale checks whether the row represents a sale
   * @return the cell factory
   */
  public static <T> TableCell<T, BigDecimal> transactionTotal(java.util.function.Function<T, Boolean> isSale) {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeAll(POSITIVE_CHANGE_CLASS, NEGATIVE_CHANGE_CLASS);

        if (empty || item == null) {
          setText(null);
          return;
        }

        T row = getTableRow() == null ? null : getTableRow().getItem();
        setText(formatCurrency(item));
        setAlignment(Pos.CENTER_RIGHT);
        if (row != null && Boolean.TRUE.equals(isSale.apply(row))) {
          getStyleClass().add(POSITIVE_CHANGE_CLASS);
        } else {
          getStyleClass().add(NEGATIVE_CHANGE_CLASS);
        }
      }
    };
  }

  private static String formatCurrency(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
  }

  private static String formatQuantity(BigDecimal quantity) {
    return quantity.stripTrailingZeros().toPlainString();
  }

  private static String formatSignedChange(BigDecimal change) {
    if (change.compareTo(BigDecimal.ZERO) > 0) {
      return "+" + formatCurrency(change);
    }
    if (change.compareTo(BigDecimal.ZERO) < 0) {
      return formatCurrency(change);
    }
    return formatCurrency(BigDecimal.ZERO);
  }
}
