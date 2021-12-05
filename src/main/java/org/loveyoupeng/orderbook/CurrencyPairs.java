package org.loveyoupeng.orderbook;

import static org.loveyoupeng.orderbook.Side.*;
import static org.loveyoupeng.orderbook.Currency.*;

public enum CurrencyPairs {
  USD_CNY(USD, CNY);

  private final Currency baseCurrency;
  private final Currency quoteCurrency;
  private final int value;

  private CurrencyPairs(final Currency baseCurrency,
                        final Currency quoteCurrency) {
    this.baseCurrency = baseCurrency;
    this.quoteCurrency = quoteCurrency;
    this.value = value(baseCurrency.ordinal(), quoteCurrency.ordinal());
  }

  private static int value(final int left, final int right) {
    return (Integer.min(left, right) << 16) | Integer.max(left, right);
  }

  public Currency getBaseCurrency() {
    return baseCurrency;
  }

  public Currency getQuoteCurrency() {
    return quoteCurrency;
  }

  public static Side side(final Currency toBuy, final Currency toSell) {
    final int value = value(toBuy.ordinal(), toSell.ordinal());
    for (final CurrencyPairs pair : values()) {
      if (value == pair.value) {
        return toBuy == pair.baseCurrency ? OFFER : BID;
      }
    }
    throw new IllegalArgumentException("not supported currency pair");
  }

  public static Currency baseCurrency(final Currency toBuy, final Currency toSell) {
    final int value = value(toBuy.ordinal(), toSell.ordinal());
    for (final CurrencyPairs pair : values()) {
      if (value == pair.value) {
        return pair.baseCurrency;
      }
    }
    throw new IllegalArgumentException("not supported currency pair");
  }
    
  public static Currency quoteCurrency(final Currency toBuy, final Currency toSell) {
    final int value = value(toBuy.ordinal(), toSell.ordinal());
    for (final CurrencyPairs pair : values()) {
      if (value == pair.value) {
        return pair.quoteCurrency;
      }
    }
    throw new IllegalArgumentException("not supported currency pair");
  }

}
