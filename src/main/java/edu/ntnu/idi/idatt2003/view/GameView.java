package edu.ntnu.idi.idatt2003.view;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Main game shell with shared navigation, stats, and swappable content.
 */
public class GameView {

  private static final String SELECTED_NAV_CLASS = "sidebar-button-selected";

  private final StackPane root;
  private final BorderPane shell;
  private final StackPane modalOverlay;
  private final Label toastLabel;
  private final Button marketButton;
  private final Button portfolioButton;
  private final Button transactionsButton;
  private final Button exitButton;
  private final Button saveButton;
  private final Button advanceWeekButton;
  private final Label playerNameValue;
  private final Label playerRankValue;
  private final Label balanceValue;
  private final Label netWorthValue;
  private final Label netWorthChangeLabel;
  private final Label weekValue;

  /**
   * Creates the main game shell.
   */
  public GameView() {
    shell = new BorderPane();
    shell.getStyleClass().add("game-page");

    marketButton = createSidebarButton("Market");
    portfolioButton = createSidebarButton("Portfolio");
    transactionsButton = createSidebarButton("Transactions");
    saveButton = createSidebarButton("Save game");
    exitButton = createSidebarButton("Exit game");

    playerNameValue = new Label("-");
    playerRankValue = new Label("-");
    balanceValue = new Label("-");
    netWorthValue = new Label("-");
    netWorthChangeLabel = new Label("");
    weekValue = new Label("-");
    advanceWeekButton = new Button("Advance week");
    advanceWeekButton.getStyleClass().add("primary-button");
    advanceWeekButton.setAccessibleText("Advance to the next week");

    shell.setLeft(buildSidebar());
    shell.setTop(buildTopbar());

    modalOverlay = buildModalLayer();
    toastLabel = buildToast();

    root = new StackPane(shell, modalOverlay, toastLabel);
    StackPane.setAlignment(toastLabel, Pos.BOTTOM_CENTER);
    StackPane.setMargin(toastLabel, new Insets(0, 0, 28, 0));
  }

