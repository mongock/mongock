package io.mongock.utils;

import java.util.Objects;

public class Triple<F,S,T> extends Pair<F,S> {
  private T third;

  public Triple(F first, S second, T third) {
    super(first, second);
    this.third = third;
  }

  public T getThird() {
    return third;
  }

  public void setThird(T third) {
    this.third = third;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
    return Objects.equals(third, triple.third);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), third);
  }
}
