package edu.ntnu.idi.idatt2003.view;

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

/**
 * Main game shell with shared navigation, stats, and swappable content.
 */
public class GameView {

    private static final String SELECTED_NAV_CLASS = "sidebar-button-selected";

    private final StackPane root;
    private final BorderPane shell;
    private final StackPane modalOverlay;
    private final Button marketButton;
    private final Button portfolioButton;
    private final Button transactionsButton;
    private final Button exitButton;
    private final Button advanceWeekButton;
    private final Label playerNameValue;
    private final Label balanceValue;
    private final Label netWorthValue;
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
        exitButton = createSidebarButton("Exit game");

        VBox sidebar = new VBox(10, marketButton, portfolioButton, transactionsButton);
        sidebar.getStyleClass().add("sidebar");

        Region sidebarSpacer = new Region();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(sidebarSpacer, exitButton);
        shell.setLeft(sidebar);

        playerNameValue = new Label("-");
        balanceValue = new Label("-");
        netWorthValue = new Label("-");
        weekValue = new Label("-");

        HBox stats = new HBox(
            18,
            createStat("Player", playerNameValue),
            createStat("Balance", balanceValue),
            createStat("Net worth", netWorthValue),
            createStat("Week", weekValue)
        );
        stats.setAlignment(Pos.CENTER);

        advanceWeekButton = new Button("Advance week");
        advanceWeekButton.getStyleClass().add("primary-button");
        advanceWeekButton.setAccessibleText("Advance to the next week");

        StackPane topbar = new StackPane(stats, advanceWeekButton);
        StackPane.setAlignment(stats, Pos.CENTER);
        StackPane.setAlignment(advanceWeekButton, Pos.CENTER_RIGHT);
        topbar.setPadding(new Insets(18, 22, 18, 22));
        topbar.getStyleClass().add("topbar");
        shell.setTop(topbar);

        modalOverlay = new StackPane();
        modalOverlay.getStyleClass().add("modal-overlay");
        modalOverlay.setAlignment(Pos.CENTER);
        modalOverlay.setPadding(new Insets(24));
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);

        root = new StackPane(shell, modalOverlay);
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
     * Updates the shared game statistics shown in the topbar.
     *
     * @param playerName the player name
     * @param balance the formatted balance
     * @param netWorth the formatted net worth
     * @param week the current week
     */
    public void updateStats(String playerName, String balance, String netWorth, int week) {
        updateStat(playerNameValue, "Player", playerName);
        updateStat(balanceValue, "Balance", balance);
        updateStat(netWorthValue, "Net worth", netWorth);
        updateStat(weekValue, "Week", String.valueOf(week));
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
}
