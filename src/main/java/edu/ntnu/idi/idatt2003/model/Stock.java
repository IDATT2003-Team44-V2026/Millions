package edu.ntnu.idi.idatt2003.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class Stock {
  private String symbol;
  private String company;
  private List<BigDecimal> prices;

  public Stock(String symbol, String company, BigDecimal salesPrice) {
    this.symbol = symbol;
    this.company = company;
  }

  public String getSymbol() {
    return null;
  }

  public String getCompany() {
    return null;
  }

  public BigDecimal getSalesPrice() {
    return null;
  }

  public void addNewSalesPrice(BigDecimal price) {
  }
}
