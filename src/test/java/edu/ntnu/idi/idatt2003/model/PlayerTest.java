package edu.ntnu.idi.idatt2003.model;

import edu.ntnu.idi.idatt2003.transactions.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import edu.ntnu.idi.idatt2003.model.InsufficientFundsException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player Tests")
class PlayerTest {
    private Player player;
    private BigDecimal startingMoney;

    @BeforeEach
    void setUp() {
        startingMoney = new BigDecimal("10000.00");
        player = new Player("John Doe", startingMoney);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create player with valid parameters")
        void shouldCreatePlayerWithValidParameters() {
            Player newPlayer = new Player("Alice", new BigDecimal("5000.00"));

            assertNotNull(newPlayer);
            assertEquals("Alice", newPlayer.getName());
            assertEquals(0, new BigDecimal("5000.00").compareTo(newPlayer.getMoney()));
            assertNotNull(newPlayer.getPortfolio());
            assertNotNull(newPlayer.getTransactionArchive());
            assertTrue(newPlayer.getPortfolio().getShares().isEmpty());
            assertTrue(newPlayer.getTransactionArchive().isEmpty());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when name is invalid")
        void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
            BigDecimal money = new BigDecimal("1000.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Player(invalidName, money)
            );
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"-1000.00"})
        @DisplayName("Should throw exception when starting money is invalid (null or negative)")
        void shouldThrowExceptionWhenStartingMoneyIsInvalid(String invalidMoney) {
            BigDecimal money = invalidMoney == null ? null : new BigDecimal(invalidMoney);
            assertThrows(IllegalArgumentException.class, () ->
                new Player("Bob", money)
            );
        }

        @Test
        @DisplayName("Should allow zero starting money")
        void shouldAllowZeroStartingMoney() {
            Player poorPlayer = new Player("Broke Bob", BigDecimal.ZERO);
            assertEquals(0, BigDecimal.ZERO.compareTo(poorPlayer.getMoney()));
        }

