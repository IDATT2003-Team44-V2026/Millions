package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.transactions.Purchase;
import edu.ntnu.idi.idatt2003.transactions.Sale;
import edu.ntnu.idi.idatt2003.transactions.Transaction;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View for the transactions section of the game screen.
 */
public class TransactionsView {

    private static final String ALL_TYPES = "All";
    private static final String BUY_TYPE = "Buy";
    private static final String SELL_TYPE = "Sell";
    private static final String ALL_WEEKS = "All weeks";
    private static final String POSITIVE_CHANGE_CLASS = "positive-change";
    private static final String NEGATIVE_CHANGE_CLASS = "negative-change";

    private final VBox root;
    private final TextField searchField;
    private final ComboBox<String> typeFilter;
    private final ComboBox<String> weekFilter;
    private final TableView<TransactionRow> transactionTable;
    private final ObservableList<TransactionRow> transactionRows;
    private final FilteredList<TransactionRow> filteredTransactionRows;
    private final SortedList<TransactionRow> sortedTransactionRows;

    /**
     * Creates the transactions section view.
     */
    public TransactionsView() {
        Label titleLabel = new Label("Transactions");
        titleLabel.getStyleClass().add("section-title");

        Label subtitleLabel = new Label("Review your buy and sell history.");
        subtitleLabel.getStyleClass().add("subtitle-label");

        searchField = new TextField();
        searchField.setPromptText("Search by symbol or company");
        searchField.getStyleClass().add("form-input");
        searchField.setMaxWidth(Double.MAX_VALUE);

        typeFilter = new ComboBox<>(FXCollections.observableArrayList(ALL_TYPES, BUY_TYPE, SELL_TYPE));
        typeFilter.setValue(ALL_TYPES);
        typeFilter.getStyleClass().add("form-input");
        typeFilter.setMaxWidth(Double.MAX_VALUE);

        weekFilter = new ComboBox<>();
        weekFilter.getItems().add(ALL_WEEKS);
        weekFilter.setValue(ALL_WEEKS);
        weekFilter.getStyleClass().add("form-input");
        weekFilter.setMaxWidth(Double.MAX_VALUE);

        transactionRows = FXCollections.observableArrayList();
        filteredTransactionRows = new FilteredList<>(transactionRows, row -> true);
        sortedTransactionRows = new SortedList<>(filteredTransactionRows);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        typeFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        weekFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        transactionTable = new TableView<>();
        transactionTable.getStyleClass().add("data-table");
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        transactionTable.setFixedCellSize(44);
        transactionTable.setMaxHeight(Double.MAX_VALUE);
        sortedTransactionRows.comparatorProperty().bind(transactionTable.comparatorProperty());
        transactionTable.setItems(sortedTransactionRows);
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
        configureColumns();

        HBox filters = new HBox(12, searchField, typeFilter, weekFilter);
        filters.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        HBox.setHgrow(typeFilter, Priority.SOMETIMES);
        HBox.setHgrow(weekFilter, Priority.SOMETIMES);
        typeFilter.setPrefWidth(120);
        weekFilter.setPrefWidth(140);

        VBox transactionsCard = new VBox(14, titleLabel, subtitleLabel, filters, transactionTable);
        transactionsCard.getStyleClass().add("content-card");
        transactionsCard.setPadding(new Insets(18));
        transactionsCard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        root = transactionsCard;
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
     * Updates the transactions shown in the table.
     *
     * @param transactions the transactions to show
     * @param currentWeek the current exchange week used to populate week filter options
     */
    public void setTransactions(List<Transaction> transactions, int currentWeek) {
        transactionRows.setAll(transactions.stream()
            .map(TransactionRow::new)
            .toList());
        updateWeekFilterOptions(currentWeek);
        applyFilters();
    }

    private void updateWeekFilterOptions(int currentWeek) {
        String previousSelection = weekFilter.getValue();
        weekFilter.getItems().setAll(ALL_WEEKS);
        for (int week = 1; week <= currentWeek; week++) {
            weekFilter.getItems().add("Week " + week);
        }
        if (previousSelection != null && weekFilter.getItems().contains(previousSelection)) {
            weekFilter.setValue(previousSelection);
        } else {
            weekFilter.setValue(ALL_WEEKS);
        }
    }

    private void configureColumns() {
        TableColumn<TransactionRow, Integer> weekColumn = new TableColumn<>("Week");
        weekColumn.setCellValueFactory(cellData -> cellData.getValue().weekProperty().asObject());
        weekColumn.setCellFactory(column -> integerCell());

        TableColumn<TransactionRow, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

        TableColumn<TransactionRow, String> symbolColumn = new TableColumn<>("Symbol");
        symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());

        TableColumn<TransactionRow, String> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

        TableColumn<TransactionRow, BigDecimal> quantityColumn = new TableColumn<>("Qty");
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        quantityColumn.setCellFactory(column -> quantityCell());

        TableColumn<TransactionRow, BigDecimal> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
        totalColumn.setCellFactory(column -> totalCell());

        transactionTable.getColumns().add(weekColumn);
        transactionTable.getColumns().add(typeColumn);
        transactionTable.getColumns().add(symbolColumn);
        transactionTable.getColumns().add(companyColumn);
        transactionTable.getColumns().add(quantityColumn);
        transactionTable.getColumns().add(totalColumn);
    }

