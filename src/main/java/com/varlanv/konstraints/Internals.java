package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

interface Internals {

  static <T> Supplier<@NotNull T> onceSupplier(Supplier<@NotNull T> delegate) {
    Objects.requireNonNull(delegate);
    @SuppressWarnings("unchecked")
    var cache = (T[]) new Object[1];
    return () -> {
      var cached = cache[0];
      if (cached == null) {
        cached = Objects.requireNonNull(delegate.get());
        cache[0] = cached;
      }
      return cached;
    };
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
    return List.of(result);
  }

  static <T> List<T> addToList(List<T> left, T right) {
    return mergeLists(left, List.of(right));
  }
}
