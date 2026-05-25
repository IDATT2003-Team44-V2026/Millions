package edu.ntnu.idi.idatt2003.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Start menu view for choosing how to enter the application.
 */
public class StartView {

  private final StackPane root;
  private final Button newGameButton;
  private final Button continueGameButton;
  private final Button leaderboardButton;
  private final Label messageLabel;

  public StartView() {
    Label eyebrowLabel = new Label("Millions");
    eyebrowLabel.getStyleClass().add("eyebrow-label");

    Label titleLabel = new Label("Welcome to Millions");
    titleLabel.getStyleClass().add("title-label");

    Label subtitleLabel = new Label(
        "Choose how you want to play."
    );
    subtitleLabel.getStyleClass().add("subtitle-label");

    newGameButton = createMenuButton("New game", true);
    continueGameButton = createMenuButton("Continue game", false);
    leaderboardButton = createMenuButton("Leaderboard", false);

    messageLabel = new Label();
    messageLabel.getStyleClass().add("error-label");
    messageLabel.setWrapText(true);
    messageLabel.setAlignment(Pos.CENTER);
    messageLabel.setPrefWidth(360);
    messageLabel.setMaxWidth(360);

    VBox card = new VBox(
        18,
        eyebrowLabel,
        titleLabel,
        subtitleLabel,
        newGameButton,
        continueGameButton,
        leaderboardButton,
        messageLabel
    );
    card.setAlignment(Pos.CENTER_LEFT);
    card.setFillWidth(false);
    card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    card.getStyleClass().add("start-card");

    root = new StackPane(card);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("start-page");
  }

  private static Button createMenuButton(String text, boolean primary) {
    Button button = new Button(text);
    button.getStyleClass().add(primary ? "primary-button" : "secondary-button");
    button.setPrefWidth(360);
    button.setMaxWidth(360);

    return button;
  }

  public Parent getRoot() {
    return root;
  }

  public void showMessage(String message) {
    messageLabel.setText(message);
  }

  public void clearMessage() {
    messageLabel.setText("");
  }

  public void setOnNewGame(EventHandler<ActionEvent> handler) {
    newGameButton.setOnAction(handler);
  }

  public void setOnContinueGame(EventHandler<ActionEvent> handler) {
    continueGameButton.setOnAction(handler);
  }

  public void setOnLeaderboard(EventHandler<ActionEvent> handler) {
    leaderboardButton.setOnAction(handler);
  }
}