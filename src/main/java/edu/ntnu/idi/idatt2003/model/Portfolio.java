package edu.ntnu.idi.idatt2003.model;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private final List<Share> shares;

    public Portfolio() {
        this.shares = new ArrayList<>();
    }

    public boolean addShare(Share share) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        return shares.add(share);
    }

    public boolean removeShare(Share share) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        return shares.remove(share);
    }

    public List<Share> getShares() {
        return new ArrayList<>(shares);
    }

    
    public List<Share> getShares(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        List<Share> filteredShares = new ArrayList<>();
        for (Share share : shares) {
            if (share.getStock().getSymbol().equalsIgnoreCase(symbol)) {
                filteredShares.add(share);
            }
        }
        return filteredShares;
    }

    public boolean contains(Share share) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        return shares.contains(share);
    }
}