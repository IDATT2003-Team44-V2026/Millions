package edu.ntnu.idi.idatt2003.io;

/**
 * Thrown when a CSV line cannot be parsed into a valid stock record.
 */
public class StockDataParseException extends Exception {

  public StockDataParseException(String message) {
    super(message);
  }

  public StockDataParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
