package org.loveyoupeng.orderbook.order;

import org.loveyoupeng.orderbook.*;
import static org.loveyoupeng.orderbook.Side.*;
import static org.loveyoupeng.orderbook.Currency.*;
import static org.loveyoupeng.orderbook.CurrencyPairs.*;

public final class MarketOrder implements Order {
  public interface MarketOrderBuilderToBuy {
    MarketOrderBuilderToSell toBuy(final Money money);
  }

  public interface MarketOrderBuilderToSell {
    Builder<MarketOrder> toSell(final Currency currency);
  }

  public static final class MarketOrderBuilder
      implements MarketOrderBuilderToBuy, MarketOrderBuilderToSell, Builder<MarketOrder> {
    private Currency currencyToBuy;
    private Currency currencyToSell;
    private Money transactAmount;

    @Override
    public MarketOrderBuilderToSell toBuy(final Money money) {
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
