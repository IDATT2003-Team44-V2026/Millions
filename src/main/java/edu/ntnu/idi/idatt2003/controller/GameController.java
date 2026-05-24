package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.observer.GameObserver;
import edu.ntnu.idi.idatt2003.service.GameSession;
import edu.ntnu.idi.idatt2003.util.CurrencyFormatter;
import edu.ntnu.idi.idatt2003.view.GameView;
import edu.ntnu.idi.idatt2003.view.MarketView;
import edu.ntnu.idi.idatt2003.view.PortfolioView;
import edu.ntnu.idi.idatt2003.view.TransactionsView;
import java.math.BigDecimal;

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
     * @param view the main game shell
     * @param gameSession the active game session
     * @param navigator the navigator used to leave the game screen
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
        showMarket();
        refreshStats();
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
        marketView.setOnBuy(this::showBuyPlaceholder);
        marketView.setOnConfirmBuy(this::confirmBuy);
        marketView.setModalHandlers(view::showModal, view::hideModal);
        view.setOnExit(event -> {
            gameSession.endSession();
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
            format(gameSession.getPlayer().getMoney()),
            format(gameSession.getPlayer().getNetWorth()),
            gameSession.getExchange().getWeek()
        );
        marketView.setStocks(gameSession.getExchange().getStocks());
    }

    private static String format(BigDecimal amount) {
        return CurrencyFormatter.formatToNOK(amount.doubleValue());
    }

    private void showBuyPlaceholder(Stock stock) {
        marketView.showBuyDialog(stock);
    }

    private void confirmBuy(Stock stock, BigDecimal quantity) {
        try {
            gameSession.buy(stock.getSymbol(), quantity);
            marketView.closeBuyDialog();
            view.showSuccessToast("Purchased " + quantity + " share(s) of " + stock.getSymbol() + ".");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            marketView.showBuyError(toUserMessage(exception));
        }
    }

    private static String toUserMessage(RuntimeException exception) {
        if (exception.getMessage() != null && exception.getMessage().contains("Insufficient funds")) {
            return "You do not have enough money for this purchase.";
        }
        return "Could not complete purchase. " + exception.getMessage();
    }
}
