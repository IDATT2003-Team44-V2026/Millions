package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.observer.GameObserver;
import edu.ntnu.idi.idatt2003.service.GameSession;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import edu.ntnu.idi.idatt2003.view.GameView;
import edu.ntnu.idi.idatt2003.view.MarketView;
import edu.ntnu.idi.idatt2003.view.PortfolioView;
import edu.ntnu.idi.idatt2003.view.TransactionsView;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Controller for the main game shell and its swappable sections.
 */
public class GameController implements GameObserver {

  private final GameView view;
  private final GameSession gameSession;
  private final Navigator navigator;
  private final MarketView marketView;
  private final PortfolioView portfolioView;
  private final TransactionsView transactionsView;

  /**
   * Creates a controller for the main game shell.
   *
   * @param view        the main game shell
   * @param gameSession the active game session
   * @param navigator   the navigator used to leave the game screen
   */
  public GameController(GameView view, GameSession gameSession, Navigator navigator) {
    if (view == null) {
      throw new IllegalArgumentException("View cannot be null");
    }
    if (gameSession == null) {
      throw new IllegalArgumentException("Game session cannot be null");
    }
    if (navigator == null) {
      throw new IllegalArgumentException("Navigator cannot be null");
    }

    this.view = view;
    this.gameSession = gameSession;
    this.navigator = navigator;
    this.marketView = new MarketView();
    this.portfolioView = new PortfolioView();
    this.transactionsView = new TransactionsView();

    initializeHandlers();
    gameSession.addObserver(this);
    view.setAdvanceWeekEnabled(gameSession.isActive());
    showMarket();
    refreshStats();
  }

  private static String format(BigDecimal amount) {
    return CurrencyFormatter.formatToNOK(amount.doubleValue());
  }

  private static BigDecimal netWorthChangePct(BigDecimal starting, BigDecimal current) {
    if (starting.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return current.subtract(starting)
        .divide(starting, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"))
        .setScale(1, RoundingMode.HALF_UP);
  }

  private static String netWorthChangeText(BigDecimal starting, BigDecimal current) {
    BigDecimal pct = netWorthChangePct(starting, current);
    if (pct.compareTo(BigDecimal.ZERO) > 0) {
      return "+" + pct.toPlainString() + "%";
    }
    return pct.toPlainString() + "%";
  }

  private static String netWorthChangeCss(BigDecimal starting, BigDecimal current) {
    int cmp = netWorthChangePct(starting, current).compareTo(BigDecimal.ZERO);
    if (cmp > 0) {
      return "positive-change";
    }
    if (cmp < 0) {
      return "negative-change";
    }
    return "neutral-change";
  }

  private static String toBuyMessage(RuntimeException exception) {
    if (exception.getMessage() != null && exception.getMessage().contains("Insufficient funds")) {
      return "You do not have enough money for this purchase.";
    }
    return "Could not complete purchase. " + exception.getMessage();
  }

  private static String toSellMessage(RuntimeException exception) {
    if (exception.getMessage() != null && exception.getMessage().contains("does not own")) {
      return "You no longer own this share.";
    }
    return "Could not complete sale. " + exception.getMessage();
  }

  @Override
  public void onGameStateChanged() {
    refreshStats();
  }

  private void initializeHandlers() {
    view.setOnMarket(event -> showMarket());
    view.setOnPortfolio(event -> showPortfolio());
    view.setOnTransactions(event -> showTransactions());
    view.setOnAdvanceWeek(event -> gameSession.advanceWeek());
    marketView.setOnBuy(this::showBuyDialog);
    marketView.setOnConfirmBuy(this::confirmBuy);
    marketView.setOnDetails(marketView::showDetailsDialog);
    marketView.setModalHandlers(view::showModal, view::hideModal);
    portfolioView.setOnSell(this::showSellDialog);
    portfolioView.setOnConfirmSell(this::confirmSell);
    portfolioView.setModalHandlers(view::showModal, view::hideModal);
    view.setOnExit(event -> {
      gameSession.endSession();
      view.setAdvanceWeekEnabled(false);
      gameSession.removeObserver(this);
      navigator.navigateTo(Route.START);
    });
  }

  private void showMarket() {
    view.showContent(marketView.getRoot());
    view.selectSection(view.getMarketButton());
  }

  private void showPortfolio() {
    view.showContent(portfolioView.getRoot());
    view.selectSection(view.getPortfolioButton());
  }

  private void showTransactions() {
    view.showContent(transactionsView.getRoot());
    view.selectSection(view.getTransactionsButton());
  }

  private void refreshStats() {
    view.updateStats(
        gameSession.getPlayer().getName(),
        gameSession.getPlayer().getStatus(gameSession.getExchange().getWeek()).getDisplayName(),
        format(gameSession.getPlayer().getMoney()),
        format(gameSession.getPlayer().getNetWorth()),
        gameSession.getExchange().getWeek(),
        netWorthChangeText(gameSession.getPlayer().getStartingMoney(),
            gameSession.getPlayer().getNetWorth()),
        netWorthChangeCss(gameSession.getPlayer().getStartingMoney(),
            gameSession.getPlayer().getNetWorth())
    );
    marketView.setStocks(gameSession.getExchange().getStocks());
    marketView.setMovers(
        gameSession.getExchange().getGainers(3),
        gameSession.getExchange().getLosers(3)
    );
    portfolioView.setShares(gameSession.getPlayer().getPortfolio().getShares());
    transactionsView.setTransactions(
        gameSession.getPlayer().getTransactionArchive().getAllTransactions(),
        gameSession.getExchange().getWeek()
    );
  }

  private void showBuyDialog(Stock stock) {
    marketView.showBuyDialog(stock);
  }

  private void confirmBuy(Stock stock, BigDecimal quantity) {
    try {
      gameSession.buy(stock.getSymbol(), quantity);
      marketView.showBuyReceipt(stock, quantity);
    } catch (IllegalArgumentException | IllegalStateException exception) {
      marketView.showBuyError(toBuyMessage(exception));
    }
  }

  private void showSellDialog(Share share) {
    portfolioView.showSellDialog(share);
  }

  private void confirmSell(Share share) {
    try {
      gameSession.sell(share);
      portfolioView.showSellReceipt(share);
    } catch (IllegalArgumentException | IllegalStateException exception) {
      portfolioView.showSellError(toSellMessage(exception));
    }
  }
}
