package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.calculators.PurchaseCalculator;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
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
 * View for the market section of the game screen.
 */
public class MarketView {

    private static final String POSITIVE_CHANGE_CLASS = "positive-change";
    private static final String NEGATIVE_CHANGE_CLASS = "negative-change";
    private static final String NEUTRAL_CHANGE_CLASS = "neutral-change";

    private final VBox root;
    private final TextField searchField;
    private final TableView<StockRow> stockTable;
    private final ObservableList<StockRow> stockRows;
    private final FilteredList<StockRow> filteredStockRows;
    private final SortedList<StockRow> sortedStockRows;
    private final VBox buyDialog;
    private final Label buyTitleLabel;
    private final Label buySubtitleLabel;
    private final Label grossCostValue;
    private final Label commissionValue;
    private final Label totalCostValue;
    private final Label buyErrorLabel;
    private final TextField quantityField;
    private Stock selectedStock;
    private Consumer<Stock> buyHandler = stock -> {};
    private Consumer<Parent> showModalHandler = modal -> {};
    private Runnable hideModalHandler = () -> {};

    /**
     * Creates the market section view.
     */
    public MarketView() {
        Label titleLabel = new Label("Market");
        titleLabel.getStyleClass().add("section-title");

        Label subtitleLabel = new Label("Search available stocks and prepare purchases.");
        subtitleLabel.getStyleClass().add("subtitle-label");

        searchField = new TextField();
        searchField.setPromptText("Search by symbol or company");
        searchField.getStyleClass().add("form-input");
        searchField.setMaxWidth(Double.MAX_VALUE);

        stockRows = FXCollections.observableArrayList();
        filteredStockRows = new FilteredList<>(stockRows, row -> true);
        sortedStockRows = new SortedList<>(filteredStockRows);
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
            applySearchFilter(newValue)
        );

        stockTable = new TableView<>();
        stockTable.getStyleClass().add("data-table");
        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        stockTable.setFixedCellSize(44);
        stockTable.setMaxHeight(Double.MAX_VALUE);
        sortedStockRows.comparatorProperty().bind(stockTable.comparatorProperty());
        stockTable.setItems(sortedStockRows);
        VBox.setVgrow(stockTable, Priority.ALWAYS);
        configureColumns();

        VBox marketCard = new VBox(14, titleLabel, subtitleLabel, searchField, stockTable);
        marketCard.getStyleClass().add("content-card");
        marketCard.setPadding(new Insets(18));
        marketCard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        buyTitleLabel = new Label();
        buySubtitleLabel = new Label();
        grossCostValue = new Label();
        commissionValue = new Label();
        totalCostValue = new Label();
        buyErrorLabel = new Label();
        quantityField = new TextField("1");
        buyDialog = createBuyDialog();

        root = marketCard;
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
     * Shows a placeholder buy modal for future buy flow.
     *
     * @param stock the selected stock
     */
    public void showBuyPlaceholder(Stock stock) {
        selectedStock = stock;
        quantityField.setText("1");
        buyTitleLabel.setText("Buy " + stock.getSymbol());
        buySubtitleLabel.setText(stock.getCompany()
            + " · Current price: " + formatCurrency(stock.getSalesPrice()));
        updateBuyPreview();
        showModalHandler.accept(buyDialog);
        quantityField.requestFocus();
    }

    private void configureColumns() {
        TableColumn<StockRow, String> symbolColumn = new TableColumn<>("Symbol");
        symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());

        TableColumn<StockRow, String> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

        TableColumn<StockRow, BigDecimal> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        priceColumn.setCellFactory(column -> currencyCell());

        TableColumn<StockRow, BigDecimal> changeColumn = new TableColumn<>("Change");
        changeColumn.setCellValueFactory(cellData -> cellData.getValue().changeProperty());
        changeColumn.setCellFactory(column -> changeCell());

        TableColumn<StockRow, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(column -> buyButtonCell());
        actionColumn.setMinWidth(96);
        actionColumn.setMaxWidth(110);

