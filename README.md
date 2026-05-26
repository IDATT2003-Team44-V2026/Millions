# Millions

A stock trading game built with JavaFX. Buy and sell shares over weekly market cycles, grow your portfolio, and climb the ranks.

## About

Millions is a turn-based stock market game made as a course project for IDATT2003 at NTNU. Each week the market updates stock prices using Geometric Brownian Motion (GBM). You decide what to buy or sell, then move on to the next week. You can save at any point and come back later. When you're done, you can sell everything at once and see a summary of how your session went.

Player rank is based on how many weeks you've traded and how much your net worth has grown:

| Rank       | Weeks traded | Net worth                   |
| ---------- | ------------ | --------------------------- |
| Novice     |              |                             |
| Investor   | >= 10        | >= 120% of starting capital |
| Speculator | >= 20        | >= 200% of starting capital |

## Features

**Difficulty** is picked at the start and controls how the market moves each week:

| Difficulty | Annual drift | Annual volatility | Feel                              |
| ---------- | ------------ | ----------------- | --------------------------------- |
| Easy       | 12%          | 10%               | Market trends upward, mild swings |
| Normal     | 8%           | 20%               | Standard market conditions        |
| Hard       | 2%           | 35%               | Barely rising, highly volatile    |

**GBM price simulation** - stock prices follow `S·exp((μ−σ²/2)·dt + σ·√dt·Z)` with a weekly time step, so prices can never go negative.

**Market view** - the Change % column shows percentage change with green/red/black colouring. Top gainers and losers are ranked by percentage change, not absolute value.

**Portfolio** - you can sell any amount up to what you own. The sell dialog pre-fills your full holding and updates the proceeds live as you change the quantity.

**Stock details** - available from both the market table and the portfolio table. Shows a price history chart and key stats.

**Saves** - difficulty is stored in the save file. Saves from before difficulty was added load as Normal.


## Prerequisites

- Java 25 or later
- Maven 3.9 or later

## Getting Started

### Build

```bash
mvn compile
```

### Run

```bash
mvn javafx:run
```

On first launch, choose **New game**, enter a player name and starting capital, and pick a stock data CSV file. The CSV format is one stock per line:

```
# symbol,company,price
AAPL,Apple Inc.,276.43
MSFT,Microsoft Corporation,404.68
```

Lines starting with `#` and blank lines are ignored.

### Run Tests

```bash
mvn test
```

## Project Structure

```
src/main/java/.../
├── controller/     # Event handling and application logic
├── io/             # CSV loading/saving and JSON game persistence
├── logic/          # Exchange: price simulation, buy/sell execution
├── model/          # Player, Portfolio, Stock, Share
├── navigation/     # Screen routing
├── observer/       # GameObserver interface
├── service/        # GameSession: coordinates player, exchange, observers
├── transactions/   # Purchase, Sale, TransactionArchive, calculators
├── util/           # Currency formatting
└── view/           # JavaFX views and reusable dialog components
```

Save files are written to `~/.millions/saves/` as JSON.

## Technologies

- [JavaFX 25](https://openjfx.io) - UI framework
- [Gson 2.11](https://github.com/google/gson) - JSON serialisation for save files
- [JUnit Jupiter 6](https://junit.org/junit5/) - unit testing
