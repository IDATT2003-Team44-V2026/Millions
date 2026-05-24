package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private static final String POSITIVE_CHANGE_CLASS = "positive-change";
    private static final String NEGATIVE_CHANGE_CLASS = "negative-change";
    private static final String NEUTRAL_CHANGE_CLASS = "neutral-change";

    private final VBox root;
    private final TextField searchField;
    private final TableView<StockRow> stockTable;
    private final ObservableList<StockRow> stockRows;
    private final FilteredList<StockRow> filteredStockRows;
    private Consumer<Stock> buyHandler = stock -> {};

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
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
            applySearchFilter(newValue)
        );

        stockTable = new TableView<>();
        stockTable.getStyleClass().add("data-table");
        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        stockTable.setFixedCellSize(44);
        stockTable.setMaxHeight(Double.MAX_VALUE);
        stockTable.setItems(filteredStockRows);
        VBox.setVgrow(stockTable, Priority.ALWAYS);
        configureColumns();

        root = new VBox(14, titleLabel, subtitleLabel, searchField, stockTable);
        root.getStyleClass().add("content-card");
        root.setPadding(new Insets(18));
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
     * Shows a placeholder popup for future buy flow.
     *
     * @param stock the selected stock
     */
    public void showBuyPlaceholder(Stock stock) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Buy stock");
        alert.setHeaderText("Buy flow is not available yet");
        alert.setContentText("Buying " + stock.getSymbol() + " will be added later.");
        alert.showAndWait();
    }

    private void configureColumns() {
        TableColumn<StockRow, String> symbolColumn = new TableColumn<>("Symbol");
        symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());

        TableColumn<StockRow, String> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

        TableColumn<StockRow, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        priceColumn.setCellFactory(column -> rightAlignedCell());

        TableColumn<StockRow, String> changeColumn = new TableColumn<>("Change");
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

    private static TableCell<StockRow, String> rightAlignedCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setAlignment(Pos.CENTER_RIGHT);
            }
        };
    }

    private static TableCell<StockRow, String> changeCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
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

                setText(item);
                setAlignment(Pos.CENTER_RIGHT);
                if (item.startsWith("+")) {
                    getStyleClass().add(POSITIVE_CHANGE_CLASS);
                } else if (item.startsWith("-")) {
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

    private static final class StockRow {
        private final Stock stock;
        private final StringProperty symbol;
        private final StringProperty company;
        private final StringProperty price;
        private final StringProperty change;

        private StockRow(Stock stock) {
            this.stock = stock;
            this.symbol = new SimpleStringProperty(stock.getSymbol());
            this.company = new SimpleStringProperty(stock.getCompany());
            this.price = new SimpleStringProperty(formatCurrency(stock.getSalesPrice()));
            this.change = new SimpleStringProperty(formatChange(stock.getLatestPriceChange()));
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

        private StringProperty priceProperty() {
            return price;
        }

        private StringProperty changeProperty() {
            return change;
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
    }
}
