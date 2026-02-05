package edu.ntnu.idi.idatt2003.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class Stock {
  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  public Stock(String symbol, String company, BigDecimal salesPrice) {
    this.symbol = symbol;
    this.company = company;
    this.prices = new ArrayList<>();
    this.prices.add(salesPrice);
  }

  public String getSymbol() {
    return this.symbol;
  }

  public String getCompany() {
    return this.company;
  }

  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      return null;
    }
    return prices.getLast();
  }

  public void addNewSalesPrice(BigDecimal price) {
    if (price != null) {
      this.prices.add(price);
    }
  }
}