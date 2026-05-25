package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.calculators.SaleCalculator;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * View for the portfolio section of the game screen.
 */
public class PortfolioView {

  private static final String POSITIVE_CHANGE_CLASS = "positive-change";
  private static final String NEGATIVE_CHANGE_CLASS = "negative-change";
  private static final String NEUTRAL_CHANGE_CLASS = "neutral-change";

  private final VBox root;
  private final TextField searchField;
  private final TableView<ShareRow> shareTable;
  private final ObservableList<ShareRow> shareRows;
  private final FilteredList<ShareRow> filteredShareRows;
  private final SortedList<ShareRow> sortedShareRows;
  private final VBox sellDialog;
  private final Label sellTitleLabel;
  private final Label sellSubtitleLabel;
  private final Label grossValueLabel;
  private final Label commissionValue;
  private final Label taxValue;
  private final Label totalProceedsValue;
  private final Label sellErrorLabel;
  private Share selectedShare;
  private Consumer<Share> sellHandler = share -> {
  };
  private Consumer<Share> confirmSellHandler = share -> {
  };
  private Consumer<Parent> showModalHandler = modal -> {
  };
  private Runnable hideModalHandler = () -> {
  };

  /**
   * Creates the portfolio section view.
   */
  public PortfolioView() {
    Label titleLabel = new Label("Portfolio");
    titleLabel.getStyleClass().add("section-title");

    Label subtitleLabel = new Label("Review owned shares and sell holdings.");
    subtitleLabel.getStyleClass().add("subtitle-label");

    searchField = new TextField();
    searchField.setPromptText("Search by symbol or company");
    searchField.getStyleClass().add("form-input");
    searchField.setMaxWidth(Double.MAX_VALUE);

    shareRows = FXCollections.observableArrayList();
    filteredShareRows = new FilteredList<>(shareRows, row -> true);
    sortedShareRows = new SortedList<>(filteredShareRows);
    searchField.textProperty().addListener((observable, oldValue, newValue) ->
        applySearchFilter(newValue)
    );

    shareTable = new TableView<>();
    shareTable.getStyleClass().add("data-table");
    shareTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    shareTable.setFixedCellSize(44);
    shareTable.setMaxHeight(Double.MAX_VALUE);
    sortedShareRows.comparatorProperty().bind(shareTable.comparatorProperty());
    shareTable.setItems(sortedShareRows);
    VBox.setVgrow(shareTable, Priority.ALWAYS);
    configureColumns();

    VBox portfolioCard = new VBox(14, titleLabel, subtitleLabel, searchField, shareTable);
    portfolioCard.getStyleClass().add("content-card");
    portfolioCard.setPadding(new Insets(18));
    portfolioCard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

    sellTitleLabel = new Label();
    sellSubtitleLabel = new Label();
    grossValueLabel = new Label();
    commissionValue = new Label();
    taxValue = new Label();
    totalProceedsValue = new Label();
    sellErrorLabel = new Label();
    sellDialog = createSellDialog();

    root = portfolioCard;
  }

