package org.loveyoupeng.orderbook;

import org.junit.*;
import static org.junit.Assert.*;

import org.loveyoupeng.orderbook.order.*;
import org.loveyoupeng.orderbook.order.Order.*;
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
