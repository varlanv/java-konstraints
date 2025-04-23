package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> extends BaseCustomAssertions<TARGET, ROOT, CURRENT> {

  NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      Function<@NotNull TARGET, @NotNull Boolean> action);

  NestedCustomAssertions<TARGET, PARENT, ROOT, CURRENT> assertTrue(
      BiFunction<@NotNull TARGET, @NotNull PARENT, @NotNull Boolean> action);

  @Override
  Rules<ROOT> rules();

  @Override
  NullSpec<ROOT, CURRENT> parent();
}
