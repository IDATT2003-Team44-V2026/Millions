package edu.ntnu.idi.idatt2003.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class Stock {
  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  public Stock(String symbol, String company, BigDecimal salesPrice) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Symbol cannot be null or empty");
    }
    if (company == null || company.trim().isEmpty()) {
      throw new IllegalArgumentException("Company name cannot be null or empty");
    }
    if (salesPrice == null) {
      throw new IllegalArgumentException("Sales price cannot be null");
    }
    if (salesPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Sales price must be positive");
    }
    
    this.symbol = symbol.trim();
    this.company = company.trim();
    this.prices = new ArrayList<>();
    this.prices.add(salesPrice);
  }

  public String getSymbol() {
    return symbol;
  }

  public String getCompany() {
    return company;
  }

  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      return null;
    }
    return prices.getLast();
  }

  public void addNewSalesPrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be positive");
    }
    this.prices.add(price);
  }
}