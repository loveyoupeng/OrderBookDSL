package org.loveyoupeng.orderbook;

import org.junit.*;
import static org.junit.Assert.*;
import static org.loveyoupeng.orderbook.Side.*;
import static org.loveyoupeng.orderbook.Currency.*;
import static org.loveyoupeng.orderbook.CurrencyPairs.*;

public final class OrderBookDesignTest {
  @Test
  public final void test_market_order_usd() {
    final Order order = OrderBuilder.market().toBuy(USD.amount(100)).toSell(CNY).build();
    assertSame(OFFER, order.getSide());
    assertSame(USD, order.getBaseCurrency());
    assertSame(CNY, order.getQuoteCurrency());
    assertEquals(USD.amount(100), order.getTransactAmount());
    assertEquals("Market", order.accept(new OrderProcessor<String>() {
      @Override
      public String on(final MarketOrder order) {
        return "Market";
      }
    }));
  }

  @Test
  public final void test_market_order_cny() {
    final Order order = OrderBuilder.market().toBuy(CNY.amount(100)).toSell(USD).build();
    assertSame(BID, order.getSide());
    assertSame(USD, order.getBaseCurrency());
    assertSame(CNY, order.getQuoteCurrency());
    assertEquals(CNY.amount(100), order.getTransactAmount());
    assertEquals("Market", order.accept(new OrderProcessor<String>() {
      @Override
      public String on(final MarketOrder order) {
        return "Market";
      }
    }));
  }
}

enum Side {
  BID, OFFER
}

enum Currency {
  USD, CNY;

  public Money amount(final int amount) {
    return new Money(this, amount);
  }
}

enum CurrencyPairs {
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

final class Money {
  private final Currency currency;
  private final double amount;

  public Money(final Currency currency, final double amount) {
    this.currency = currency;
    this.amount = amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    }
    if (Money.class.equals(other.getClass())) {
      final Money value = (Money) other;
      return currency == value.currency && amount == value.amount;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Double.valueOf(amount).hashCode() + currency.hashCode();
  }
}

interface Order {
  Side getSide();
  Currency getBaseCurrency();
  Currency getQuoteCurrency();
  Money getTransactAmount();
  <R> R accept(final OrderProcessor<R> processor);
}

interface OrderProcessor<R> {
  default R on(final Order order) {
    throw new UnsupportedOperationException("not supported");
  } 

  default R on(final MarketOrder order) {
    throw new UnsupportedOperationException("not supported");
  }
}

final class OrderBuilder {
  public static BuilderToBuy market() {
    return new MarketOrderBuilder();
  }
}

interface BuilderToBuy {
  BuilderToSell toBuy(final Money money);
}

interface BuilderToSell {
  Builder<MarketOrder> toSell(final Currency currency);
}

interface Builder<O extends Order> {
  O build();
}

final class MarketOrderBuilder implements BuilderToBuy, BuilderToSell, Builder<MarketOrder> {
  private Currency currencyToBuy;
  private Currency currencyToSell;
  private Money transactAmount;

  @Override
  public BuilderToSell toBuy(final Money money) {
    this.currencyToBuy = money.getCurrency();
    this.transactAmount = money;
    return this;
  }

  @Override
  public Builder<MarketOrder> toSell(final Currency currency) {
    this.currencyToSell = currency;
    return this;
  }

  @Override
  public MarketOrder build() {
    return new MarketOrder(side(currencyToBuy, currencyToSell), 
        baseCurrency(currencyToBuy, currencyToSell), 
        quoteCurrency(currencyToBuy, currencyToSell), 
        transactAmount);
  }
}

final class MarketOrder implements Order {
  private final Side side;
  private final Currency baseCurrency;
  private final Currency quoteCurrency;
  private final Money transactAmount;

  public MarketOrder(final Side side, final Currency baseCurrency, final Currency quoteCurrency, final Money transactAmount) {
    this.side = side;
    this.baseCurrency = baseCurrency;
    this.quoteCurrency = quoteCurrency;
    this.transactAmount = transactAmount;
  }

  @Override
  public <R> R accept(final OrderProcessor<R> processor) {
    return processor.on(this);
  }

  @Override
  public Side getSide() {
    return side;
  }

  @Override
  public Currency getBaseCurrency() {
    return baseCurrency;
  }

  @Override
  public Currency getQuoteCurrency() {
    return quoteCurrency;
  }

  @Override
  public Money getTransactAmount() {
    return transactAmount;
  }
}

