package io.mongock.utils;

import java.util.Objects;

public class Pair<F, S> {

  private final F first;
  private final S second;

  public Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }

  public F getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pair)) return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return getFirst().equals(pair.getFirst()) && getSecond().equals(pair.getSecond());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFirst(), getSecond());
  }
}