    private void applyFilters() {
        String query = searchField.getText() == null
            ? ""
            : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedType = typeFilter.getValue() == null ? ALL_TYPES : typeFilter.getValue();
        String selectedWeek = weekFilter.getValue() == null ? ALL_WEEKS : weekFilter.getValue();
        Integer weekNumber = parseWeekFilter(selectedWeek);

        filteredTransactionRows.setPredicate(row ->
            matchesSearch(row, query)
                && matchesType(row, selectedType)
                && matchesWeek(row, weekNumber)
        );
    }

    private static boolean matchesSearch(TransactionRow row, String query) {
        if (query.isEmpty()) {
            return true;
        }
        return row.getSymbol().toLowerCase(Locale.ROOT).contains(query)
            || row.getCompany().toLowerCase(Locale.ROOT).contains(query);
    }

    private static boolean matchesType(TransactionRow row, String selectedType) {
        if (ALL_TYPES.equals(selectedType)) {
            return true;
        }
        return selectedType.equals(row.getType());
    }

    private static boolean matchesWeek(TransactionRow row, Integer weekNumber) {
        if (weekNumber == null) {
            return true;
        }
        return row.getWeek() == weekNumber;
    }

    private static Integer parseWeekFilter(String selectedWeek) {
        if (ALL_WEEKS.equals(selectedWeek) || selectedWeek == null || !selectedWeek.startsWith("Week ")) {
            return null;
        }
        return Integer.parseInt(selectedWeek.substring("Week ".length()));
    }

    private static TableCell<TransactionRow, Integer> integerCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setAlignment(Pos.CENTER_RIGHT);
            }
        };
    }

    private static TableCell<TransactionRow, BigDecimal> quantityCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatQuantity(item));
                setAlignment(Pos.CENTER_RIGHT);
            }
        };
    }

    private TableCell<TransactionRow, BigDecimal> totalCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll(POSITIVE_CHANGE_CLASS, NEGATIVE_CHANGE_CLASS);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                TransactionRow row = getTableRow() == null ? null : getTableRow().getItem();
                setText(formatCurrency(item));
                setAlignment(Pos.CENTER_RIGHT);
                if (row != null && row.isSale()) {
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

    private static final class TransactionRow {
        private final IntegerProperty week;
        private final StringProperty type;
        private final StringProperty symbol;
        private final StringProperty company;
        private final ObjectProperty<BigDecimal> quantity;
        private final ObjectProperty<BigDecimal> total;
        private final boolean sale;

        private TransactionRow(Transaction transaction) {
            this.week = new SimpleIntegerProperty(transaction.getWeek());
            this.type = new SimpleStringProperty(transaction instanceof Purchase ? BUY_TYPE : SELL_TYPE);
            this.symbol = new SimpleStringProperty(transaction.getShare().getStock().getSymbol());
            this.company = new SimpleStringProperty(transaction.getShare().getStock().getCompany());
            this.quantity = new SimpleObjectProperty<>(transaction.getShare().getQuantity());
            this.total = new SimpleObjectProperty<>(transaction.getCalculator().calculateTotal());
            this.sale = transaction instanceof Sale;
        }

        private int getWeek() {
            return week.get();
        }

        private String getType() {
            return type.get();
        }

        private String getSymbol() {
            return symbol.get();
        }

        private String getCompany() {
            return company.get();
        }

        private boolean isSale() {
            return sale;
        }

        private IntegerProperty weekProperty() {
            return week;
        }

        private StringProperty typeProperty() {
            return type;
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

        private ObjectProperty<BigDecimal> totalProperty() {
            return total;
        }
    }
}
