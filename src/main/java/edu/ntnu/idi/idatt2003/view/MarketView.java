package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.calculators.PurchaseCalculator;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import edu.ntnu.idi.idatt2003.view.components.PurchaseDialogPane;
import edu.ntnu.idi.idatt2003.view.components.ReceiptDialogPane;
import edu.ntnu.idi.idatt2003.view.components.SectionCard;
import edu.ntnu.idi.idatt2003.view.components.StockDetailDialogPane;
import edu.ntnu.idi.idatt2003.view.components.TableCells;
import edu.ntnu.idi.idatt2003.view.components.TablePlaceholders;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View for the market section of the game screen.
 */
public class MarketView {

  private final VBox root;
  private final TextField searchField;
  private final TableView<StockRow> stockTable;
  private final ObservableList<StockRow> stockRows;
  private final FilteredList<StockRow> filteredStockRows;
  private final SortedList<StockRow> sortedStockRows;
  private final PurchaseDialogPane buyDialogPane;
  private final ReceiptDialogPane receiptPane;
  private final StockDetailDialogPane detailPane;
  private final VBox gainersRows;
  private final VBox losersRows;
  private Stock selectedStock;
  private Consumer<Stock> buyHandler = stock -> {};
  private Consumer<Stock> detailsHandler = stock -> {};
  private BiConsumer<Stock, BigDecimal> confirmBuyHandler = (stock, quantity) -> {};
  private Consumer<Parent> showModalHandler = modal -> {};
  private Runnable hideModalHandler = () -> {};

