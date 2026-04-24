package edu.ntnu.idi.idatt2003.ui.sidebar;

import edu.ntnu.idi.idatt2003.ui.ViewNavigator;

/**
 * Controller for the sidebar navigation component.
 *
 * <p>Handles navigation actions triggered by sidebar buttons. Does not handle animation or layout
 * positioning.
 */
public class SidebarController {

  /** Called when "Stocks" is clicked. */
  public void onStocks() {
    System.out.println("Stocks clicked");
    ViewNavigator.get().show("/ui/stock/StockView.fxml");
  }

  /** Called when "Portfolio" is clicked. */
  public void onPortfolio() {
    System.out.println("Portfolio clicked");
    ViewNavigator.get().show("/ui/portfolio/PortfolioView.fxml");
  }

  /** Called when "Settings" is clicked. */
  public void onSettings() {
    System.out.println("Settings clicked");
  }
}