  private VBox buildSidebar() {
    VBox sidebar = new VBox(10, marketButton, portfolioButton, transactionsButton, saveButton);
    sidebar.getStyleClass().add("sidebar");

    Region sidebarSpacer = new Region();
    VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);
    sidebar.getChildren().addAll(sidebarSpacer, exitButton);
    return sidebar;
  }

  private HBox buildTopbar() {
    HBox stats = new HBox(
        18,
        createPlayerStat("Player", playerNameValue, playerRankValue),
        createStat("Balance", balanceValue),
        createNetWorthStat("Net worth", netWorthValue, netWorthChangeLabel),
        createStat("Week", weekValue)
    );
    stats.setAlignment(Pos.CENTER);
    stats.getStyleClass().add("topbar-stats");

    HBox advanceColumn = new HBox(advanceWeekButton);
    advanceColumn.setAlignment(Pos.CENTER);
    advanceColumn.getStyleClass().add("topbar-advance-column");

    // Mirror the button column on the left so stats stay centered in the full topbar width.
    Region balanceColumn = new Region();
    balanceColumn.getStyleClass().add("topbar-balance-column");
    balanceColumn.minWidthProperty().bind(advanceColumn.widthProperty());
    balanceColumn.prefWidthProperty().bind(advanceColumn.widthProperty());
    balanceColumn.maxWidthProperty().bind(advanceColumn.widthProperty());

    Region statsLeading = new Region();
    Region statsTrailing = new Region();
    HBox.setHgrow(statsLeading, Priority.ALWAYS);
    HBox.setHgrow(statsTrailing, Priority.ALWAYS);

    HBox statsRow = new HBox(statsLeading, stats, statsTrailing);
    statsRow.setAlignment(Pos.CENTER);
    HBox.setHgrow(statsRow, Priority.ALWAYS);
    statsRow.setMaxWidth(Double.MAX_VALUE);

    HBox topbar = new HBox(balanceColumn, statsRow, advanceColumn);
    topbar.setAlignment(Pos.CENTER);
    topbar.setPadding(new Insets(18, 22, 18, 22));
    topbar.getStyleClass().add("topbar");
    return topbar;
  }

  private StackPane buildModalLayer() {
    StackPane overlay = new StackPane();
    overlay.getStyleClass().add("modal-overlay");
    overlay.setAlignment(Pos.CENTER);
    overlay.setPadding(new Insets(24));
    overlay.setVisible(false);
    overlay.setManaged(false);
    return overlay;
  }

  private Label buildToast() {
    Label toast = new Label();
    toast.getStyleClass().addAll("toast", "toast-success");
    toast.setVisible(false);
    toast.setManaged(false);
    toast.setMouseTransparent(true);
    return toast;
  }

  private static VBox createStat(String label, Label value) {
    Label statLabel = new Label(label);
    statLabel.getStyleClass().add("stat-label");
    value.getStyleClass().add("stat-value");
    statLabel.setLabelFor(value);

    VBox box = new VBox(4, statLabel, value);
    box.getStyleClass().add("stat");
    box.setAlignment(Pos.CENTER);
    return box;
  }

  private static VBox createNetWorthStat(String label, Label value, Label changeLabel) {
    Label statLabel = new Label(label);
    statLabel.getStyleClass().add("stat-label");
    value.getStyleClass().add("stat-value");
    changeLabel.getStyleClass().add("stat-change");
    statLabel.setLabelFor(value);

    VBox values = new VBox(2, value, changeLabel);
    values.setAlignment(Pos.CENTER);

    VBox box = new VBox(4, statLabel, values);
    box.getStyleClass().add("stat");
    box.setAlignment(Pos.CENTER);
    return box;
  }

  private static VBox createPlayerStat(String label, Label nameValue, Label rankValue) {
    Label statLabel = new Label(label);
    statLabel.getStyleClass().add("stat-label");
    nameValue.getStyleClass().add("stat-value");
    rankValue.getStyleClass().add("stat-rank");
    statLabel.setLabelFor(nameValue);

    VBox values = new VBox(2, nameValue, rankValue);
    values.setAlignment(Pos.CENTER);

    VBox box = new VBox(4, statLabel, values);
    box.getStyleClass().add("stat");
    box.setAlignment(Pos.CENTER);
    return box;
  }

  private static void updateStat(Label label, String accessibleName, String value) {
    label.setText(value);
    label.setAccessibleText(accessibleName + ": " + value);
  }

  private static Button createSidebarButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().addAll("secondary-button", "sidebar-button");
    button.setMaxWidth(Double.MAX_VALUE);
    return button;
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
   * Replaces the center content of the game shell.
   *
   * @param content the content to show
   */
  public void showContent(Node content) {
    if (content instanceof Region region) {
      region.setMaxWidth(Double.MAX_VALUE);
      region.setMaxHeight(Double.MAX_VALUE);
    }
    BorderPane.setMargin(content, new Insets(18, 22, 22, 22));
    shell.setCenter(content);
  }

  /**
   * Shows modal content over the full game shell.
   *
   * @param content the modal content to show
   */
  public void showModal(Node content) {
    modalOverlay.getChildren().setAll(content);
    modalOverlay.setManaged(true);
    modalOverlay.setVisible(true);
  }

  /**
   * Hides the current modal content.
   */
  public void hideModal() {
    modalOverlay.setVisible(false);
    modalOverlay.setManaged(false);
    modalOverlay.getChildren().clear();
  }

  /**
   * Shows a temporary success toast.
   *
   * @param message the message to show
   */
  public void showSuccessToast(String message) {
    showToast(message, "toast-success");
  }

  /**
   * Shows a temporary error toast.
   *
   * @param message the message to show
   */
  public void showErrorToast(String message) {
    showToast(message, "toast-error");
  }

  private void showToast(String message, String styleClass) {
    toastLabel.getStyleClass().removeAll("toast-success", "toast-error");
    toastLabel.getStyleClass().add(styleClass);
    toastLabel.setText(message);
    toastLabel.setManaged(true);
    toastLabel.setVisible(true);

    PauseTransition delay = new PauseTransition(Duration.seconds(2.8));
    delay.setOnFinished(event -> {
      toastLabel.setVisible(false);
      toastLabel.setManaged(false);
    });
    delay.play();
  }

  /**
   * Updates the shared game statistics shown in the topbar.
   *
   * @param playerName          the player name
   * @param playerRank          the player rank label
   * @param balance             the formatted balance
   * @param netWorth            the formatted net worth
   * @param week                the current week
   * @param netWorthChange      the formatted percentage change from starting capital (e.g. "+12.5%")
   * @param netWorthChangeCss   one of: "positive-change", "negative-change", "neutral-change"
   */
  public void updateStats(
      String playerName,
      String playerRank,
      String balance,
      String netWorth,
      int week,
      String netWorthChange,
      String netWorthChangeCss
  ) {
    updateStat(playerNameValue, "Player", playerName);
    updateStat(playerRankValue, "Rank", playerRank);
    updateStat(balanceValue, "Balance", balance);
    updateStat(netWorthValue, "Net worth", netWorth);
    updateStat(weekValue, "Week", String.valueOf(week));
    netWorthChangeLabel.getStyleClass()
        .removeAll("positive-change", "negative-change", "neutral-change");
    netWorthChangeLabel.getStyleClass().add(netWorthChangeCss);
    updateStat(netWorthChangeLabel, "Net worth change", netWorthChange);
  }

  /**
   * Enables or disables the advance week control.
   *
   * @param enabled whether advancing the week is allowed
   */
  public void setAdvanceWeekEnabled(boolean enabled) {
    advanceWeekButton.setDisable(!enabled);
  }

  /**
   * Marks one sidebar item as selected.
   *
   * @param selectedButton the selected button
   */
  public void selectSection(Button selectedButton) {
    marketButton.getStyleClass().remove(SELECTED_NAV_CLASS);
    portfolioButton.getStyleClass().remove(SELECTED_NAV_CLASS);
    transactionsButton.getStyleClass().remove(SELECTED_NAV_CLASS);
    if (!selectedButton.getStyleClass().contains(SELECTED_NAV_CLASS)) {
      selectedButton.getStyleClass().add(SELECTED_NAV_CLASS);
    }
  }

  public Button getMarketButton() {
    return marketButton;
  }

  public Button getPortfolioButton() {
    return portfolioButton;
  }

  public Button getTransactionsButton() {
    return transactionsButton;
  }

  public void setOnMarket(EventHandler<ActionEvent> handler) {
    marketButton.setOnAction(handler);
  }

  public void setOnPortfolio(EventHandler<ActionEvent> handler) {
    portfolioButton.setOnAction(handler);
  }

  public void setOnTransactions(EventHandler<ActionEvent> handler) {
    transactionsButton.setOnAction(handler);
  }

  public void setOnExit(EventHandler<ActionEvent> handler) {
    exitButton.setOnAction(handler);
  }

  public void setOnAdvanceWeek(EventHandler<ActionEvent> handler) {
    advanceWeekButton.setOnAction(handler);
  }

  public void setOnSave(EventHandler<ActionEvent> handler) {
    saveButton.setOnAction(handler);
  }
}
