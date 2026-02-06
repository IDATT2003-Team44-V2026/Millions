package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Purchase Tests")
class PurchaseTest {
    private Stock stock;
    private Share share;
    private Player player;
    private Purchase purchase;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
        player = new Player("John Doe", new BigDecimal("10000.00"));
        purchase = new Purchase(share, 1);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create purchase with valid parameters")
        void shouldCreatePurchaseWithValidParameters() {
            Purchase newPurchase = new Purchase(share, 5);

            assertNotNull(newPurchase);
            assertEquals(share, newPurchase.getShare());
            assertEquals(5, newPurchase.getWeek());
            assertFalse(newPurchase.isCommitted());
        }

        @Test
        @DisplayName("Should throw exception when share is null")
        void shouldThrowExceptionWhenShareIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Purchase(null, 1)
            );
        }

        @Test
        @DisplayName("Should throw exception when week is negative")
        void shouldThrowExceptionWhenWeekIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                new Purchase(share, -1)
            );
        }

        @Test
        @DisplayName("Should allow week zero")
        void shouldAllowWeekZero() {
            Purchase weekZeroPurchase = new Purchase(share, 0);

            assertEquals(0, weekZeroPurchase.getWeek());
        }

        @Test
        @DisplayName("Should initialize with calculator")
        void shouldInitializeWithCalculator() {
            assertNotNull(purchase.getCalculator());
        }

        @Test
        @DisplayName("Should initialize as not committed")
        void shouldInitializeAsNotCommitted() {
            assertFalse(purchase.isCommitted());
        }
    }

    @Nested
    @DisplayName("Commit Tests - Success Scenarios")
    class CommitSuccessTests {

        @Test
        @DisplayName("Should withdraw money from player when committed")
        void shouldWithdrawMoneyFromPlayerWhenCommitted() {
            BigDecimal initialMoney = player.getMoney();
            BigDecimal totalCost = purchase.getCalculator().calculateTotal();

            purchase.commit(player);

            assertEquals(0, player.getMoney().compareTo(initialMoney.subtract(totalCost)));
        }

        @Test
        @DisplayName("Should add share to player portfolio when committed")
        void shouldAddShareToPlayerPortfolioWhenCommitted() {
            purchase.commit(player);

            assertTrue(player.getPortfolio().contains(share));
            assertEquals(1, player.getPortfolio().getShares().size());
        }

        @Test
        @DisplayName("Should add transaction to player archive when committed")
        void shouldAddTransactionToPlayerArchiveWhenCommitted() {
            purchase.commit(player);

            assertFalse(player.getTransactionArchive().isEmpty());
            assertEquals(1, player.getTransactionArchive().getTransactions(1).size());
        }

        @Test
        @DisplayName("Should set committed flag to true after commit")
        void shouldSetCommittedFlagToTrueAfterCommit() {
            purchase.commit(player);

            assertTrue(purchase.isCommitted());
        }

        @Test
        @DisplayName("Should perform all operations when committed")
        void shouldPerformAllOperationsWhenCommitted() {
            BigDecimal initialMoney = player.getMoney();
            BigDecimal totalCost = purchase.getCalculator().calculateTotal();

            purchase.commit(player);

            // Verify money withdrawn
            assertEquals(0, player.getMoney().compareTo(initialMoney.subtract(totalCost)));
            // Verify share added
            assertTrue(player.getPortfolio().contains(share));
            // Verify transaction archived
            assertFalse(player.getTransactionArchive().isEmpty());
            // Verify committed flag set
            assertTrue(purchase.isCommitted());
        }

        @Test
        @DisplayName("Should commit successfully with exact funds")
        void shouldCommitSuccessfullyWithExactFunds() {
            BigDecimal totalCost = purchase.getCalculator().calculateTotal();
            Player poorPlayer = new Player("Poor Joe", totalCost);

            purchase.commit(poorPlayer);

            assertEquals(0, poorPlayer.getMoney().compareTo(BigDecimal.ZERO));
            assertTrue(poorPlayer.getPortfolio().contains(share));
        }

        @Test
        @DisplayName("Should commit successfully with large purchase")
        void shouldCommitSuccessfullyWithLargePurchase() {
            Share largeShare = new Share(stock, new BigDecimal("100"), new BigDecimal("50.00"));
            Purchase largePurchase = new Purchase(largeShare, 2);
            Player richPlayer = new Player("Rich Rick", new BigDecimal("100000.00"));

            largePurchase.commit(richPlayer);

            assertTrue(largePurchase.isCommitted());
            assertTrue(richPlayer.getPortfolio().contains(largeShare));
        }
    }

    @Nested
    @DisplayName("Commit Tests - Failure Scenarios")
    class CommitFailureTests {

        @Test
        @DisplayName("Should throw exception when already committed")
        void shouldThrowExceptionWhenAlreadyCommitted() {
            purchase.commit(player);

            assertThrows(IllegalStateException.class, () ->
                purchase.commit(player)
            );
        }

        @Test
        @DisplayName("Should throw exception when player is null")
        void shouldThrowExceptionWhenPlayerIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                purchase.commit(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when player has insufficient funds")
        void shouldThrowExceptionWhenPlayerHasInsufficientFunds() {
            Player poorPlayer = new Player("Broke Bob", new BigDecimal("10.00"));

            assertThrows(IllegalStateException.class, () ->
                purchase.commit(poorPlayer)
            );
        }

        @Test
        @DisplayName("Should throw exception when funds are slightly insufficient")
        void shouldThrowExceptionWhenFundsAreSlightlyInsufficient() {
            BigDecimal totalCost = purchase.getCalculator().calculateTotal();
            BigDecimal almostEnough = totalCost.subtract(new BigDecimal("0.01"));
            Player almostRichPlayer = new Player("Almost Rich", almostEnough);

            assertThrows(IllegalStateException.class, () ->
                purchase.commit(almostRichPlayer)
            );
        }

        @Test
        @DisplayName("Should not modify player state when commit fails due to insufficient funds")
        void shouldNotModifyPlayerStateWhenCommitFailsDueToInsufficientFunds() {
            Player poorPlayer = new Player("Poor Pete", new BigDecimal("10.00"));
            BigDecimal initialMoney = poorPlayer.getMoney();
            int initialPortfolioSize = poorPlayer.getPortfolio().getShares().size();

            try {
                purchase.commit(poorPlayer);
            } catch (IllegalStateException _) {
                // Expected
            }

            assertEquals(0, poorPlayer.getMoney().compareTo(initialMoney));
            assertEquals(initialPortfolioSize, poorPlayer.getPortfolio().getShares().size());
            assertFalse(purchase.isCommitted());
        }

        @Test
        @DisplayName("Should not modify player state when commit fails due to already committed")
        void shouldNotModifyPlayerStateWhenCommitFailsDueToAlreadyCommitted() {
            purchase.commit(player);
            
            BigDecimal moneyAfterFirstCommit = player.getMoney();
            int portfolioSizeAfterFirstCommit = player.getPortfolio().getShares().size();

            try {
                purchase.commit(player);
            } catch (IllegalStateException _) {
                // Expected
            }

            assertEquals(0, player.getMoney().compareTo(moneyAfterFirstCommit));
            assertEquals(portfolioSizeAfterFirstCommit, player.getPortfolio().getShares().size());
        }
    }

    @Nested
    @DisplayName("Multiple Purchases Tests")
    class MultiplePurchasesTests {

        @Test
        @DisplayName("Should allow multiple different purchases by same player")
        void shouldAllowMultipleDifferentPurchasesBySamePlayer() {
            Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
            Purchase purchase2 = new Purchase(share2, 2);

            purchase.commit(player);
            purchase2.commit(player);

            assertTrue(player.getPortfolio().contains(share));
            assertTrue(player.getPortfolio().contains(share2));
            assertEquals(2, player.getPortfolio().getShares().size());
        }

        @Test
        @DisplayName("Should deduct money correctly for multiple purchases")
        void shouldDeductMoneyCorrectlyForMultiplePurchases() {
            BigDecimal initialMoney = player.getMoney();
            Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
            Purchase purchase2 = new Purchase(share2, 2);

            BigDecimal totalCost1 = purchase.getCalculator().calculateTotal();
            BigDecimal totalCost2 = purchase2.getCalculator().calculateTotal();

            purchase.commit(player);
            purchase2.commit(player);

            BigDecimal expectedMoney = initialMoney.subtract(totalCost1).subtract(totalCost2);
            assertEquals(0, player.getMoney().compareTo(expectedMoney));
        }
    }
}