        stockTable.getColumns().add(symbolColumn);
        stockTable.getColumns().add(companyColumn);
        stockTable.getColumns().add(priceColumn);
        stockTable.getColumns().add(changeColumn);
        stockTable.getColumns().add(actionColumn);
    }

    private void applySearchFilter(String searchText) {
        String query = searchText == null ? "" : searchText.trim().toLowerCase(Locale.ROOT);
        filteredStockRows.setPredicate(row -> query.isEmpty()
            || row.getSymbol().toLowerCase(Locale.ROOT).contains(query)
            || row.getCompany().toLowerCase(Locale.ROOT).contains(query)
        );
    }

    private static TableCell<StockRow, BigDecimal> currencyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatCurrency(item));
                setAlignment(Pos.CENTER_RIGHT);
            }
        };
    }

    private static TableCell<StockRow, BigDecimal> changeCell() {
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

                setText(formatChange(item));
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

    private VBox createBuyDialog() {
        buyTitleLabel.getStyleClass().add("modal-title");
        buySubtitleLabel.getStyleClass().add("modal-subtitle");
        buySubtitleLabel.setWrapText(true);

        VBox heading = new VBox(4, buyTitleLabel, buySubtitleLabel);
        heading.getStyleClass().add("modal-heading");

        Label quantityLabel = new Label("Quantity");
        quantityLabel.getStyleClass().add("field-label");
        quantityLabel.setLabelFor(quantityField);

        quantityField.getStyleClass().add("form-input");
        quantityField.setMaxWidth(Double.MAX_VALUE);
        quantityField.textProperty().addListener((observable, oldValue, newValue) ->
            updateBuyPreview()
        );

        VBox quantityGroup = new VBox(6, quantityLabel, quantityField);
        quantityGroup.getStyleClass().add("field-group");

        HBox grossCostRow = createSummaryRow("Gross cost", grossCostValue);
        HBox commissionRow = createSummaryRow("Commission (0.5 %)", commissionValue);
        HBox totalCostRow = createSummaryRow("Total cost", totalCostValue);
        totalCostRow.getStyleClass().add("summary-row-total");

        VBox summaryBox = new VBox(6, grossCostRow, commissionRow, totalCostRow);
        summaryBox.getStyleClass().add("summary-card");

        buyErrorLabel.getStyleClass().add("error-label");
        buyErrorLabel.setWrapText(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setOnAction(event -> hideBuyOverlay());

        Button confirmButton = new Button("Confirm purchase");
        confirmButton.getStyleClass().add("primary-button");
        confirmButton.setOnAction(event -> hideBuyOverlay());
        cancelButton.setMinWidth(150);
        confirmButton.setMinWidth(170);

        HBox actions = new HBox(12, cancelButton, confirmButton);
        actions.getStyleClass().add("modal-actions");
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox dialog = new VBox(
            16,
            heading,
            quantityGroup,
            summaryBox,
            buyErrorLabel,
            actions
        );
        dialog.getStyleClass().add("modal-card");
        dialog.setMaxSize(430, Region.USE_PREF_SIZE);
        return dialog;
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

    private void updateBuyPreview() {
        if (selectedStock == null) {
            return;
        }

        String quantityText = quantityField.getText().trim();
        if (!quantityText.matches("\\d+(\\.\\d+)?")) {
            showInvalidQuantity();
            return;
        }

        BigDecimal quantity = new BigDecimal(quantityText);
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            showInvalidQuantity();
            return;
        }

        Share previewShare = new Share(selectedStock, quantity, selectedStock.getSalesPrice());
        PurchaseCalculator calculator = new PurchaseCalculator(previewShare);

        grossCostValue.setText(formatCurrency(calculator.calculateGross()));
        commissionValue.setText(formatCurrency(calculator.calculateCommission()));
        totalCostValue.setText(formatCurrency(calculator.calculateTotal()));
        buyErrorLabel.setText("");
    }

    private void showInvalidQuantity() {
        grossCostValue.setText("-");
        commissionValue.setText("-");
        totalCostValue.setText("-");
        buyErrorLabel.setText("Enter a positive quantity.");
    }

    private void hideBuyOverlay() {
        hideModalHandler.run();
        selectedStock = null;
    }

    private static String formatCurrency(BigDecimal amount) {
        return CurrencyFormatter.formatToNOK(amount.doubleValue());
    }

    private static String formatChange(BigDecimal change) {
        if (change.compareTo(BigDecimal.ZERO) > 0) {
            return "+" + formatCurrency(change);
        }
        if (change.compareTo(BigDecimal.ZERO) < 0) {
            return formatCurrency(change);
        }
        return formatCurrency(BigDecimal.ZERO);
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
