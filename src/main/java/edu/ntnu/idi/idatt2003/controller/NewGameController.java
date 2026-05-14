package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.io.ExchangeCsvHandler;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.view.NewGameView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Controller for the new game setup screen.
 */
public class NewGameController {

    private static final String INVALID_STOCK_FILE_MESSAGE =
        "Could not load stock data. Please choose a valid CSV file.";

    private final NewGameView view;
    private final Navigator navigator;
    private final ExchangeCsvHandler csvHandler;
    private Path selectedStockFile;
    private List<Stock> loadedStocks = List.of();

    /**
     * Creates a controller for the new game setup screen.
     *
     * @param view the new game setup view
     * @param navigator the navigator used to switch routes
     * @param csvHandler the CSV handler used to load stock data
     */
    public NewGameController(NewGameView view, Navigator navigator, ExchangeCsvHandler csvHandler) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        if (navigator == null) {
            throw new IllegalArgumentException("Navigator cannot be null");
        }
        if (csvHandler == null) {
            throw new IllegalArgumentException("CSV handler cannot be null");
        }

        this.view = view;
        this.navigator = navigator;
        this.csvHandler = csvHandler;
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
        view.setOnStart(event -> navigator.navigateTo(Route.PLACEHOLDER));
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
        try {
            List<Stock> stocks = csvHandler.loadStocks(stockFile);
            selectedStockFile = stockFile;
            loadedStocks = List.copyOf(stocks);
            view.setSelectedFile(stockFile.getFileName().toString());
            view.clearError();
        } catch (IOException | IllegalArgumentException _) {
            view.showError(INVALID_STOCK_FILE_MESSAGE);
        }
    }

    private Window getOwnerWindow() {
        Scene scene = view.getRoot().getScene();
        return scene == null ? null : scene.getWindow();
    }
}
