package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BaseCustomAssertions<TARGET, ROOT, CURRENT> {

  BaseCustomAssertions<TARGET, ROOT, CURRENT> assertTrue(
      Function<@NotNull TARGET, @NotNull Boolean> action);

  Rules<ROOT> rules();

  BaseNullSpec<ROOT, CURRENT> parent();
}
