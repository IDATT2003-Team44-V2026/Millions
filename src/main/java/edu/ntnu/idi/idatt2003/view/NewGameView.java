package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.model.Difficulty;
import edu.ntnu.idi.idatt2003.view.components.FormLayouts;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * View for setting up a new game.
 */
public class NewGameView {

  private final StackPane root;
  private final VBox card;
  private final TextField nameField;
  private final TextField capitalField;
  private final Label fileLabel;
  private final Button chooseFileButton;
  private final Button easyButton;
  private final Button normalButton;
  private final Button hardButton;
  private final Button startButton;
  private final Button backButton;
  private final Label errorLabel;
  private Difficulty selectedDifficulty = Difficulty.NORMAL;

  public NewGameView() {
    Label eyebrowLabel = new Label("Millions");
    eyebrowLabel.getStyleClass().add("eyebrow-label");

    Label titleLabel = new Label("Start a new game");
    titleLabel.getStyleClass().add("title-label");

    Label subtitleLabel = new Label(
        "Set up your player, starting capital, and stock data file."
    );
    subtitleLabel.getStyleClass().add("subtitle-label");

    nameField = new TextField();
    nameField.setPromptText("Enter your name");
    nameField.getStyleClass().addAll("form-input", "form-width");

    capitalField = new TextField();
    capitalField.setPromptText("e.g. 10000");
    capitalField.getStyleClass().addAll("form-input", "form-width");

    fileLabel = new Label("No file selected");
    fileLabel.getStyleClass().addAll("file-label", "form-width");
    fileLabel.setWrapText(true);

    chooseFileButton = FormLayouts.menuButton("Choose file", false);
    chooseFileButton.setAccessibleText("Choose stock data file");

    easyButton = new Button("Easy");
    normalButton = new Button("Normal");
    hardButton = new Button("Hard");
    easyButton.getStyleClass().addAll("secondary-button");
    normalButton.getStyleClass().addAll("primary-button");
    hardButton.getStyleClass().addAll("secondary-button");
    easyButton.setMaxWidth(Double.MAX_VALUE);
    normalButton.setMaxWidth(Double.MAX_VALUE);
    hardButton.setMaxWidth(Double.MAX_VALUE);
    easyButton.setOnAction(e -> applyDifficulty(Difficulty.EASY));
    normalButton.setOnAction(e -> applyDifficulty(Difficulty.NORMAL));
    hardButton.setOnAction(e -> applyDifficulty(Difficulty.HARD));

    startButton = FormLayouts.menuButton("Start game", true);

    backButton = new Button("Back");
    backButton.getStyleClass().add("discreet-button");
    backButton.setPrefWidth(120);
    backButton.setMaxWidth(120);

    errorLabel = new Label();
    errorLabel.getStyleClass().addAll("error-label", "form-width");
    errorLabel.setWrapText(true);
    errorLabel.setAlignment(Pos.CENTER);

    VBox fileControls = new VBox(6, fileLabel, chooseFileButton);
    final VBox fileGroup = FormLayouts.fieldGroup("Stock data", fileControls);

    HBox difficultyRow = new HBox(8, easyButton, normalButton, hardButton);
    difficultyRow.getStyleClass().add("form-width");
    HBox.setHgrow(easyButton, Priority.ALWAYS);
    HBox.setHgrow(normalButton, Priority.ALWAYS);
    HBox.setHgrow(hardButton, Priority.ALWAYS);

    card = new VBox(
        18,
        eyebrowLabel,
        titleLabel,
        subtitleLabel,
        FormLayouts.fieldGroup("Player name", nameField),
        FormLayouts.fieldGroup("Starting capital", capitalField),
        fileGroup,
        FormLayouts.fieldGroup("Difficulty", difficultyRow),
        startButton,
        backButton,
        errorLabel
    );
    card.setAlignment(Pos.CENTER_LEFT);
    card.setFillWidth(false);
    card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    card.getStyleClass().add("start-card");

    root = new StackPane(card);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("start-page");
  }

  public Parent getRoot() {
    return root;
  }

  public String getPlayerName() {
    return nameField.getText();
  }

  public String getStartCapital() {
    return capitalField.getText();
  }

  public void setSelectedFile(String fileName) {
    fileLabel.setText(fileName);
  }

  public void showError(String message) {
    errorLabel.setText(message);
  }

  public void clearError() {
    errorLabel.setText("");
  }

  public void setChooseFileEnabled(boolean enabled) {
    chooseFileButton.setDisable(!enabled);
  }

  public Difficulty getDifficulty() {
    return selectedDifficulty;
  }

  private void applyDifficulty(Difficulty difficulty) {
    selectedDifficulty = difficulty;
    setDifficultyStyle(easyButton, difficulty == Difficulty.EASY);
    setDifficultyStyle(normalButton, difficulty == Difficulty.NORMAL);
    setDifficultyStyle(hardButton, difficulty == Difficulty.HARD);
  }

  private void setDifficultyStyle(Button button, boolean selected) {
    button.getStyleClass().removeAll("primary-button", "secondary-button");
    button.getStyleClass().add(selected ? "primary-button" : "secondary-button");
  }

  public void setOnChooseFile(EventHandler<ActionEvent> handler) {
    chooseFileButton.setOnAction(handler);
  }

  public void setOnStart(EventHandler<ActionEvent> handler) {
    startButton.setOnAction(handler);
  }

  public void setOnBack(EventHandler<ActionEvent> handler) {
    backButton.setOnAction(handler);
  }
}
