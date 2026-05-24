package edu.ntnu.idi.idatt2003.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Placeholder foundation for the portfolio section of the game screen.
 */
public class PortfolioView {

    private final VBox root;

    /**
     * Creates the portfolio section view.
     */
    public PortfolioView() {
        Label titleLabel = new Label("Portfolio");
        titleLabel.getStyleClass().add("section-title");

        Label subtitleLabel = new Label("Owned shares and sell actions will be added here.");
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
