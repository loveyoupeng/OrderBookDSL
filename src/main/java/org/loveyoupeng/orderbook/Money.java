package org.loveyoupeng.orderbook;


public final class Money {
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
