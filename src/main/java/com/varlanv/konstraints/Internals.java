package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

interface Internals {

  class LazySupplier<T> implements Supplier<T> {

    @Nullable
    private T cached;
    private final Supplier<@NotNull T> delegate;

    private LazySupplier(Supplier<@NotNull T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T get() {
      var val = cached;
      if (val == null) {
        val = delegate.get();
        cached = val;
      }
      return val;

    }

    T or(T other) {
      return Objects.requireNonNullElse(cached, other);
    }
  }


  static <T> Supplier<@NotNull T> onceSupplier(Supplier<@NotNull T> delegate) {
    return new LazySupplier<>(delegate);
  }

  static <IN, OUT> Function<@NotNull IN, @NotNull Optional<OUT>> wrapOptional(
      Function<@NotNull IN, @Nullable OUT> fn) {
    return t -> Optional.ofNullable(fn.apply(t));
  }

  @SuppressWarnings("unchecked")
  static <T extends Throwable> T hide(Throwable t) throws T {
    throw (T) t;
  }

  static <T> List<T> mergeLists(List<T> left, List<T> right) {
    if (left.isEmpty()) {
      return right;
    } else if (right.isEmpty()) {
      return left;
    }
    var leftSize = left.size();
    var rightSize = right.size();
    var resultArray = new Object[leftSize + rightSize];
    for (var idx = 0; idx < leftSize; idx++) {
      resultArray[idx] = left.get(idx);
    }
    for (var idx = 0; idx < rightSize; idx++) {
      resultArray[idx + leftSize] = right.get(idx);
    }
    @SuppressWarnings("unchecked")
    T[] result = (T[]) resultArray;
    return Arrays.asList(result);
  }

  static <T> List<T> newListWithItem(List<T> left, T right) {
    var size = left.size();
    if (size == 0) {
      return List.of(right);
    } else if (size == 1) {
      return List.of(left.get(0), right);
    }
    var resultArray = new Object[size + 1];
    for (var idx = 0; idx < size; idx++) {
      resultArray[idx] = left.get(idx);
    }
    resultArray[size] = right;
    @SuppressWarnings("unchecked")
    T[] result = (T[]) resultArray;
    return Arrays.asList(result);
  }
}
