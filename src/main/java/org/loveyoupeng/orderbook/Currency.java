package org.loveyoupeng.orderbook;

public enum Currency {
  USD, CNY;

  public Money amount(final int amount) {
    return new Money(this, amount);
  }
}
