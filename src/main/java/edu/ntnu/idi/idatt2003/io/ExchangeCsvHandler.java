package edu.ntnu.idi.idatt2003.io;

import edu.ntnu.idi.idatt2003.model.Stock;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles loading and saving stock data in CSV format.
 *
 * <p>Each non-empty, non-comment line in the file must be on the form
 * {@code symbol,name,price}. Blank lines and lines starting with {@code #} are ignored.</p>
 */
public class ExchangeCsvHandler {

  /**
   * Loads stocks from the given CSV file.
   *
   * @param path the file to load from; must not be {@code null}
   * @return a list of stocks loaded from the file
   * @throws IllegalArgumentException if {@code path} is {@code null}
   * @throws StockDataParseException  if a row has invalid format
   * @throws IOException              if the file cannot be read
   */
  public List<Stock> loadStocks(Path path) throws StockDataParseException, IOException {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }

    List<Stock> stocks = new ArrayList<>();
    List<String> lines = Files.readAllLines(path);

    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i).trim();
      if (line.isEmpty() || line.startsWith("#")) {
        continue;
      }

      stocks.add(parseStock(line, i + 1));
    }

    return stocks;
  }

  /**
   * Saves the given stocks to the specified CSV file.
   *
   * @param path   the file to save to; must not be {@code null}
   * @param stocks the stocks to save; must not be {@code null} or contain {@code null}
   * @throws IllegalArgumentException if {@code path} is {@code null}, if {@code stocks} is
   *                                  {@code null}, or if {@code stocks} contains {@code null}
   * @throws IOException              if the file cannot be written
   */
  public void saveStocks(Path path, Collection<Stock> stocks) throws IOException {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }
    if (stocks == null) {
      throw new IllegalArgumentException("Stocks collection cannot be null");
    }

    List<String> lines = new ArrayList<>();
    for (Stock stock : stocks) {
      if (stock == null) {
        throw new IllegalArgumentException("Stocks collection cannot contain null");
      }
      lines.add(String.join(",",
          stock.getSymbol(),
          stock.getCompany(),
          stock.getSalesPrice().toPlainString()));
    }

    Files.write(path, lines);
  }

  Stock parseStock(String line, int lineNumber) throws StockDataParseException {
    int firstComma = line.indexOf(',');
    int lastComma = line.lastIndexOf(',');

    if (firstComma <= 0 || lastComma <= firstComma || lastComma >= line.length() - 1) {
      throw new StockDataParseException("Invalid stock data at line " + lineNumber);
    }

    String symbol = line.substring(0, firstComma).trim();
    String company = line.substring(firstComma + 1, lastComma).trim();
    String priceText = line.substring(lastComma + 1).trim();

    try {
      return new Stock(symbol, company, new BigDecimal(priceText));
    } catch (NumberFormatException e) {
      throw new StockDataParseException("Invalid stock data at line " + lineNumber, e);
    }
  }
}
