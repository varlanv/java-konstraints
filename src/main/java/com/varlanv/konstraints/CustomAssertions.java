package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public final class CustomAssertions<TARGET, ROOT, CURRENT> extends BaseCustomAssertions<TARGET, ROOT, CURRENT> {

  @Override
  CustomAssertions<TARGET, ROOT, CURRENT> assertTrue(
      Function<@NotNull TARGET, @NotNull Boolean> action) {
    return null;
  }

  @Override
  RootNullSpec<ROOT, CURRENT> parent() {
    return null;
  }

  @Override
  Rules<ROOT> rules() {
    return null;
  }
}
