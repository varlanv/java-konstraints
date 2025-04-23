package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> {

  IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      Function<@NotNull TARGET, @NotNull Boolean> action);

  IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      BiFunction<@NotNull TARGET, @NotNull PARENT, @NotNull Boolean> action);

  Rules<ROOT> rules();

  NullSpec<ROOT, CURRENT> parent();

  static <TARGET, PARENT, ROOT, CURRENT> IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> of(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Rules<ROOT> rules,
      Supplier<PARENT> parentSupplier,
      NullSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
