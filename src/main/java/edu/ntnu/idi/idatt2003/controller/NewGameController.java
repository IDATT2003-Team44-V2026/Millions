package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.io.ExchangeCsvHandler;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.service.GameSession;
import edu.ntnu.idi.idatt2003.service.GameSessionFactory;
import edu.ntnu.idi.idatt2003.view.NewGameView;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Controller for the new game setup screen.
 */
public class NewGameController {

  private static final Logger LOGGER = Logger.getLogger(NewGameController.class.getName());
  private static final String INVALID_STOCK_FILE_MESSAGE =
      "Could not load stock data. Please choose a valid CSV file.";
  private static final String MISSING_PLAYER_NAME_MESSAGE = "Enter a player name.";
  private static final String INVALID_STARTING_CAPITAL_MESSAGE = "Enter a valid starting capital.";
  private static final String MISSING_STOCK_FILE_MESSAGE = "Choose a valid stock data file.";
  private static final String START_GAME_FAILED_MESSAGE =
      "Could not start game. Please check your setup.";

  private final NewGameView view;
  private final Navigator navigator;
  private final ExchangeCsvHandler csvHandler;
  private final GameSessionFactory gameSessionFactory;
  private final Consumer<GameSession> activeSessionConsumer;
  private Path selectedStockFile;
  private List<Stock> loadedStocks = List.of();

  /**
   * Creates a controller for the new game setup screen.
   *
   * @param view                  the new game setup view
   * @param navigator             the navigator used to switch routes
   * @param csvHandler            the CSV handler used to load stock data
   * @param gameSessionFactory    the factory used to create game sessions
   * @param activeSessionConsumer receives the created active session
   */
  public NewGameController(
      NewGameView view,
      Navigator navigator,
      ExchangeCsvHandler csvHandler,
      GameSessionFactory gameSessionFactory,
      Consumer<GameSession> activeSessionConsumer
  ) {
    if (view == null) {
      throw new IllegalArgumentException("View cannot be null");
    }
    if (navigator == null) {
      throw new IllegalArgumentException("Navigator cannot be null");
    }
    if (csvHandler == null) {
      throw new IllegalArgumentException("CSV handler cannot be null");
    }
    if (gameSessionFactory == null) {
      throw new IllegalArgumentException("Game session factory cannot be null");
    }
    if (activeSessionConsumer == null) {
      throw new IllegalArgumentException("Active session consumer cannot be null");
    }

    this.view = view;
    this.navigator = navigator;
    this.csvHandler = csvHandler;
    this.gameSessionFactory = gameSessionFactory;
    this.activeSessionConsumer = activeSessionConsumer;
    initializeHandlers();
  }

  /**
   * Returns the selected stock data file.
   *
   * @return the selected stock data file, or {@code null} if no valid file has been selected
   */
  public Path getSelectedStockFile() {
    return selectedStockFile;
  }

  /**
   * Returns the stocks loaded from the selected stock data file.
   *
   * @return the loaded stocks
   */
  public List<Stock> getLoadedStocks() {
    return loadedStocks;
  }

  private void initializeHandlers() {
    view.setOnChooseFile(event -> chooseStockFile());
    view.setOnStart(event -> startGame());
    view.setOnBack(event -> {
      view.clearError();
      navigator.navigateTo(Route.START);
    });
  }

  private void chooseStockFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose stock data file");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("CSV files", "*.csv")
    );

    File selectedFile = fileChooser.showOpenDialog(getOwnerWindow());
    if (selectedFile == null) {
      return;
    }

    loadSelectedStockFile(selectedFile.toPath());
  }

  private void loadSelectedStockFile(Path stockFile) {
    view.setChooseFileEnabled(false);
    Task<List<Stock>> task = new Task<>() {
      @Override
      protected List<Stock> call() throws Exception {
        return csvHandler.loadStocks(stockFile);
      }
    };
    task.setOnSucceeded(e -> {
      selectedStockFile = stockFile;
      loadedStocks = List.copyOf(task.getValue());
      view.setSelectedFile(stockFile.getFileName().toString());
      view.clearError();
      view.setChooseFileEnabled(true);
    });
    task.setOnFailed(e -> {
      view.showError(INVALID_STOCK_FILE_MESSAGE);
      view.setChooseFileEnabled(true);
    });
    Thread.ofVirtual().start(task);
  }

  private void startGame() {
    String playerName = view.getPlayerName();
    if (playerName == null || playerName.trim().isEmpty()) {
      view.showError(MISSING_PLAYER_NAME_MESSAGE);
      return;
    }

    BigDecimal startingCapital = parseStartingCapital();
    if (startingCapital == null) {
      return;
    }
    if (startingCapital.compareTo(BigDecimal.ZERO) < 0) {
      view.showError(INVALID_STARTING_CAPITAL_MESSAGE);
      return;
    }

    if (selectedStockFile == null || loadedStocks.isEmpty()) {
      view.showError(MISSING_STOCK_FILE_MESSAGE);
      return;
    }

    try {
      GameSession gameSession = gameSessionFactory.create(
          playerName,
          startingCapital,
          loadedStocks
      );
      activeSessionConsumer.accept(gameSession);
      logGameSessionDetails(gameSession);
      view.clearError();
      navigator.navigateTo(Route.GAME);
    } catch (IllegalArgumentException _) {
      view.showError(START_GAME_FAILED_MESSAGE);
    }
  }

  private BigDecimal parseStartingCapital() {
    try {
      return new BigDecimal(view.getStartCapital().trim());
    } catch (NullPointerException | NumberFormatException _) {
      view.showError(INVALID_STARTING_CAPITAL_MESSAGE);
      return null;
    }
  }

  private void logGameSessionDetails(GameSession gameSession) {
    LOGGER.info(() -> String.format(
        "Started game session: player=%s, startingCapital=%s, exchange=%s, week=%d, stocks=%d, file=%s",
        gameSession.getPlayer().getName(),
        gameSession.getPlayer().getStartingMoney(),
        gameSession.getExchange().getName(),
        gameSession.getExchange().getWeek(),
        loadedStocks.size(),
        selectedStockFile
    ));
  }

  private Window getOwnerWindow() {
    Scene scene = view.getRoot().getScene();
    return scene == null ? null : scene.getWindow();
  }
}