  /**
   * Creates the market section view.
   */
  public MarketView() {
    stockRows = FXCollections.observableArrayList();
    filteredStockRows = new FilteredList<>(stockRows, row -> true);
    sortedStockRows = new SortedList<>(filteredStockRows);

    searchField = buildSearchField();
    stockTable = buildTable();
    buyDialogPane = new PurchaseDialogPane(this::hideBuyOverlay, this::confirmBuy);
    buyDialogPane.getQuantityField().textProperty().addListener(
        (observable, oldValue, newValue) -> updateBuyPreview()
    );
    receiptPane = new ReceiptDialogPane(this::hideReceiptOverlay);
    detailPane = new StockDetailDialogPane(() -> hideModalHandler.run());

    gainersRows = new VBox(4);
    losersRows = new VBox(4);
    HBox moversStrip = buildMoversStrip();

    VBox content = new VBox(14, moversStrip, searchField, stockTable);
    content.setFillWidth(true);
    content.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(stockTable, Priority.ALWAYS);

    root = SectionCard.create(
        "Market",
        "Search available stocks and prepare purchases.",
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

  private TableView<StockRow> buildTable() {
    TableView<StockRow> table = new TableView<>();
    table.getStyleClass().add("data-table");
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setFixedCellSize(44);
    table.setMaxHeight(Double.MAX_VALUE);
    table.setPlaceholder(TablePlaceholders.create("No stocks match your search."));
    sortedStockRows.comparatorProperty().bind(table.comparatorProperty());
    table.setItems(sortedStockRows);
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
   * Updates the stocks shown in the market table.
   *
   * @param stocks the listed stocks to show
   */
  public void setStocks(List<Stock> stocks) {
    stockRows.setAll(stocks.stream()
        .map(StockRow::new)
        .toList());
    applySearchFilter(searchField.getText());
  }

  /**
   * Sets the handler for placeholder buy actions.
   *
   * @param handler the handler receiving the selected stock
   */
  public void setOnBuy(Consumer<Stock> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    buyHandler = handler;
  }

  /**
   * Sets the handler invoked when the user clicks Details on a stock row.
   *
   * @param handler the handler receiving the selected stock
   */
  public void setOnDetails(Consumer<Stock> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    detailsHandler = handler;
  }

  /**
   * Shows the stock detail modal for a given stock.
   *
   * @param stock the stock to display
   */
  public void showDetailsDialog(Stock stock) {
    detailPane.setStock(stock);
    showModalHandler.accept(detailPane.getRoot());
  }

  /**
   * Sets the handler for confirmed buy actions.
   *
   * @param handler the handler receiving the selected stock and quantity
   */
  public void setOnConfirmBuy(BiConsumer<Stock, BigDecimal> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    confirmBuyHandler = handler;
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
   * Shows the buy modal for a selected stock.
   *
   * @param stock the selected stock
   */
  public void showBuyDialog(Stock stock) {
    selectedStock = stock;
    buyDialogPane.resetQuantity();
    buyDialogPane.clearError();
    buyDialogPane.setHeading(
        "Buy " + stock.getSymbol(),
        stock.getCompany() + " · Current price: " + formatCurrency(stock.getSalesPrice())
    );
    updateBuyPreview();
    showModalHandler.accept(buyDialogPane.getRoot());
    buyDialogPane.getQuantityField().requestFocus();
  }

  /**
   * Shows an error in the buy modal.
   *
   * @param message the error message to show
   */
  public void showBuyError(String message) {
    buyDialogPane.showError(message);
  }

  /**
   * Closes the buy modal.
   */
  public void closeBuyDialog() {
    hideBuyOverlay();
  }

  /**
   * Replaces the buy dialog with a purchase receipt for the completed transaction.
   *
   * @param stock    the purchased stock
   * @param quantity the number of shares purchased
   */
  public void showBuyReceipt(Stock stock, BigDecimal quantity) {
    Share share = new Share(stock, quantity, stock.getSalesPrice());
    PurchaseCalculator calc = new PurchaseCalculator(share);
    String qty = quantity.stripTrailingZeros().toPlainString() + " share(s)";
    receiptPane.setContent(
        "Purchase Receipt",
        stock.getSymbol() + " · " + stock.getCompany() + " · " + qty,
        List.of(
            new ReceiptDialogPane.ReceiptRow(
                "Price per share", formatCurrency(stock.getSalesPrice()), false),
            new ReceiptDialogPane.ReceiptRow(
                "Quantity", quantity.stripTrailingZeros().toPlainString(), false),
            new ReceiptDialogPane.ReceiptRow(
                "Gross cost", formatCurrency(calc.calculateGross()), false),
            new ReceiptDialogPane.ReceiptRow(
                "Commission (0.5 %)", formatCurrency(calc.calculateCommission()), false),
            new ReceiptDialogPane.ReceiptRow(
                "Total paid", formatCurrency(calc.calculateTotal()), true)
        )
    );
    showModalHandler.accept(receiptPane.getRoot());
  }

  private HBox buildMoversStrip() {
    VBox gainersCard = buildMoversCard("Top Gainers", gainersRows);
    VBox losersCard = buildMoversCard("Top Losers", losersRows);
    HBox strip = new HBox(12, gainersCard, losersCard);
    HBox.setHgrow(gainersCard, Priority.ALWAYS);
    HBox.setHgrow(losersCard, Priority.ALWAYS);
    return strip;
  }

  private static VBox buildMoversCard(String header, VBox rowsContainer) {
    Label headerLabel = new Label(header);
    headerLabel.getStyleClass().add("movers-header");
    VBox card = new VBox(0, headerLabel, rowsContainer);
    card.getStyleClass().add("movers-card");
    card.setPrefWidth(0);
    card.setMaxWidth(Double.MAX_VALUE);
    return card;
  }

  /**
   * Updates the top gainers and losers strip.
   *
   * @param gainers stocks with the highest positive price change
   * @param losers  stocks with the lowest negative price change
   */
  public void setMovers(List<Stock> gainers, List<Stock> losers) {
    populateMoverRows(gainersRows, gainers, true);
    populateMoverRows(losersRows, losers, false);
  }

  private static final int MOVERS_COUNT = 3;

  private void populateMoverRows(VBox container, List<Stock> stocks, boolean positive) {
    container.getChildren().clear();
    for (int i = 0; i < MOVERS_COUNT; i++) {
      if (i < stocks.size()) {
        Stock stock = stocks.get(i);
        Label symbol = new Label(stock.getSymbol());
        symbol.getStyleClass().add("movers-symbol");
        Label change = new Label(formatSignedChange(stock.getLatestPriceChange()));
        change.getStyleClass().addAll("movers-change",
            positive ? "positive-change" : "negative-change");
        HBox row = new HBox(symbol, change);
        row.getStyleClass().add("movers-row");
        HBox.setHgrow(symbol, Priority.ALWAYS);
        container.getChildren().add(row);
      } else {
        Label placeholder = new Label("—");
        placeholder.getStyleClass().addAll("movers-row", "empty-state");
        container.getChildren().add(placeholder);
      }
    }
  }

  private void configureColumns(TableView<StockRow> table) {
    TableColumn<StockRow, String> symbolColumn = new TableColumn<>("Symbol");
    symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());

    TableColumn<StockRow, String> companyColumn = new TableColumn<>("Company");
    companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

    TableColumn<StockRow, BigDecimal> priceColumn = new TableColumn<>("Price");
    priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
    priceColumn.setCellFactory(column -> TableCells.currency());

    TableColumn<StockRow, BigDecimal> changeColumn = new TableColumn<>("Change");
    changeColumn.setCellValueFactory(cellData -> cellData.getValue().changeProperty());
    changeColumn.setCellFactory(column -> TableCells.signedChange());

    TableColumn<StockRow, Void> actionColumn = new TableColumn<>("Actions");
    actionColumn.setCellFactory(column -> actionsCell());
    actionColumn.setMinWidth(168);
    actionColumn.setMaxWidth(184);

    table.getColumns().add(symbolColumn);
    table.getColumns().add(companyColumn);
    table.getColumns().add(priceColumn);
    table.getColumns().add(changeColumn);
    table.getColumns().add(actionColumn);
  }

  private void applySearchFilter(String searchText) {
    String query = searchText == null ? "" : searchText.trim().toLowerCase(Locale.ROOT);
    filteredStockRows.setPredicate(row -> query.isEmpty()
        || row.getSymbol().toLowerCase(Locale.ROOT).contains(query)
        || row.getCompany().toLowerCase(Locale.ROOT).contains(query)
    );
  }

  private TableCell<StockRow, Void> actionsCell() {
    return new TableCell<>() {
      private final Button buyButton = new Button("Buy");
      private final Button detailsButton = new Button("Details");
      private final HBox buttons = new HBox(6, detailsButton, buyButton);

      {
        buyButton.getStyleClass().addAll("secondary-button", "table-action-button");
        buyButton.setOnAction(event -> {
          StockRow row = getTableView().getItems().get(getIndex());
          buyHandler.accept(row.getStock());
        });
        detailsButton.getStyleClass().addAll("discreet-button", "table-action-button");
        detailsButton.setOnAction(event -> {
          StockRow row = getTableView().getItems().get(getIndex());
          detailsHandler.accept(row.getStock());
        });
        buttons.setAlignment(Pos.CENTER);
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : buttons);
        setAlignment(Pos.CENTER);
      }
    };
  }

  private void updateBuyPreview() {
    if (selectedStock == null) {
      return;
    }

    BigDecimal quantity = parseQuantity(buyDialogPane.getQuantityField().getText());
    if (quantity == null) {
      buyDialogPane.showInvalidPreview();
      buyDialogPane.showError("Enter a positive quantity.");
      return;
    }

    Share previewShare = new Share(selectedStock, quantity, selectedStock.getSalesPrice());
    PurchaseCalculator calculator = new PurchaseCalculator(previewShare);

    buyDialogPane.setPreview(
        formatCurrency(calculator.calculateGross()),
        formatCurrency(calculator.calculateCommission()),
        formatCurrency(calculator.calculateTotal())
    );
    buyDialogPane.clearError();
  }

  private void confirmBuy() {
    if (selectedStock == null) {
      return;
    }

    BigDecimal quantity = parseQuantity(buyDialogPane.getQuantityField().getText());
    if (quantity == null) {
      buyDialogPane.showInvalidPreview();
      buyDialogPane.showError("Enter a positive quantity.");
      return;
    }

    confirmBuyHandler.accept(selectedStock, quantity);
  }

  private static BigDecimal parseQuantity(String quantityText) {
    if (quantityText == null) {
      return null;
    }
    String trimmed = quantityText.trim();
    if (!trimmed.matches("\\d+(\\.\\d+)?")) {
      return null;
    }

    BigDecimal quantity = new BigDecimal(trimmed);
    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
      return null;
    }
    return quantity;
  }

  private void hideBuyOverlay() {
    hideModalHandler.run();
    selectedStock = null;
  }

  private void hideReceiptOverlay() {
    hideModalHandler.run();
  }

  private static String formatCurrency(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
  }

  private static String formatSignedChange(BigDecimal change) {
    if (change.compareTo(BigDecimal.ZERO) > 0) {
      return "+" + formatCurrency(change);
    }
    return formatCurrency(change);
  }

  private static final class StockRow {

    private final Stock stock;
    private final StringProperty symbol;
    private final StringProperty company;
    private final ObjectProperty<BigDecimal> price;
    private final ObjectProperty<BigDecimal> change;

    private StockRow(Stock stock) {
      this.stock = stock;
      this.symbol = new SimpleStringProperty(stock.getSymbol());
      this.company = new SimpleStringProperty(stock.getCompany());
      this.price = new SimpleObjectProperty<>(stock.getSalesPrice());
      this.change = new SimpleObjectProperty<>(stock.getLatestPriceChange());
    }

    private Stock getStock() {
      return stock;
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

    private ObjectProperty<BigDecimal> priceProperty() {
      return price;
    }

    private ObjectProperty<BigDecimal> changeProperty() {
      return change;
    }
  }
}
