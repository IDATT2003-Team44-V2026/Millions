package edu.ntnu.idi.idatt2003.view.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Builds a standard section card with title, subtitle, and custom content.
 */
public final class SectionCard {

  private SectionCard() {
  }

  /**
   * Creates a content card section.
   *
   * @param title    the section title
   * @param subtitle the section subtitle
   * @param content  the main section content (search, filters, table, etc.)
   * @return the root node for the section
   */
  public static VBox create(String title, String subtitle, Node content) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("section-title");

    Label subtitleLabel = new Label(subtitle);
    subtitleLabel.getStyleClass().add("subtitle-label");

    VBox card = new VBox(14, titleLabel, subtitleLabel, content);
    card.getStyleClass().add("content-card");
    card.setPadding(new Insets(18));
    card.setFillWidth(true);
    card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return card;
  }
}
