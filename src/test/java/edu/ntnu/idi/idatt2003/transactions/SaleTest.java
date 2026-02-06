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

@DisplayName("Sale Tests")
class SaleTest {
    private Stock stock;
    private Share share;
    private Player player;
    private Sale sale;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
        player = new Player("John Doe", new BigDecimal("10000.00"));
        player.getPortfolio().addShare(share);
        sale = new Sale(share, 1);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create sale with valid parameters")
        void shouldCreateSaleWithValidParameters() {
            Sale newSale = new Sale(share, 5);

            assertNotNull(newSale);
            assertEquals(share, newSale.getShare());
            assertEquals(5, newSale.getWeek());
            assertFalse(newSale.isCommitted());
        }

        @Test
        @DisplayName("Should throw exception when share is null")
        void shouldThrowExceptionWhenShareIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Sale(null, 1)
            );
        }

        @Test
        @DisplayName("Should throw exception when week is negative")
        void shouldThrowExceptionWhenWeekIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                new Sale(share, -1)
            );
        }

        @Test
        @DisplayName("Should allow week zero")
        void shouldAllowWeekZero() {
            Sale weekZeroSale = new Sale(share, 0);

            assertEquals(0, weekZeroSale.getWeek());
        }

        @Test
        @DisplayName("Should initialize with calculator")
        void shouldInitializeWithCalculator() {
            assertNotNull(sale.getCalculator());
        }

        @Test
        @DisplayName("Should initialize as not committed")
        void shouldInitializeAsNotCommitted() {
            assertFalse(sale.isCommitted());
        }
    }

    @Nested
    @DisplayName("Commit Tests - Success Scenarios")
    class CommitSuccessTests {

        @Test
        @DisplayName("Should add money to player when committed")
        void shouldAddMoneyToPlayerWhenCommitted() {
            BigDecimal initialMoney = player.getMoney();
            BigDecimal totalValue = sale.getCalculator().calculateTotal();

            sale.commit(player);

            assertEquals(0, player.getMoney().compareTo(initialMoney.add(totalValue)));
        }

        @Test
        @DisplayName("Should remove share from player portfolio when committed")
        void shouldRemoveShareFromPlayerPortfolioWhenCommitted() {
            sale.commit(player);

            assertFalse(player.getPortfolio().contains(share));
            assertEquals(0, player.getPortfolio().getShares().size());
        }

        @Test
        @DisplayName("Should add transaction to player archive when committed")
        void shouldAddTransactionToPlayerArchiveWhenCommitted() {
            sale.commit(player);

            assertFalse(player.getTransactionArchive().isEmpty());
            assertEquals(1, player.getTransactionArchive().getTransactions(1).size());
        }

        @Test
        @DisplayName("Should set committed flag to true after commit")
        void shouldSetCommittedFlagToTrueAfterCommit() {
            sale.commit(player);

            assertTrue(sale.isCommitted());
        }

        @Test
        @DisplayName("Should perform all operations when committed")
        void shouldPerformAllOperationsWhenCommitted() {
            BigDecimal initialMoney = player.getMoney();
            BigDecimal totalValue = sale.getCalculator().calculateTotal();

            sale.commit(player);

            assertEquals(0, player.getMoney().compareTo(initialMoney.add(totalValue)));
            assertFalse(player.getPortfolio().contains(share));
            assertFalse(player.getTransactionArchive().isEmpty());
            assertTrue(sale.isCommitted());
        }

        @Test
        @DisplayName("Should commit successfully with large sale")
        void shouldCommitSuccessfullyWithLargeSale() {
            Stock expensiveStock = new Stock("TSLA", "Tesla Inc.", new BigDecimal("800.00"));
            Share largeShare = new Share(expensiveStock, new BigDecimal("100"), new BigDecimal("500.00"));
            Player richPlayer = new Player("Rich Rick", new BigDecimal("100000.00"));
            richPlayer.getPortfolio().addShare(largeShare);
            Sale largeSale = new Sale(largeShare, 2);

            largeSale.commit(richPlayer);

            assertTrue(largeSale.isCommitted());
            assertFalse(richPlayer.getPortfolio().contains(largeShare));
        }

        @Test
        @DisplayName("Should sell share at profit")
        void shouldSellShareAtProfit() {
            Stock profitableStock = new Stock("GOOG", "Google Inc.", new BigDecimal("200.00"));
            Share profitShare = new Share(profitableStock, new BigDecimal("10"), new BigDecimal("100.00"));
            player.getPortfolio().addShare(profitShare);
            Sale profitSale = new Sale(profitShare, 1);

            BigDecimal initialMoney = player.getMoney();
            BigDecimal purchaseCost = profitShare.getPurchasePrice().multiply(profitShare.getQuantity());
            profitSale.commit(player);

            BigDecimal received = player.getMoney().subtract(initialMoney);
            assertTrue(received.compareTo(purchaseCost) > 0);
        }

        @Test
        @DisplayName("Should sell share at loss")
        void shouldSellShareAtLoss() {
            Stock unprofitableStock = new Stock("LOSS", "Loss Corp", new BigDecimal("50.00"));
            Share lossShare = new Share(unprofitableStock, new BigDecimal("10"), new BigDecimal("100.00"));
            player.getPortfolio().addShare(lossShare);
            Sale lossSale = new Sale(lossShare, 1);

            BigDecimal initialMoney = player.getMoney();
            BigDecimal purchaseCost = lossShare.getPurchasePrice().multiply(lossShare.getQuantity());
            lossSale.commit(player);

            BigDecimal received = player.getMoney().subtract(initialMoney);
            assertTrue(received.compareTo(purchaseCost) < 0);
        }
    }

    @Nested
    @DisplayName("Commit Tests - Failure Scenarios")
    class CommitFailureTests {

        @Test
        @DisplayName("Should throw exception when already committed")
        void shouldThrowExceptionWhenAlreadyCommitted() {
            sale.commit(player);

            assertThrows(IllegalStateException.class, () ->
                sale.commit(player)
            );
        }

        @Test
        @DisplayName("Should throw exception when player is null")
        void shouldThrowExceptionWhenPlayerIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                sale.commit(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when player does not own the share")
        void shouldThrowExceptionWhenPlayerDoesNotOwnShare() {
            Player otherPlayer = new Player("Jane Doe", new BigDecimal("10000.00"));

            assertThrows(IllegalStateException.class, () ->
                sale.commit(otherPlayer)
            );
        }

        @Test
        @DisplayName("Should throw exception when player owns different share of same stock")
        void shouldThrowExceptionWhenPlayerOwnsDifferentShareOfSameStock() {
            Share differentShare = new Share(stock, new BigDecimal("5"), new BigDecimal("120.00"));
            Player otherPlayer = new Player("Jane Doe", new BigDecimal("10000.00"));
            otherPlayer.getPortfolio().addShare(differentShare);
            Sale saleOfOriginalShare = new Sale(share, 1);

            assertThrows(IllegalStateException.class, () ->
                saleOfOriginalShare.commit(otherPlayer)
            );
        }

        @Test
        @DisplayName("Should not modify player state when commit fails due to not owning share")
        void shouldNotModifyPlayerStateWhenCommitFailsDueToNotOwningShare() {
            Player otherPlayer = new Player("Poor Pete", new BigDecimal("5000.00"));
            BigDecimal initialMoney = otherPlayer.getMoney();
            int initialPortfolioSize = otherPlayer.getPortfolio().getShares().size();

            try {
                sale.commit(otherPlayer);
            } catch (IllegalStateException _) {
                // Expected
            }

            assertEquals(0, otherPlayer.getMoney().compareTo(initialMoney));
            assertEquals(initialPortfolioSize, otherPlayer.getPortfolio().getShares().size());
            assertFalse(sale.isCommitted());
        }

        @Test
        @DisplayName("Should not modify player state when commit fails due to already committed")
        void shouldNotModifyPlayerStateWhenCommitFailsDueToAlreadyCommitted() {
            sale.commit(player);
            
            BigDecimal moneyAfterFirstCommit = player.getMoney();
            int portfolioSizeAfterFirstCommit = player.getPortfolio().getShares().size();

            try {
                sale.commit(player);
            } catch (IllegalStateException _) {
                // Expected
            }

            assertEquals(0, player.getMoney().compareTo(moneyAfterFirstCommit));
            assertEquals(portfolioSizeAfterFirstCommit, player.getPortfolio().getShares().size());
        }
    }

    @Nested
    @DisplayName("Multiple Sales Tests")
    class MultipleSalesTests {

        @Test
        @DisplayName("Should allow multiple different sales by same player")
        void shouldAllowMultipleDifferentSalesBySamePlayer() {
            Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
            player.getPortfolio().addShare(share2);
            Sale sale2 = new Sale(share2, 2);

            sale.commit(player);
            sale2.commit(player);

            assertFalse(player.getPortfolio().contains(share));
            assertFalse(player.getPortfolio().contains(share2));
            assertEquals(0, player.getPortfolio().getShares().size());
        }

        @Test
        @DisplayName("Should add money correctly for multiple sales")
        void shouldAddMoneyCorrectlyForMultipleSales() {
            BigDecimal initialMoney = player.getMoney();
            Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
            player.getPortfolio().addShare(share2);
            Sale sale2 = new Sale(share2, 2);

            BigDecimal totalValue1 = sale.getCalculator().calculateTotal();
            BigDecimal totalValue2 = sale2.getCalculator().calculateTotal();

            sale.commit(player);
            sale2.commit(player);

            BigDecimal expectedMoney = initialMoney.add(totalValue1).add(totalValue2);
            assertEquals(0, player.getMoney().compareTo(expectedMoney));
        }

        @Test
        @DisplayName("Should not allow selling share twice")
        void shouldNotAllowSellingShareTwice() {
            sale.commit(player);
            
            Sale secondSaleOfSameShare = new Sale(share, 2);
            
            assertThrows(IllegalStateException.class, () ->
                secondSaleOfSameShare.commit(player)
            );
        }
    }
}
