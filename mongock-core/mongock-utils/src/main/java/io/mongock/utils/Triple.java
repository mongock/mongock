package io.mongock.utils;

import java.util.Objects;

public class Triple<F,S,T> extends Pair<F,S> {
  private final T third;

  public Triple(F first, S second, T third) {
    super(first, second);
    this.third = third;
  }

  public T getThird() {
    return third;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Triple)) return false;
    if (!super.equals(o)) return false;
    Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
    return getThird().equals(triple.getThird());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getThird());
  }
}
