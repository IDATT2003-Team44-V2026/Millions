package edu.ntnu.idi.idatt2003.view;

import edu.ntnu.idi.idatt2003.io.GameSave;
import edu.ntnu.idi.idatt2003.view.components.FormLayouts;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * View for selecting and loading a saved game.
 */
public class LoadGameView {

  private final StackPane root;
  private final ListView<GameSave> saveList;
  private final Button loadButton;
  private final Button backButton;
  private final Label errorLabel;
  private Consumer<GameSave> deleteHandler = save -> {
  };
  private boolean controlsDisabled = false;

  /**
   * Creates the load game view.
   */
  public LoadGameView() {
    Label eyebrowLabel = new Label("Millions");
    eyebrowLabel.getStyleClass().add("eyebrow-label");

    Label titleLabel = new Label("Continue game");
    titleLabel.getStyleClass().add("title-label");

    Label subtitleLabel = new Label("Select a saved game to load.");
    subtitleLabel.getStyleClass().add("subtitle-label");

    saveList = new ListView<>();
    saveList.getStyleClass().addAll("save-list", "form-width");
    saveList.setPrefHeight(220);
    saveList.setCellFactory(lv -> new SaveCell());

    loadButton = FormLayouts.menuButton("Load", true);
    loadButton.setDisable(true);

    backButton = new Button("Back");
    backButton.getStyleClass().add("discreet-button");
    backButton.setPrefWidth(120);
    backButton.setMaxWidth(120);

    errorLabel = new Label();
    errorLabel.getStyleClass().addAll("error-label", "form-width");
    errorLabel.setWrapText(true);
    errorLabel.setAlignment(Pos.CENTER);

    saveList.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> updateLoadButton()
    );

    VBox card = new VBox(
        18,
        eyebrowLabel,
        titleLabel,
        subtitleLabel,
        saveList,
        loadButton,
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

  /**
   * Returns the root node.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Replaces the list of displayed saves.
   *
   * @param saves the saves to show
   */
  public void setSaves(List<GameSave> saves) {
    saveList.getItems().setAll(saves);
    if (!saves.isEmpty()) {
      saveList.getSelectionModel().selectFirst();
    }
  }

  /**
   * Returns the currently selected save, or {@code null} if nothing is selected.
   *
   * @return the selected save
   */
  public GameSave getSelectedSave() {
    return saveList.getSelectionModel().getSelectedItem();
  }

  /**
   * Shows an error or status message below the buttons.
   *
   * @param message the message to display
   */
  public void showMessage(String message) {
    errorLabel.setText(message);
  }

  /**
   * Clears any displayed message.
   */
  public void clearMessage() {
    errorLabel.setText("");
  }

  /**
   * Sets the handler invoked when the Load button is pressed.
   *
   * @param handler the action handler
   */
  public void setOnLoad(EventHandler<ActionEvent> handler) {
    loadButton.setOnAction(handler);
  }

  /**
   * Sets the handler invoked when the Back button is pressed.
   *
   * @param handler the action handler
   */
  public void setOnBack(EventHandler<ActionEvent> handler) {
    backButton.setOnAction(handler);
  }

  /**
   * Disables or re-enables all interactive controls. Used while a background task is running.
   *
   * @param disabled {@code true} to disable controls, {@code false} to restore them
   */
  public void setControlsDisabled(boolean disabled) {
    this.controlsDisabled = disabled;
    backButton.setDisable(disabled);
    updateLoadButton();
  }

  private void updateLoadButton() {
    loadButton.setDisable(
        controlsDisabled || saveList.getSelectionModel().getSelectedItem() == null);
  }

  /**
   * Sets the handler invoked when the user clicks Delete on a save row.
   *
   * @param handler receives the save to delete; must not be {@code null}
   */
  public void setOnDelete(Consumer<GameSave> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    this.deleteHandler = handler;
  }

  private final class SaveCell extends ListCell<GameSave> {

    @Override
    protected void updateItem(GameSave save, boolean empty) {
      super.updateItem(save, empty);
      if (empty || save == null) {
        setText(null);
        setGraphic(null);
      } else {
        Label nameLabel = new Label(save.getPlayerName());
        nameLabel.getStyleClass().add("save-cell-name");

        Label metaLabel = new Label("Week " + save.getSavedWeek() + "  ·  " + save.getSavedAt());
        metaLabel.getStyleClass().add("save-cell-meta");

        final VBox info = new VBox(2, nameLabel, metaLabel);

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().addAll("discreet-button", "save-cell-delete");
        deleteBtn.setOnAction(e -> deleteHandler.accept(save));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(8, info, spacer, deleteBtn);
        row.setAlignment(Pos.CENTER_LEFT);

        setGraphic(row);
        setText(null);
      }
    }
  }
}
