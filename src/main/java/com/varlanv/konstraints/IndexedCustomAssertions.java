package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> extends BaseCustomAssertions<TARGET, ROOT, CURRENT> {

  IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      Function<@NotNull TARGET, @NotNull Boolean> action);

  IndexedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      BiFunction<@NotNull TARGET, @NotNull PARENT, @NotNull Boolean> action);

  @Override
  Rules<ROOT> rules();

  @Override
  NullSpec<ROOT, CURRENT> parent();
}
