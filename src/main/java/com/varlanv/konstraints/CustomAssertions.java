package com.varlanv.konstraints;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface CustomAssertions<TARGET, ROOT, CURRENT> extends BaseCustomAssertions<TARGET, ROOT, CURRENT> {

  @Override
  CustomAssertions<TARGET, ROOT, CURRENT> assertTrue(
      Function<@NotNull TARGET, @NotNull Boolean> action);

  @Override
  NullSpec<ROOT, CURRENT> parent();
}
