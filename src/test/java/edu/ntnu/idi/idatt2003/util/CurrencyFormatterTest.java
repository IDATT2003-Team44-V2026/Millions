package edu.ntnu.idi.idatt2003.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Currency Formatter Tests")
class CurrencyFormatterTest {

  @ParameterizedTest(name = "Amount: {0}")
  @CsvSource({
      "0.0",
      "1.0",
      "1234.5",
      "1234567.89",
      "-12.34"
  })
  @DisplayName("Should format amount using Norwegian currency format")
  void shouldFormatAmountUsingNorwegianCurrencyFormat(double amount) {
    Locale norwegian = Locale.forLanguageTag("nb-NO");
    String expected = NumberFormat.getCurrencyInstance(norwegian).format(amount);

    String actual = CurrencyFormatter.formatToNOK(amount);

    assertEquals(expected, actual);
  }
}

