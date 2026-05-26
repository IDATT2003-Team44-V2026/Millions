# Millions

A desktop stock-trading simulation built with JavaFX. Players buy and sell shares across weekly market cycles, track their portfolio, and work toward the Investor and Speculator status tiers by growing their net worth over time.

## About

Millions is a turn-based stock market game developed as a course project for IDATT2003 at NTNU. Each week the market updates stock prices. Players decide which shares to buy or sell, then advance to the next week. A session can be saved at any time and resumed later. When a player is done, they can sell all their holdings at once and review a session summary before returning to the main menu.

Player status is determined by weeks actively traded and net worth relative to starting capital:

| Status | Weeks traded | Net worth |
|---|---|---|
| Novice | — | — |
| Investor | ≥ 10 | ≥ 120 % of starting capital |
| Speculator | ≥ 20 | ≥ 200 % of starting capital |

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

On first launch, choose **New game**, enter a player name and starting capital, and select a stock data CSV file. The CSV format is one stock per line:

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

- [JavaFX 25](https://openjfx.io) — UI framework
- [Gson 2.11](https://github.com/google/gson) — JSON serialisation for save files
- [JUnit Jupiter 6](https://junit.org/junit5/) — unit testing

## Authors

- Patrik Johansen ([@patrikpj](https://github.com/patrikpj))
