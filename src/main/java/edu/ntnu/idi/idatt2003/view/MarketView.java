package edu.ntnu.idi.idatt2003.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Placeholder foundation for the market section of the game screen.
 */
public class MarketView {

    private final VBox root;

    /**
     * Creates the market section view.
     */
    public MarketView() {
        Label titleLabel = new Label("Market");
        titleLabel.getStyleClass().add("section-title");

        Label subtitleLabel = new Label("Market table and buy actions will be added here.");
        subtitleLabel.getStyleClass().add("subtitle-label");

        root = new VBox(12, titleLabel, subtitleLabel);
        root.getStyleClass().add("content-card");
        root.setPadding(new Insets(18));
    }

    /**
     * Returns the root node.
     *
     * @return the root node
     */
    public Parent getRoot() {
        return root;
    }
}
