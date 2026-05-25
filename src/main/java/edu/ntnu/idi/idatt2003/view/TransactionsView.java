package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.transactions.Purchase;
import edu.ntnu.idi.idatt2003.transactions.Sale;
import edu.ntnu.idi.idatt2003.transactions.Transaction;
import edu.ntnu.idi.idatt2003.view.components.SectionCard;
import edu.ntnu.idi.idatt2003.view.components.TableCells;
import edu.ntnu.idi.idatt2003.view.components.TablePlaceholders;
import edu.ntnu.idi.idatt2003.view.components.TransactionDetailDialogPane;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

  private final VBox root;
  private final TextField searchField;
  private final ComboBox<String> typeFilter;
  private final ComboBox<String> weekFilter;
  private final TableView<TransactionRow> transactionTable;
  private final ObservableList<TransactionRow> transactionRows;
  private final FilteredList<TransactionRow> filteredTransactionRows;
  private final SortedList<TransactionRow> sortedTransactionRows;
  private final TransactionDetailDialogPane detailPane;
  private Set<Integer> lastDistinctWeeks = Set.of();
  private Consumer<Transaction> detailsHandler = tx -> {};
  private Consumer<Parent> showModalHandler = modal -> {};
  private Runnable hideModalHandler = () -> {};

  /**
   * Creates the transactions section view.
   */
  public TransactionsView() {
    transactionRows = FXCollections.observableArrayList();
    filteredTransactionRows = new FilteredList<>(transactionRows, row -> true);
    sortedTransactionRows = new SortedList<>(filteredTransactionRows);

    searchField = buildSearchField();
    typeFilter = buildTypeFilter();
    weekFilter = buildWeekFilter();
    transactionTable = buildTable();
    detailPane = new TransactionDetailDialogPane(() -> hideModalHandler.run());

    HBox filters = new HBox(12, searchField, typeFilter, weekFilter);
    filters.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(searchField, Priority.ALWAYS);
    HBox.setHgrow(typeFilter, Priority.SOMETIMES);
    HBox.setHgrow(weekFilter, Priority.SOMETIMES);
    typeFilter.setPrefWidth(120);
    weekFilter.setPrefWidth(140);

    VBox content = new VBox(14, filters, transactionTable);
    content.setFillWidth(true);
    content.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(transactionTable, Priority.ALWAYS);

    root = SectionCard.create(
        "Transactions",
        "Review your buy and sell history.",
        content
    );
  }

  private TextField buildSearchField() {
    TextField field = new TextField();
    field.setPromptText("Search by symbol or company");
    field.getStyleClass().add("form-input");
    field.setMaxWidth(Double.MAX_VALUE);
    field.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    return field;
  }

  private ComboBox<String> buildTypeFilter() {
    ComboBox<String> filter = new ComboBox<>(
        FXCollections.observableArrayList(ALL_TYPES, BUY_TYPE, SELL_TYPE)
    );
    filter.setValue(ALL_TYPES);
    filter.getStyleClass().add("form-input");
    filter.setMaxWidth(Double.MAX_VALUE);
    filter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    return filter;
  }

  private ComboBox<String> buildWeekFilter() {
    ComboBox<String> filter = new ComboBox<>();
    filter.getItems().add(ALL_WEEKS);
    filter.setValue(ALL_WEEKS);
    filter.getStyleClass().add("form-input");
    filter.setMaxWidth(Double.MAX_VALUE);
    filter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    return filter;
  }

  private TableView<TransactionRow> buildTable() {
    TableView<TransactionRow> table = new TableView<>();
    table.getStyleClass().add("data-table");
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setFixedCellSize(44);
    table.setMaxHeight(Double.MAX_VALUE);
    table.setPlaceholder(TablePlaceholders.create("No transactions yet."));
    sortedTransactionRows.comparatorProperty().bind(table.comparatorProperty());
    table.setItems(sortedTransactionRows);
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
   * Sets the handler invoked when the user clicks Details on a transaction row.
   *
   * @param handler the handler receiving the selected transaction
   */
  public void setOnDetails(Consumer<Transaction> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    detailsHandler = handler;
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
   * Shows the transaction detail modal for a given transaction.
   *
   * @param transaction the transaction to display
   */
  public void showDetailsDialog(Transaction transaction) {
    detailPane.setTransaction(transaction);
    showModalHandler.accept(detailPane.getRoot());
  }

  /**
   * Updates the transactions shown in the table.
   *
   * @param transactions the transactions to show
   * @param currentWeek  the current exchange week used to populate week filter options
   */
  public void setTransactions(List<Transaction> transactions, int currentWeek) {
    transactionRows.setAll(transactions.stream()
        .map(TransactionRow::new)
        .toList());
    updateWeekFilterOptions(transactions, currentWeek);
    applyFilters();
  }

  private void updateWeekFilterOptions(List<Transaction> transactions, int currentWeek) {
    Set<Integer> distinctWeeks = new TreeSet<>();
    for (Transaction transaction : transactions) {
      distinctWeeks.add(transaction.getWeek());
    }
    if (currentWeek > 0) {
      distinctWeeks.add(currentWeek);
    }

    if (distinctWeeks.equals(lastDistinctWeeks)) {
      return;
    }
    lastDistinctWeeks = Set.copyOf(distinctWeeks);

    String previousSelection = weekFilter.getValue();
    List<String> weekOptions = new ArrayList<>();
    weekOptions.add(ALL_WEEKS);
    for (Integer week : distinctWeeks) {
      weekOptions.add("Week " + week);
    }
    weekFilter.getItems().setAll(weekOptions);

    if (previousSelection != null && weekFilter.getItems().contains(previousSelection)) {
      weekFilter.setValue(previousSelection);
    } else {
      weekFilter.setValue(ALL_WEEKS);
    }
  }

  private void configureColumns(TableView<TransactionRow> table) {
    TableColumn<TransactionRow, Integer> weekColumn = new TableColumn<>("Week");
    weekColumn.setCellValueFactory(cellData -> cellData.getValue().weekProperty().asObject());
    weekColumn.setCellFactory(column -> TableCells.integer());

    TableColumn<TransactionRow, String> typeColumn = new TableColumn<>("Type");
    typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

    TableColumn<TransactionRow, String> symbolColumn = new TableColumn<>("Symbol");
    symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());

    TableColumn<TransactionRow, String> companyColumn = new TableColumn<>("Company");
    companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

    TableColumn<TransactionRow, BigDecimal> quantityColumn = new TableColumn<>("Qty");
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    quantityColumn.setCellFactory(column -> TableCells.quantity());

    TableColumn<TransactionRow, BigDecimal> totalColumn = new TableColumn<>("Total");
    totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
    totalColumn.setCellFactory(column -> TableCells.transactionTotal(TransactionRow::isSale));

    TableColumn<TransactionRow, Void> actionColumn = new TableColumn<>("");
    actionColumn.setCellFactory(column -> detailsCell());
    actionColumn.setMinWidth(88);
    actionColumn.setMaxWidth(96);
    actionColumn.setSortable(false);

    table.getColumns().add(weekColumn);
    table.getColumns().add(typeColumn);
    table.getColumns().add(symbolColumn);
    table.getColumns().add(companyColumn);
    table.getColumns().add(quantityColumn);
    table.getColumns().add(totalColumn);
    table.getColumns().add(actionColumn);
  }

  private TableCell<TransactionRow, Void> detailsCell() {
    return new TableCell<>() {
      private final Button btn = new Button("Details");

      {
        btn.getStyleClass().addAll("discreet-button", "table-action-button");
        btn.setOnAction(e -> {
          TransactionRow row = getTableView().getItems().get(getIndex());
          detailsHandler.accept(row.getTransaction());
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : btn);
        setAlignment(Pos.CENTER);
      }
    };
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

  private static final class TransactionRow {

    private final Transaction transaction;
    private final IntegerProperty week;
    private final StringProperty type;
    private final StringProperty symbol;
    private final StringProperty company;
    private final ObjectProperty<BigDecimal> quantity;
    private final ObjectProperty<BigDecimal> total;
    private final boolean sale;

    private TransactionRow(Transaction transaction) {
      this.transaction = transaction;
      this.week = new SimpleIntegerProperty(transaction.getWeek());
      this.type = new SimpleStringProperty(transaction instanceof Purchase ? BUY_TYPE : SELL_TYPE);
      this.symbol = new SimpleStringProperty(transaction.getShare().stock().getSymbol());
      this.company = new SimpleStringProperty(transaction.getShare().stock().getCompany());
      this.quantity = new SimpleObjectProperty<>(transaction.getShare().quantity());
      this.total = new SimpleObjectProperty<>(transaction.getCalculator().calculateTotal());
      this.sale = transaction instanceof Sale;
    }

    private Transaction getTransaction() {
      return transaction;
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
