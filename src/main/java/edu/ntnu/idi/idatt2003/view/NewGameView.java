package edu.ntnu.idi.idatt2003.view;

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
    private static final String FIELD_LABEL_CLASS = "field-label";
    private static final String FIELD_GROUP_CLASS = "field-group";

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

        Label nameLabel = new Label("Player name");
        nameLabel.getStyleClass().add(FIELD_LABEL_CLASS);

        nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.getStyleClass().add("form-input");
        nameField.setPrefWidth(360);
        nameField.setMaxWidth(360);

        VBox nameGroup = new VBox(6, nameLabel, nameField);
        nameGroup.getStyleClass().add(FIELD_GROUP_CLASS);

        Label capitalLabel = new Label("Starting capital");
        capitalLabel.getStyleClass().add(FIELD_LABEL_CLASS);

        capitalField = new TextField();
        capitalField.setPromptText("e.g. 10000");
        capitalField.getStyleClass().add("form-input");
        capitalField.setPrefWidth(360);
        capitalField.setMaxWidth(360);

        VBox capitalGroup = new VBox(6, capitalLabel, capitalField);
        capitalGroup.getStyleClass().add(FIELD_GROUP_CLASS);

        Label fileSectionLabel = new Label("Stock data");
        fileSectionLabel.getStyleClass().add(FIELD_LABEL_CLASS);

        fileLabel = new Label("No file selected");
        fileLabel.getStyleClass().add("file-label");
        fileLabel.setWrapText(true);
        fileLabel.setMaxWidth(360);

        chooseFileButton = new Button("Choose file");
        chooseFileButton.getStyleClass().add("secondary-button");
        chooseFileButton.setPrefWidth(360);
        chooseFileButton.setMaxWidth(360);
        chooseFileButton.setAccessibleText("Choose stock data file");

        VBox fileGroup = new VBox(6, fileSectionLabel, fileLabel, chooseFileButton);
        fileGroup.getStyleClass().add(FIELD_GROUP_CLASS);

        startButton = new Button("Start game");
        startButton.getStyleClass().add("primary-button");
        startButton.setPrefWidth(360);
        startButton.setMaxWidth(360);

        backButton = new Button("Back");
        backButton.getStyleClass().add("discreet-button");
        backButton.setPrefWidth(120);
        backButton.setMaxWidth(120);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setPrefWidth(360);
        errorLabel.setMaxWidth(360);

        card = new VBox(
            18,
            eyebrowLabel,
            titleLabel,
            subtitleLabel,
            nameGroup,
            capitalGroup,
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
