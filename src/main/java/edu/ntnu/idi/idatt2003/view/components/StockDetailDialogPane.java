package edu.ntnu.idi.idatt2003.view.components;

import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import java.math.BigDecimal;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Modal pane showing per-stock statistics: current price, all-time high/low, and price history.
 */
public final class StockDetailDialogPane {

  private final VBox root;
  private final Label titleLabel;
  private final Label subtitleLabel;
  private final Label currentPriceValue;
  private final Label highPriceValue;
  private final Label lowPriceValue;
  private final LineChart<Number, Number> chart;

  /**
   * Creates a stock detail dialog pane.
   *
   * @param onClose action invoked when the Close button is pressed
   */
  public StockDetailDialogPane(Runnable onClose) {
    titleLabel = new Label();
    titleLabel.getStyleClass().add("modal-title");

    subtitleLabel = new Label();
    subtitleLabel.getStyleClass().add("modal-subtitle");

    VBox heading = new VBox(4, titleLabel, subtitleLabel);
    heading.getStyleClass().add("modal-heading");

    Region divider = new Region();
    divider.getStyleClass().add("receipt-divider");

    currentPriceValue = new Label();
    highPriceValue = new Label();
    lowPriceValue = new Label();

    final HBox statsStrip = new HBox(8,
        buildStatTile("Current Price", currentPriceValue),
        buildStatTile("All-time High", highPriceValue),
        buildStatTile("All-time Low", lowPriceValue)
    );

    NumberAxis weekAxis = new NumberAxis();
    weekAxis.setLabel("Week");
    weekAxis.setTickUnit(1);
    weekAxis.setMinorTickVisible(false);
    weekAxis.setForceZeroInRange(false);

    NumberAxis priceAxis = new NumberAxis();
    priceAxis.setLabel("Price (NOK)");
    priceAxis.setMinorTickVisible(false);
    priceAxis.setForceZeroInRange(false);

    chart = new LineChart<>(weekAxis, priceAxis);
    chart.setLegendVisible(false);
    chart.setAnimated(false);
    chart.getStyleClass().add("stock-detail-chart");
    chart.setPrefHeight(210);

    Button closeButton = new Button("Close");
    closeButton.getStyleClass().add("primary-button");
    closeButton.setMinWidth(100);
    closeButton.setOnAction(e -> onClose.run());

    HBox actions = new HBox(closeButton);
    actions.getStyleClass().add("modal-actions");
    actions.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, heading, divider, statsStrip, chart, actions);
    root.getStyleClass().addAll("modal-card", "stock-detail-card");
    root.setMaxSize(560, Region.USE_PREF_SIZE);
  }

  private static VBox buildStatTile(String labelText, Label valueLabel) {
    Label label = new Label(labelText);
    label.getStyleClass().add("stat-label");
    valueLabel.getStyleClass().add("stat-value");
    valueLabel.getStyleClass().add("detail-stat-value");
    VBox tile = new VBox(2, label, valueLabel);
    tile.getStyleClass().add("stat");
    tile.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(tile, Priority.ALWAYS);
    return tile;
  }

  private static String format(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
  }

  /**
   * Returns the modal root node.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Populates the pane with data from the given stock.
   *
   * @param stock the stock to display
   */
  public void setStock(Stock stock) {
    List<BigDecimal> history = stock.getHistoricalPrices();
    titleLabel.setText(stock.getSymbol() + " — " + stock.getCompany());
    subtitleLabel.setText(history.size() + " price record(s)");

    currentPriceValue.setText(format(stock.getSalesPrice()));
    highPriceValue.setText(format(stock.getHighestPrice()));
    lowPriceValue.setText(format(stock.getLowestPrice()));

    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    for (int i = 0; i < history.size(); i++) {
      series.getData().add(new XYChart.Data<>(i + 1, history.get(i).doubleValue()));
    }
    chart.getData().setAll(List.of(series));
  }
}
