package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.calculators.SaleCalculator;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import edu.ntnu.idi.idatt2003.view.components.ReceiptDialogPane;
import edu.ntnu.idi.idatt2003.view.components.SaleDialogPane;
import edu.ntnu.idi.idatt2003.view.components.SectionCard;
import edu.ntnu.idi.idatt2003.view.components.TableCells;
import edu.ntnu.idi.idatt2003.view.components.TablePlaceholders;
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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View for the portfolio section of the game screen.
 */
public class PortfolioView {

  private final VBox root;
  private final TextField searchField;
  private final TableView<ShareRow> shareTable;
  private final ObservableList<ShareRow> shareRows;
  private final FilteredList<ShareRow> filteredShareRows;
  private final SortedList<ShareRow> sortedShareRows;
  private final SaleDialogPane sellDialogPane;
  private final ReceiptDialogPane receiptPane;
  private Share selectedShare;
  private Consumer<Share> sellHandler = share -> {};
  private Consumer<Share> confirmSellHandler = share -> {};
  private Consumer<Parent> showModalHandler = modal -> {};
  private Runnable hideModalHandler = () -> {};

  /**
   * Creates the portfolio section view.
   */
  public PortfolioView() {
    shareRows = FXCollections.observableArrayList();
    filteredShareRows = new FilteredList<>(shareRows, row -> true);
    sortedShareRows = new SortedList<>(filteredShareRows);

    searchField = buildSearchField();
    shareTable = buildTable();
    sellDialogPane = new SaleDialogPane(this::hideSellOverlay, this::confirmSell);
    receiptPane = new ReceiptDialogPane(this::hideReceiptOverlay);

    VBox content = new VBox(14, searchField, shareTable);
    content.setFillWidth(true);
    content.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(shareTable, Priority.ALWAYS);

    root = SectionCard.create(
        "Portfolio",
        "Review owned shares and sell holdings.",
        content
    );
  }

  private TextField buildSearchField() {
    TextField field = new TextField();
    field.setPromptText("Search by symbol or company");
    field.getStyleClass().add("form-input");
    field.setMaxWidth(Double.MAX_VALUE);
    field.textProperty().addListener((observable, oldValue, newValue) ->
        applySearchFilter(newValue)
    );
    return field;
  }

  private TableView<ShareRow> buildTable() {
    TableView<ShareRow> table = new TableView<>();
    table.getStyleClass().add("data-table");
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setFixedCellSize(44);
    table.setMaxHeight(Double.MAX_VALUE);
    table.setPlaceholder(TablePlaceholders.create("You don't own any shares yet."));
    sortedShareRows.comparatorProperty().bind(table.comparatorProperty());
    table.setItems(sortedShareRows);
    configureColumns(table);
    return table;
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
    sellDialogPane.clearError();
    sellDialogPane.setHeading(
        "Sell " + share.stock().getSymbol(),
        share.stock().getCompany()
            + " · Quantity: " + formatQuantity(share.quantity())
            + " · Avg cost: " + formatCurrency(share.purchasePrice())
    );
    updateSellPreview();
    showModalHandler.accept(sellDialogPane.getRoot());
  }

  /**
   * Shows an error in the sell modal.
   *
   * @param message the error message to show
   */
  public void showSellError(String message) {
    sellDialogPane.showError(message);
  }

  /**
   * Closes the sell modal.
   */
  public void closeSellDialog() {
    hideSellOverlay();
  }

  /**
   * Replaces the sell dialog with a sale receipt for the completed transaction.
   *
   * @param share the share that was sold
   */
  public void showSellReceipt(Share share) {
    SaleCalculator calc = new SaleCalculator(share);
    String qty = share.quantity().stripTrailingZeros().toPlainString() + " share(s)";
    receiptPane.setContent(
        "Sale Receipt",
        share.stock().getSymbol() + " · " + share.stock().getCompany() + " · " + qty,
        List.of(
            new ReceiptDialogPane.ReceiptRow(
                "Sale price", formatCurrency(share.stock().getSalesPrice()), false),
            new ReceiptDialogPane.ReceiptRow(
                "Quantity", share.quantity().stripTrailingZeros().toPlainString(), false),
            new ReceiptDialogPane.ReceiptRow(
                "Gross value", formatCurrency(calc.calculateGross()), false),
            new ReceiptDialogPane.ReceiptRow(
                "Commission (1 %)", formatCurrency(calc.calculateCommission()), false),
            new ReceiptDialogPane.ReceiptRow(
                "Tax (30 % of profit)", formatCurrency(calc.calculateTax()), false),
            new ReceiptDialogPane.ReceiptRow(
                "Net proceeds", formatCurrency(calc.calculateTotal()), true)
        )
    );
    showModalHandler.accept(receiptPane.getRoot());
  }

  private void configureColumns(TableView<ShareRow> table) {
    TableColumn<ShareRow, String> symbolColumn = new TableColumn<>("Symbol");
    symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());

    TableColumn<ShareRow, String> companyColumn = new TableColumn<>("Company");
    companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

    TableColumn<ShareRow, BigDecimal> quantityColumn = new TableColumn<>("Qty");
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    quantityColumn.setCellFactory(column -> TableCells.quantity());

    TableColumn<ShareRow, BigDecimal> avgCostColumn = new TableColumn<>("Avg cost");
    avgCostColumn.setCellValueFactory(cellData -> cellData.getValue().avgCostProperty());
    avgCostColumn.setCellFactory(column -> TableCells.currency());

    TableColumn<ShareRow, BigDecimal> valueColumn = new TableColumn<>("Value");
    valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
    valueColumn.setCellFactory(column -> TableCells.currency());

    TableColumn<ShareRow, BigDecimal> profitLossColumn = new TableColumn<>("P/L");
    profitLossColumn.setCellValueFactory(cellData -> cellData.getValue().profitLossProperty());
    profitLossColumn.setCellFactory(column -> TableCells.signedChange());

    TableColumn<ShareRow, Void> actionColumn = new TableColumn<>("Action");
    actionColumn.setCellFactory(column -> sellButtonCell());
    actionColumn.setMinWidth(96);
    actionColumn.setMaxWidth(110);

    table.getColumns().add(symbolColumn);
    table.getColumns().add(companyColumn);
    table.getColumns().add(quantityColumn);
    table.getColumns().add(avgCostColumn);
    table.getColumns().add(valueColumn);
    table.getColumns().add(profitLossColumn);
    table.getColumns().add(actionColumn);
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

  private void updateSellPreview() {
    if (selectedShare == null) {
      return;
    }

    SaleCalculator calculator = new SaleCalculator(selectedShare);
    sellDialogPane.setPreview(
        formatCurrency(calculator.calculateGross()),
        formatCurrency(calculator.calculateCommission()),
        formatCurrency(calculator.calculateTax()),
        formatCurrency(calculator.calculateTotal())
    );
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

  private void hideReceiptOverlay() {
    hideModalHandler.run();
  }

  private static String formatCurrency(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
  }

  private static String formatQuantity(BigDecimal quantity) {
    return quantity.stripTrailingZeros().toPlainString();
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
