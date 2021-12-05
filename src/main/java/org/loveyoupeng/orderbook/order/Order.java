package org.loveyoupeng.orderbook.order;

import org.loveyoupeng.orderbook.*;

public interface Order {
  Side getSide();

  Currency getBaseCurrency();
  
  Currency getQuoteCurrency();

  Money getTransactAmount();

  <R> R accept(final OrderProcessor<R> processor);

  public interface OrderProcessor<R> {
    default R on(final Order order) {
      throw new UnsupportedOperationException("not supported");
    }

    default R on(final MarketOrder order) {
      throw new UnsupportedOperationException("not supported");
    }
  }
}
