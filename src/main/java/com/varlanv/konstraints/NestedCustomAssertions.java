package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> {

  NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      Function<@NotNull TARGET, @NotNull Boolean> action);

  NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      BiFunction<@NotNull TARGET, @NotNull PARENT, @NotNull Boolean> action);

  Rules<ROOT> rules();

  NullSpec<ROOT, CURRENT> parent();

  static <TARGET, PARENT, ROOT, CURRENT> NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> of(
      Function<@NotNull CURRENT, @Nullable TARGET> extract,
      Rules<ROOT> rules,
      Supplier<PARENT> parentSupplier,
      NullSpec<ROOT, CURRENT> parent) {
    return null;
  }
}
