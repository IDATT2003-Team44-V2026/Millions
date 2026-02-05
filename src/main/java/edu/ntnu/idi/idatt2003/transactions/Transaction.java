package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.calculators.TransactionCalculator;

public abstract class Transaction {
    private final Share share;
    private final int week;
    private final TransactionCalculator calculator;
    private boolean committed;

    protected Transaction(Share share, int week, TransactionCalculator calculator) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        if (week < 0) {
            throw new IllegalArgumentException("Week cannot be negative");
        }
        if (calculator == null) {
            throw new IllegalArgumentException("Calculator cannot be null");
        }
        
        this.share = share;
        this.week = week;
        this.calculator = calculator;
        this.committed = false;
    }

    public Share getShare() {
        return share;
    }

    public int getWeek() {
        return week;
    }

    public TransactionCalculator getCalculator() {
        return calculator;
    }

    public boolean isCommitted() {
        return committed;
    }

    public abstract void commit(Player player);
}