        @Test
        @DisplayName("Should trim whitespace from name")
        void shouldTrimWhitespaceFromName() {
            Player trimmedPlayer = new Player("  John Smith  ", startingMoney);
            assertEquals("John Smith", trimmedPlayer.getName());
        }
    }

    @Nested
    @DisplayName("Money Management Tests")
    class MoneyManagementTests {

        @Test
        @DisplayName("Should return initial money amount")
        void shouldReturnInitialMoneyAmount() {
            assertEquals(0, startingMoney.compareTo(player.getMoney()));
        }

        @Test
        @DisplayName("Should add money successfully")
        void shouldAddMoneySuccessfully() {
            BigDecimal amountToAdd = new BigDecimal("500.00");
            player.addMoney(amountToAdd);
            assertEquals(0, new BigDecimal("10500.00").compareTo(player.getMoney()));
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"0.0", "-100.00"})
        @DisplayName("Should throw exception when adding invalid amount (null, zero, or negative)")
        void shouldThrowExceptionWhenAddingInvalidAmount(String invalidAmount) {
            BigDecimal amount = invalidAmount == null ? null : new BigDecimal(invalidAmount);
            assertThrows(IllegalArgumentException.class, () ->
                player.addMoney(amount)
            );
        }

        @Test
        @DisplayName("Should withdraw money successfully")
        void shouldWithdrawMoneySuccessfully() {
            BigDecimal amountToWithdraw = new BigDecimal("500.00");
            player.withdrawMoney(amountToWithdraw);
            assertEquals(0, new BigDecimal("9500.00").compareTo(player.getMoney()));
        }

        @Test
        @DisplayName("Should withdraw all money")
        void shouldWithdrawAllMoney() {
            player.withdrawMoney(startingMoney);
            assertEquals(0, BigDecimal.ZERO.compareTo(player.getMoney()));
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"0.0", "-100.00"})
        @DisplayName("Should throw exception when withdrawing invalid amount (null, zero, or negative)")
        void shouldThrowExceptionWhenWithdrawingInvalidAmount(String invalidAmount) {
            BigDecimal amount = invalidAmount == null ? null : new BigDecimal(invalidAmount);
            assertThrows(IllegalArgumentException.class, () ->
                player.withdrawMoney(amount)
            );
        }

        @Test
        @DisplayName("Should throw exception when withdrawing more than available")
        void shouldThrowExceptionWhenWithdrawingMoreThanAvailable() {
            BigDecimal tooMuch = new BigDecimal("10000.01");
            assertThrows(InsufficientFundsException.class, () ->
                player.withdrawMoney(tooMuch)
            );
        }

        @Test
        @DisplayName("Should handle combined add and withdraw operations")
        void shouldHandleCombinedAddAndWithdrawOperations() {
            player.addMoney(new BigDecimal("1000.00"));
            player.withdrawMoney(new BigDecimal("500.00"));
            player.addMoney(new BigDecimal("250.00"));
            assertEquals(0, new BigDecimal("10750.00").compareTo(player.getMoney()));
        }
    }

    @Nested
    @DisplayName("Net Worth Tests")
    class NetWorthTests {

        @Test
        @DisplayName("Should return starting money as net worth when portfolio is empty")
        void shouldReturnStartingMoneyAsNetWorthWhenPortfolioIsEmpty() {
            assertEquals(0, startingMoney.compareTo(player.getNetWorth()));
        }

        @Test
        @DisplayName("Should return combined money and portfolio net worth")
        void shouldReturnCombinedMoneyAndPortfolioNetWorth() {
            Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
            Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("145.00"));
            player.getPortfolio().addShare(share);

            assertEquals(0, new BigDecimal("11474.5000").compareTo(player.getNetWorth()));
        }

        @Test
        @DisplayName("Should return starting money accessor")
        void shouldReturnStartingMoneyAccessor() {
            assertEquals(0, startingMoney.compareTo(player.getStartingMoney()));
        }
    }

    @Nested
    @DisplayName("Status Tests")
    class StatusTests {

        @Test
        @DisplayName("Should return novice status by default")
        void shouldReturnNoviceStatusByDefault() {
            assertEquals(PlayerStatus.NOVICE, player.getStatus());
        }

        @Test
        @DisplayName("Should return novice status when weeks requirement is not met")
        void shouldReturnNoviceStatusWhenWeeksRequirementIsNotMet() {
            Stock stock = new Stock("GAIN", "Gain Corp", new BigDecimal("150.00"));
            Share share = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
            player.getPortfolio().addShare(share);
            for (int week = 1; week <= 9; week++) {
                player.getTransactionArchive().add(new Purchase(share, week));
            }

            assertEquals(PlayerStatus.NOVICE, player.getStatus());
        }

        @Test
        @DisplayName("Should return investor status when investor requirements are met")
        void shouldReturnInvestorStatusWhenInvestorRequirementsAreMet() {
            Player investor = new Player("Investor Ida", new BigDecimal("1000.00"));
            Stock stock = new Stock("GAIN", "Gain Corp", new BigDecimal("150.00"));
            Share share = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
            investor.getPortfolio().addShare(share);
            for (int week = 1; week <= 10; week++) {
                investor.getTransactionArchive().add(new Purchase(share, week));
            }

            assertEquals(PlayerStatus.INVESTOR, investor.getStatus());
        }

        @Test
        @DisplayName("Should return investor status when speculator profit is not met")
        void shouldReturnInvestorStatusWhenSpeculatorProfitIsNotMet() {
            Player investor = new Player("Investor Ida", new BigDecimal("1000.00"));
            Stock stock = new Stock("MID", "Middle Corp", new BigDecimal("180.00"));
            Share share = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
            investor.getPortfolio().addShare(share);
            for (int week = 1; week <= 20; week++) {
                investor.getTransactionArchive().add(new Purchase(share, week));
            }

            assertEquals(PlayerStatus.INVESTOR, investor.getStatus());
        }

        @Test
        @DisplayName("Should return speculator status when speculator requirements are met")
        void shouldReturnSpeculatorStatusWhenSpeculatorRequirementsAreMet() {
            Player speculator = new Player("Speculator Sam", new BigDecimal("1000.00"));
            Stock stock = new Stock("MOON", "Moon Corp", new BigDecimal("200.00"));
            Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("10.00"));
            speculator.getPortfolio().addShare(share);
            for (int week = 1; week <= 20; week++) {
                speculator.getTransactionArchive().add(new Purchase(share, week));
            }

            assertEquals(PlayerStatus.SPECULATOR, speculator.getStatus());
        }

        @Test
        @DisplayName("Should use distinct transaction weeks, not exchange week, for status")
        void shouldUseDistinctTransactionWeeksForStatus() {
            Stock stock = new Stock("GAIN", "Gain Corp", new BigDecimal("500.00"));
            Share share = new Share(stock, new BigDecimal("8"), new BigDecimal("100.00"));
            player.getPortfolio().addShare(share);
            player.getTransactionArchive().add(new Purchase(share, 1));

            // Only 1 distinct week → NOVICE even though net worth is well above thresholds
            assertEquals(PlayerStatus.NOVICE, player.getStatus());
        }
    }
}
