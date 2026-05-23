package edu.ntnu.idi.idatt2003.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Placeholder foundation for the transactions section of the game screen.
 */
public class TransactionsView {

    private final VBox root;

    /**
     * Creates the transactions section view.
     */
    public TransactionsView() {
        Label titleLabel = new Label("Transactions");
        titleLabel.getStyleClass().add("section-title");

        Label subtitleLabel = new Label("Transaction history will be added here.");
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
