package edu.ntnu.idi.idatt2003.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility for formatting amounts as Norwegian currency (NOK) using Norwegian locale rules.
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>{@code 1234.5} &rarr; {@code 1 234,50 kr}</li>
 *   <li>{@code 1234567.89} &rarr; {@code 1 234 567,89 kr}</li>
 * </ul>
 */
public final class CurrencyFormatter {

  private static final Locale NORWEGIAN_BOKMAL_NORWAY = Locale.forLanguageTag("nb-NO");

  private CurrencyFormatter() {}

  /**
   * Formats the given amount using Norwegian currency formatting conventions.
   *
   * @param amount the amount to format
   * @return a string formatted according to Norwegian currency rules
   */
  public static String formatToNOK(double amount) {
    NumberFormat formatter = NumberFormat.getCurrencyInstance(NORWEGIAN_BOKMAL_NORWAY);
    return formatter.format(amount);
  }
}

