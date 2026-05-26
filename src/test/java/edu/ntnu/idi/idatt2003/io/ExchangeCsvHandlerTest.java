package edu.ntnu.idi.idatt2003.io;

import edu.ntnu.idi.idatt2003.model.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExchangeCsvHandler Tests")
class ExchangeCsvHandlerTest {
    private final ExchangeCsvHandler handler = new ExchangeCsvHandler();

    @TempDir
    Path tempDir;

    @Nested
    @DisplayName("Parse Stock Tests")
    class ParseStockTests {

        @Test
        @DisplayName("Should parse valid line into stock")
        void shouldParseValidLine() throws StockDataParseException {
            Stock stock = handler.parseStock("AAPL,Apple Inc.,276.43", 1);

            assertEquals("AAPL", stock.getSymbol());
            assertEquals("Apple Inc.", stock.getCompany());
            assertEquals(0, new BigDecimal("276.43").compareTo(stock.getSalesPrice()));
        }

        @Test
        @DisplayName("Should throw when price field is missing")
        void shouldThrowWhenPriceFieldIsMissing() {
            assertThrows(StockDataParseException.class, () ->
                handler.parseStock("AAPL,Apple Inc.", 1)
            );
        }

        @Test
        @DisplayName("Should throw when price is non-numeric")
        void shouldThrowWhenPriceIsNonNumeric() {
            assertThrows(StockDataParseException.class, () ->
                handler.parseStock("AAPL,Apple Inc.,abc", 1)
            );
        }

        @Test
        @DisplayName("Should throw when symbol is empty")
        void shouldThrowWhenSymbolIsEmpty() {
            assertThrows(StockDataParseException.class, () ->
                handler.parseStock(",Apple Inc.,276.43", 1)
            );
        }
    }

    @Nested
    @DisplayName("Load Stocks Tests")
    class LoadStocksTests {

        @Test
        @DisplayName("Should load stocks from valid CSV file")
        void shouldLoadStocksFromValidCsvFile() throws IOException, StockDataParseException {
            Path file = tempDir.resolve("stocks.csv");
            Files.writeString(file, """
                # Top stocks
                # Ticker,Name,Price
                AAPL,Apple Inc.,276.43
                MSFT,Microsoft,404.68
                """);

            List<Stock> stocks = handler.loadStocks(file);

            assertEquals(2, stocks.size());
            assertEquals("AAPL", stocks.get(0).getSymbol());
            assertEquals("Apple Inc.", stocks.get(0).getCompany());
            assertEquals(0, new BigDecimal("276.43").compareTo(stocks.get(0).getSalesPrice()));
            assertEquals("MSFT", stocks.get(1).getSymbol());
        }

        @Test
        @DisplayName("Should ignore blank lines and comments")
        void shouldIgnoreBlankLinesAndComments() throws IOException, StockDataParseException {
            Path file = tempDir.resolve("stocks.csv");
            Files.writeString(file, """

                # Comment
                AAPL,Apple Inc.,276.43

                # Another comment
                MSFT,Microsoft,404.68
                """);

            List<Stock> stocks = handler.loadStocks(file);

            assertEquals(2, stocks.size());
        }

        @Test
        @DisplayName("Should throw exception when path is null")
        void shouldThrowExceptionWhenPathIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                handler.loadStocks(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when stock row has invalid format")
        void shouldThrowExceptionWhenStockRowHasInvalidFormat() throws IOException {
            Path file = tempDir.resolve("stocks.csv");
            Files.writeString(file, "AAPL,Apple Inc.");

            assertThrows(StockDataParseException.class, () ->
                handler.loadStocks(file)
            );
        }

        @Test
        @DisplayName("Should throw exception when stock price is invalid")
        void shouldThrowExceptionWhenStockPriceIsInvalid() throws IOException {
            Path file = tempDir.resolve("stocks.csv");
            Files.writeString(file, "AAPL,Apple Inc.,abc");

            assertThrows(StockDataParseException.class, () ->
                handler.loadStocks(file)
            );
        }
    }

    @Nested
    @DisplayName("Save Stocks Tests")
    class SaveStocksTests {

        @Test
        @DisplayName("Should save stocks to CSV file")
        void shouldSaveStocksToCsvFile() throws IOException {
            Path file = tempDir.resolve("stocks.csv");
            List<Stock> stocks = List.of(
                new Stock("AAPL", "Apple Inc.", new BigDecimal("276.43")),
                new Stock("MSFT", "Microsoft", new BigDecimal("404.68"))
            );

            handler.saveStocks(file, stocks);

            List<String> lines = Files.readAllLines(file);
            assertEquals(2, lines.size());
            assertEquals("AAPL,Apple Inc.,276.43", lines.get(0));
            assertEquals("MSFT,Microsoft,404.68", lines.get(1));
        }

        @Test
        @DisplayName("Should save latest stock price")
        void shouldSaveLatestStockPrice() throws IOException {
            Path file = tempDir.resolve("stocks.csv");
            Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("276.43"));
            stock.addNewSalesPrice(new BigDecimal("280.00"));

            handler.saveStocks(file, List.of(stock));

            List<String> lines = Files.readAllLines(file);
            assertEquals("AAPL,Apple Inc.,280.00", lines.getFirst());
        }

        @Test
        @DisplayName("Should throw exception when save path is null")
        void shouldThrowExceptionWhenSavePathIsNull() {
            List<Stock> stocks = List.of(
                new Stock("AAPL", "Apple Inc.", new BigDecimal("276.43"))
            );

            assertThrows(IllegalArgumentException.class, () ->
                handler.saveStocks(null, stocks)
            );
        }

        @Test
        @DisplayName("Should throw exception when stocks collection is null")
        void shouldThrowExceptionWhenStocksCollectionIsNull() {
            Path file = tempDir.resolve("stocks.csv");

            assertThrows(IllegalArgumentException.class, () ->
                handler.saveStocks(file, null)
            );
        }

        @Test
        @DisplayName("Should throw exception when stocks collection contains null")
        void shouldThrowExceptionWhenStocksCollectionContainsNull() {
            Path file = tempDir.resolve("stocks.csv");
            List<Stock> stocks = new ArrayList<>();
            stocks.add(new Stock("AAPL", "Apple Inc.", new BigDecimal("276.43")));
            stocks.add(null);

            assertThrows(IllegalArgumentException.class, () ->
                handler.saveStocks(file, stocks)
            );
        }

        @Test
        @DisplayName("Should round trip saved and loaded stocks")
        void shouldRoundTripSavedAndLoadedStocks() throws IOException, StockDataParseException {
            Path file = tempDir.resolve("stocks.csv");
            List<Stock> stocks = List.of(
                new Stock("AAPL", "Apple Inc.", new BigDecimal("276.43")),
                new Stock("MSFT", "Microsoft", new BigDecimal("404.68"))
            );

            handler.saveStocks(file, stocks);
            List<Stock> loadedStocks = handler.loadStocks(file);

            assertEquals(2, loadedStocks.size());
            assertEquals("AAPL", loadedStocks.get(0).getSymbol());
            assertEquals(0, new BigDecimal("404.68").compareTo(loadedStocks.get(1).getSalesPrice()));
        }
    }
}
