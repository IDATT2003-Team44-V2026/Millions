package edu.ntnu.idi.idatt2003.model;

import edu.ntnu.idi.idatt2003.transactions.TransactionArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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
            assertEquals(new BigDecimal("5000.00"), newPlayer.getMoney());
            assertNotNull(newPlayer.getPortfolio());
            assertNotNull(newPlayer.getTransactionArchive());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when name is null, empty, or whitespace")
        void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
            BigDecimal money = new BigDecimal("1000.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Player(invalidName, money)
            );
        }

        @Test
        @DisplayName("Should throw exception when starting money is null")
        void shouldThrowExceptionWhenStartingMoneyIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Player("Bob", null)
            );
        }

        @Test
        @DisplayName("Should throw exception when starting money is negative")
        void shouldThrowExceptionWhenStartingMoneyIsNegative() {
            BigDecimal negativeMoney = new BigDecimal("-1000.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Player("Bob", negativeMoney)
            );
        }

        @Test
        @DisplayName("Should allow zero starting money")
        void shouldAllowZeroStartingMoney() {
            Player poorPlayer = new Player("Broke Bob", BigDecimal.ZERO);

            assertEquals(BigDecimal.ZERO, poorPlayer.getMoney());
        }

        @Test
        @DisplayName("Should trim whitespace from name")
        void shouldTrimWhitespaceFromName() {
            Player trimmedPlayer = new Player("  John Smith  ", startingMoney);

            assertEquals("John Smith", trimmedPlayer.getName());
        }

        @Test
        @DisplayName("Should initialize money to starting money")
        void shouldInitializeMoneyToStartingMoney() {
            Player newPlayer = new Player("Charlie", new BigDecimal("7500.00"));

            assertEquals(new BigDecimal("7500.00"), newPlayer.getMoney());
        }

        @Test
        @DisplayName("Should initialize empty portfolio")
        void shouldInitializeEmptyPortfolio() {
            Player newPlayer = new Player("Diana", startingMoney);

            assertNotNull(newPlayer.getPortfolio());
            assertTrue(newPlayer.getPortfolio().getShares().isEmpty());
        }

        @Test
        @DisplayName("Should initialize empty transaction archive")
        void shouldInitializeEmptyTransactionArchive() {
            Player newPlayer = new Player("Eve", startingMoney);

            assertNotNull(newPlayer.getTransactionArchive());
            assertTrue(newPlayer.getTransactionArchive().isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Name Tests")
    class GetNameTests {

        @Test
        @DisplayName("Should return correct name")
        void shouldReturnCorrectName() {
            assertEquals("John Doe", player.getName());
        }
    }

    @Nested
    @DisplayName("Money Management Tests")
    class MoneyManagementTests {

        @Test
        @DisplayName("Should return initial money amount")
        void shouldReturnInitialMoneyAmount() {
            assertEquals(startingMoney, player.getMoney());
        }

        @Test
        @DisplayName("Should add money successfully")
        void shouldAddMoneySuccessfully() {
            BigDecimal amountToAdd = new BigDecimal("500.00");
            player.addMoney(amountToAdd);

            assertEquals(new BigDecimal("10500.00"), player.getMoney());
        }

        @Test
        @DisplayName("Should add money multiple times")
        void shouldAddMoneyMultipleTimes() {
            player.addMoney(new BigDecimal("100.00"));
            player.addMoney(new BigDecimal("200.00"));
            player.addMoney(new BigDecimal("300.00"));

            assertEquals(new BigDecimal("10600.00"), player.getMoney());
        }

        @Test
        @DisplayName("Should throw exception when adding null amount")
        void shouldThrowExceptionWhenAddingNullAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                player.addMoney(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when adding zero amount")
        void shouldThrowExceptionWhenAddingZeroAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                player.addMoney(BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("Should throw exception when adding negative amount")
        void shouldThrowExceptionWhenAddingNegativeAmount() {
            BigDecimal negativeAmount = new BigDecimal("-100.00");
            assertThrows(IllegalArgumentException.class, () ->
                player.addMoney(negativeAmount)
            );
        }

        @Test
        @DisplayName("Should withdraw money successfully")
        void shouldWithdrawMoneySuccessfully() {
            BigDecimal amountToWithdraw = new BigDecimal("500.00");
            player.withdrawMoney(amountToWithdraw);

            assertEquals(new BigDecimal("9500.00"), player.getMoney());
        }

        @Test
        @DisplayName("Should withdraw all money")
        void shouldWithdrawAllMoney() {
            player.withdrawMoney(startingMoney);

            assertEquals(new BigDecimal("0.00"), player.getMoney());
        }

        @Test
        @DisplayName("Should withdraw money multiple times")
        void shouldWithdrawMoneyMultipleTimes() {
            player.withdrawMoney(new BigDecimal("100.00"));
            player.withdrawMoney(new BigDecimal("200.00"));
            player.withdrawMoney(new BigDecimal("300.00"));

            assertEquals(new BigDecimal("9400.00"), player.getMoney());
        }

        @Test
        @DisplayName("Should throw exception when withdrawing null amount")
        void shouldThrowExceptionWhenWithdrawingNullAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                player.withdrawMoney(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when withdrawing zero amount")
        void shouldThrowExceptionWhenWithdrawingZeroAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                player.withdrawMoney(BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("Should throw exception when withdrawing negative amount")
        void shouldThrowExceptionWhenWithdrawingNegativeAmount() {
            BigDecimal negativeAmount = new BigDecimal("-100.00");
            assertThrows(IllegalArgumentException.class, () ->
                player.withdrawMoney(negativeAmount)
            );
        }

        @Test
        @DisplayName("Should throw exception when withdrawing more than available")
        void shouldThrowExceptionWhenWithdrawingMoreThanAvailable() {
            BigDecimal tooMuch = new BigDecimal("10000.01");
            assertThrows(IllegalArgumentException.class, () ->
                player.withdrawMoney(tooMuch)
            );
        }

        @Test
        @DisplayName("Should handle combined add and withdraw operations")
        void shouldHandleCombinedAddAndWithdrawOperations() {
            player.addMoney(new BigDecimal("1000.00"));
            player.withdrawMoney(new BigDecimal("500.00"));
            player.addMoney(new BigDecimal("250.00"));

            assertEquals(new BigDecimal("10750.00"), player.getMoney());
        }
    }

    @Nested
    @DisplayName("Portfolio Tests")
    class PortfolioTests {

        @Test
        @DisplayName("Should return portfolio instance")
        void shouldReturnPortfolioInstance() {
            Portfolio portfolio = player.getPortfolio();

            assertNotNull(portfolio);
        }

        @Test
        @DisplayName("Should return same portfolio instance on multiple calls")
        void shouldReturnSamePortfolioInstanceOnMultipleCalls() {
            Portfolio portfolio1 = player.getPortfolio();
            Portfolio portfolio2 = player.getPortfolio();

            assertSame(portfolio1, portfolio2);
        }
    }

    @Nested
    @DisplayName("Transaction Archive Tests")
    class TransactionArchiveTests {

        @Test
        @DisplayName("Should return transaction archive instance")
        void shouldReturnTransactionArchiveInstance() {
            TransactionArchive archive = player.getTransactionArchive();

            assertNotNull(archive);
        }

        @Test
        @DisplayName("Should return same transaction archive instance on multiple calls")
        void shouldReturnSameTransactionArchiveInstanceOnMultipleCalls() {
            TransactionArchive archive1 = player.getTransactionArchive();
            TransactionArchive archive2 = player.getTransactionArchive();

            assertSame(archive1, archive2);
        }
    }
}
