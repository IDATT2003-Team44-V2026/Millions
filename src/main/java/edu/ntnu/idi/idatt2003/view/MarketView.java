package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.calculators.PurchaseCalculator;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import edu.ntnu.idi.idatt2003.view.components.PurchaseDialogPane;
import edu.ntnu.idi.idatt2003.view.components.SectionCard;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
  private Stock selectedStock;
  private Consumer<Stock> buyHandler = stock -> {};
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

    VBox content = new VBox(14, searchField, stockTable);
    content.setFillWidth(true);
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

    TableColumn<StockRow, Void> actionColumn = new TableColumn<>("Action");
    actionColumn.setCellFactory(column -> buyButtonCell());
    actionColumn.setMinWidth(96);
    actionColumn.setMaxWidth(110);

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

  private TableCell<StockRow, Void> buyButtonCell() {
    return new TableCell<>() {
      private final Button buyButton = new Button("Buy");

      {
        buyButton.getStyleClass().addAll("secondary-button", "table-action-button");
        buyButton.setOnAction(event -> {
          StockRow row = getTableView().getItems().get(getIndex());
          buyHandler.accept(row.getStock());
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : buyButton);
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

  private static String formatCurrency(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
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