  private static TableCell<ShareRow, BigDecimal> currencyCell() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : formatCurrency(item));
        setAlignment(Pos.CENTER_RIGHT);
      }
    };
  }

  private static TableCell<ShareRow, BigDecimal> quantityCell() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : formatQuantity(item));
        setAlignment(Pos.CENTER_RIGHT);
      }
    };
  }

  private static TableCell<ShareRow, BigDecimal> profitLossCell() {
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

        setText(formatProfitLoss(item));
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

  private static HBox createSummaryRow(String labelText, Label valueLabel) {
    Label label = new Label(labelText);
    label.getStyleClass().add("summary-label");
    valueLabel.getStyleClass().add("summary-value");

    HBox row = new HBox(16, label, valueLabel);
    row.getStyleClass().add("summary-row");
    row.setAlignment(Pos.CENTER_LEFT);
    row.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(label, Priority.ALWAYS);
    valueLabel.setAlignment(Pos.CENTER_RIGHT);
    return row;
  }

  private static String formatCurrency(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
  }

  private static String formatQuantity(BigDecimal quantity) {
    return quantity.stripTrailingZeros().toPlainString();
  }

  private static String formatProfitLoss(BigDecimal profitLoss) {
    if (profitLoss.compareTo(BigDecimal.ZERO) > 0) {
      return "+" + formatCurrency(profitLoss);
    }
    if (profitLoss.compareTo(BigDecimal.ZERO) < 0) {
      return formatCurrency(profitLoss);
    }
    return formatCurrency(BigDecimal.ZERO);
  }

  /**
   * Returns the root node.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Updates the shares shown in the portfolio table.
   *
   * @param shares the owned shares to show
   */
  public void setShares(List<Share> shares) {
    shareRows.setAll(shares.stream()
        .map(ShareRow::new)
        .toList());
    applySearchFilter(searchField.getText());
  }

  /**
   * Sets the handler for sell actions.
   *
   * @param handler the handler receiving the selected share
   */
  public void setOnSell(Consumer<Share> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    sellHandler = handler;
  }

  /**
   * Sets the handler for confirmed sell actions.
   *
   * @param handler the handler receiving the selected share
   */
  public void setOnConfirmSell(Consumer<Share> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    confirmSellHandler = handler;
  }

  /**
   * Sets handlers used to show and hide modal content in the owning shell.
   *
   * @param showModalHandler the handler used to show modal content
   * @param hideModalHandler the handler used to hide modal content
   */
  public void setModalHandlers(Consumer<Parent> showModalHandler, Runnable hideModalHandler) {
    if (showModalHandler == null || hideModalHandler == null) {
      throw new IllegalArgumentException("Modal handlers cannot be null");
    }
    this.showModalHandler = showModalHandler;
    this.hideModalHandler = hideModalHandler;
  }

  /**
   * Shows the sell modal for a selected share.
   *
   * @param share the selected share
   */
  public void showSellDialog(Share share) {
    selectedShare = share;
    sellErrorLabel.setText("");
    sellTitleLabel.setText("Sell " + share.stock().getSymbol());
    sellSubtitleLabel.setText(share.stock().getCompany()
        + " · Quantity: " + formatQuantity(share.quantity())
        + " · Avg cost: " + formatCurrency(share.purchasePrice()));
    updateSellPreview();
    showModalHandler.accept(sellDialog);
  }

  /**
   * Shows an error in the sell modal.
   *
   * @param message the error message to show
   */
  public void showSellError(String message) {
    sellErrorLabel.setText(message);
  }

  /**
   * Closes the sell modal.
   */
  public void closeSellDialog() {
    hideSellOverlay();
  }

  private void configureColumns() {
    TableColumn<ShareRow, String> symbolColumn = new TableColumn<>("Symbol");
    symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());

    TableColumn<ShareRow, String> companyColumn = new TableColumn<>("Company");
    companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

    TableColumn<ShareRow, BigDecimal> quantityColumn = new TableColumn<>("Qty");
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    quantityColumn.setCellFactory(column -> quantityCell());

    TableColumn<ShareRow, BigDecimal> avgCostColumn = new TableColumn<>("Avg cost");
    avgCostColumn.setCellValueFactory(cellData -> cellData.getValue().avgCostProperty());
    avgCostColumn.setCellFactory(column -> currencyCell());

    TableColumn<ShareRow, BigDecimal> valueColumn = new TableColumn<>("Value");
    valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
    valueColumn.setCellFactory(column -> currencyCell());

    TableColumn<ShareRow, BigDecimal> profitLossColumn = new TableColumn<>("P/L");
    profitLossColumn.setCellValueFactory(cellData -> cellData.getValue().profitLossProperty());
    profitLossColumn.setCellFactory(column -> profitLossCell());

    TableColumn<ShareRow, Void> actionColumn = new TableColumn<>("Action");
    actionColumn.setCellFactory(column -> sellButtonCell());
    actionColumn.setMinWidth(96);
    actionColumn.setMaxWidth(110);

    shareTable.getColumns().add(symbolColumn);
    shareTable.getColumns().add(companyColumn);
    shareTable.getColumns().add(quantityColumn);
    shareTable.getColumns().add(avgCostColumn);
    shareTable.getColumns().add(valueColumn);
    shareTable.getColumns().add(profitLossColumn);
    shareTable.getColumns().add(actionColumn);
  }

  private void applySearchFilter(String searchText) {
    String query = searchText == null ? "" : searchText.trim().toLowerCase(Locale.ROOT);
    filteredShareRows.setPredicate(row -> query.isEmpty()
        || row.getSymbol().toLowerCase(Locale.ROOT).contains(query)
        || row.getCompany().toLowerCase(Locale.ROOT).contains(query)
    );
  }

  private TableCell<ShareRow, Void> sellButtonCell() {
    return new TableCell<>() {
      private final Button sellButton = new Button("Sell");

      {
        sellButton.getStyleClass().addAll("secondary-button", "table-action-button");
        sellButton.setOnAction(event -> {
          ShareRow row = getTableView().getItems().get(getIndex());
          sellHandler.accept(row.getShare());
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : sellButton);
        setAlignment(Pos.CENTER);
      }
    };
  }

  private VBox createSellDialog() {
    sellTitleLabel.getStyleClass().add("modal-title");
    sellSubtitleLabel.getStyleClass().add("modal-subtitle");
    sellSubtitleLabel.setWrapText(true);

    VBox heading = new VBox(4, sellTitleLabel, sellSubtitleLabel);
    heading.getStyleClass().add("modal-heading");

    HBox grossRow = createSummaryRow("Gross value", grossValueLabel);
    HBox commissionRow = createSummaryRow("Commission (1 %)", commissionValue);
    HBox taxRow = createSummaryRow("Tax (30 % of profit)", taxValue);
    HBox totalRow = createSummaryRow("Total proceeds", totalProceedsValue);
    totalRow.getStyleClass().add("summary-row-total");

    VBox summaryBox = new VBox(6, grossRow, commissionRow, taxRow, totalRow);
    summaryBox.getStyleClass().add("summary-card");

    sellErrorLabel.getStyleClass().add("error-label");
    sellErrorLabel.setWrapText(true);

    Button cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("secondary-button");
    cancelButton.setOnAction(event -> hideSellOverlay());

    Button confirmButton = new Button("Confirm sale");
    confirmButton.getStyleClass().add("primary-button");
    confirmButton.setOnAction(event -> confirmSell());
    cancelButton.setMinWidth(150);
    confirmButton.setMinWidth(170);

    HBox actions = new HBox(12, cancelButton, confirmButton);
    actions.getStyleClass().add("modal-actions");
    actions.setAlignment(Pos.CENTER_RIGHT);

    VBox dialog = new VBox(
        16,
        heading,
        summaryBox,
        sellErrorLabel,
        actions
    );
    dialog.getStyleClass().add("modal-card");
    dialog.setMaxSize(430, Region.USE_PREF_SIZE);
    return dialog;
  }

  private void updateSellPreview() {
    if (selectedShare == null) {
      return;
    }

    SaleCalculator calculator = new SaleCalculator(selectedShare);
    grossValueLabel.setText(formatCurrency(calculator.calculateGross()));
    commissionValue.setText(formatCurrency(calculator.calculateCommission()));
    taxValue.setText(formatCurrency(calculator.calculateTax()));
    totalProceedsValue.setText(formatCurrency(calculator.calculateTotal()));
  }

  private void confirmSell() {
    if (selectedShare == null) {
      return;
    }
    confirmSellHandler.accept(selectedShare);
  }

  private void hideSellOverlay() {
    hideModalHandler.run();
    selectedShare = null;
  }

  private static final class ShareRow {

    private final Share share;
    private final StringProperty symbol;
    private final StringProperty company;
    private final ObjectProperty<BigDecimal> quantity;
    private final ObjectProperty<BigDecimal> avgCost;
    private final ObjectProperty<BigDecimal> value;
    private final ObjectProperty<BigDecimal> profitLoss;

    private ShareRow(Share share) {
      this.share = share;
      this.symbol = new SimpleStringProperty(share.stock().getSymbol());
      this.company = new SimpleStringProperty(share.stock().getCompany());
      this.quantity = new SimpleObjectProperty<>(share.quantity());
      this.avgCost = new SimpleObjectProperty<>(share.purchasePrice());
      this.value = new SimpleObjectProperty<>(new SaleCalculator(share).calculateTotal());
      this.profitLoss = new SimpleObjectProperty<>(calculateProfitLoss(share));
    }

    private static BigDecimal calculateProfitLoss(Share share) {
      SaleCalculator calculator = new SaleCalculator(share);
      BigDecimal purchaseCost = share.purchasePrice().multiply(share.quantity());
      return calculator.calculateTotal().subtract(purchaseCost);
    }

    private Share getShare() {
      return share;
    }

    private String getSymbol() {
      return symbol.get();
    }

    private String getCompany() {
      return company.get();
    }

    private StringProperty symbolProperty() {
      return symbol;
    }

    private StringProperty companyProperty() {
      return company;
    }

    private ObjectProperty<BigDecimal> quantityProperty() {
      return quantity;
    }

    private ObjectProperty<BigDecimal> avgCostProperty() {
      return avgCost;
    }

    private ObjectProperty<BigDecimal> valueProperty() {
      return value;
    }

    private ObjectProperty<BigDecimal> profitLossProperty() {
      return profitLoss;
    }
  }
}
