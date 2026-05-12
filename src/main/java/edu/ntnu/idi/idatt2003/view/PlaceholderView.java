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
 * Temporary destination view used to verify route-based navigation.
 */
public class PlaceholderView {

    private final StackPane root;
    private final Button backButton;

    public PlaceholderView() {
        Label titleLabel = new Label("Game screen placeholder");
        titleLabel.getStyleClass().add("title-label");

        Label subtitleLabel = new Label("The main game view will be added in a later branch.");
        subtitleLabel.getStyleClass().add("subtitle-label");

        backButton = new Button("Back to start");
        backButton.getStyleClass().add("secondary-button");
        backButton.setPrefWidth(360);
        backButton.setMaxWidth(360);

        VBox card = new VBox(18, titleLabel, subtitleLabel, backButton);
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

    public void setOnBack(EventHandler<ActionEvent> handler) {
        backButton.setOnAction(handler);
    }
}
