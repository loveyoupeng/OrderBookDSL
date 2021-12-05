package org.loveyoupeng.orderbook.order;

import org.loveyoupeng.orderbook.*;
import org.loveyoupeng.orderbook.order.MarketOrder.*;

public final class OrderBuilder {
  public static MarketOrderBuilderToBuy market() {
    return new MarketOrderBuilder();
  }
}

