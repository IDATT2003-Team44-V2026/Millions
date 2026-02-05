package edu.ntnu.idi.idatt2003.logic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.transactions.Purchase;
import edu.ntnu.idi.idatt2003.transactions.Sale;
import edu.ntnu.idi.idatt2003.transactions.Transaction;

public class Exchange {
    private final String name;
    private int week;
    private final Map<String, Stock> stockMap;
    private final Random random;

    public Exchange(String name, List<Stock> stocks) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (stocks == null || stocks.isEmpty()) {
            throw new IllegalArgumentException("Stocks list cannot be null or empty");
        }
        
        this.name = name.trim();
        this.week = 1;
        this.stockMap = new HashMap<>();
        this.random = new Random();
        
        for (Stock stock : stocks) {
            if (stock == null) {
                throw new IllegalArgumentException("Stock cannot be null");
            }
            this.stockMap.put(stock.getSymbol(), stock);
        }
    }

    public String getName() {
        return name;
    }

    public int getWeek() {
        return week;
    }

    public boolean hasStock(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        return stockMap.containsKey(symbol);
    }

    public Stock getStock(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        return stockMap.get(symbol);
    }

    public List<Stock> findStocks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        String searchLower = searchTerm.toLowerCase();
        return stockMap.values().stream()
            .filter(stock -> stock.getSymbol().toLowerCase().contains(searchLower) 
                          || stock.getCompany().toLowerCase().contains(searchLower))
            .toList();
    }

    public Transaction buy(String symbol, BigDecimal quantity, Player player) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        Stock stock = getStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock with symbol '" + symbol + "' not found on this exchange");
        }
        
        Share share = new Share(stock, quantity, stock.getSalesPrice());
        return new Purchase(share, week);
    }

    public Transaction sell(Share share, Player player) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        if (!hasStock(share.getStock().getSymbol())) {
            throw new IllegalArgumentException("Stock is not listed on this exchange");
        }
        
        return new Sale(share, week);
    }

    public void advance() {
        week++;
        
        for (Stock stock : stockMap.values()) {
            BigDecimal currentPrice = stock.getSalesPrice();
            
            double changePercent = (random.nextDouble() * 0.20) - 0.10;
            BigDecimal change = currentPrice.multiply(BigDecimal.valueOf(changePercent));
            BigDecimal newPrice = currentPrice.add(change);
            
            if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                newPrice = currentPrice.multiply(BigDecimal.valueOf(0.5));
            }
            
            stock.addNewSalesPrice(newPrice);
        }
    }
} 
