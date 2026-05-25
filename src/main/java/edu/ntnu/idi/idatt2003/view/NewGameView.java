package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.view.components.FormLayouts;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
  private final Button startButton;
  private final Button backButton;
  private final Label errorLabel;

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
    VBox fileGroup = FormLayouts.fieldGroup("Stock data", fileControls);

    card = new VBox(
        18,
        eyebrowLabel,
        titleLabel,
        subtitleLabel,
        FormLayouts.fieldGroup("Player name", nameField),
        FormLayouts.fieldGroup("Starting capital", capitalField),
        fileGroup,
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
