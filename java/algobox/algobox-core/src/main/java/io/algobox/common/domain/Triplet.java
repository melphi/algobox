package io.algobox.common.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public final class Triplet<S extends Serializable, T extends Serializable, V extends Serializable>
    implements Serializable {
  private final S first;

  private final T second;

  private final V third;

  public Triplet(S first, T second, V third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public S getFirst() {
    return first;
  }

  public T getSecond() {
    return second;
  }

  public V getThird() {
    return third;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
    return Objects.equal(first, triplet.first) &&
        Objects.equal(second, triplet.second) &&
        Objects.equal(third, triplet.third);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(first, second, third);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("first", first)
        .add("second", second)
        .add("third", third)
        .toString();
  }
}
